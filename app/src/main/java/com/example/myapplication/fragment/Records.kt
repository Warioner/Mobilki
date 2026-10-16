package com.example.myapplication.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.classes.DataBase
import com.example.myapplication.classes.RecordAdapter
import com.example.myapplication.classes.RecordDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Records : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.records, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recordsRv)
        val emptyTv = view.findViewById<TextView>(R.id.emptyRecordsTv)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<Button>(R.id.btnRecordsBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val dao = DataBase.getDatabase(requireContext()).userDao()

        viewLifecycleOwner.lifecycleScope.launch {
            val sorted = withContext(Dispatchers.Default) {
                dao.getAllRecords()
                    .groupBy { it.userName }
                    .map { (_, games) -> games.maxWith(compareBy<RecordDB> { it.score }.thenByDescending { it.date }) }
                    .sortedWith(compareByDescending<RecordDB> { it.score }.thenBy { it.date })
            }
            recycler.adapter = RecordAdapter(sorted)
            emptyTv.visibility = if (sorted.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}