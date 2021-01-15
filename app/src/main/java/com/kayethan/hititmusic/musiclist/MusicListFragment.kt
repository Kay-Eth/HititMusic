package com.kayethan.hititmusic.musiclist

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kayethan.hititmusic.MainActivity
import com.kayethan.hititmusic.R
import com.kayethan.hititmusic.data.MusicFile
import com.kayethan.hititmusic.databinding.MusicListFragmentBinding
import com.kayethan.hititmusic.player.PlayerFragment
import kotlinx.android.synthetic.main.music_list_fragment.*

class MusicListFragment : Fragment(), MusicListAdapter.OnItemClickListener {
    private lateinit var binding: MusicListFragmentBinding

    companion object {
        fun newInstance() = MusicListFragment()
    }

    private lateinit var viewModel: MusicListViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.music_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MusicListViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MusicListFragmentBinding.bind(view)

        viewManager = LinearLayoutManager(context)
        MusicListAdapter.context = requireContext()

        viewAdapter = MusicListAdapter(MainActivity.getService()!!.getAllMusic(), this)

        recyclerView = musicRV.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onItemClick(position: Int, musicFile: MusicFile) {
        MainActivity.getService()?.playMusic(position)
        MainActivity.changeToPlayer()
    }
}