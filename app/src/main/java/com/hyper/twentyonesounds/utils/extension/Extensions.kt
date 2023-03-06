package com.hyper.twentyonesounds.utils.extension

import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.hyper.twentyonesounds.R
import com.hyper.twentyonesounds.domain.SongUI
import com.squareup.picasso.Picasso

fun ImageView.loadImage(path: String, isRounded: Boolean = true, isCircular: Boolean = false) {

    if(isRounded) {
        if(isCircular) {
            setBackgroundResource(R.drawable.shape_circle_artist_icon)
        } else {
            setBackgroundResource(R.drawable.round_outline)
        }
        clipToOutline = true
    }

    Picasso.get().load(path)
        .fit()
        .centerCrop()
        .error(R.drawable.ic_album_loading)
        .placeholder(R.drawable.ic_album_loading)
        .into(this)
}

inline fun <reified T : Fragment> FragmentActivity.addToStack(clazz: Class<T>, args: Bundle? = null) {
    this.supportFragmentManager.commit {
        setCustomAnimations(
            com.google.android.material.R.anim.abc_fade_in,
            com.google.android.material.R.anim.abc_fade_out,
            com.google.android.material.R.anim.abc_fade_in,
            com.google.android.material.R.anim.abc_fade_out
        )
        setReorderingAllowed(true)
        replace(R.id.fcv_main, clazz, args)
        addToBackStack(null)
    }
}

inline fun <reified T : Fragment> FragmentActivity.replaceStack(clazz : Class<T>, arguments: Bundle? = null) {
    this.supportFragmentManager.commit {
        setCustomAnimations(
            com.google.android.material.R.anim.abc_fade_in,
            com.google.android.material.R.anim.abc_fade_out,
            com.google.android.material.R.anim.abc_fade_in,
            com.google.android.material.R.anim.abc_fade_out
        )
        setReorderingAllowed(true)
        replace(R.id.fcv_main, clazz, arguments)
    }
}

fun Int.secondsToMinutes() : String {

    if(this == 0) return "00:00"

    val minutesNum : Int
    var secondsNum = 0

    val quotient = (this.toDouble() / 60)
    val module = this % 60
    val integer = quotient.toInt()

    minutesNum = integer

    if (quotient >= 1) {
        if (module != 0) {
            secondsNum = ((quotient - integer) * 60).toInt()
        }
    } else {
        secondsNum = this
    }

    return "${minutesNum.toStringTimeFormat()}:${secondsNum.toStringTimeFormat()}"
}

fun minutesToHours(minutes: Int) : String {
    return if (minutes >= 60) {
        val module = (minutes.toDouble() % 60)

        if ((minutes.toDouble() % 60) == (0).toDouble()) {
            "${minutes/ 60}h"
        } else {
            "${minutes / 60}h${module.toInt()}m"
        }

    } else {
        "${minutes}m"
    }
}

fun Int.toStringTimeFormat() : String {
    val value = this.toString()
    return when (value.length) {
        1 -> "0$value"
        else -> value
    }
}

fun List<Int>.toSongsUI(songs: List<SongUI>) : List<SongUI> {
    val result = mutableListOf<SongUI>()
    this.forEach { songId ->
        songs.firstOrNull { it.id == songId }?.let { result.add(it) }
    }
    return result
}

fun String.isValid() : Boolean {
    return this.isNotEmpty() && this.isNotBlank()
}
