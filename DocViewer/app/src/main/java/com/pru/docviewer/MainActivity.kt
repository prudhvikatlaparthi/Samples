package com.pru.docviewer

import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<Button>(R.id.btnDocx).setOnClickListener {
            startActivity(Intent(this, DocxViewerActivity::class.java).apply {
                putExtra("fileName", "doc3.docx")
            })
        }

        findViewById<Button>(R.id.btnXlsx).setOnClickListener {
            startActivity(Intent(this, DocxViewerActivity::class.java).apply {
                putExtra("fileName", "doc1.xlsx")
            })
        }

        findViewById<Button>(R.id.btnPdf).setOnClickListener {
            pickPdfFile.launch("application/*")
        }
    }

    private val pickPdfFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        // Handle the selected PDF file URI here
        uri?.let {
            // Do something with the PDF file
            val pathFromUri = PathUtils.getPathFromUri(it)
            val file = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis().toString().plus(".pdf"))
            startActivity(Intent(this, DocxViewerActivity::class.java).apply {
                putExtra("actualPath", pathFromUri)
                putExtra("outputPath", file.absolutePath)
            })
        }
    }
}