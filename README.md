
```kotlin
actual class GoogleAuthUiProviderImpl(
    private val launcher: GoogleAuthLauncher,
    private val activity: Activity,
    private val onTokenAvailable: (String?) -> Unit
) : GoogleAuthUiProvider {

    override suspend fun requestAccessToken(): String? {
        // Trigger launch. Return null; result will come async via onTokenAvailable
        val request = launcher.buildAuthorizationRequest()
        launcher.launchAuthorizationIntent(activity, request)
        return null
    }

    fun handleAuthorizationResult(data: Intent?) {
        val result = launcher.extractAuthorizationResult(data ?: return)
        val code = result?.serverAuthCode ?: return onTokenAvailable(null)
        CoroutineScope(Dispatchers.IO).launch {
            val token = exchangeCodeForAccessToken(code)
            withContext(Dispatchers.Main) { onTokenAvailable(token) }
        }
    }
}



val launcher = remember { GoogleAuthLauncher(context) }

val authProvider = remember {
    GoogleAuthUiProviderImpl(
        launcher = launcher,
        activity = activity,
        onTokenAvailable = { token ->
            viewModel.onGoogleAccessToken(token)
        }
    )
}


val authResultLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.StartIntentSenderForResult()
) { result ->
    authProvider.handleAuthorizationResult(result.data)
}


private val _googleToken = MutableStateFlow<String?>(null)
val googleToken: StateFlow<String?> = _googleToken

fun onGoogleAccessToken(token: String?) {
    _googleToken.value = token
}

GlobalViewModel.googleToken.collectAsState()


class GoogleAuthUiProviderImpl(
  private val context: Context,
  private val activity: Activity,
  private val clientId: String,
  private val onTokenAvailable: (String?) -> Unit
) {
  private val authClient = Identity.getAuthorizationClient(context)

  fun launchAuthorization(
    activity: Activity,
    launcher: ActivityResultLauncher<IntentSenderRequest>
  ) {
    val request = AuthorizationRequest.builder()
      .setRequestedScopes(
        listOf(Scope("https://www.googleapis.com/auth/directory.readonly"))
      )
      .requestOfflineAccess(clientId, true)
      .build()

    val intentSender = authClient.authorize(request).intentSender
    val requestWrapper = IntentSenderRequest.Builder(intentSender).build()
    launcher.launch(requestWrapper)
  }

  fun handleAuthorizationResult(data: Intent) {
    val result = authClient.getAuthorizationResultFromIntent(data)
    val code = result?.serverAuthCode ?: return onTokenAvailable(null)

    CoroutineScope(Dispatchers.IO).launch {
      val token = exchangeCodeForAccessToken(code)
      withContext(Dispatchers.Main) {
        onTokenAvailable(token)
      }
    }
  }
}


val authClient = Identity.getAuthorizationClient(context)

val request = AuthorizationRequest.builder()
    .setRequestedScopes(
        listOf(Scope("https://www.googleapis.com/auth/directory.readonly"))
    )
    .requestOfflineAccess(clientId, true)
    .build()

val result = authClient.authorize(request)
val intentSender = result.pendingIntent.intentSender

val launcherRequest = IntentSenderRequest.Builder(intentSender).build()
(launcher as ActivityResultLauncher<IntentSenderRequest>).launch(launcherRequest)






import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.AuthorizationClient
import com.google.android.gms.auth.api.identity.Scope
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.*

actual class GoogleAuthUiProvider(
    private val context: Context,
    private val clientId: String
) {
    private val authClient: AuthorizationClient by lazy {
        Identity.getAuthorizationClient(context)
    }

    private var tokenCallback: ((String?) -> Unit)? = null

    actual fun launch(
        activity: Any,
        launcher: Any,
        onTokenAvailable: (String?) -> Unit
    ) {
        tokenCallback = onTokenAvailable

        val request = AuthorizationRequest.builder()
            .setRequestedScopes(
                listOf(Scope("https://www.googleapis.com/auth/directory.readonly"))
            )
            .requestOfflineAccess(clientId, true)
            .build()

        val pendingIntentResult = authClient.authorize(request)
        val intentSender = pendingIntentResult.pendingIntent.intentSender
        val requestWrapper = IntentSenderRequest.Builder(intentSender).build()

        @Suppress("UNCHECKED_CAST")
        (launcher as ActivityResultLauncher<IntentSenderRequest>).launch(requestWrapper)
    }

    actual fun handleAuthorizationResult(data: Any) {
        val intent = data as? Intent ?: return
        val result = authClient.getAuthorizationResultFromIntent(intent)
        val code = result?.serverAuthCode ?: return tokenCallback?.invoke(null)

        CoroutineScope(Dispatchers.IO).launch {
            val token = exchangeCodeForAccessToken(code)
            withContext(Dispatchers.Main) {
                tokenCallback?.invoke(token)
                tokenCallback = null
            }
        }
    }

    private suspend fun exchangeCodeForAccessToken(code: String): String? {
        val http = HttpClient()
        val response = http.submitForm(
            url = "https://oauth2.googleapis.com/token",
            formParameters = Parameters.build {
                append("code", code)
                append("client_id", clientId)
                append("grant_type", "authorization_code")
                append("redirect_uri", "https://www.google.com")
            }
        )

        if (!response.status.isSuccess()) return null
        val body = response.bodyAsText()
        val json = Json.parseToJsonElement(body).jsonObject
        return json["access_token"]?.jsonPrimitive?.content
    }
}
expect class GoogleAuthUiProvider {
    fun launch(
        activity: Any,
        launcher: Any,
        onTokenAvailable: (String?) -> Unit
    )

    fun handleAuthorizationResult(data: Any)
}





