package com.zetta.takumiscan.model

data class History(
    val id: Int,
    val status: String,
    val photo: ByteArray,
    val mood: String,
    val dateTime: String
)