package com.morellana.turneroapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.morellana.turneroapp.databinding.ActivityDashboardUserBinding
import com.morellana.turneroapp.ui.DashboardUserFragment

class DashboardUserActivity : AppCompatActivity(){

    private lateinit var binding: ActivityDashboardUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.frag, DashboardUserFragment())
        transaction.commit()

        bottomNav()

    }
    private fun bottomNav(){
        val bottomNavigationView: BottomNavigationView = binding.main.bottomnav
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.categ -> {fragSelect(DashboardUserFragment())
                    Toast.makeText(this, "Categorias", Toast.LENGTH_LONG).show()}
                R.id.now -> { Toast.makeText(this, "En Linea", Toast.LENGTH_LONG).show() }
                R.id.esp -> { Toast.makeText(this, "Especialistas", Toast.LENGTH_LONG).show() }
                }
                true
        }
    }

    private fun fragSelect(frag: Fragment) {
        val id: Int = (R.id.frag)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(id, frag)
        transaction.commit()
    }
}