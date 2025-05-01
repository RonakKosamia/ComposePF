// -- Chip Row --
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

