package com.morellana.turneroapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.morellana.turneroapp.databinding.FragmentTabLoginBinding
import com.morellana.turneroapp.utilities.Utilities
import java.util.*


// #### IMPORTAMOS ####
// NATIVO
// DATABINDING
// FIREBASE
// JAVA
class TabLoginFragment : Fragment() {
    // VARIABLES MAIN
    private var auth: FirebaseAuth? = null

    // VARIABLES ANIMACION
    var v = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentTabLoginBinding.inflate(inflater, container, false)
        val view: View = binding.root

        // ## Obtenemos la instancia de FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // ## Creamos e instanciamos las variables donde obtendremos los datos de registro
        val fragmentBody = view.findViewById<View>(R.id.login_fragmentBody)
        val btnLogin = view.findViewById<Button>(R.id.login_Btn)
        val loginEmail = view.findViewById<EditText>(R.id.login_email)
        val loginPassword = view.findViewById<EditText>(R.id.login_password)
        val fgtPassword = view.findViewById<TextView>(R.id.fgtPass)

        // ## Utilidad para esconder el teclado cuando tocamos otra parte de la pantalla
        Utilities.setupUI(fragmentBody, activity)

        // ## Animacion de los campos de texto
        loginEmail.translationX = 800f
        loginPassword.translationX = 800f
        fgtPassword.translationX = 800f
        btnLogin.translationY = -200f
        loginEmail.alpha = v
        loginPassword.alpha = v
        btnLogin.alpha = v
        fgtPassword.alpha = v
        loginEmail.animate().translationX(0f).alpha(1f).setDuration(800).setStartDelay(300).start()
        loginPassword.animate().translationX(0f).alpha(1f).setDuration(800).setStartDelay(500)
            .start()
        fgtPassword.animate().translationX(0f).alpha(1f).setDuration(800).setStartDelay(500)
            .start()
        btnLogin.animate().translationY(0f).alpha(1f).setDuration(1200).setStartDelay(600).start()


        // #### CUANDO PULSAMOS EL BOTON DE INGRESO ####
        btnLogin.setOnClickListener { view1: View? ->

            // ## Obtenemos los datos desde editText instanciado mas arriba
            val loginEmailText = loginEmail.text.toString()
            val loginPasswordText = loginPassword.text.toString()

            // #### Condiciones: ####
            // ## 1- si la contrasena es menor a 6, enviar toast ##
            if (loginPasswordText.length >= 6) {
                // ## Utilizamos el metodo login para acceder con las credenciales proporcionadas
                login(loginEmailText, loginPasswordText)
            } else {
                Toast.makeText(context, "La contrasena es muy corta", Toast.LENGTH_LONG).show()
            }
        }
        return view
    }

    // #### METODO PRINCIPAL DE INGRESO POR EMAIL Y CONTRASENA ####
    fun login(email: String?, password: String?) {

        // ## Utilizamos el metodo signInWithEmailAndPassword de FirebaseAuth, y le agregamos un listener para ver si el registro fue exitoso
        auth!!.signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(requireActivity()) { task: Task<AuthResult?> ->

                // ## Si el ingreso fue exitoso haremos lo siguiente:
                if (task.isSuccessful) {
                    // ## Indicamos a traves de un toast que el usuario ingreso exitosamente
                    Toast.makeText(context, "Login exitoso, bienvenido", Toast.LENGTH_LONG).show()

                    // ## Navegamos a la pagina de inicio
                    val intent_ = Intent(context, DashboardUserActivity::class.java)
                    startActivity(intent_)
                    activity?.finish()
                    activity?.overridePendingTransition(R.anim.enter, R.anim.leave)
                } else {
                    val errorMessage = Objects.requireNonNull(task.exception)?.localizedMessage
                    Toast.makeText(context, "Hubo un error$errorMessage", Toast.LENGTH_LONG).show()
                }
            }
    }

}