package com.hyper.twentyonesounds.ui.main.adapter.library

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.hyper.twentyonesounds.R
import com.hyper.twentyonesounds.databinding.ItemLibraryAlbumBinding
import com.hyper.twentyonesounds.domain.AlbumUI
import com.hyper.twentyonesounds.ui.main.view.library.MainLibraryFragment
import com.hyper.twentyonesounds.ui.main.model.library.AlbumList
import com.hyper.twentyonesounds.utils.extension.loadImage

class AlbumInLibraryAdapter
(
    val resources: Resources,
    val repository: List<Pair<MainLibraryFragment.AlbumType, Any>>,
    val actionOpenAlbumList: (AlbumList) -> Unit,
    val actionOpenAlbumUI: (AlbumUI) -> Unit,

    ) : Adapter<AlbumInLibraryAdapter.AlbumHolder>()
{
    abstract class AlbumHolder(item: View) : ViewHolder(item) {
        abstract fun bind(data: Any)
    }

    inner class AlbumOfSongs(binding: ItemLibraryAlbumBinding) : AlbumHolder(binding.root) {
        val tvAlbumName = binding.tvAlbumNameInLibrary
        val tvAlbumDesc = binding.tvAlbumDescInLibrary
        val ivAlbumIcon = binding.ivAlbumIconInLibrary
        val item = binding.root

        override fun bind(data: Any) {
            val album = data as AlbumUI
            val songsCount = album.songs.count()

            tvAlbumName.text = album.title
            tvAlbumDesc.text = if (songsCount <= 0) {
                resources.getString(R.string.no_songs_yet_desc)
            } else  {
                buildString {
                    append(songsCount)
                    append(" ")
                    append(
                        if(songsCount > 1)
                             resources.getString(R.string.song_plural_title)
                        else
                            resources.getString(R.string.song_singular_title)
                    )
                }
            }

            ivAlbumIcon.loadImage(album.image_url)
            item.setOnClickListener { actionOpenAlbumUI(album) }
        }

    }

     inner class AlbumOfAlbums(binding: ItemLibraryAlbumBinding) : AlbumHolder(binding.root) {
         val tvAlbumName = binding.tvAlbumNameInLibrary
         val tvAlbumDesc = binding.tvAlbumDescInLibrary
         val ivAlbumIcon = binding.ivAlbumIconInLibrary
         val item = binding.root

         override fun bind(data: Any) {
             val album = data as AlbumList
             val albumCount = album.albums.count()

             tvAlbumName.text = album.title
             tvAlbumDesc.text = if (albumCount <= 0 || album.albums.isEmpty()) {
                 resources.getString(R.string.no_album_yet_desc)
             } else  {
                 buildString {
                     append(albumCount)
                     append(" ")
                     append(
                         if(albumCount > 1)
                             resources.getString(R.string.album_plural_title)
                         else
                             resources.getString(R.string.album_singular_title)
                     )
                 }
             }

             ivAlbumIcon.loadImage(album.image_url)
             item.setOnClickListener { actionOpenAlbumList(album) }
         }
     }

    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): AlbumHolder {
        return when(viewType) {
            // Song
            0 -> AlbumOfSongs(ItemLibraryAlbumBinding.inflate(LayoutInflater.from(group.context), group, false))
            // Album
            1 -> AlbumOfAlbums(ItemLibraryAlbumBinding.inflate(LayoutInflater.from(group.context), group, false))
            else -> throw IllegalArgumentException("This AlbumType type is not valid")
        }
    }

    override fun onBindViewHolder(holder: AlbumHolder, position: Int) {
        holder.bind(repository[position].second)
    }

    override fun getItemCount(): Int = repository.size

    override fun getItemViewType(position: Int): Int {
        return when(repository[position].first) {
            MainLibraryFragment.AlbumType.SONGS -> 0
            MainLibraryFragment.AlbumType.ALBUMS -> 1
        }
    }
}