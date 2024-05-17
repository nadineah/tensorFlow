package org.tensorflow.lite.examples.poseestimation

import android.content.Context
import android.graphics.Outline
import android.graphics.Rect
import android.net.Uri
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView;

//this class handles the exo player video
class PlayerViewManager(private val context: Context, private val playerView: PlayerView) {
    fun setupPlayerView() {
        playerView.alpha = 0.7f
        playerView.useController = false

        val player = ExoPlayer.Builder(context).build()
        playerView.player = player

        val videoUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.splits_test)
        val mediaItem = MediaItem.fromUri(videoUri)
        player.setMediaItem(mediaItem)
        player.prepare()

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_READY) {
                    setOutlineProvider(playerView, player)
                }
            }
        })

        playerView.clipToOutline = true
        player.play()
    }

    private fun setOutlineProvider(playerView: PlayerView, player: ExoPlayer) {
        playerView.setOutlineProvider(object : ViewOutlineProvider() {
            @OptIn(UnstableApi::class) override fun getOutline(view: View, outline: Outline) {
                val videoAspectRatio = player.videoFormat?.let {
                    (it.width.toFloat() / it.height)
                } ?: 1f

                val viewAspectRatio = view.width.toFloat() / view.height

                val scaleFactor = if (videoAspectRatio > viewAspectRatio) {
                    view.width.toFloat() / player.videoFormat!!.width
                } else {
                    view.height.toFloat() / player.videoFormat!!.height
                }

                val scaledVideoHeight = player.videoFormat!!.height * scaleFactor
                val centerY = (view.height / 2).toInt()
                val centerX = (view.width / 2).toInt()
                val radius = (scaledVideoHeight / 2).toInt()

                val rect = Rect(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
                outline.setOval(rect)
            }
        })
    }

    fun hidePlayerView() {
        playerView.visibility = View.GONE
    }
}