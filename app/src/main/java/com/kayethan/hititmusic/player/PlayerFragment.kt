package com.kayethan.hititmusic.player

import android.annotation.SuppressLint
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
    private var handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            try {
                var currentPosition = msg.what
                this@PlayerFragment.updateView(currentPosition, MainActivity.getService()!!.getDuration(), MainActivity.getService()!!.isPlaying())
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
                        MainActivity.getService()?.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }
        )

        previousIB.setOnClickListener { this@PlayerFragment.onPreviousButtonClicked() }
        playIB.setOnClickListener { this@PlayerFragment.onPlayPauseButtonClicked() }
        nextIB.setOnClickListener { this@PlayerFragment.onNextButtonClicked() }
    }

    override fun onResume() {
        super.onResume()

        thread?.interrupt()

        thread = Thread(Runnable {
            try {

                while (true) {
                    val service = MainActivity.getService()
                    if (service != null)
                    {
                        var msg = Message()
                        msg.what = service!!.getCurrentPosition()
                        handler.sendMessage(msg)
                        Thread.sleep(500)
                    }
                }
            } catch (e: InterruptedException) {

            } catch (e: Exception) {
                Log.e("Player", e.toString())
            }
        })
        thread?.start()

        Log.i("Player", "onResume")
    }

    override fun onPause() {
        super.onPause()

        thread?.interrupt()
        thread = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PlayerViewModel::class.java)
    }

    private fun onPreviousButtonClicked() {
        Log.i("Player", "onPreviousButtonClicked")
        if (MainActivity.isServiceBound()) {
            MainActivity.getService()?.previousSong()
        }
    }

    private fun onPlayPauseButtonClicked() {
        Log.i("Player", "onPlayPauseButtonClicked")
        if (MainActivity.isServiceBound()) {
            if (MainActivity.getService()?.isPlaying() == true) {
                MainActivity.getService()?.pause()
                setPlayPauseButtonIcon(false)
            } else if (MainActivity.getService()?.isPlaying() == false) {
                MainActivity.getService()?.play()
                setPlayPauseButtonIcon(true)
            }
        }
    }

    private fun onNextButtonClicked() {
        Log.i("Player", "onNextButtonClicked")
        if (MainActivity.isServiceBound()) {
            MainActivity.getService()?.nextSong()
        }
    }

    private fun updateView(position: Int, maxPosition: Int, isPlaying: Boolean) {
        // Update song info
        val musicFile = MainActivity.getService()!!.getCurrentSong()
        if (musicFile != null) {
            titleTV.text = musicFile.title
            artistTV.text = musicFile.artist
        }

        // Update progressBar
        progressSB.progress = position
        progressSB.max = maxPosition

        // Update labels
        elapsedTimeTV.text = TimeHelper.secondsToLabel(position)
        totalTimeTV.text = TimeHelper.secondsToLabel(maxPosition)

        setPlayPauseButtonIcon(isPlaying)
    }

    private fun setPlayPauseButtonIcon(isPlaying: Boolean) {
        if (isPlaying)
            playIB.setImageResource(R.drawable.ic_round_pause_circle_24)
        else
            playIB.setImageResource(R.drawable.ic_round_play_circle_24)
    }
}