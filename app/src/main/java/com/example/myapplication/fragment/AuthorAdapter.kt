package com.example.myapplication.fragment


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.R

class AuthorAdapter(
    context: Context,
    private val authors: List<Author>
) : ArrayAdapter<Author>(context, 0, authors) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.items_author, parent, false)

        val author = authors[position]

        val imageView = view.findViewById<ImageView>(R.id.imageAuthorPhoto)
        val textView = view.findViewById<TextView>(R.id.textAuthorName)

        imageView.setImageResource(author.photoResId)
        textView.text = author.name

        return view
    }
}