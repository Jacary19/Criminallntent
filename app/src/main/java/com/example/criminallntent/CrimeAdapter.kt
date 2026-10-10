package com.example.criminallntent

import com.example.criminallntent.Crime
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import java.text.DateFormat

class CrimeAdapter(
    private val crimes: List<Crime>,
    private val onCrimeClicked: (Crime) -> Unit
) : RecyclerView.Adapter<CrimeAdapter.CrimeViewHolder>() {

    class CrimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.crime_title)
        val dateText: TextView = view.findViewById(R.id.crime_date)
        val solvedCheckBox: CheckBox = view.findViewById(R.id.crime_solved)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CrimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_crime, parent, false)

        return CrimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CrimeViewHolder, position: Int) {
        val crime = crimes[position]

        holder.titleText.text = crime.title
        holder.dateText.text = DateFormat.getDateInstance().format(crime.date)
        holder.solvedCheckBox.isChecked = crime.isSolved

        holder.itemView.setOnClickListener {
            onCrimeClicked(crime)
        }
    }

    override fun getItemCount(): Int = crimes.size
}