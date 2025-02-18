@file:OptIn(ExperimentalMaterial3Api::class)

package com.compose.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.IntentCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest

class ViewProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val people = IntentCompat.getParcelableExtra(intent, "people", People::class.java)
        setContent { AppTheme { ProfileUI(people = people!!) } }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileUIPreview() {
    ProfileUI()
}

@Composable
fun ProfileUI(people: People = samplePeoples[0]) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        ), title = {
            Text(
                people.name ?: "-", style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
            )
        }, navigationIcon = {
            IconButton(onClick = { /* Handle back action */ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }, actions = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Search, contentDescription = "Search"
                )
            }
        })

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    people.jobFamily ?: "-",
                    style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onPrimary)
                )

                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(people.profileUrl)
                        .diskCacheKey(System.currentTimeMillis().toString()).build(),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,

                    ) {
                    ProfileActionItem(icon = R.drawable.ic_email, text = "Email")
                    ProfileActionItem(icon = R.drawable.ic_message, text = "Text")
                    ProfileActionItem(icon = R.drawable.ic_phone_call, text = "Call")
                    ProfileActionItem(icon = R.drawable.ic_download, text = "Download")
                    ProfileActionItem(icon = R.drawable.ic_star, text = "Follow")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_slack), // Replace with your image resource
                                contentDescription = "Slack",
                                modifier = Modifier
                                    .size(20.dp),
                            )
                            Text(
                                text = "Slack",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                            .clickable { },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_google_calendar), // Replace with your image resource
                                contentDescription = "Google Calendar",
                                modifier = Modifier
                                    .size(20.dp),
                            )
                            Text(
                                text = "Google Calendar",
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ProfileDetailItem(title = "EID", value = people.eid ?: "-")
            ProfileDetailItem(title = "Location", value = people.location ?: "-")
            ProfileDetailItem(title = "Department", value = people.department ?: "-")
            ProfileDetailItem(title = "Organization", value = people.organization ?: "-")
            ProfileDetailItem(title = "Job Family", value = people.jobFamily ?: "-")
            ProfileDetailItem(title = "Email", value = people.email ?: "-")
            ProfileDetailItem(title = "Mobile", value = people.mobile ?: "-")
            ProfileDetailItem(title = "Manager", value = people.manager ?: "-")
        }
    }
}

@Composable
fun ProfileActionItem(icon: Int, text: String) {
    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(icon),
            contentDescription = text,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimary)
        )
    }
}

@Composable
fun ProfileDetailItem(title: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(0.3f),
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            modifier = Modifier.padding(8.dp),
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}