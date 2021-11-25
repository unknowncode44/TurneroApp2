package com.morellana.turneroapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.morellana.turneroapp.databinding.ActivityDashboardUserBinding
import com.morellana.turneroapp.ui.DashboardUserFragment
import com.morellana.turneroapp.ui.MyAppointmentFragment

class DashboardUserActivity : AppCompatActivity(){

    private lateinit var binding: ActivityDashboardUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fragSelect(DashboardUserFragment())

        bottomNav()

    }

    private fun bottomNav(){
        val bottomNavigationView: BottomNavigationView = binding.main.bottomnav
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home_page -> {fragSelect(DashboardUserFragment())}
                R.id.myAppointment -> { fragSelect(MyAppointmentFragment())}
                R.id.esp -> {  }
                R.id.myAccount -> {  }
                }
                true
        }
    }

    private fun fragSelect(fragClass: Fragment) {
        val frag = supportFragmentManager.findFragmentById(R.id.frag)
        val transaction = supportFragmentManager.beginTransaction()
        if (frag == null){
            transaction
                .add(R.id.frag, fragClass)
                .commit()
        } else {
            deleteFrag()
            transaction
                .add(R.id.frag, fragClass)
                .commit()
        }
    }

    private fun deleteFrag(){
        val frag = supportFragmentManager.findFragmentById(R.id.frag)
        val transaction = supportFragmentManager.beginTransaction()
        if (frag != null) {
            transaction
                .remove(frag)
                .commit()
        }
    }
}