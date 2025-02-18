package com.compose.example

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest


@Preview(showBackground = true)
@Composable
fun RecentPagePreview() {
    RecentPage(true)
}

@Composable
fun RecentPage(isRecent: Boolean, viewModel: MainActivityViewModel = viewModel()) {
    val data = if (isRecent) viewModel.recent else viewModel.followed
    val recent by data.observeAsState(LoadingStatus.Loading)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (recent) {
            LoadingStatus.Loading -> {
                CircularProgressIndicator()
            }

            is LoadingStatus.Success -> {
                val list = (recent as LoadingStatus.Success).data
                if (list.isEmpty()) {
                    Image(
                        painter = painterResource(id = R.drawable.team_work),
                        contentDescription = "Sample Image"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Search for an Associate",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 19.sp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxHeight(),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(list) { item -> RecentListItem(item) }
                    }
                }
            }

            is LoadingStatus.Error -> {
                Text(text = "Error please try again")
            }
        }
    }
}

@Composable
fun RecentListItem(people: People) {
    val mContext = LocalContext.current
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .clickable {
                val intent = Intent(mContext, ViewProfileActivity::class.java)
                intent.putExtra("people", people)
                mContext.startActivity(intent)
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    people.name ?: "-",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingContent = {
                Column {
                    Text("2 reports", style = MaterialTheme.typography.bodyMedium)
                    Text(people.department ?: "-", style = MaterialTheme.typography.labelLarge)
                }
            },
            leadingContent = {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(people.profileUrl)
                        .diskCacheKey(System.currentTimeMillis().toString())
                        .build(),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)),
                    contentDescription = null
                )
            },
            trailingContent = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Favorite"
                    )
                }
            })
    }
}