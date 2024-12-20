package com.zetta.takumiscan.presentation.main.menu.home.notes

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.zetta.takumiscan.R
import com.zetta.takumiscan.data.local.DBHelper
import com.zetta.takumiscan.databinding.ActivityNotesBinding
import com.zetta.takumiscan.util.MarginItemDecoration
import com.zetta.takumiscan.util.core.CoreFunction.dpToPx

class NotesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesBinding
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesBinding.inflate(layoutInflater)
        dbHelper = DBHelper(this)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.navy)

        setRecyclerView()

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnAddNote.setOnClickListener {
            Intent(this, NotesDetailActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun setRecyclerView(){
        binding.rvNotes.layoutManager = LinearLayoutManager(this)

        val bottomMargin = dpToPx(0)
        val lastItemBottomMargin = dpToPx(96)
        binding.rvNotes.addItemDecoration(MarginItemDecoration(bottomMargin, lastItemBottomMargin))

        val listNotes = dbHelper.getListNotes()
        binding.rvNotes.adapter = NotesAdapter(listNotes)
    }
}