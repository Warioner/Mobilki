package com.example.myapplication.classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class UserAdapter(
    private val onUserClick: (UserDB) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val items = mutableListOf<UserDB>()

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.imageUserAvatar)
        val name: TextView = view.findViewById(R.id.textUserName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(v)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = items[position]
        holder.name.text = user.name
        holder.avatar.setImageResource(zodiacImage(user.zodiac))
        holder.itemView.setOnClickListener { onUserClick(user) }
    }

    override fun getItemCount() = items.size

    fun addAll(newItems: List<UserDB>) {
        val start = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(start, newItems.size)
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    private fun zodiacImage(zodiac: String): Int = when (zodiac) {
        "Овен" -> R.drawable.oven
        "Телец" -> R.drawable.telec
        "Близнецы" -> R.drawable.blizneci
        "Рак" -> R.drawable.rak
        "Лев" -> R.drawable.lev
        "Дева" -> R.drawable.deva
        "Весы" -> R.drawable.vesi
        "Скорпион" -> R.drawable.scorpion
        "Стрелец" -> R.drawable.strelec
        "Козерог" -> R.drawable.kozerog
        "Водолей" -> R.drawable.vodoley
        "Рыбы" -> R.drawable.ribi
        else -> R.drawable.ic_launcher_foreground
    }
}