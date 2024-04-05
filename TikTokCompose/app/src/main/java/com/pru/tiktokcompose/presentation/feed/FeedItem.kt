package com.pru.tiktokcompose.presentation.feed

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView

@Composable
fun FeedItem(url: String) {
    AndroidView(factory = {
        val playerView = PlayerView(it)
        playerView.player?.setMediaItem(MediaItem.fromUri(Uri.parse(url)))
        playerView.player?.play()
        playerView.keepScreenOn = true
        playerView
    },modifier = Modifier.fillMaxSize())
}