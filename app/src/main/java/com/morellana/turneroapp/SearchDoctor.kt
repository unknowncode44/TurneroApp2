package com.morellana.turneroapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SearchDoctor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_doctor)

        val backBtn: FloatingActionButton = findViewById(R.id.imageButton)

        backBtn.setOnClickListener {
            back()
        }

    }
    fun back(){
        val intent = Intent(this, DashboardUserActivity::class.java)
        startActivity(intent)
        this.overridePendingTransition(R.anim.leave_back, R.anim.enter_back)
    }

    override fun onBackPressed() {
        back()
        super.onBackPressed()
    }
}