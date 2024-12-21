package com.zetta.takumiscan.presentation.main.menu.home.notes

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zetta.takumiscan.R
import com.zetta.takumiscan.data.local.DBHelper
import com.zetta.takumiscan.databinding.ActivityNotesBinding
import com.zetta.takumiscan.util.CacheController
import com.zetta.takumiscan.util.MarginItemDecoration
import com.zetta.takumiscan.util.core.CoreFunction.dpToPx
import com.zetta.takumiscan.util.core.CoreFunction.showDialog

class NotesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesBinding
    private lateinit var dbHelper: DBHelper
    private lateinit var cacheController: CacheController

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

        setRecyclerView()
        loadData()

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnAddNote.setOnClickListener {
            Intent(this, NotesDetailActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun loadData(){
        val listNotes = dbHelper.getListNotes()
        val adapter = NotesAdapter(listNotes)
        binding.rvNotes.adapter = adapter

        val touchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return if (viewHolder.adapterPosition != 0){
                    super.getSwipeDirs(recyclerView, viewHolder)
                }
                else ItemTouchHelper.ACTION_STATE_IDLE
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val note = listNotes[viewHolder.adapterPosition - 1]
                if (direction == ItemTouchHelper.LEFT){
                    Toast.makeText(this@NotesActivity, "Deleted ${viewHolder.adapterPosition}, id ${note.id}", Toast.LENGTH_SHORT).show()
                    adapter.notifyItemRemoved(viewHolder.adapterPosition)
                }
                else{
                    Toast.makeText(this@NotesActivity, "Edited", Toast.LENGTH_SHORT).show()
                }
            }
        })

        touchHelper.attachToRecyclerView(binding.rvNotes)
    }

    private fun setRecyclerView(){
        binding.rvNotes.layoutManager = LinearLayoutManager(this)

        val bottomMargin = dpToPx(0)
        val lastItemBottomMargin = dpToPx(96)
        binding.rvNotes.addItemDecoration(MarginItemDecoration(bottomMargin, lastItemBottomMargin))
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}