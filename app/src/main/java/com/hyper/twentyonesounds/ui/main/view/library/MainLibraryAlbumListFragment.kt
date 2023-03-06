package com.hyper.twentyonesounds.ui.main.view.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.hyper.twentyonesounds.R
import com.hyper.twentyonesounds.databinding.FragmentMainLibraryAlbumsListBinding
import com.hyper.twentyonesounds.ui.main.view.home.AlbumFragment
import com.hyper.twentyonesounds.ui.main.adapter.library.AlbumListAdapter
import com.hyper.twentyonesounds.ui.main.model.library.AlbumList
import com.hyper.twentyonesounds.utils.extension.addToStack
import com.hyper.twentyonesounds.utils.extension.loadImage

class MainLibraryAlbumListFragment : Fragment() {

    private var _binding : FragmentMainLibraryAlbumsListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, group: ViewGroup?, saved: Bundle?): View{
        _binding = FragmentMainLibraryAlbumsListBinding.inflate(inflater, group, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    private fun setupUI() {
        val albumList = requireArguments().getSerializable("ALBUM_LIST") as AlbumList
        val albumCount = albumList.albums.count()

        binding.tvAlbumListName.text = albumList.title
        binding.tvAlbumListInfo.text = buildString {
            if (albumCount <= 0) {
                append(resources.getString(R.string.no_album_yet_desc))
            } else {
                append(albumList.albums.count())
                append(" ")
                append(
                    if(albumList.albums.count() > 1)
                        resources.getString(R.string.album_plural_title)
                    else
                        resources.getString(R.string.album_singular_title)
                )
            }
        }
        binding.cvAlbumList.loadImage(albumList.image_url)

        binding.rvMusicsAlbum.adapter = AlbumListAdapter(albumList.albums) { album ->
            val args = bundleOf("ALBUM" to album)
            requireActivity().addToStack(AlbumFragment::class.java, args)
        }

        binding.btnBackAlbumList.onClick = { requireActivity().supportFragmentManager.popBackStack() }
    }
}