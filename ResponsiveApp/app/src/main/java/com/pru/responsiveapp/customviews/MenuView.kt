package com.pru.responsiveapp.customviews

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.pru.responsiveapp.data.models.MyMenuItem
import com.pru.responsiveapp.databinding.MenuViewBinding

class MenuView(context: Context, myMenuItem: MyMenuItem, callBack: (String) -> Unit) :
    LinearLayout(context) {

    init {
        val menuViewBinding = MenuViewBinding.inflate(LayoutInflater.from(context), this, true)
        myMenuItem.imgResource?.let {

        }
        if (myMenuItem.imgResource != null) {
            menuViewBinding.imgMenu.isVisible = true
            menuViewBinding.imgMenu.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    myMenuItem.imgResource!!
                )
            )
            menuViewBinding.imgMenu.contentDescription = context.getString(myMenuItem.title)
        } else {
            menuViewBinding.titleMenu.isVisible = true
            menuViewBinding.titleMenu.text = resources.getString(myMenuItem.title)
        }
        menuViewBinding.root.setOnClickListener {
            callBack.invoke(context.getString(myMenuItem.title))
        }
    }
}