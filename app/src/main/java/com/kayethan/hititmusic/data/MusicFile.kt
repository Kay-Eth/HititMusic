package com.kayethan.hititmusic.data

data class MusicFile(
    val path: String,
    val title: String,
    val artist: String?,
    val album: String?,
    val duration: Int
) {

}
