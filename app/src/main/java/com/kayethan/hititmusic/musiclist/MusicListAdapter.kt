package com.kayethan.hititmusic.musiclist

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kayethan.hititmusic.R
import com.kayethan.hititmusic.data.MusicFile
import com.kayethan.hititmusic.helpers.TimeHelper

class MusicListAdapter(var musicFiles: ArrayList<MusicFile>, private val listener: OnItemClickListener) : RecyclerView.Adapter<MusicListAdapter.MusicViewHolder>() {
    companion object {
        lateinit var context: Context
    }

    interface ClickListener {
        fun onItemClick(position: Int, view: View)
    }

    inner class MusicViewHolder(musicView: View) : RecyclerView.ViewHolder(musicView), View.OnClickListener {
        val albumImageIV: ImageView = musicView.findViewById(R.id.holderAlbumImageIV)
        val titleTextView: TextView = musicView.findViewById(R.id.holderTitleTV)
        val artistTextView: TextView = musicView.findViewById(R.id.holderArtistTV)
        val albumTextView: TextView = musicView.findViewById(R.id.holderAlbumTV)
        val durationTextView: TextView = musicView.findViewById(R.id.holderDurationTV)

        lateinit var musicFile: MusicFile

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(adapterPosition, musicFile)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val musicView = LayoutInflater.from(parent.context).inflate(R.layout.music_file_holder, parent, false)
        return MusicViewHolder(musicView)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val musicFile = musicFiles[position]
        holder.musicFile = musicFile

        if (musicFile.title.isNotEmpty()) {
            holder.titleTextView.text = musicFile.title
        }
        if (musicFile.artist.isNotEmpty()) {
            holder.artistTextView.text = musicFile.artist
        }
        if (musicFile.album.isNotEmpty()) {
            holder.albumTextView.text = musicFile.album
        }

        holder.durationTextView.text = TimeHelper.secondsToLabel(musicFile.duration)
    }

    override fun getItemCount(): Int {
        return musicFiles.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, musicFile: MusicFile)
    }
}