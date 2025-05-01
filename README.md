# People Finder – Chip-based Filter Refactor

This update replaces the old `TabIndex`-based logic with chip-based filters (`Recent` and `Followed`). Based on the selected chip, the correct data is fetched and displayed using `StateFlow`-driven UI state.

---

## ViewModel

```kotlin







@## RecentListItem – using Gravity Card

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
          text = people.names?.firstOrNull()?.givenName ?: "-",
          style = GravityTheme.typography.textStyles.titleMedium
        )
      },
      supportingContent = {
        Column {
          people.emailAddresses?.firstOrNull()?.value?.let {
            Text(it, style = GravityTheme.typography.textStyles.bodyMedium)
          }
          Text(
            text = people.organizations?.firstOrNull()?.department ?: "--",
            style = GravityTheme.typography.textStyles.labelLarge
          )
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
            .background(
              GravityTheme.colors.background.inactiveEmphasisLow
            ),
          contentDescription = null
        )
      },
      trailingContent = {
        IconButton(onClick = { /* TODO: Favorite */ }) {
          Icon(Icons.Default.Star, contentDescription = "Favorite")
        }
      }
    )
  }
}

class PeopleFinderViewModel @Inject constructor(
  private val repository: PeopleFinderRepository
) : ViewModel() {

  private val _uiState =
    MutableStateFlow<PeopleFinderUIState>(PeopleFinderUIState.Loading)
  val uiState: StateFlow<PeopleFinderUIState> = _uiState

  private val _filterState =
    MutableStateFlow(PeopleFilterState())
  val filterState: StateFlow<PeopleFilterState> = _filterState

  init {
    fetchContactsFor(FilterType.RECENT)
  }

  fun onAction(action: PFViewModelAction) {
    when (action) {
      is PFViewModelAction.OnFilterSelected -> {
        _filterState.value =
          _filterState.value.copy(selectedFilterOption = action.filterType)
        fetchContactsFor(action.filterType)
      }
    }
  }

  private fun fetchContactsFor(filter: FilterType) {
    viewModelScope.launch {
      _uiState.value = PeopleFinderUIState.Loading
      try {
        val contacts = when (filter) {
          FilterType.RECENT -> repository.getRecentContacts()
          FilterType.FOLLOWED -> repository.getFollowedContacts()
        }
        _uiState.value = PeopleFinderUIState.Data(contacts)
      } catch (e: Exception) {
        _uiState.value = PeopleFinderUIState.Error(
          e.message ?: "Unknown error"
        )
      }
    }
  }
}

@Composable
fun PeopleFilterView(
  viewModel: PeopleFinderViewModel,
  onAction: (PFViewModelAction) -> Unit
) {
  val filterState by viewModel.filterState.collectAsStateWithLifecycle()

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = GravityTheme.spacing.medium1)
  ) {
    Chip(
      chipType = ChipType.ACTION,
      params = chipParams {
        selected = filterState.selectedFilterOption == FilterType.RECENT
        label = "Recent"
        chipColor = ChipColor.ON_IMAGE
        hasMenu = false
      },
      onClick = {
        onAction(
          PFViewModelAction.OnFilterSelected(FilterType.RECENT)
        )
      }
    )

    Spacer(modifier = Modifier.width(GravityTheme.spacing.small))

    Chip(
      chipType = ChipType.ACTION,
      params = chipParams {
        selected = filterState.selectedFilterOption == FilterType.FOLLOWED
        label = "Followed"
        chipColor = ChipColor.ON_IMAGE
        hasMenu = false
      },
      onClick = {
        onAction(
          PFViewModelAction.OnFilterSelected(FilterType.FOLLOWED)
        )
      }
    )
  }
}

@Composable
fun PeopleFinderScreen(
  onPersonClicked: (DirectoryPerson) -> Unit,
  navController: NavController,
  token: String?
) {
  val viewModel: PeopleFinderViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val filterState by viewModel.filterState.collectAsStateWithLifecycle()

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      CenterAlignedTopAppBar(
        title = { Text("People") },
        navigationIcon = {
          IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack,
                 contentDescription = "Back")
          }
        }
      )
    }
  ) { innerPadding ->
    Box(Modifier.fillMaxSize().padding(innerPadding)) {
      Column(Modifier.fillMaxSize()) {
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
            if (state.contacts.isEmpty()) {
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
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                items(state.contacts) { person ->
                  RecentListItem(
                    people = person,
                    navController = navController
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


@Composable
fun RecentListItem(
  people: DirectoryPerson,
  navController: NavController
) {
  Card(
    modifier = Modifier
      .padding(horizontal = 12.dp)
      .clickable {
        navController.navigate("peopleFinderDetails/${people.externalIds?.firstOrNull()?.value}")
      },
    shape = RoundedCornerShape(8.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    ListItem(
      headlineContent = {
        Text(
          text = people.names?.firstOrNull()?.givenName ?: "-",
          style = MaterialTheme.typography.titleMedium
        )
      },
      supportingContent = {
        Column {
          people.emailAddresses?.firstOrNull()?.value?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium)
          }
          Text(
            text = people.organizations?.firstOrNull()?.department ?: "--",
            style = MaterialTheme.typography.labelLarge
          )
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
            .background(
              MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
            ),
          contentDescription = null
        )
      },
      trailingContent = {
        IconButton(onClick = { /* TODO: favorite */ }) {
          Icon(Icons.Default.Star, contentDescription = "Favorite")
        }
      }
    )
  }
}


Here is the complete, integrated solution in README.md format including:
	•	ViewModel
	•	UIState + Filter Models
	•	PeopleFilterView
	•	PeopleFinderScreen
	•	Use of LargeNavigationTop for SearchBar integration

⸻



# People Finder – Chip + Search Bar Based Filtering

This implementation replaces tab-based filtering with:
- Chip filters: `Recent`, `Followed`
- Implicit filter: `Search` (via SearchBar inside `LargeNavigationTop`)
- Uses state-driven rendering with `StateFlow`, Jetpack Compose, and custom navigation components (`LargeNavigationTop`)

---

## ViewModel

```kotlin
@HiltViewModel
class PeopleFinderViewModel @Inject constructor(
  private val repository: PeopleFinderRepository
) : ViewModel() {

  private val _uiState =
    MutableStateFlow<PeopleFinderUIState>(PeopleFinderUIState.Loading)
  val uiState: StateFlow<PeopleFinderUIState> = _uiState

  private val _filterState =
    MutableStateFlow(PeopleFilterState())
  val filterState: StateFlow<PeopleFilterState> = _filterState

  private val _searchText = MutableStateFlow("")
  val searchText: StateFlow<String> = _searchText

  init {
    fetchContactsFor(FilterType.RECENT)
  }

  fun onAction(action: PFViewModelAction) {
    when (action) {
      is PFViewModelAction.OnFilterSelected -> {
        _filterState.value =
          _filterState.value.copy(selectedFilterOption = action.filterType)
        if (action.filterType != FilterType.SEARCH) {
          fetchContactsFor(action.filterType)
        }
      }
    }
  }

  fun onSearchQueryChanged(query: String) {
    _searchText.value = query
    _filterState.value = _filterState.value.copy(selectedFilterOption = FilterType.SEARCH)
    fetchContacts()
  }

  private fun fetchContacts(token: String? = "") {
    viewModelScope.launch {
      _uiState.value = PeopleFinderUIState.Loading

      val filter = _filterState.value.selectedFilterOption
      try {
        val contacts = when (filter) {
          FilterType.SEARCH -> repository.searchContacts(_searchText.value)
          FilterType.RECENT -> repository.getRecentContacts()
          FilterType.FOLLOWED -> repository.getFollowedContacts()
        }
        _uiState.value = PeopleFinderUIState.Data(contacts)
      } catch (e: Exception) {
        _uiState.value = PeopleFinderUIState.Error(e.message ?: "Unknown error")
      }
    }
  }
}



⸻

Filter and UI State Models

enum class FilterType {
  RECENT, FOLLOWED, SEARCH
}

data class PeopleFilterState(
  val selectedFilterOption: FilterType = FilterType.RECENT
)

sealed class PeopleFinderUIState {
  object Loading : PeopleFinderUIState()
  data class Data(val contacts: List<DirectoryPerson>) : PeopleFinderUIState()
  data class Error(val errorMessage: String) : PeopleFinderUIState()
}

sealed class PFViewModelAction {
  data class OnFilterSelected(val filterType: FilterType) : PFViewModelAction()
}



⸻

PeopleFilterView

@Composable
fun PeopleFilterView(
  viewModel: PeopleFinderViewModel,
  onAction: (PFViewModelAction) -> Unit
) {
  val filterState by viewModel.filterState.collectAsStateWithLifecycle()

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = GravityTheme.spacing.medium1)
  ) {
    Chip(
      chipType = ChipType.ACTION,
      params = chipParams {
        selected = filterState.selectedFilterOption == FilterType.RECENT
        label = "Recent"
        chipColor = ChipColor.ON_IMAGE
        hasMenu = false
      },
      onClick = {
        onAction(PFViewModelAction.OnFilterSelected(FilterType.RECENT))
      }
    )

    Spacer(modifier = Modifier.width(GravityTheme.spacing.small))

    Chip(
      chipType = ChipType.ACTION,
      params = chipParams {
        selected = filterState.selectedFilterOption == FilterType.FOLLOWED
        label = "Followed"
        chipColor = ChipColor.ON_IMAGE
        hasMenu = false
      },
      onClick = {
        onAction(PFViewModelAction.OnFilterSelected(FilterType.FOLLOWED))
      }
    )
  }
}



⸻

PeopleFinderScreen

@Composable
fun PeopleFinderScreen(
  navController: NavController,
  onPersonClicked: (DirectoryPerson) -> Unit,
  token: String?
) {
  val viewModel: PeopleFinderViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val filterState by viewModel.filterState.collectAsStateWithLifecycle()
  val searchText by viewModel.searchText.collectAsStateWithLifecycle()

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      LargeNavigationTop(
        title = "People",
        background = NavigationTopBackground.Color(
          color = GravityTheme.colors.background.brand
        ),
        searchBarParams = NavigationTopSearchBarParams(
          showSearchBar = true,
          searchInitialQuery = searchText,
          searchPlaceholderText = "Search for an Associate",
          onSearch = { viewModel.onSearchQueryChanged(it) },
          onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
          onSearchBarDismiss = {
            viewModel.onAction(PFViewModelAction.OnFilterSelected(FilterType.RECENT))
          }
        ),
        leadingAction = NavigationTopAction.Icon(
          icon = vectorResource(Res.drawable.ic_back),
          contentDescription = stringResource(Res.string.back)
        ),
        onLeadingActionClick = {
          navController.popBackStack()
        }
      )
    }
  ) { innerPadding ->
    Box(Modifier.fillMaxSize().padding(innerPadding)) {
      Column(Modifier.fillMaxSize()) {

        if (filterState.selectedFilterOption != FilterType.SEARCH) {
          PeopleFilterView(
            viewModel = viewModel,
            onAction = viewModel::onAction
          )
        }

        when (val state = uiState) {
          is PeopleFinderUIState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              CircularProgressIndicator()
            }
          }

          is PeopleFinderUIState.Data -> {
            if (state.contacts.isEmpty()) {
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
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                items(state.contacts) { person ->
                  RecentListItem(
                    people = person,
                    navController = navController
                  )
                }
              }
            }
          }

          is PeopleFinderUIState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              Text("Error: ${state.errorMessage}")
            }
          }
        }
      }
    }
  }
}



⸻

Reusable List Item (RecentListItem)

No changes needed:

@Composable
fun RecentListItem(
  people: DirectoryPerson,
  navController: NavController
) {
  Card(
    modifier = Modifier
      .padding(horizontal = 12.dp)
      .clickable {
        navController.navigate("peopleFinderDetails/${people.externalIds?.firstOrNull()?.value}")
      },
    shape = RoundedCornerShape(8.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    ListItem(
      headlineContent = {
        Text(
          text = people.names?.firstOrNull()?.givenName ?: "-",
          style = MaterialTheme.typography.titleMedium
        )
      },
      supportingContent = {
        Column {
          people.emailAddresses?.firstOrNull()?.value?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium)
          }
          Text(
            text = people.organizations?.firstOrNull()?.department ?: "--",
            style = MaterialTheme.typography.labelLarge
          )
        }
      },
      leadingContent = {
        AsyncImage(
          model = ImageRequest.Builder(LocalContext.current)
            .data(people.photos?.firstOrNull()?.url)
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
        IconButton(onClick = { /* TODO: Favorite */ }) {
          Icon(Icons.Default.Star, contentDescription = "Favorite")
        }
      }
    )
  }
}



⸻

Let me know if you want to extract this as a starter template or zip structure for onboarding teammates.











