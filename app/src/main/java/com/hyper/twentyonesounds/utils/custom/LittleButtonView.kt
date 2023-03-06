package com.hyper.twentyonesounds.utils.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.material.card.MaterialCardView
import com.hyper.twentyonesounds.R
import com.hyper.twentyonesounds.databinding.ViewLittleButtonBinding

class LittleButtonView
    (context: Context, attrs: AttributeSet) : MaterialCardView(context, attrs) {

    private val binding = ViewLittleButtonBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    var onClick : () -> Unit = {}
    var setIconFun: (Int) -> Unit = { resId -> setIcon(resId) }

    init {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.LittleButtonView, 0, 0).apply {
            try {
                setIcon(getResourceId(R.styleable.LittleButtonView_iconLittleButton,  R.drawable.ic_album_loading))
                setIconColor(getColor(R.styleable.LittleButtonView_backgroundClColor, resources.getColor(R.color.transparent)))
                setListeners()
            } finally {
                recycle()
            }
        }
    }

    private fun setIconColor(color: Int) {
        binding.clLittleButton.setBackgroundColor(color)
        invalidate()
        requestLayout()
    }

    private fun setListeners() {
        binding.cvLittleButton.setOnClickListener {
            onClick()
        }
    }

    private fun setIcon(resourceId: Int) {
        binding.ivLittleButton.setImageResource(resourceId)
        invalidate()
        requestLayout()
    }
}
