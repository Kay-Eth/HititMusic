package com.kayethan.hititmusic.data

data class MusicFile(
    val id: Long,
    val path: String,
    val title: String,
    val artist: String?,
    val album: String?,
    val duration: Int
) {

}
