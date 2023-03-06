package com.hyper.twentyonesounds.utils.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.material.card.MaterialCardView
import com.hyper.twentyonesounds.R
import com.hyper.twentyonesounds.databinding.ViewLoginButtonBinding

class LoginButtonView
    (context: Context, attrs: AttributeSet) : MaterialCardView(context, attrs)
{

    private val binding = ViewLoginButtonBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    init {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.LoginComponent, 0, 0).apply {

            try {
                setIcon(getResourceId(R.styleable.LoginComponent_icon, 0))
                setText(getString(R.styleable.LoginComponent_text))
                setLayout(
                    getColor(R.styleable.LoginComponent_CardBackgroundColor, resources.getColor(R.color.white)),
                    getColor(R.styleable.LoginComponent_textColor, resources.getColor(R.color.gray)),
                )
            } finally {
                recycle()
            }
        }
    }

    private fun setLayout(cardColor: Int, textColor: Int) {
        binding.apply {
            loginComponentText.setTextColor(textColor)
            cvLogin.setBackgroundColor(cardColor)
            cvLogin.setBackgroundColor(cardColor)
            loginComponentText.setBackgroundColor(cardColor)
            llLogin.setBackgroundColor(cardColor)
            loginComponentIcon.setBackgroundColor(cardColor)
        }
        invalidate()
        requestLayout()
    }

    private fun setIcon(resourceId: Int) {
        binding.loginComponentIcon.setImageResource(resourceId)
        invalidate()
        requestLayout()
    }

    private fun setText(text: String?) {
        binding.loginComponentText.text = text ?: "text example"
        invalidate()
        requestLayout()
    }
}