package com.ddd.attendance.core.network

import com.ddd.attendance.core.model.attendance.Attendance
import com.ddd.attendance.core.model.attendance.AttendanceCount
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    fun attendanceCount(): Flow<AttendanceCount>
    fun attendanceList(): Flow<List<Attendance>>
}