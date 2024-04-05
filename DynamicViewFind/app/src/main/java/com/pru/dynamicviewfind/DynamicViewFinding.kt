package com.pru.dynamicviewfind

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding

object DynamicViewFinding {
    private const val DATA_BINDING = ".databinding."
    private const val IMPL = "Impl"

    private fun View.viewNameAsString(): String {
        return this.toString()
            .substring(this.toString().indexOf("app:id/") + 7, this.toString().length - 1)
    }

    private fun ViewDataBinding.findView(vararg vs: String) {
        vs.forEach { name ->
            val view: View? = findView(name)
            view?.isVisible = false
        }
    }

    private fun ViewDataBinding.findView(name: String): View? {
        val id: Int =
            this.root.context.resources.getIdentifier(name, "id", this.root.context.packageName)
        return this.root.findViewById(id)
    }

    private fun getControllers(): List<Item> {
        val list = mutableListOf<Item>()
        list.add(Item(screenName = "ActivityMainBinding", controllerNames = listOf("add")))
        list.add(
            Item(
                screenName = "ActivitySecondBinding",
                controllerNames = listOf( "plus","alpha")
            )
        )
        return list
    }

    private fun ViewDataBinding.findBindingName(): String {
        val db = this.toString().indexOf(DATA_BINDING)
        val impl = this.toString().indexOf(IMPL)
        return this.toString().substring(db + DATA_BINDING.length, impl)
    }

    fun ViewDataBinding.hideControllers() {
        val apiData = getControllers()
        for (item in apiData) {
            if (item.screenName == this.findBindingName()) {
                item.controllerNames.forEach { controller ->
                    val view: View? = this.findView(controller)
                    view?.restrictVisible()
                }
                break
            }
        }
    }
}

fun View?.restrictVisible() {
    this?.isVisible = false
    this?.tag = ViewRestriction.VIEW_RESTRICT
}

fun View?.checkNShow() {
    if (this?.tag != ViewRestriction.VIEW_RESTRICT) {
        this?.isVisible = true
    }
}

enum class ViewRestriction {
    VIEW_NONE,
    VIEW_RESTRICT
}

data class Item(var screenName: String, var controllerNames: List<String>)