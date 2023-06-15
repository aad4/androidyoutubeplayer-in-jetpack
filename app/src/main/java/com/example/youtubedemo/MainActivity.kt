package com.example.youtubedemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.youtubedemo.ui.theme.YouTubeDemoTheme
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YouTubeDemoTheme {
                // A surface container using the 'background' color from the theme
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .scrollable(state = rememberScrollState(), Orientation.Vertical),
                    verticalArrangement = Arrangement.spacedBy(32.dp, CenterVertically),
                ) {
                    listOf(
                        "ScMzIvxBSi4",
                        "ALwt-trnyME"
                    ).forEach { videoId ->
                        YouTubeScreen(videoId)
                    }
                }
            }
        }
    }
}

@Composable
fun YouTubeScreen(
    videoId: String,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var youtubePlayerView: YouTubePlayerView? by remember { mutableStateOf(null) }
    var player: YouTubePlayer? by remember { mutableStateOf(null) }
    var fullscreenViewContainer: FrameLayout? by remember { mutableStateOf(null) }
    AndroidView(
        factory = { context ->
            val fsView = LayoutInflater.from(context).inflate(R.layout.fullscreen, null, false)
            fullscreenViewContainer = fsView.findViewById(R.id.full_screen_view_container)

            val view = YouTubePlayerView(context).apply {
                val self = this
                enableAutomaticInitialization = false
                addFullscreenListener(object : FullscreenListener {
                    override fun onEnterFullscreen(
                        fullscreenView: View,
                        exitFullscreen: () -> Unit
                    ) {
                        self.visibility = View.GONE
                        fullscreenViewContainer?.let {
                            it.visibility = View.VISIBLE
                            it.addView(fullscreenView)
                        }
                    }

                    override fun onExitFullscreen() {
                        self.visibility = View.VISIBLE
                        fullscreenViewContainer?.let {
                            visibility = View.GONE
                            removeAllViews()
                        }
                    }
                })

                initialize(
                    youTubePlayerListener = object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            player = youTubePlayer
                            super.onReady(youTubePlayer)
                            youTubePlayer.cueVideo(videoId, 0f)
                        }
                    },
                    playerOptions = IFramePlayerOptions
                        .Builder()
                        .controls(1)
                        .autoplay(0)
                        .fullscreen(1)
                        .build()
                )
                lifecycleOwner.lifecycle.addObserver(this)
            }
            youtubePlayerView = view
            view
        },
        modifier = modifier,
        update = {},
    )
    DisposableEffect(Unit) {
        onDispose {
            player?.pause()
            youtubePlayerView?.let {
                it.release()
                lifecycleOwner.lifecycle.removeObserver(it)
            }
        }
    }
}
