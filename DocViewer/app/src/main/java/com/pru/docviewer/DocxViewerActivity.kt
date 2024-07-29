package com.pru.docviewer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.barteksc.pdfviewer.PDFView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class DocxViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docx_viewer)

        val actualPath = intent.getStringExtra("actualPath")
        val outputPath = intent.getStringExtra("outputPath")

        val pdfView = findViewById<PDFView>(R.id.pdfView)

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                /*val document = Document(actualPath)
                document.save(outputPath, SaveFormat.PDF)*/
                withContext(Dispatchers.Main) {
                    pdfView.fromFile(File(outputPath!!)).load()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@DocxViewerActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}