package com.hyper.twentyonesounds.utils.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.hyper.twentyonesounds.R
import com.hyper.twentyonesounds.databinding.ItemHomeTopBarBinding

class TopBarComponent
    (context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs)
{
    private val binding = ItemHomeTopBarBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    var setIcon: (Int) -> Unit = { resId -> setIconAction(resId) }
    var setOnClickAction: () -> Unit = {}
    var setOnClickProfile: () -> Unit = {}

    init {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.TopBarComponent, 0, 0).apply {

            try {
                setIconTopBar(getResourceId(R.styleable.TopBarComponent_iconTopBar, R.drawable.ic_notifications))
                setIconAction(getResourceId(R.styleable.TopBarComponent_iconAction, R.drawable.ic_album_loading))
                setTitle(getString(R.styleable.TopBarComponent_pageName))
                setupListeners()
            } finally {
                recycle()
            }
        }
    }

    private fun setupListeners() {
        binding.ivActionMain.onClick = { setOnClickAction() }
        binding.ivProfile.setOnClickListener { setOnClickProfile() }
    }

    private fun setIconAction(resourceId: Int) {
        binding.ivActionMain.setIconFun(resourceId)
        invalidate()
        requestLayout()
    }

    private fun setTitle(title: String?) {
        binding.tvPageName.text = title ?: ""
        invalidate()
        requestLayout()
    }

    private fun setIconTopBar(resourceId: Int) {
        binding.ivProfile.setImageResource(resourceId)
        invalidate()
        requestLayout()
    }
}