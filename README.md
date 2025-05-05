
```kotlin
@Composable
fun PeopleDetailsScreen(person: DirectoryPerson) {
  val context = LocalContext.current

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      NavigationTop(
        titleVariant = NavigationTopTitle.Text(
          title = person.names?.firstOrNull()?.displayName ?: "--"
        ),
        background = NavigationTopBackground.Color(
          color = GravityTheme.colors.background.brand
        ),
        alignment = NavigationTopTitleAlignment.CENTER,
        contentColorOnImage = true,
        leadingAction = NavigationTopAction.Icon(
          icon = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back"
        ),
        onLeadingActionClick = { (context as? Activity)?.onBackPressed() }
      )
    }
  ) { innerPadding ->

    Column(
      modifier = Modifier
        .padding(innerPadding)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {

      // --- Blue Header Block ---
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(GravityTheme.colors.background.brand)
          .padding(horizontal = GravityTheme.spacing.medium1)
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Spacer(Modifier.height(GravityTheme.spacing.medium2))

          AsyncImage(
            model = person.photos?.firstOrNull()?.url,
            contentDescription = "Profile photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .size(96.dp)
              .clip(CircleShape)
          )

          Spacer(Modifier.height(GravityTheme.spacing.medium1))

          Text(
            text = person.organizations?.firstOrNull()?.title ?: "--",
            style = GravityTheme.typography.textStyles.medium2SemiBold
          )

          Spacer(Modifier.height(GravityTheme.spacing.medium2))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            ProfileActionItem(R.drawable.ppl_finder_mail, "Email", person)
            ProfileActionItem(R.drawable.ppl_finder_chat, "Text", person)
            ProfileActionItem(R.drawable.ppl_finder_phone, "Call", person)
            ProfileActionItem(R.drawable.ic_download, "Download", person)
            ProfileActionItem(R.drawable.ic_star, "Follow", person)
          }

          Spacer(Modifier.height(GravityTheme.spacing.medium1))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            GravityButton(text = "Slack") {
              context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("slack://open")
              })
            }

            GravityButton(text = "Google Calendar") {
              context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("com.google.calendar://")
              })
            }
          }

          Spacer(Modifier.height(GravityTheme.spacing.large1))
        }
      }

      // --- Profile Detail Grid (White Background) ---
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(GravityTheme.colors.background.canvas)
          .padding(horizontal = GravityTheme.spacing.medium1)
      ) {
        ProfileDetailItem("EID", person.metadata?.sources?.firstOrNull()?.id ?: "--")
        ProfileDetailItem("Location", person.locations?.firstOrNull()?.value ?: "--")
        ProfileDetailItem("Department", person.organizations?.firstOrNull()?.department ?: "--")
        ProfileDetailItem("Organization", person.organizations?.firstOrNull()?.title ?: "--")
        ProfileDetailItem("Job Family", person.organizations?.firstOrNull()?.department ?: "--")
        ProfileDetailItem("Email", person.emailAddresses?.firstOrNull()?.value ?: "--")
        ProfileDetailItem("Phone", person.phoneNumbers?.firstOrNull()?.value ?: "--")
        ProfileDetailItem("Manager", person.relations?.firstOrNull()?.person ?: "--")
      }
    }
  }
}