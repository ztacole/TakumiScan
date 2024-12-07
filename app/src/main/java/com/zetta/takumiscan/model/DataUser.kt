package com.zetta.takumiscan.model

data class DataUser(
    val nisn: String,
    val nama: String,
    val kelas: String,
    val jurusan: String,
    val photo: ByteArray,
    val password: String
)
