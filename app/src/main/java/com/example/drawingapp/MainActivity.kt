package com.example.drawingapp

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_brush_size.*

class MainActivity : AppCompatActivity() {
    private var previouslySelectedColorSwatch: ImageButton? = null
    private val pickPhotoIntent =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView.setBrushSize(10.toFloat())
        brush_icon.setOnClickListener {
            showBrushSizeDialog()
        }

        gallery_icon.setOnClickListener {
            if (isReadStorageAllowed()) {
                // code to add background image from storage files
                startActivityForResult(pickPhotoIntent, GALLERY)
            } else {
                requestStoragePermission()
            }
        }

        undo_icon.setOnClickListener {
            drawingView.onClickUndo()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                try {
                    data?.data?.let {
                        drawingBackground.run {
                            visibility = View.VISIBLE
                            setImageURI(it)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
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

    private fun requestStoragePermission() {
        // checking if permission is granted
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).toString()
            )
        ) {
            Toast.makeText(this, "Need permission to add a background image", Toast.LENGTH_SHORT)
                .show()
        }

        // request permission
        // the result would be in onRequestPermissionsResult() with the code STORAGE_PERMISSION_CODE
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            STORAGE_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "Permission granted, you can read the storage files",
                    Toast.LENGTH_SHORT
                )
                    .show()
                startActivityForResult(pickPhotoIntent, GALLERY)
            } else {
                Toast.makeText(
                    this,
                    "You just denied permission",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun isReadStorageAllowed(): Boolean {
        val result =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        return result == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 1
        private const val GALLERY = 2
    }
}