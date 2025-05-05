
```kotlin
@Composable
fun PeopleDetailsScreen(person: DirectoryPerson) {
  val context = LocalContext.current

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      NavigationTop(
        titleVariant = NavigationTopTitle.Text(person.names?.firstOrNull()?.displayName ?: "-"),
        background = NavigationTopBackground.Color(GravityTheme.colors.background.brand),
        alignment = NavigationTopTitleAlignment.CENTER,
        contentColorOnImage = true,
        leadingAction = NavigationTopAction.Icon(
          icon = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back"
        ),
        onLeadingActionClick = {
          (context as? Activity)?.onBackPressedDispatcher?.onBackPressed()
        }
      )
    }
  ) { innerPadding ->

    Column(
      modifier = Modifier
        .padding(innerPadding)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = GravityTheme.spacing.medium1)
    ) {
      Spacer(modifier = Modifier.height(GravityTheme.spacing.medium2))

      // Avatar
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        AsyncImage(
          model = person.photos?.firstOrNull()?.url,
          contentDescription = "Profile photo",
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(GravityTheme.colors.background.inactiveEmphasisLow)
        )
      }

      Spacer(modifier = Modifier.height(GravityTheme.spacing.medium1))

      // Job Title
      Text(
        text = person.organizations?.firstOrNull()?.title ?: "--",
        modifier = Modifier.align(Alignment.CenterHorizontally),
        style = GravityTheme.typography.textStyles.medium2Bold
      )

      Spacer(modifier = Modifier.height(GravityTheme.spacing.medium2))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        ProfileActionItem(icon = R.drawable.ic_email, text = "Email", person)
        ProfileActionItem(icon = R.drawable.ic_text, text = "Text", person)
        ProfileActionItem(icon = R.drawable.ic_call, text = "Call", person)
        ProfileActionItem(icon = R.drawable.ic_star, text = "Follow", person)
      }

      Spacer(modifier = Modifier.height(GravityTheme.spacing.large1))

      // Profile Grid
      Column {
        ProfileDetailItem("EID", person.metadata?.sources?.firstOrNull()?.id ?: "--")
        ProfileDetailItem("Location", person.locations?.firstOrNull()?.value ?: "--")
        ProfileDetailItem("Department", person.organizations?.firstOrNull()?.department ?: "--")
        ProfileDetailItem("Organization", person.organizations?.firstOrNull()?.name ?: "--")
        ProfileDetailItem("Job Family", person.organizations?.firstOrNull()?.jobFamily ?: "--")
        ProfileDetailItem("Email", person.emailAddresses?.firstOrNull()?.value ?: "--")
        ProfileDetailItem("Phone", person.phoneNumbers?.firstOrNull()?.value ?: "--")
        ProfileDetailItem("Manager", person.relations?.firstOrNull()?.person?.displayName ?: "--")
      }
    }
  }
}



@Composable
fun ProfileActionItem(icon: Int, text: String, person: DirectoryPerson) {
  val context = LocalContext.current

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clickable {
        when (text) {
          "Email" -> sendEmail(context, person.emailAddresses?.firstOrNull()?.value ?: "")
          "Call"  -> sendPhoneCall(context, person.phoneNumbers?.firstOrNull()?.value ?: "")
          "Text"  -> sendTextMessage(context, person.phoneNumbers?.firstOrNull()?.value ?: "")
          "Follow" -> {/* TODO: Hook into follow state */}
        }
      }
  ) {
    Icon(
      painter = painterResource(icon),
      contentDescription = text,
      modifier = Modifier.size(28.dp),
      tint = GravityTheme.colors.icon.neutralPrimary
    )
    Text(
      text = text,
      style = GravityTheme.typography.textStyles.smallRegular,
      modifier = Modifier.padding(top = 4.dp)
    )
  }
}
@Composable
fun ProfileDetailItem(title: String, value: String) {
  Column(modifier = Modifier
    .fillMaxWidth()
    .padding(vertical = GravityTheme.spacing.small1)
  ) {
    Text(
      text = title,
      style = GravityTheme.typography.textStyles.bodyMedium.copy(fontWeight = FontWeight.Bold)
    )
    Text(
      text = value,
      style = GravityTheme.typography.textStyles.bodyMedium
    )
  }
}









