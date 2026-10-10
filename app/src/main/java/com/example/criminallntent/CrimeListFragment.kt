package com.example.criminallntent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criminallntent.Crime
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.Button
import kotlinx.coroutines.launch

object CrimeRepository {
    val crimes = mutableListOf(
        Crime(title = "The Vanishing USB Drive"),
        Crime(title = "The Midnight Server Outage", isSolved = true),
        Crime(title = "Unauthorized Network Access"),
        Crime(title = "The Missing Laptop"),
        Crime(title = "The Encrypted Evidence"),
        Crime(title = "The Disappearing Security Badge", isSolved = true)
    )
}
class CrimeListFragment : Fragment() {

    private val crimes = CrimeRepository.crimes


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_crime_list,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        val addCrimeButton = view.findViewById<Button>(R.id.add_crime_button)

        addCrimeButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val newCrime = Crime(title = "New Crime")

                val database = CrimeDatabaseProvider.getDatabase(requireContext())
                database.crimeDao().insertCrime(newCrime)

                CrimeRepository.crimes.add(newCrime)

                val crimeRecyclerView =
                    view.findViewById<RecyclerView>(R.id.crime_recycler_view)

                crimeRecyclerView.adapter?.notifyItemInserted(
                    CrimeRepository.crimes.lastIndex
                )
            }
        }
        val crimeRecyclerView =
            view.findViewById<RecyclerView>(R.id.crime_recycler_view)
        viewLifecycleOwner.lifecycleScope.launch {
            val database = CrimeDatabaseProvider.getDatabase(requireContext())
            val crimeDao = database.crimeDao()

            val savedCrimes = crimeDao.getCrimes()


            val loadedCrimes = crimeDao.getCrimes()

            CrimeRepository.crimes.clear()
            CrimeRepository.crimes.addAll(loadedCrimes)

            crimeRecyclerView.adapter?.notifyDataSetChanged()
        }




        crimeRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        crimeRecyclerView.adapter = CrimeAdapter(crimes) { crime ->
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    CrimeDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString("CRIME_ID", crime.id.toString())
                            putString("CRIME_TITLE", crime.title)
                            putLong("CRIME_DATE", crime.date.time)
                            putBoolean("CRIME_SOLVED", crime.isSolved)
                        }
                    }
                )
                .addToBackStack(null)
                .commit()
        }
    }
}