package com.hyper.twentyonesounds.ui.main.adapter.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.hyper.twentyonesounds.databinding.ItemHomeAlbumInRowBinding
import com.hyper.twentyonesounds.domain.AlbumUI
import com.hyper.twentyonesounds.utils.extension.loadImage

class AlbumInRowAdapter
(
    private val albums: List<AlbumUI>,
    val actionOpenAlbum: (AlbumUI) -> Unit

) : RecyclerView.Adapter<AlbumInRowAdapter.AlbumInRowViewHolder>()
{
    private var _binding: ItemHomeAlbumInRowBinding? = null
    private val binding get() = _binding!!

    inner class AlbumInRowViewHolder(item: View) : ViewHolder(item) {
        val tvAlbumTitle = binding.tvAlbumTitle
        val tvAlbumDesc = binding.tvAlbumDesc
        val ivAlbumIcon = binding.ivAlbumIcon
        val cvAlbumItem = binding.cvAlbumItem

        fun bind(album: AlbumUI) {
            tvAlbumTitle.text = album.title
            tvAlbumDesc.text = buildString {
                append(album.title)
                append(" Playlist")
            }
            ivAlbumIcon.loadImage(album.image_url)
            cvAlbumItem.setOnClickListener{ actionOpenAlbum(album) }
        }
    }

    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): AlbumInRowViewHolder {
        _binding = ItemHomeAlbumInRowBinding.inflate(LayoutInflater.from(group.context), group, false)
        return AlbumInRowViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: AlbumInRowViewHolder, position: Int) {
        holder.bind(albums[position])
    }

    override fun getItemCount(): Int = albums.size
}


