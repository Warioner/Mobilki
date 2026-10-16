package com.example.myapplication.classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordAdapter(
    private val items: List<RecordDB>
) : RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())

    class RecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rank: TextView = view.findViewById(R.id.textRank)
        val name: TextView = view.findViewById(R.id.textRecordName)
        val date: TextView = view.findViewById(R.id.textRecordDate)
        val score: TextView = view.findViewById(R.id.textRecordScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = items[position]
        holder.rank.text = "${position + 1}."
        holder.name.text = record.userName
        holder.date.text = dateFormat.format(Date(record.date))
        holder.score.text = record.score.toString()
    }

    override fun getItemCount() = items.size
}