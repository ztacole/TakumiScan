package com.zetta.takumiscan.model

data class GeofenceData(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val transitionTypes: Int
)
