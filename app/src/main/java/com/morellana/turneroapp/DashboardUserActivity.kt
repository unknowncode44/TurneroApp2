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

        //("para no destruir los fragments, usar la opcion hide() show()")
        val transaction = supportFragmentManager.beginTransaction()
        deleteFrag()
        transaction
            .add(R.id.frag, MyAppointmentFragment()).hide(MyAppointmentFragment())
            .add(R.id.frag, DashboardUserFragment()).hide(DashboardUserFragment())
            .add(R.id.frag, MyAccountFragment()).hide(MyAccountFragment())
            .commit()

        fragSelect(DashboardUserFragment(), MyAppointmentFragment(), MyAccountFragment())

        bottomNav()

    }

    private fun bottomNav(){
        val bottomNavigationView: BottomNavigationView = binding.main.bottomnav
        var open = ""
        bottomNavigationView.setOnItemSelectedListener { item ->

            when(item.itemId) {
                R.id.home_page -> {
                    if (open != "a"){
                        fragSelect(DashboardUserFragment(), MyAppointmentFragment(), MyAccountFragment())
                        open = "a"
                    }
                }
                R.id.myAppointment -> {
                    if (open != "b"){
                        fragSelect(MyAppointmentFragment(), DashboardUserFragment(), MyAccountFragment())
                        open = "b"
                    }
                }
                R.id.esp -> {  }
                R.id.myAccount -> {
                    if (open != "c"){
                        fragSelect(MyAccountFragment(), MyAppointmentFragment(), DashboardUserFragment())
                        open = "c"
                    }
                }
            }
            true
        }
    }

    private fun fragSelect(fragShow: Fragment, fragHide: Fragment, fragHide2: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
            transaction
                .hide(fragHide)
                .hide(fragHide2)
                .show(fragShow)
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