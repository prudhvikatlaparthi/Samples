package cloud.mariapps.chatapp.ui.users

import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cloud.mariapps.chatapp.base.BaseAdapter
import cloud.mariapps.chatapp.databinding.UserSelectLayoutBinding
import cloud.mariapps.chatapp.model.internal.User

class UsersAdapter(private var isViewMode: Boolean = false) :
    BaseAdapter<User, UserSelectLayoutBinding>(
        UserSelectLayoutBinding::inflate, usersDifferCallback
    ) {
    companion object {
        val usersDifferCallback = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        }
    }

    fun updateViewMode(isViewMode: Boolean) {
        this.isViewMode = isViewMode
        submitList(this.currentList)
    }

    override fun bindData(binding: UserSelectLayoutBinding, item: User) {
        binding.cbUser.isChecked = item.isSelected
        binding.txUser.text = item.userName
        binding.cbUser.isVisible = !isViewMode
    }

    override fun clickListener(binding: UserSelectLayoutBinding, holderCallback: () -> Int) {
        if (!isViewMode) {
            binding.cbUser.setOnCheckedChangeListener { _, isChecked ->
                if (holderCallback.position != RecyclerView.NO_POSITION) {
                    currentList[holderCallback.position].isSelected = isChecked
                }
            }
        }
    }
}