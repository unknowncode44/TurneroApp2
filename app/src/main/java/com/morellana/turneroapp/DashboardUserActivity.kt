package com.morellana.turneroapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.morellana.turneroapp.databinding.ActivityDashboardUserBinding
import com.morellana.turneroapp.dashboarduser.ui.DashboardUserFragment
import com.morellana.turneroapp.ui.MyAccountFragment
import com.morellana.turneroapp.myappointment.ui.MyAppointmentFragment

class DashboardUserActivity : AppCompatActivity(){

    private lateinit var binding: ActivityDashboardUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val transaction = supportFragmentManager.beginTransaction()
        transaction
            .add(R.id.frag, DashboardUserFragment())
            .commit()

        bottomNav()

    }

    private fun bottomNav(){
        val bottomNavigationView: BottomNavigationView = binding.bottomnav
        //Recurri a algo basico, no sabia como controlar la doble seleccion de los botones de navegacion :P
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

    private fun fragSelect(fragShow: Fragment) {
        //Instanciamos el contenedor
        val frag: Fragment? = supportFragmentManager.findFragmentById(R.id.frag)
        //Instanciamos la transicion
        val transaction = supportFragmentManager.beginTransaction()
        //Condicional para saber si el fragment ya esta añadido
        if (fragShow.isAdded){
            frag?.let {
                transaction
                    .hide(it)  //Esconde el fragment actual
                    .show(fragShow)  //Muestra el frament
            }
        } else {
            frag?.let {
                transaction
                    .hide(it)  //Esconde el fragment actual
                    .add(R.id.frag, fragShow)  //Añade el nuevo frament
            }
        }
        transaction.commit()
    }

    override fun onBackPressed() {
        //El boton de atras vuelve a la pantalla principal
        //Si se cumple la condicion, se cierra la app
        if (binding.bottomnav.selectedItemId == R.id.home_page){
            finish()
            super.onBackPressed()
        } else {
            //Con esta linea, selecciona un item del bottom nav!!
            binding.bottomnav.selectedItemId = R.id.home_page
        }
    }
}