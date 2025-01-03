package com.zetta.takumiscan.presentation.main.menu.home.notes

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.ColorFilter
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ImageViewCompat
import com.zetta.takumiscan.R
import com.zetta.takumiscan.data.local.DBHelper
import com.zetta.takumiscan.databinding.ActivityNotesDetailBinding
import com.zetta.takumiscan.model.Note
import com.zetta.takumiscan.util.notification.NotificationHelper

class NotesDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesDetailBinding
    private lateinit var dbHelper: DBHelper

    private var mode: String = "show"
    private var id = -1
    private var note: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesDetailBinding.inflate(layoutInflater)
        dbHelper = DBHelper(this)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.navy)

        mode = intent.getStringExtra("mode") ?: "show"
        id = intent.getIntExtra("id", -1)

        setMode()

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnDate.setOnClickListener{
            val currentDate = Calendar.getInstance()
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                binding.lblDate.text = "$dayOfMonth-${month + 1}-$year"
                binding.btnDelete.visibility  = View.VISIBLE
            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.btnDelete.setOnClickListener {
            binding.lblDate.text = ""
            binding.btnDelete.visibility  = View.GONE
        }

        binding.iconSave.setOnClickListener {
            when(mode){
                "show" -> {
                    mode = "edit"
                    Toast.makeText(this, "Edit Mode", Toast.LENGTH_SHORT).show()
                    setMode()
                }
                "edit" -> {
                    if (binding.tbNote.text.isEmpty()){
                        Toast.makeText(this, "Catatan harus diisi", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    mode = "show"
                    editNote()
                    note = dbHelper.getNoteByID(id)
                    Toast.makeText(this, "Catatan berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    setMode()
                }
                "add" -> {
                    if (binding.tbNote.text.isEmpty()){
                        Toast.makeText(this, "Catatan harus diisi", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    mode = "show"
                    addNote()
                    note = dbHelper.getNoteByID(id)
                    Toast.makeText(this, "Catatan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    setMode()
                }
            }
        }
    }

    private fun setMode(){
        binding.iconSave.setImageResource(R.drawable.baseline_check_24)
        binding.tbTitle.isFocusable = true
        binding.tbTitle.isFocusableInTouchMode = true
        binding.tbNote.isFocusable = true
        binding.tbNote.isFocusableInTouchMode = true
        binding.btnDate.isEnabled = true
        binding.btnDelete.isEnabled = true

        when(mode){
            "show" -> {
                note = dbHelper.getNoteByID(id)
                binding.iconSave.setImageResource(R.drawable.add_note)
                binding.lblMode.text = "Mode Lihat"

                binding.tbTitle.isFocusable = false
                binding.tbTitle.isFocusableInTouchMode = false
                binding.tbNote.isFocusable = false
                binding.tbNote.isFocusableInTouchMode = false
                binding.btnDate.isEnabled = false
                binding.btnDelete.isEnabled = false

                showNote(note!!)
            }
            "edit" -> {
                note = dbHelper.getNoteByID(id)
                binding.lblMode.text = "Mode Edit"

                showNote(note!!)
            }
            "add" -> {
                binding.lblMode.text = "Mode Tambah"
            }
        }
    }

    private fun showNote(note: Note){
        note.title?.let { binding.tbTitle.setText(it) }
        note.date?.let {
            binding.lblDate.text = it
            binding.btnDelete.visibility  = View.VISIBLE
        }
        binding.tbNote.setText(note.notes)
    }

    private fun editNote(){
        var title: String? = binding.tbTitle.text.toString()
        var date: String? = binding.lblDate.text.toString()
        if (binding.tbTitle.text.isEmpty()) title = null
        if (binding.lblDate.text.isEmpty()) date = null
        if (binding.tbNote.text.isEmpty()){
            Toast.makeText(this, "Catatan harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        date?.let {
            NotificationHelper.cancelNotification(this, id)
            NotificationHelper.registerNotification(this, it, id)
        }

        dbHelper.editNote(
            id = id,
            note = Note(id, title, binding.tbNote.text.toString(), date)
        )
    }

    private fun addNote(){
        var title: String? = binding.tbTitle.text.toString()
        var date: String? = binding.lblDate.text.toString()
        if (binding.tbTitle.text.isEmpty()) title = null
        if (binding.lblDate.text.isEmpty()) date = null


        id = dbHelper.addNote(Note(0, title, binding.tbNote.text.toString(), date)).toInt()
        date?.let { NotificationHelper.registerNotification(this, it, id) }
    }
}