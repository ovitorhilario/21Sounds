package com.hyper.twentyonesounds.ui.main.adapter.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.hyper.twentyonesounds.databinding.ItemHomeSongInListBinding
import com.hyper.twentyonesounds.domain.AlbumUI
import com.hyper.twentyonesounds.utils.extension.loadImage

class AlbumListAdapter
(
    private val albumList: List<AlbumUI>,
    private val actionOpenAlbum: (AlbumUI) -> Unit

) : Adapter<AlbumListAdapter.AlbumHolder>()
{
    private var _binding : ItemHomeSongInListBinding? = null
    private val binding get() = _binding!!

    inner class AlbumHolder(item: View) : ViewHolder(item) {
        val tvTitle = binding.tvSongNameInList
        val tvDesc = binding.tvSongArtistInList
        val ivAlbumIcon = binding.ivSongIconInList
        val container = binding.root

        fun bind(album: AlbumUI) {
            tvTitle.text = album.title
            tvDesc.text = buildString {
                append(album.title)
                append(" Playlist")
            }
            ivAlbumIcon.loadImage(album.image_url)

            container.setOnClickListener { actionOpenAlbum(album) }
        }
    }

    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): AlbumHolder {
        _binding = ItemHomeSongInListBinding.inflate(LayoutInflater.from(group.context), group, false)
        return AlbumHolder(binding.root)
    }

    override fun onBindViewHolder(holder: AlbumHolder, position: Int) {
        holder.bind(albumList[position])
    }

    override fun getItemCount(): Int = albumList.size
}