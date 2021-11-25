package com.morellana.turneroapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.morellana.turneroapp.databinding.ActivityDashboardUserBinding
import com.morellana.turneroapp.ui.DashboardUserFragment
import com.morellana.turneroapp.ui.MyAccountFragment
import com.morellana.turneroapp.ui.MyAppointmentFragment

class DashboardUserActivity : AppCompatActivity(){

    private lateinit var binding: ActivityDashboardUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val transaction = supportFragmentManager.beginTransaction()
        deleteFrag()
        transaction
            .add(R.id.frag3, MyAppointmentFragment())
            .commit()

        deleteFrag()
        transaction
            .add(R.id.frag, DashboardUserFragment())
            .commit()

        deleteFrag()
        transaction
            .add(R.id.frag, MyAccountFragment())
            .commit()


        bottomNav()

    }

    private fun bottomNav(){
        val bottomNavigationView: BottomNavigationView = binding.main.bottomnav
        var open = ""
        bottomNavigationView.setOnItemSelectedListener { item ->

            when(item.itemId) {
                R.id.home_page -> {
                    if (open != "a"){
                        fragSelect(DashboardUserFragment())
                        open = "a"
                    }
                }
                R.id.myAppointment -> {
                    if (open != "b"){
                        fragSelect(MyAppointmentFragment())
                        open = "b"
                    }
                }
                R.id.esp -> {  }
                R.id.myAccount -> {
                    if (open != "c"){
                        fragSelect(MyAccountFragment())
                        open = "c"
                    }
                }
            }
            true
        }
    }

    private fun fragSelect(fragClass: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
            deleteFrag()
            transaction
                .add(R.id.frag, fragClass)
                .commit()

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