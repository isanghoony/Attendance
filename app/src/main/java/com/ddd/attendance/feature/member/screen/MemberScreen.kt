package com.ddd.attendance.feature.member.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ddd.attendance.R
import com.ddd.attendance.core.designsystem.AttendanceStatusRow
import com.ddd.attendance.core.designsystem.DDDMemberSituation
import com.ddd.attendance.core.designsystem.DDDText
import com.ddd.attendance.core.model.Schedule
import com.ddd.attendance.core.model.attendance.Attendance
import com.ddd.attendance.core.ui.theme.DDD_BLACK
import com.ddd.attendance.core.ui.theme.DDD_NEUTRAL_BLUE_20
import com.ddd.attendance.core.ui.theme.DDD_NEUTRAL_GRAY_20
import com.ddd.attendance.core.ui.theme.DDD_NEUTRAL_GRAY_90
import com.ddd.attendance.core.ui.theme.DDD_WHITE
import com.ddd.attendance.feature.main.model.attendance.AttendanceCountUiState
import com.ddd.attendance.feature.main.model.attendance.AttendanceListUiState
import com.ddd.attendance.feature.main.screen.ScreenName
import com.ddd.attendance.feature.member.MemberViewModel

@Composable
fun MemberScreen(
    userName: String,
    navController: NavController,
    viewModel: MemberViewModel = hiltViewModel()
) {
    val attendanceCountUiState by viewModel.attendanceCountUiState.collectAsStateWithLifecycle()
    val attendanceListUiState by viewModel.attendanceListUiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Content(
            userName = userName,
            attendanceCountUiState = attendanceCountUiState,
            attendanceListUiState = attendanceListUiState,
            onPressQrcode = { navController.navigate(ScreenName.QR_IMAGE.name) },
            onPressMyInfo = { navController.navigate(ScreenName.MY_PAGE.name) },
            onBackClicked = {}
        )
    }
}

@Composable
private fun Content(
    userName: String,
    attendanceCountUiState: AttendanceCountUiState,
    attendanceListUiState: AttendanceListUiState,
    onPressMyInfo: () -> Unit,
    onPressQrcode: () -> Unit,
    onBackClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DDD_BLACK)

    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 88.dp, start = 24.dp, end = 24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))

                Column {
                    DDDText(
                        text = stringResource(R.string.member_attendance_status, userName),
                        color = DDD_WHITE,
                        textStyle = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(16.dp))

                    DDDText(
                        text = stringResource(R.string.member_activity_period, "2025.03.12 ~ 2025.08.30"),
                        color = DDD_NEUTRAL_GRAY_20,
                        textStyle = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(8.dp))

                    if (attendanceCountUiState is AttendanceCountUiState.Success) {
                        attendanceCountUiState.data.apply {
                            DDDMemberSituation(
                                attendanceCount = presentCount,
                                tardyCount = lateCount,
                                absentCount = absentCount
                            )
                        }
                    } else DDDMemberSituation()

                    Spacer(Modifier.height(56.dp))

                    DDDText(
                        text = stringResource(R.string.member_th_schedule, "12"),
                        color = DDD_WHITE,
                        textStyle = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(16.dp))
                }
            }

            if (attendanceListUiState is AttendanceListUiState.Success) {
                val attendances = attendanceListUiState.data

                items(attendances) { attendance ->
                    AttendanceItem(attendance)
                    Spacer(Modifier.height(12.dp))
                }
            }
        }

        AttendanceStatusRow(
            modifier = Modifier,
            onPressQrcode = onPressQrcode,
            onPressMyInfo = onPressMyInfo
        )
    }
}

@Composable
private fun AttendanceItem(attendance: Attendance) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DDD_NEUTRAL_GRAY_90)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AttendanceDateBox(attendance)
            Spacer(Modifier.width(12.dp))
            AttendanceDetails(attendance)
        }
    }
}

@Composable
private fun AttendanceDateBox(attendance: Attendance) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(DDD_NEUTRAL_BLUE_20),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DDDText(
                text = "3",
                color = DDD_BLACK,
                textStyle = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(4.dp))
            DDDText(
                text = "22",
                color = DDD_BLACK,
                textStyle = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
private fun AttendanceDetails(attendance: Attendance) {
    Column(verticalArrangement = Arrangement.Center) {
        DDDText(
            text = attendance.note,
            color = DDD_WHITE,
            textStyle = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        DDDText(
            text = attendance.note,
            color = DDD_NEUTRAL_GRAY_20,
            textStyle = MaterialTheme.typography.bodySmall
        )
    }
}

private fun getSchedules(): List<Schedule> = listOf(
    Schedule("6월", "11", "오리엔테이션", "커리큘럼에 대한 설명 문구 작성"),
    Schedule("6월", "22", "부스팅 데이 1", "커리큘럼에 대한 설명 문구 작성"),
    Schedule("7월", "06", "St. Patrick's Day", "Irish cultural celebration"),
    Schedule("6월", "25", "April Fools' Day", "Day for jokes and pranks"),
    Schedule("9월", "21", "부스팅 데이 2", "설명 문구 작성"),
    Schedule("10월", "11", "직군 세션", "놀자 놀자")
)