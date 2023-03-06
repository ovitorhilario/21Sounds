package com.hyper.twentyonesounds.ui.main.adapter.home

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.hyper.twentyonesounds.R
import com.hyper.twentyonesounds.databinding.ItemHomeSongInNotificationBinding
import com.hyper.twentyonesounds.domain.SongUI
import com.hyper.twentyonesounds.utils.extension.loadImage

class SongInNotificationAdapter
(
    private val resources: Resources,
    private val type : Int,
    private val songsOrPodcasts: List<SongUI>,
    private val openNotification: (SongUI) -> Unit

) : RecyclerView.Adapter<SongInNotificationAdapter.SongInNotificationHolder>()
{
    private var _binding : ItemHomeSongInNotificationBinding? = null
    private val binding get() = _binding!!

    inner class SongInNotificationHolder(item: View) : ViewHolder(item) {
        var tvSongName = binding.tvSongNameInNotification
        var tvSongArtist = binding.tvSongArtistInNotification
        var tvDesc = binding.tvDescNotification
        var ivSongIcon = binding.ivSongIconInNotification
        var cvSongItem = binding.cvSongItemInNotification

        fun bind(song: SongUI) {
            tvSongName.text = song.title
            tvSongArtist.text = song.artist.name
            tvDesc.text = if (type == 0) {
                resources.getString(R.string.new_song_message)
            } else {
                resources.getString(R.string.new_episode_message)
            }
            ivSongIcon.loadImage(song.image_url)
            cvSongItem.setOnClickListener { openNotification(song) }
        }
    }

    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): SongInNotificationHolder {
        _binding = ItemHomeSongInNotificationBinding.inflate(LayoutInflater.from(group.context), group, false)
        return SongInNotificationHolder(binding.root)
    }

    override fun onBindViewHolder(holder: SongInNotificationHolder, position: Int) {
        holder.bind(songsOrPodcasts[position])
    }

    override fun getItemCount(): Int = songsOrPodcasts.size
}