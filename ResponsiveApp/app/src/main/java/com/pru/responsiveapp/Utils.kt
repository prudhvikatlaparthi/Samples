package com.pru.responsiveapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.pru.responsiveapp.data.models.MyMenuItem

fun FragmentManager.replaceFragment(
    isAnimationRequired: Boolean = false,
    fragment: Fragment,
    containerID: Int
) {
    this.beginTransaction().apply {
        if (isAnimationRequired) {
            setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            addToBackStack(fragment.tag)
        }
        replace(containerID, fragment)
        commit()
    }
}

val standardMenuList = listOf(
    MyMenuItem(title = R.string.add, imgResource = R.drawable.ic_add),
    MyMenuItem(title = R.string.search, imgResource = R.drawable.ic_search),
    MyMenuItem(title = R.string.sort, imgResource = R.drawable.vector_cus_circle)
)

val searchMenuList = listOf(
    MyMenuItem(title = R.string.sort, imgResource = null)
)

val detailMenuList = listOf(
    MyMenuItem(title = R.string.edit, imgResource = null)
)

val createMenuList = listOf(
    MyMenuItem(title = R.string.add, imgResource = R.drawable.ic_done),
)

fun FragmentManager.popFragment(){
    if (this.backStackEntryCount >0){
        this.popBackStack()
    }
}