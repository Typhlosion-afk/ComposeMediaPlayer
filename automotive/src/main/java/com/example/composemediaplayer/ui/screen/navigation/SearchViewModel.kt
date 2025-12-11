package com.example.composemediaplayer.ui.screen.navigation

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.composemediaplayer.data.SharedPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    @ApplicationContext private val context: Context
): ViewModel() {

    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history: StateFlow<List<String>> = _history


    private val _searchText = MutableStateFlow<String>("")
    val searchText = _searchText.asStateFlow()

    private val _showHistory = MutableStateFlow<Boolean>(false)
    val showHistory = _showHistory.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        _history.value = sharedPreferencesHelper.getHistory()
    }

    fun onSearchChange(text: String) {
        _searchText.value = text
    }

    fun onSearchSubmit() {
        if (_searchText.value.isNotBlank()) {
            sharedPreferencesHelper.saveQuery(searchText.value)
            loadHistory()
        }
        _showHistory.value = false
    }

    fun onSearchBarClick() {
        _showHistory.value = true
    }
}