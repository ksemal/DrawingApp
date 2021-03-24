package com.example.drawingapp

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_brush_size.*

class MainActivity : AppCompatActivity() {
    private var previouslySelectedColorSwatch: ImageButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView.setBrushSize(10.toFloat())
        brush_icon.setOnClickListener {
            showBrushSizeDialog()
        }
    }

    private fun showBrushSizeDialog() {
        val brushDialog = Dialog(this)
        brushDialog.run {
            setContentView(R.layout.dialog_brush_size)
            setTitle(R.string.choose_brush_size)
            brush_sm_button.setOnClickListener { changeBrushSize(10.toFloat(), this) }
            brush_md_button.setOnClickListener { changeBrushSize(20.toFloat(), this) }
            brush_lg_button.setOnClickListener { changeBrushSize(40.toFloat(), this) }
            show()
        }
    }

    private fun changeBrushSize(size: Float, dialog: Dialog) {
        drawingView.setBrushSize(size)
        dialog.dismiss()
    }

    fun changeBrushColor(view: View) {
        previouslySelectedColorSwatch?.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.pallet_swatch
            )
        )
        drawingView.setBrushColor(view.tag.toString())
        (view as? ImageButton)?.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.pallet_swatch_selected
            )
        )
        previouslySelectedColorSwatch = view as? ImageButton
    }
}