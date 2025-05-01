## People Finder Filter Chip View

This section renders the filter chips for the `People Finder` screen, allowing users to toggle between **Recent** and **Followed** views.

### UX Behavior

- Two **mutually exclusive chips**: `Recent` and `Followed`.
- The selected chip shows a **filled background**.
- The unselected chip is **outlined**.
- Selection triggers an `onAction` to update the view model.

### Composable Snippet

```kotlin
Row(
  modifier = Modifier
    .fillMaxWidth()
    .padding(horizontal = GravityTheme.spacing.medium1)
) {
  // -- Recent Chip --
  Chip(
    chipType = ChipType.ACTION,
    params = chipParams {
      selected = filterState.selectedDateFilterOption == DateFilter.RECENT
      label = "Recent"
      chipColor = ChipColor.ON_IMAGE
      hasMenu = false
      isSelected = true
    },
    onClick = {
      onAction(
        PeopleFinderViewModel.PFViewModelAction
          .OnFilterSelected(ChipFilterType.DATE, DateFilter.RECENT)
      )
    }
  )

  Spacer(modifier = Modifier.width(GravityTheme.spacing.small))

  // -- Followed Chip --
  Chip(
    chipType = ChipType.ACTION,
    params = chipParams {
      selected = filterState.selectedDateFilterOption == DateFilter.FOLLOWED
      label = "Followed"
      chipColor = ChipColor.ON_IMAGE
      hasMenu = false
      isSelected = true
    },
    onClick = {
      onAction(
        PeopleFinderViewModel.PFViewModelAction
          .OnFilterSelected(ChipFilterType.DATE, DateFilter.FOLLOWED)
      )
    }
  )
}




```kotlin

enum class DateFilter {
  RECENT, FOLLOWED
}

sealed class PFViewModelAction {
  data class OnFilterSelected(
    val filterType: ChipFilterType,
    val selected: DateFilter
  ) : PFViewModelAction()
}

