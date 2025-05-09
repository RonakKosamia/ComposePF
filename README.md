
```kotlin


/////////////////////////////////////MainActivity.kt///////////////////////////

lateinit var triggerAuthRequest: () -> Unit

override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)

  GoogleTokenStoreProvider.initialize(applicationContext)

 val pendingIntentLauncher = registerForActivityResult(
  ActivityResultContracts.StartIntentSenderForResult()
) { result ->
  try {
    val authResult = Identity.getAuthorizationClient(this)
      .getAuthorizationResultFromIntent(result.data)

    val token = authResult.accessToken
    val expiry = System.currentTimeMillis() + 3600_000
    GoogleTokenStoreProvider.get().setToken(token, expiry)
    GoogleAuthDispatcher.notifyTokenReceived(token)

  } catch (e: ApiException) {
    Log.e("MainActivity", "Auth failed: ${e.status}")
    GoogleAuthDispatcher.notifyTokenReceived(null) // Fallback
  }
}


triggerAuthRequest = {
    val scope = Scope("https://www.googleapis.com/auth/directory.readonly")
    val authRequest = AuthorizationRequest.Builder()
      .setRequestedScopes(listOf(scope))
      .requestOfflineAccess(
        serverClientId = "765854762916-<client-id>.apps.googleusercontent.com",
        forceCodeForRefreshToken = true
      )
      .build()

    Identity.getAuthorizationClient(this)
      .authorize(authRequest)
      .addOnSuccessListener { result ->
        if (result.hasResolution()) {
          val intentSender = result.pendingIntent!!.intentSender
          val intent = IntentSenderRequest.Builder(intentSender).build()
          pendingIntentLauncher.launch(intent)
        } else {
          val token = result.accessToken
          val expiry = System.currentTimeMillis() + 3600_000
          GoogleTokenStoreProvider.get().setToken(token, expiry)
          GoogleAuthDispatcher.notifyTokenReceived(token)
        }
      }
      .addOnFailureListener { e ->
        Log.e("MainActivity", "Authorization failed", e)
        GoogleAuthDispatcher.notifyTokenReceived(null)
      }
  }

  GoogleAuthDispatcher.setLauncher {
    triggerAuthRequest()
  }

  setContent {
    App(platformFeatureProvider)
  }
}

/////////////////////////////////////GoogleAuthDispatcher.kt///////////////////////////
features/core/commonMain/kotlin/com/..../core/auth/GoogleAuthDispatcher.kt
////////////////////////////////////////////////////////////////

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object GoogleAuthDispatcher {

  // Holds reference to trigger launcher from feature
  private var launcher: (() -> Unit)? = null

  // State-safe way to observe token after launch
  private val _pendingTokenCallback =
    MutableStateFlow<((String?) -> Unit)?>(null)

  fun setLauncher(launchFunc: () -> Unit) {
    launcher = launchFunc
  }

  fun requestTokenIfNeeded(
    context: Any, // ViewModel-safe param
    onTokenAvailable: (String?) -> Unit
  ) {
    val store = GoogleTokenStoreProvider.get()
    val token = store.getToken()
    if (token != null) {
      onTokenAvailable(token)
    } else {
      _pendingTokenCallback.update { onTokenAvailable }
      launcher?.invoke()
    }
  }

  fun notifyTokenReceived(token: String?) {
    _pendingTokenCallback.value?.invoke(token)
    _pendingTokenCallback.update { null }
  }

  fun reset() {
    _pendingTokenCallback.update { null }
  }
}


//////////////////////PFViewmodel.kt-//////////////////////
fun onScreenOpened(context: Context) {
  GoogleAuthDispatcher.requestTokenIfNeeded(context) { token ->
    if (!token.isNullOrBlank()) {
      fetchPeople(token)
    } else {
      showLoginError()
    }
  }
}


//////////////////////////////
features/core/commonMain/kotlin/com/..../core/auth/GoogleTokenStore.kt
////////////////////////


interface GoogleTokenStore {
  suspend fun setToken(token: String, expiresAtMillis: Long)
  suspend fun getToken(): String?
  suspend fun isTokenValid(): Boolean
  suspend fun clearToken()
}

//////////////////////////////
features/core/commonMain/kotlin/com/..../core/auth/GoogleTokenStoreProvider.kt
////////////////////////


expect object GoogleTokenStoreProvider {
  fun get(): GoogleTokenStore
}

/////////////////////////////
features/core/androidMain/kotlin/com/..../core/auth/GoogleTokenStoreProvider.kt
//////////////////////////


import android.content.Context

actual object GoogleTokenStoreProvider {
  private lateinit var instance: GoogleTokenStore

  fun initialize(context: Context) {
    instance = AndroidGoogleTokenStore(context)
  }

  actual fun get(): GoogleTokenStore = instance
}


/////////////////////////////
features/core/androidMain/kotlin/com/..../core/auth/AndroidGoogleTokenStore.kt
//////////////////////////


import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AndroidGoogleTokenStore(context: Context) : GoogleTokenStore {
  private val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

  private val prefs = EncryptedSharedPreferences.create(
    context,
    "secure_token_prefs",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
  )

  override suspend fun setToken(token: String, expiresAtMillis: Long) {
    prefs.edit()
      .putString("google_token", token)
      .putLong("expires_at", expiresAtMillis)
      .apply()
  }

  override suspend fun getToken(): String? {
    val token = prefs.getString("google_token", null)
    val expires = prefs.getLong("expires_at", 0L)
    return if (System.currentTimeMillis() < expires) token else null
  }

  override suspend fun isTokenValid(): Boolean = getToken() != null

  override suspend fun clearToken() {
    prefs.edit().clear().apply()
  }
}


