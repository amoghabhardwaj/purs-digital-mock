package com.example.pursdigital

data class LocationResponse(
    val location_name: String,
    val hours: List<Hours>
)

data class Hours(
    val day_of_week: String,
    val start_local_time: String,
    val end_local_time: String
)
