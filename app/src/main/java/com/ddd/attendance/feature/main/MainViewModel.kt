package com.ddd.attendance.feature.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddd.attendance.core.domain.usecase.profiles.GetProfileMeUseCase
import com.ddd.attendance.feature.login.model.ProfileMeUiState
import com.ddd.attendance.feature.main.screen.ScreenName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getProfileMeUseCase: GetProfileMeUseCase
) : ViewModel() {
    private val _startDestination = MutableStateFlow(ScreenName.NONE.name)
    val startDestination: StateFlow<String> = _startDestination.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow = _errorFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            getProfileMeUseCase()
                .map { ProfileMeUiState.Success(it) }
                .catch { _errorFlow.emit(it) }
                .collect { state ->
                    _startDestination.value = if (state.data.isStaff) ScreenName.ADMIN.name else ScreenName.MEMBER.name
                    _userName.value = state.data.name
                }
        }
    }
}