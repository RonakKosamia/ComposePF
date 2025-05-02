
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







