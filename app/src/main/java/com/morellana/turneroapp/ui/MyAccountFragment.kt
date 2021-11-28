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
import com.morellana.turneroapp.R
import com.morellana.turneroapp.SplashActivity
import com.morellana.turneroapp.databinding.FragmentMyAccountBinding

class MyAccountFragment : Fragment() {

    private var _binding: FragmentMyAccountBinding? = null
    private val binding get() = _binding!!

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
        _binding =  FragmentMyAccountBinding.inflate(inflater, container, false)

        binding.ok.translationX = -500f

        binding.data.setOnClickListener {
            binding.ok.animate().translationX(0f).setDuration(500).setStartDelay(300).start()
            binding.data.text = "Volver"
            binding.data.setOnClickListener {
                binding.data.text = "Modificar Datos"
            }
        }
//        val btn: Button = view?.findViewById(R.id.boton) ?:
//        btn.setOnClickListener {
//            logOut()
//        }
//
//        val passText: EditText = view.findViewById(R.id.new_pass)
//
//        val btnPass: Button = view.findViewById(R.id.new_pass_buttom)
//        btnPass.setOnClickListener {
//            newPass(passText)
//        }

        return binding.root
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