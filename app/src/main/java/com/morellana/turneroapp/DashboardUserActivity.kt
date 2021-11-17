package com.morellana.turneroapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import com.morellana.turneroapp.adapters.DoctorCardAdapter
import com.morellana.turneroapp.adapters.OnlineDoctorsAdapter
import com.morellana.turneroapp.adapters.SpecialityCardAdapter
import com.morellana.turneroapp.databinding.ActivityDashboardUserBinding
import com.morellana.turneroapp.dataclass.OnlineDoctor
import com.morellana.turneroapp.dataclass.Profesional
import com.morellana.turneroapp.dataclass.Speciality
import com.morellana.turneroapp.ui.OnlineDoctorFragment
import com.morellana.turneroapp.ui.ProfessionalFragment
import com.morellana.turneroapp.ui.SpecialistFragment

class DashboardUserActivity : AppCompatActivity(){

    private lateinit var binding: ActivityDashboardUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.frag, ProfessionalFragment())
        transaction.commit()

        navBar()

        bottomNav()

    }

    //Creamos la funcion para la apertura del Navegador Superior
    fun navBar(){
        val nav: androidx.appcompat.widget.Toolbar = binding.main.toolbar
        setSupportActionBar(nav)
        val ab: ActionBar? = supportActionBar
        if(ab != null){
            ab.setHomeAsUpIndicator(R.drawable.nav_icon)
            ab.setDisplayHomeAsUpEnabled(true)
            ab.title = "Nombre de Usuario"
        }
    }
    //Implementa el boton para abrir el Nav Lateral
    @SuppressLint("WrongConstant")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(Gravity.START)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun bottomNav(){
        val bottomNavigationView: BottomNavigationView = binding.main.bottomnav
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.categ -> {fragSelect(ProfessionalFragment())
                    Toast.makeText(this, "Categorias", Toast.LENGTH_LONG).show()}
                R.id.now -> {fragSelect(OnlineDoctorFragment())
                    Toast.makeText(this, "En Linea", Toast.LENGTH_LONG).show()}
                R.id.esp -> {fragSelect(SpecialistFragment())
                    Toast.makeText(this, "Especialistas", Toast.LENGTH_LONG).show()}
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