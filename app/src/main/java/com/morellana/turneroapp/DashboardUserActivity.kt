package com.morellana.turneroapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
                R.id.home_page -> {fragSelect(DashboardUserFragment())
                    Toast.makeText(this, "Categorias", Toast.LENGTH_LONG).show()}
                R.id.myAppointment -> { fragSelect(MyAppointmentFragment())
                    Toast.makeText(this, "En Linea", Toast.LENGTH_LONG).show() }
                R.id.esp -> { Toast.makeText(this, "Especialistas", Toast.LENGTH_LONG).show() }
                R.id.myAccount -> {Toast.makeText(this, "Mi cuenta", Toast.LENGTH_LONG).show()}
                }
                true
        }
    }

    private fun fragSelect(frag: Fragment) {
        val id: Int = (R.id.frag)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter, R.anim.leave)
        transaction.replace(id, frag)
        transaction.commit()
    }

    //anulamos el backPressed para volver a la actividad entre fragments
    override fun onBackPressed() {
        val count: Int = supportFragmentManager.backStackEntryCount

        if (count == 0){
            super.onBackPressed()
            supportFragmentManager.popBackStack()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}