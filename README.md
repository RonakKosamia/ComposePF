    # ComposePF
    
    suspend fun signIn(): GoogleSignInResult {
      return try {
        val client = Identity.getAuthorizationClient(activityContext)
        val builder = AuthorizationRequest.Builder()
          .setRequestedScopes(listOf(CalendarScopes.CALENDAR_READONLY))
          .build()
    
        val result = Tasks.await(client.authorize(builder))
    
        if (result.hasResolution()) {
          val request = IntentSenderRequest.Builder(result.pendingIntent!!).build()
          GoogleSignInResult.NeedsResolution(request)
        } else {
          GoogleSignInResult.Success(result)
        }
      } catch (e: Exception) {
        GoogleSignInResult.Failure(e)
      }
    }
    
    // Create sealed class to signal results needing intent resolution
    sealed class GoogleSignInResult {
      data class Success(val account: GoogleAccount): GoogleSignInResult()
      data class NeedsResolution(val request: IntentSenderRequest): GoogleSignInResult()
      data class Failure(val error: Throwable): GoogleSignInResult()
    }
    
    
    val context = LocalContext.current
    val activity = context.findActivity()
    val googleAuthUiProvider = koinInject<GoogleAuthUiProvider>()
    
    val intentSenderLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        runCatching {
          val authResult = Identity.getAuthorizationClient(context)
            .getAuthorizationResultFromIntent(result.data!!)
          googleSignInSuccess(authResult)
        }.onFailure {
          googleSignInFailure(it)
        }
      }
    
    // Launch sign-in on some user action
    LaunchedEffect(Unit) {
      when (val result = googleAuthUiProvider.signIn()) {
        is GoogleSignInResult.Success -> googleSignInSuccess(result.account)
        is GoogleSignInResult.NeedsResolution -> {
          intentSenderLauncher.launch(result.request)
        }
        is GoogleSignInResult.Failure -> googleSignInFailure(result.error)
      }
    }
    
    
    import com.google.api.services.calendar.CalendarScopes
    import com.google.api.services.people.v1.PeopleServiceScopes
    
    val scopes = arrayListOf(
      Scope(CalendarScopes.CALENDAR_READONLY),
      Scope(PeopleServiceScopes.CONTACTS_READONLY)
    )
    
    
    androidMain {
      dependencies {
        implementation("com.google.apis:google-api-services-calendar:v3-rev202-1.25.0")
        implementation("com.google.apis:google-api-services-people:v1-rev202-1.25.0")
        implementation("com.google.api-client:google-api-client-android:1.34.0")
        implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.0")
        implementation("com.google.http-client:google-http-client-gson:1.43.3")
      }
    }
    
    class GoogleAuthProvider(private val context: Context) {
    
      private val signInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          .requestEmail()
          .requestProfile()
          .requestId()
          .requestServerAuthCode(
            context.getString(R.string.default_web_client_id),
            true
          )
          .requestScopes(
            Scope("https://www.googleapis.com/auth/cloud-identity.directory.readonly")
          )
          .build()
      )
    
      fun getSignInIntent(): Intent = signInClient.signInIntent
    
      fun signOut() {
        signInClient.signOut()
      }
    
      suspend fun handleSignInResult(data: Intent?): AuthResult {
        return try {
          val account = GoogleSignIn.getSignedInAccountFromIntent(data)
            .getResult(ApiException::class.java)
    
          val authCode = account?.serverAuthCode
            ?: return AuthResult.Failure(IllegalStateException("Missing auth code"))
    
          val accessToken = exchangeCodeForAccessToken(authCode)
    
          AuthResult.Success(account, accessToken)
        } catch (e: Exception) {
          AuthResult.Failure(e)
        }
      }
    
      private suspend fun exchangeCodeForAccessToken(code: String): String {
        val client = HttpClient(CIO) {
          install(ContentNegotiation) { json() }
        }
    
        val response = client.submitForm(
          url = "https://oauth2.googleapis.com/token",
          formParameters = Parameters.build {
            append("code", code)
            append("client_id", context.getString(R.string.default_web_client_id))
            append("client_secret", context.getString(R.string.client_secret))
            append("redirect_uri", "")
            append("grant_type", "authorization_code")
          }
        )
    
        val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        return json["access_token"]?.jsonPrimitive?.content
          ?: throw IllegalStateException("Missing access_token in response")
      }
    }
    
    sealed class AuthResult {
      data class Success(
        val account: GoogleSignInAccount,
        val accessToken: String
      ) : AuthResult()
    
      data class Failure(val error: Throwable) : AuthResult()
      object SignedOut : AuthResult()
    }
    //compose
    val context = LocalContext.current
      val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
      ) { result ->
        viewModel.signInResult(result.data)
      }
    //Launched effect
    val intent = GoogleAuthProvider(context).getSignInIntent()
    launcher.launch(intent)
    
    
    //Repo
    
    class PeopleFinderRepository {
      suspend fun search(query: String, token: String): List<PeopleFinderItem> {
        return try {
          val api = PeopleFinderApiService(token)
          api.searchPeople(query).people
        } catch (e: Exception) {
          emptyList() // Handle/log better in prod
        }
      }
    }
    
    
    //API Call Ktor
    
    class PeopleFinderApiService(private val token: String) {
    
      private val client = HttpClient {
        install(ContentNegotiation) { json() }
        defaultRequest {
          header("Authorization", "Bearer $token")
          contentType(ContentType.Application.Json)
        }
      }
    
      suspend fun searchPeople(query: String): DirectorySearchResponse {
        return client.post("https://people.googleapis.com/v1/people:searchDirectoryPeople") {
          setBody(
            buildJsonObject {
              put("query", query)
              put("readMask", "names,emailAddresses,organizations")
              put("sources", buildJsonArray {
                add("DIRECTORY_SOURCE_TYPE_DOMAIN_PROFILE")
              })
            }
          )
        }
      }
    }
    
    
    
    //Data Class: 
    
    @Serializable
    data class PeopleFinderItem(
      val names: List<Name>? = null,
      val emailAddresses: List<EmailAddress>? = null,
      val organizations: List<Organization>? = null
    )
    
    @Serializable
    data class Name(val displayName: String)
    
    @Serializable
    data class EmailAddress(val value: String)
    
    @Serializable
    data class Organization(
      val name: String? = null,
      val title: String? = null
    )
    
    @Serializable
    data class DirectorySearchResponse(
      val people: List<PeopleFinderItem> = emptyList()
    )
    
    
    
