package com.ddd.attendance.feature.member

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddd.attendance.core.domain.usecase.attendance.AttendanceCountUseCase
import com.ddd.attendance.core.domain.usecase.attendance.AttendanceListUseCase
import com.ddd.attendance.feature.main.model.attendance.AttendanceCountUiState
import com.ddd.attendance.feature.main.model.attendance.AttendanceListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    attendanceCountUseCase: AttendanceCountUseCase,
    attendanceListUseCase: AttendanceListUseCase
) : ViewModel() {
    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow = _errorFlow.asSharedFlow()

    val attendanceListUiState: StateFlow<AttendanceListUiState> =
        attendanceListUseCase()
            .map { AttendanceListUiState.Success(it) }
            .catch { _errorFlow.emit(it) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000L),
                AttendanceListUiState.Loading
            )

    val attendanceCountUiState: StateFlow<AttendanceCountUiState> =
        attendanceCountUseCase()
            .map { AttendanceCountUiState.Success(it) }
            .catch { _errorFlow.emit(it) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000L),
                AttendanceCountUiState.Loading
            )
}