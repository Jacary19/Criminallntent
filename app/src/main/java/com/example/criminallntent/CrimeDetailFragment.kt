package com.example.criminallntent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.Button
import android.widget.CheckBox
import androidx.core.widget.doAfterTextChanged
import android.app.DatePickerDialog
import java.util.Calendar
import java.util.Date
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.ImageView
import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import java.io.FileOutputStream

class CrimeDetailFragment : Fragment() {
    private val cameraLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val photo = result.data?.extras?.get("data")
                        as? android.graphics.Bitmap

                if (photo != null) {
                    val crimeId = arguments?.getString("CRIME_ID")

                    val selectedCrime = CrimeRepository.crimes.find {
                        it.id.toString() == crimeId
                    }

                    if (selectedCrime != null) {
                        val fileName = "crime_${selectedCrime.id}.jpg"
                        val photoFile = File(
                            requireContext().filesDir,
                            fileName
                        )

                        FileOutputStream(photoFile).use { output ->
                            photo.compress(
                                android.graphics.Bitmap.CompressFormat.JPEG,
                                90,
                                output
                            )
                        }

                        selectedCrime.photoFileName = fileName

                        view?.findViewById<ImageView>(R.id.crime_photo)
                            ?.setImageBitmap(photo)

                        viewLifecycleOwner.lifecycleScope.launch {
                            val database =
                                CrimeDatabaseProvider.getDatabase(requireContext())

                            database.crimeDao().updateCrime(selectedCrime)
                        }
                    }
                }
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_crime_detail,
            container,
            false

        )
    }

   override fun onViewCreated(
                view: View,
                savedInstanceState: Bundle?
            ) {
                super.onViewCreated(view, savedInstanceState)

       val crimeTitle = arguments?.getString("CRIME_TITLE") ?: ""
       val crimeDate = arguments?.getLong("CRIME_DATE") ?: System.currentTimeMillis()
       val crimeSolved = arguments?.getBoolean("CRIME_SOLVED") ?: false
       val crimeId = arguments?.getString("CRIME_ID")
       val backButton = view.findViewById<Button>(R.id.back_button)

       backButton.setOnClickListener {
           parentFragmentManager.popBackStack()
       }
       val shareReportButton =
           view.findViewById<Button>(R.id.share_report_button)

       shareReportButton.setOnClickListener {
           val selectedCrime = CrimeRepository.crimes.find {
               it.id.toString() == crimeId
           }

           if (selectedCrime != null) {
               val solvedStatus = if (selectedCrime.isSolved) {
                   "Solved"
               } else {
                   "Unsolved"
               }

               val report = """
            Crime Report

            Title: ${selectedCrime.title}
            Date: ${java.text.DateFormat.getDateInstance().format(selectedCrime.date)}
            Status: $solvedStatus
        """.trimIndent()

               val shareIntent = Intent(Intent.ACTION_SEND).apply {
                   type = "text/plain"
                   putExtra(Intent.EXTRA_TEXT, report)
               }

               startActivity(Intent.createChooser(shareIntent, "Share Crime Report"))
           }
       }
       val titleEdit = view.findViewById<EditText>(R.id.crime_title_edit)
       val deleteCrimeButton =
           view.findViewById<Button>(R.id.delete_crime_button)

       deleteCrimeButton.setOnClickListener {
           val selectedCrime = CrimeRepository.crimes.find {
               it.id.toString() == crimeId
           }

           if (selectedCrime != null) {
               viewLifecycleOwner.lifecycleScope.launch {
                   val database =
                       CrimeDatabaseProvider.getDatabase(requireContext())

                   database.crimeDao().deleteCrime(selectedCrime)

                   CrimeRepository.crimes.remove(selectedCrime)

                   parentFragmentManager.popBackStack()
               }
           }
       }
       val dateButton = view.findViewById<Button>(R.id.crime_date_button)
       val solvedCheckBox = view.findViewById<CheckBox>(R.id.crime_solved_checkbox)

       titleEdit.doAfterTextChanged { text ->
           val selectedCrime = CrimeRepository.crimes.find {
               it.id.toString() == crimeId
           }

           if (selectedCrime != null) {
               selectedCrime.title = text.toString()

               viewLifecycleOwner.lifecycleScope.launch {
                   val database = CrimeDatabaseProvider.getDatabase(requireContext())
                   database.crimeDao().updateCrime(selectedCrime)
               }
           }
       }
       dateButton.text = java.text.DateFormat.getDateInstance().format(java.util.Date(crimeDate))
       dateButton.setOnClickListener {
           val calendar = Calendar.getInstance()
           calendar.timeInMillis = crimeDate

           DatePickerDialog(
               requireContext(),
               { _, year, month, day ->
                   calendar.set(year, month, day)

                   val selectedCrime = CrimeRepository.crimes.find {
                       it.id.toString() == crimeId
                   }

                   if (selectedCrime != null) {
                       selectedCrime.date = Date(calendar.timeInMillis)

                       viewLifecycleOwner.lifecycleScope.launch {
                           val database = CrimeDatabaseProvider.getDatabase(requireContext())
                           database.crimeDao().updateCrime(selectedCrime)
                       }
                   }

                   dateButton.text = java.text.DateFormat
                       .getDateInstance()
                       .format(calendar.time)
               },
               calendar.get(Calendar.YEAR),
               calendar.get(Calendar.MONTH),
               calendar.get(Calendar.DAY_OF_MONTH)
           ).show()
       }
       solvedCheckBox.isChecked = crimeSolved
       solvedCheckBox.setOnCheckedChangeListener { _, isChecked ->
           val selectedCrime = CrimeRepository.crimes.find {
               it.id.toString() == crimeId
           }

           if (selectedCrime != null) {
               selectedCrime.isSolved = isChecked

               viewLifecycleOwner.lifecycleScope.launch {
                   val database = CrimeDatabaseProvider.getDatabase(requireContext())
                   database.crimeDao().updateCrime(selectedCrime)
               }
           }
       }
       val crimePhoto = view.findViewById<ImageView>(R.id.crime_photo)
       val selectedCrime = CrimeRepository.crimes.find {
           it.id.toString() == crimeId
       }

       val photoFileName = selectedCrime?.photoFileName

       if (!photoFileName.isNullOrEmpty()) {
           val photoFile = File(requireContext().filesDir, photoFileName)

           if (photoFile.exists()) {
               val savedPhoto = android.graphics.BitmapFactory.decodeFile(
                   photoFile.absolutePath
               )

               crimePhoto.setImageBitmap(savedPhoto)
           }
       }

       val takePhotoButton = view.findViewById<Button>(R.id.take_photo_button)

       takePhotoButton.setOnClickListener {
           val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
           cameraLauncher.launch(cameraIntent)
       }


                android.util.Log.d(
                    "CriminalIntent",
                    "Selected crime ID: $crimeId"
                )
            }

    }