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




