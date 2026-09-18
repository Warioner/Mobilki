package com.example.myapplication.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ListView
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.R
data class Author (
    val name: String,
    val photoResId: Int
)


class Authors : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.autors, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val authors = listOf(
            Author("Антон", R.drawable.anton),
            Author("Катя", R.drawable.katya)
        )

        val listView = view.findViewById<ListView>(R.id.listViewAuthors)
        listView.adapter = AuthorAdapter(requireContext(), authors)
    }


}