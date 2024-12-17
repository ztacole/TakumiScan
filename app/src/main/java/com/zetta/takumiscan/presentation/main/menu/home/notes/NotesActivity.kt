package com.zetta.takumiscan.presentation.main.menu.home.notes

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zetta.takumiscan.R
import com.zetta.takumiscan.databinding.ActivityNotesBinding

class NotesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.navy)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnAddNote.setOnClickListener {
            Intent(this, NotesDetailActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}