package com.morellana.turneroapp.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.morellana.turneroapp.DashboardUserActivity
import com.morellana.turneroapp.R
import com.morellana.turneroapp.SplashActivity

class MyAccountFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //lo que hacemos es animar el inflar y el desinflar
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_rigth)
        exitTransition = inflater.inflateTransition(R.transition.slide_left)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_my_account, container, false)

        val btn: Button = view.findViewById(R.id.boton)
        btn.setOnClickListener {
            logOut()
        }

        val passText: EditText = view.findViewById(R.id.new_pass)

        val btnPass: Button = view.findViewById(R.id.new_pass_buttom)
        btnPass.setOnClickListener {
            newPass(passText)
        }

        return view
    }

    fun logOut(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, SplashActivity::class.java)
        startActivity(intent)
    }

    fun newPass(text: EditText){
        val user = FirebaseAuth.getInstance().currentUser
        val newPassword = text.text.toString()

        if (text.text.toString().length >= 6){
            user!!.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User password updated.")
                        Toast.makeText(context, "Se Cambio la Contraseña con exito", Toast.LENGTH_LONG).show()
                        text.text = null
                    }
                }
        } else {
            Toast.makeText(context, "La contraseña es muy corta", Toast.LENGTH_SHORT).show()
            text.text = null
        }
    }
}