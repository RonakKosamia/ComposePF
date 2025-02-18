@file:OptIn(ExperimentalMaterial3Api::class)

package com.compose.example

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Tasks
import com.google.api.services.calendar.CalendarScopes
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    private lateinit var intentSenderHelper: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppTheme { HomePage() } }

        intentSenderHelper =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                runCatching {
                    val authResult = Identity.getAuthorizationClient(this)
                        .getAuthorizationResultFromIntent(result.data)
                    googleSignInSuccess(authResult)
                }.onFailure {
                    googleSignInFailure(it)
                }
            }
    }

    fun requestGoogleLogin() {
        lifecycleScope.launch {
            runCatching {
                val scopes = arrayListOf(Scope(CalendarScopes.CALENDAR_READONLY))
                val builder = AuthorizationRequest.builder().setRequestedScopes(scopes).build()
                val request = Identity.getAuthorizationClient(this@MainActivity)
                withContext(IO) { Tasks.await(request.authorize(builder)) }
            }.onSuccess { result ->
                if (result.hasResolution()) {
                    runCatching {
                        val request = IntentSenderRequest.Builder(result.pendingIntent!!).build()
                        intentSenderHelper.launch(request)
                    }.onFailure {
                        googleSignInFailure(it)
                    }
                } else {
                    googleSignInSuccess(result)
                }
            }.onFailure {
                googleSignInFailure(it)
            }
        }
    }

    private fun googleSignInSuccess(result: AuthorizationResult) {
        Log.e(javaClass.simpleName, "SignIn Success: " + result.serverAuthCode)
        Toast.makeText(this, "SignIn Success", Toast.LENGTH_LONG).show()
    }

    private fun googleSignInFailure(error: Throwable) {
        Log.e(javaClass.simpleName, "SignIn Failure: " + error.message)
        Toast.makeText(this, "SignIn Failed : " + error.message, Toast.LENGTH_LONG).show()
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    AppTheme {
        HomePage()
    }
}

enum class Tabs(val text: String) {
    Recent("Recent"),
    Followed("Followed")
}

@Composable
fun HomePage() {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { Tabs.entries.size })
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage } }
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    BackHandler(enabled = isSearchVisible) {
        isSearchVisible = false
        keyboardController?.hide()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (isSearchVisible) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .height(64.dp)
                    ) {
                        TextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .focusRequester(focusRequester),
                            placeholder = { Text("Search...") },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                            leadingIcon = {
                                IconButton(onClick = { isSearchVisible = false; searchText = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Cancel"
                                    )
                                }
                            },
                            trailingIcon = {
                                IconButton(onClick = { /* Handle back action */ }) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search"
                                    )
                                }
                            }
                        )
                    }
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    }
                } else {
                    val context = LocalContext.current
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        title = {
                            Text(
                                "Peoples",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { /* Handle back action */ }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (context is MainActivity) context.requestGoogleLogin()
                            }) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_google_login),
                                    contentDescription = "Login"
                                )
                            }
                            IconButton(onClick = { isSearchVisible = true }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ScrollableTabRow(
                containerColor = MaterialTheme.colorScheme.primary,
                selectedTabIndex = selectedTabIndex,
                indicator = { },
                edgePadding = 12.dp,
                divider = {}
            ) {
                Tabs.entries.forEachIndexed { index, currentTab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(currentTab.ordinal) } },
                        text = {
                            Text(
                                text = currentTab.text,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        },
                        modifier = Modifier
                            .height(52.dp)
                            .padding(start = 4.dp, end = 8.dp, bottom = 16.dp, top = 0.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .let {
                                if (selectedTabIndex == index) it.background(MaterialTheme.colorScheme.onPrimary)
                                else it.border(
                                    BorderStroke(
                                        2.dp,
                                        MaterialTheme.colorScheme.onPrimary
                                    ), RoundedCornerShape(8.dp)
                                )
                            }
                    )
                }
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                RecentPage(it == 0)
            }
        }
    }
}

