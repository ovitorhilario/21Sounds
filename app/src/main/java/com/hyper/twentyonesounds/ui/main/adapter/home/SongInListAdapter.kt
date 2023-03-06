package com.hyper.twentyonesounds.ui.main.adapter.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import com.hyper.twentyonesounds.domain.SongUI
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.hyper.twentyonesounds.databinding.ItemHomeSongInListBinding
import com.hyper.twentyonesounds.utils.extension.loadImage

class SongInListAdapter
(
    private val actionOpenSongPage: (SongUI) -> Unit

) : ListAdapter<SongUI, SongInListAdapter.SongViewHolder>(SongDiffUtilCallBack)
{
    private var _binding : ItemHomeSongInListBinding? = null
    private val binding get() = _binding!!

    inner class SongViewHolder(item: View) : ViewHolder(item) {
        val tvTitle = binding.tvSongNameInList
        val tvArtist = binding.tvSongArtistInList
        val ivSong = binding.ivSongIconInList
        val cvContainerSong = binding.cvContainerSong

        fun bind(song: SongUI) {
            tvTitle.text = song.title
            tvArtist.text = song.artist.name
            ivSong.loadImage(song.image_url)
            cvContainerSong.setOnClickListener { actionOpenSongPage(song) }
        }
    }

    object SongDiffUtilCallBack : ItemCallback<SongUI>() {

        override fun areItemsTheSame(oldItem: SongUI, newItem: SongUI): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SongUI, newItem: SongUI): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): SongViewHolder {
        _binding = ItemHomeSongInListBinding.inflate(LayoutInflater.from(group.context), group, false)
        return SongViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}