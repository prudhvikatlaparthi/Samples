package com.pru.sqldelight.androidApp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pru.sqldelight.shared.*
import java.util.*

fun greet(): String {
    return Greeting().greeting()
}


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    val apicall = Apicall(DatabaseDriverFactory(this))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn: Button = findViewById(R.id.tapButton)

        updateData()

        btn.setOnClickListener {
            apicall.insertData(
                TblTokens(
                    staticToken = "Static",
                    dynamicToken = "Dyna",
                    secretKey = "secre",
                    domainAccountID = Random(100).nextInt(),
                    userOrgBRID = Random(100).nextInt()
                )
            )
            updateData()
        }
    }

    private fun updateData() {
        findViewById<TextView>(R.id.data).text = ""
        apicall.getData().forEach {
            findViewById<TextView>(R.id.data).append("\n" + it.toString())
        }
    }
}
