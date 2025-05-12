package com.hunglevi.expense_mdc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hunglevi.expense_mdc.databinding.ItemUserBinding
import com.hunglevi.expense_mdc.data.model.User

class UserAdapter(
    private var users: List<User>, // Make this variable mutable
    private val onItemClick: (User) -> Unit,
    private val onEditClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.username.text = user.username
            binding.email.text = user.email
            binding.role.text = user.role
            binding.createdAt.text = user.createdAt

            binding.root.setOnClickListener {
                onItemClick(user)
            }
            binding.editButton.setOnClickListener {
                onEditClick(user)
            }
            binding.deleteButton.setOnClickListener {
                onDeleteClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    // Add this method to dynamically update the user list
    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}