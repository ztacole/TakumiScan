package com.zetta.takumiscan.presentation.main.menu.home.notes

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.zetta.takumiscan.R
import com.zetta.takumiscan.data.local.DBHelper
import com.zetta.takumiscan.databinding.ActivityNotesBinding
import com.zetta.takumiscan.model.Note
import com.zetta.takumiscan.presentation.main.menu.home.notes.adapter.NotesAdapter
import com.zetta.takumiscan.util.CacheController
import com.zetta.takumiscan.util.core.CoreFunction.showDialog

class NotesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesBinding
    private lateinit var dbHelper: DBHelper
    private lateinit var cacheController: CacheController

    private lateinit var listNotes: MutableList<Note>
    private lateinit var adapter : NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesBinding.inflate(layoutInflater)
        dbHelper = DBHelper(this)
        cacheController = CacheController(this)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.navy)

        if (!cacheController.isFirstTimeOpenNotes()){
            showDialog(
                title = "Informasi",
                message = "Swipe ke kiri untuk hapus catatan.\nSwipe ke kanan untuk edit catatan.",
                positiveButtonText = "Ok",
                onPositiveButtonClick = DialogInterface.OnClickListener { dialog, _ ->
                    cacheController.alreadyOpenedNotes()
                    dialog.dismiss()
                },
                cancellable = false
            )
        }

        listNotes = mutableListOf()
        adapter = NotesAdapter(listNotes)
        setRecyclerView()

        loadData()

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnAddNote.setOnClickListener {
            Intent(this, NotesDetailActivity::class.java).also {
                it.putExtra("mode", "add")
                startActivity(it)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadData(){
        listNotes.clear()
        listNotes.addAll(dbHelper.getListNotes())
        adapter.notifyDataSetChanged()
    }

    private fun setRecyclerView(){
        binding.rvNotes.layoutManager = LinearLayoutManager(this)

        binding.rvNotes.adapter = adapter

        val touchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val position = viewHolder.adapterPosition
                val note = listNotes[position]
                if (direction == ItemTouchHelper.LEFT){
                    listNotes.removeAt(position)
                    dbHelper.deleteNote(note.id)
                    adapter.notifyItemRemoved(position)
                    Snackbar.make(binding.root, "Catatan  dihapus", Snackbar.LENGTH_LONG)
                        .setAction("Pulihkan"){
                            listNotes.add(position, note)
                            adapter.notifyItemInserted(position)
                            dbHelper.addNote(note)
                        }.show()
                }
                else{
                    Intent(this@NotesActivity, NotesDetailActivity::class.java).also {
                        it.putExtra("mode", "edit")
                        it.putExtra("id", note.id)
                        startActivity(it)
                    }
                    Toast.makeText(this@NotesActivity, "Edit Mode", Toast.LENGTH_SHORT).show()
                }
            }
        })

        touchHelper.attachToRecyclerView(binding.rvNotes)
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}