package dk.zlepper.digiapp.ui.preview


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dk.zlepper.digiapp.R
import kotlinx.android.synthetic.main.fragment_video_asset_preview.*

/**
 * A simple [Fragment] subclass.
 *
 */
class VideoAssetPreviewFragment(val videoSourceUrl: String) : Fragment() {

    private lateinit var player: ExoPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_asset_preview, container, false)
    }

    override fun onStart() {
        super.onStart()

        player = ExoPlayerFactory.newSimpleInstance(context)

        playerView.player = player

        val videoSource = getVideoSource()

        player.prepare(videoSource)
    }

    override fun onStop() {
        super.onStop()

        player.release()
    }

    private fun getVideoSource(): MediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "Digiapp"))

        val uri = Uri.parse(videoSourceUrl)
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }

}
