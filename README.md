
```kotlin
@Composable
fun RecentListItem(
  people: DirectoryPerson,
  navController: NavController
) {
  Card(
    params = cardParams {
      decoration = Decoration.ELEVATION
      cornerRadius = CornerRadius.Medium
    },
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = GravityTheme.spacing.medium1),
    onClick = {
      navController.navigate(
        "peopleFinderDetails/${people.externalIds?.firstOrNull()?.value}"
      )
    }
  ) {
    ListItem(
      headlineContent = {
        Text(
          text = people.names?.firstOrNull()?.displayName ?: "-",
          style = GravityTheme.typography.textStyles.medium2Bold
        )
      },
      supportingContent = {
        Column {
          Text(
            text = people.organizations?.firstOrNull()?.title
              ?: people.organizations?.firstOrNull()?.department
              ?: "--",
            style = GravityTheme.typography.textStyles.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          // Optional: show roll-up or reports text if available
          people.metadata?.reportingSummary?.let {
            Text(
              text = it,
              style = GravityTheme.typography.textStyles.smallRegular
            )
          }
        }
      },
      leadingContent = {
        AsyncImage(
          model = ImageRequest.Builder(LocalContext.current)
            .data(people.photos?.firstOrNull()?.url)
            .diskCacheKey(System.currentTimeMillis().toString())
            .build(),
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(GravityTheme.colors.background.inactiveEmphasisLow),
          contentDescription = null
        )
      },
      trailingContent = {
        IconButton(onClick = { /* TODO: Favorite */ }) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Favorite",
            tint = GravityTheme.colors.icon.neutralPrimary
          )
        }
      }
    )
  }
}