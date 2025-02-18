package com.compose.example

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel : ViewModel() {
    var recent = MutableLiveData<LoadingStatus<List<People>>>()
    var followed = MutableLiveData<LoadingStatus<List<People>>>()

    init {
        loadRecentPeoplesList()
        loadFollowedPeoplesList()
    }

    private fun loadRecentPeoplesList() {
        viewModelScope.launch {
            runCatching {
                recent.value = LoadingStatus.Loading
                withContext(IO) { ApiClient.get().getRecentPeoples() }
            }.onSuccess {
                recent.value = LoadingStatus.Success(it)
            }.onFailure {
                recent.value = LoadingStatus.Error(it)
            }
        }
    }

    private fun loadFollowedPeoplesList() {
        viewModelScope.launch {
            runCatching {
                followed.value = LoadingStatus.Loading
                withContext(IO) { ApiClient.get().getFollowedPeoples() }
            }.onSuccess {
                followed.value = LoadingStatus.Success(it)
            }.onFailure {
                followed.value = LoadingStatus.Error(it)
            }
        }
    }
}