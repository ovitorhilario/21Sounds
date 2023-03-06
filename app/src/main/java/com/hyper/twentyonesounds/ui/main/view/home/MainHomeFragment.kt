package com.hyper.twentyonesounds.ui.main.view.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hyper.twentyonesounds.R
import com.hyper.twentyonesounds.databinding.FragmentMainHomeBinding
import com.hyper.twentyonesounds.domain.AlbumUI
import com.hyper.twentyonesounds.domain.StudioUI
import com.hyper.twentyonesounds.ui.main.MainActivity
import com.hyper.twentyonesounds.ui.main.adapter.home.HomeAdapter
import com.hyper.twentyonesounds.ui.main.view.notification.NotificationFragment
import com.hyper.twentyonesounds.ui.main.model.home.HorizontalRowHome
import com.hyper.twentyonesounds.ui.main.model.home.InitialText
import com.hyper.twentyonesounds.ui.main.model.home.PageTopBar
import com.hyper.twentyonesounds.ui.main.viewmodel.MainViewModel
import com.hyper.twentyonesounds.utils.extension.addToStack
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.LocalTime

class MainHomeFragment : Fragment() {

    private var _binding: FragmentMainHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private var homePageData: List<Pair<Int, Any>>? = null

    override fun onCreateView(inflater: LayoutInflater, group: ViewGroup?, saved: Bundle?): View {
        _binding = FragmentMainHomeBinding.inflate(inflater, group, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.pbMainHome.visibility = View.VISIBLE

        viewModel.studioData.observe(viewLifecycleOwner) { studio ->
            if (studio != null) {
                binding.pbMainHome.visibility = View.GONE
                setupHomePage(studio.albums)
                setupListeners(studio)
            }
        }
    }

    private fun setupListeners(studio: StudioUI) {
        binding.rvMainHome.adapter = HomeAdapter (
            repository = homePageData,
            actionOpenAlbum = { album ->
                requireActivity()
                    .addToStack(AlbumFragment::class.java, bundleOf("ALBUM" to album))
            },
            actionNotification = {
                requireActivity()
                    .addToStack(NotificationFragment::class.java, bundleOf("STUDIO" to studio))
            },
            actionOpenProfile = {
                requireActivity()
                    .addToStack(ProfileFragment::class.java)
            }
        )
    }

    private fun setupHomePage(albums: List<AlbumUI>) {

        val userName = runBlocking { (requireActivity() as MainActivity).dataStore.data.first()[MainActivity.PREF_PROFILE_NAME] }

        homePageData = listOf (
            // TOP BAR
            2 to PageTopBar(
                getCurrentDayPeriod(),
                "https://celebrity.land/pt/wp-content/uploads/2021/10/Experimente-novos-mundos-em-Music-of-the-Spheres-do-Coldplay.jpeg",
                "https://i.scdn.co/image/ab676161000051743e20e1d8e43af79926b8692d"
            ),
            // HELLO $personName
            1 to InitialText(userName ?: "User"),

            // HORIZONTAL ROW
            0 to HorizontalRowHome(title = resources.getString(R.string.album_row_pop_list), albums = albums.subList(0, 4)),
            0 to HorizontalRowHome(title = resources.getString(R.string.album_row_rock_list), albums = albums.subList(5, 9)),
        )
    }

    fun getCurrentDayPeriod() : String {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val date = LocalTime.now()

            when {
                (date.hour >= 6) && (date.hour < 12) -> resources.getString(R.string.good_morning_message)
                (date.hour >= 12) && (date.hour < 18) -> resources.getString(R.string.good_afternoon_message)
                ((date.hour >= 18) && (date.hour < 24)) || ((date.hour >= 0) && (date.hour < 6)) -> resources.getString(R.string.good_night_message)
                else -> resources.getString(R.string.menu_home)
            }
        } else {
            resources.getString(R.string.menu_home)
        }
    }
}