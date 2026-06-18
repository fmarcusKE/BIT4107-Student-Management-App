package com.example.myapplication

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel

data class Attendee(val id: String, val name: String, val regNumber: String)

class AttendanceViewModel : ViewModel() {
    val students = mutableStateListOf<Attendee>(
        Attendee("1", "John Doe", "BIT/001"),
        Attendee("2", "Jane Smith", "BIT/002"),
        Attendee("3", "Alex Johnson", "BIT/003")
    )
    
    // Maps student ID to their attendance status (True = Present, False = Absent)
    val attendanceRecords = mutableStateMapOf<String, Boolean>()

    fun registerStudent(name: String, regNumber: String) {
        students.add(Attendee(System.currentTimeMillis().toString(), name, regNumber))
    }

    fun markAttendance(studentId: String, isPresent: Boolean) {
        attendanceRecords[studentId] = isPresent
    }

    fun getAttendanceReport(): String {
        val total = students.size
        val presentCount = attendanceRecords.values.count { it }
        val absentCount = total - presentCount
        val percentage = if (total > 0) (presentCount.toFloat() / total * 100).toInt() else 0
        
        return "Total Students: $total\nPresent: $presentCount\nAbsent: $absentCount\nAttendance Rate: $percentage%"
    }
}
