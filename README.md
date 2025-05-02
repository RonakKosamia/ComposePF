## PeopleFinderScreen.kt (Compose + GravityTheme + Chips + Search Toggle)

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun PeopleFinderScreen(
  navController: Any,
  onPersonClicked: (DirectoryPerson) -> Unit,
  token: String?
) {
  val viewModel: PeopleFinderViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val filterState by viewModel.filterState.collectAsStateWithLifecycle()
  val searchText by viewModel.searchText.collectAsStateWithLifecycle()

  val shouldShowSearchBar = remember { mutableStateOf(false) }
  val searchQuery = remember { mutableStateOf("") }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      NavigationTop(
        titleVariant = NavigationTopTitle.Text("People"),
        background = NavigationTopBackground.Color(
          color = GravityTheme.colors.background.brand
        ),
        contentColorOnImage = true,
        alignment = NavigationTopTitleAlignment.CENTER,
        leadingAction = NavigationTopAction.Icon(
          icon = vectorResource(Res.drawable.grv_ui_arrow_left_lined),
          contentDescription = "Back icon"
        ),
        trailingActions = NavigationTopTrailingActions.Icons(
          actions = listOf(
            NavigationTopAction.Icon(
              icon = Icons.Sharp.Search,
              contentDescription = "Search"
            )
          ).toImmutableList()
        ),
        onLeadingActionClick = {
          (navController as NavController).popBackStack()
        },
        onTrailingActionClick = { identifier ->
          if (identifier == NavigationTopActionIdentifier.PRIMARY) {
            shouldShowSearchBar.value = true
          }
        },
        searchBarParams = NavigationTopSearchBarParams(
          showSearchBar = shouldShowSearchBar.value,
          onSearchBarDismiss = {
            shouldShowSearchBar.value = false
            searchQuery.value = ""
            viewModel.setSearchVisibility(false)
            viewModel.onAction(
              PFViewModelAction.OnFilterSelected(FilterType.RECENT)
            )
          },
          onSearchQueryChange = {
            searchQuery.value = it
            viewModel.onSearchQueryChanged(it)
          },
          onSearch = {
            viewModel.onSearchQueryChanged(searchQuery.value)
          },
          searchInitialQuery = searchQuery.value,
          searchPlaceholderText = "Search for an Associate"
        )
      )
    }
  ) { innerPadding ->

    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      Column(
        modifier = Modifier.fillMaxSize()
      ) {
        // Always visible filter chips
        PeopleFilterView(
          viewModel = viewModel,
          onAction = viewModel::onAction
        )

        when (val state = uiState) {
          is PeopleFinderUIState.Loading -> {
            Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center
            ) {
              CircularProgressIndicator()
            }
          }

          is PeopleFinderUIState.Data -> {
            val people = state.contacts

            if (people.isEmpty()) {
              Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Image(
                  painter = painterResource(R.drawable.team_work),
                  contentDescription = "Empty"
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Search for an Associate")
              }
            } else {
              LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                  vertical = GravityTheme.spacing.medium1,
                  horizontal = GravityTheme.spacing.medium1
                ),
                verticalArrangement = Arrangement.spacedBy(GravityTheme.spacing.medium1)
              ) {
                items(people) { person ->
                  RecentListItem(
                    people = person,
                    navController = navController as NavController
                  )
                }
              }
            }
          }

          is PeopleFinderUIState.Error -> {
            Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center
            ) {
              Text("Error: ${state.errorMessage}")
            }
          }
        }
      }
    }
  }
}