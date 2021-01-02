package com.kayethan.hititmusic.player

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.kayethan.hititmusic.MainActivity
import com.kayethan.hititmusic.R
import com.kayethan.hititmusic.databinding.PlayerFragmentBinding
import com.kayethan.hititmusic.helpers.TimeHelper
import kotlinx.android.synthetic.main.player_fragment.*
import java.lang.Exception

class PlayerFragment : Fragment() {
    private lateinit var binding: PlayerFragmentBinding

    private var totalTime: Int = 0
    private var thread: Thread? = null

    companion object {
        fun newInstance() = PlayerFragment()
    }

    private lateinit var viewModel: PlayerViewModel
    var handler = @SuppressLint("HandlerLeak")

    object : Handler() {
        override fun handleMessage(msg: Message) {
            try {
                var currentPosition = msg.what

                // Update progressBar
                progressSB.progress = currentPosition
                progressSB.max = MainActivity.mediaPlayer.duration

                // Update labels
                elapsedTimeTV.text = TimeHelper.secondsToLabel(currentPosition)
                totalTimeTV.text = TimeHelper.secondsToLabel(MainActivity.mediaPlayer.duration)
            } catch(e: Exception) {
                Log.e("PLAYER", "STOP")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.player_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = PlayerFragmentBinding.bind(view)

        progressSB.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        MainActivity.mediaPlayer.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()

        thread?.interrupt()

        thread = Thread(Runnable {
            try {
                while (MainActivity.mediaPlayer != null) {
                    var msg = Message()
                    msg.what = MainActivity.mediaPlayer.currentPosition
                    handler.sendMessage(msg)

                    Thread.sleep(1000)
                }
            } catch (e: InterruptedException) {

            }
        })

        thread?.start()
    }

    override fun onPause() {
        super.onPause()

        thread?.interrupt()
        thread = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PlayerViewModel::class.java)
        // TODO: Use the ViewModel
    }
}