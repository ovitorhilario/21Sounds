package com.hyper.twentyonesounds.ui.main.adapter.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.hyper.twentyonesounds.R
import com.hyper.twentyonesounds.databinding.ItemHomeAlbumRowBinding
import com.hyper.twentyonesounds.databinding.ItemHomeInitialtextBinding
import com.hyper.twentyonesounds.databinding.ItemHomeTopBarBinding
import com.hyper.twentyonesounds.domain.AlbumUI
import com.hyper.twentyonesounds.ui.main.MainActivity
import com.hyper.twentyonesounds.ui.main.model.home.HorizontalRowHome
import com.hyper.twentyonesounds.ui.main.model.home.InitialText
import com.hyper.twentyonesounds.ui.main.model.home.PageTopBar

class HomeAdapter
(
    private val repository: List<Pair<Int, Any>>?,
    private val actionOpenAlbum: (AlbumUI) -> Unit,
    private val actionNotification: () -> Unit,
    private val actionOpenProfile: () -> Unit

) : RecyclerView.Adapter<HomeAdapter.Holder>()
{
    private lateinit var thisContext : Context

    abstract class Holder(item: View) : ViewHolder(item) {
        abstract fun bind(obj : Pair<Int, Any>)
    }

    inner class AlbumRowHolder(binding: ItemHomeAlbumRowBinding) : Holder(binding.root) {
        val rvNestedItem = binding.rvNestedItemHome
        val tvNestedAlbums = binding.tvNestedRowAlbums

        override fun bind(obj: Pair<Int, Any>) {
            val holder = obj.second as HorizontalRowHome

            holder.run {
                rvNestedItem.adapter = AlbumInRowAdapter(albums, actionOpenAlbum)
                tvNestedAlbums.text = title
            }
        }
    }

    inner class InitialTextHolder(binding: ItemHomeInitialtextBinding) : Holder(binding.root) {
        val tvPersonName = binding.tvPersonName

        override fun bind(obj: Pair<Int, Any>) {
            val holder = obj.second as InitialText

            holder.run {
                tvPersonName.text = personName
            }
        }
    }

    inner class PageTopBarHolder(binding: ItemHomeTopBarBinding) : Holder(binding.root) {
        val tvPageName = binding.tvPageName
        val ivActionMain = binding.ivActionMain
        val ivProfile = binding.ivProfile

        override fun bind(obj: Pair<Int, Any>) {
            val holder = obj.second as PageTopBar

            holder.run {
                tvPageName.text = title
                ivActionMain.setIconFun(R.drawable.ic_notifications)
            }

            ivActionMain.onClick = { actionNotification() }
            ivProfile.setOnClickListener { actionOpenProfile() }
        }
    }

    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): Holder {

        thisContext = group.context

        return when (viewType) {
            0 -> {
                val binding = ItemHomeAlbumRowBinding.inflate(LayoutInflater.from(group.context), group, false)
                AlbumRowHolder(binding)
            }
            1 ->  {
                val binding = ItemHomeInitialtextBinding.inflate(LayoutInflater.from(group.context), group, false)
                InitialTextHolder(binding)
            }
            2 -> {
                val binding = ItemHomeTopBarBinding.inflate(LayoutInflater.from(group.context), group, false)
                PageTopBarHolder(binding)
            }
            else -> throw IllegalArgumentException("Illegal view holder")
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        repository?.let { holder.bind(it[position]) }
    }

    override fun getItemViewType(position: Int): Int = repository?.get(position)?.first ?: 0

    override fun getItemCount(): Int = repository?.size ?: 0
}