package com.morellana.turneroapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.morellana.turneroapp.databinding.FragmentTabRegisterBinding
import com.morellana.turneroapp.dataclass.User2
import com.morellana.turneroapp.utilities.Utilities
import java.util.*

class TabRegisterFragment: Fragment() {

    // VARIABLES MAIN
    private var auth: FirebaseAuth? = null
    private var database: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentTabRegisterBinding =
            FragmentTabRegisterBinding.inflate(inflater, container, false)
        val view: View = binding.root

        // ## Obtenemos las instancias de Auth y de Firebase RTDB
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // ## Creamos e instanciamos las variables donde obtendremos los datos de registro
        val fragmentBody = view.findViewById<View>(R.id.register_fragmentBody)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister2)
        val register_email = view.findViewById<EditText>(R.id.register_email)
        val register_name = view.findViewById<EditText>(R.id.register_name)
        val register_password = view.findViewById<EditText>(R.id.register_password)
        val register_confirm_password =
            view.findViewById<EditText>(R.id.register_confirm_password)

        // ## Utilidad para esconder el teclado cuando tocamos otra parte de la pantalla
        Utilities.setupUI(fragmentBody, activity)

        // #### CUANDO PULSAMOS EL BOTON DE REGISTRO ####
        btnRegister.setOnClickListener { view1: View? ->

            //!!! IMPORTANTE: LOS DATOS DE LOS EDITTEXT SE CAPTURAN DENTRO DEL METODO ONCLICKLISTENER!!, EVITA ERRORES COMO LOS MIOS :'( !!!

            // ## Obtenemos los datos desde editText instanciado mas arriba
            val registerEmail = register_email.text.toString()
            val registerName = register_name.text.toString()
            val registerPassword = register_password.text.toString()
            val register_C_Password = register_confirm_password.text.toString()

            // #### Condiciones: ####
            // ## 1- si la contrasena es menor a 6, enviar toast ##
            if (registerPassword.length >= 6) {

                // ## 2- si la contrasena no coincide con la confirmacion de la contrasena, enviar toast ##
                if (registerPassword == register_C_Password) {

                    // ## Si pasamos las dos condiciones, ejecutamos el metodo createAccount y navegamos a la siguiente pagina ##
                    createAccount(registerEmail, registerPassword, registerName)
                } else {
                    Toast.makeText(context, "Las contrasenas no coinciden", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(context, "La contrasena es muy corta", Toast.LENGTH_LONG).show()
            }
        }
        return view
    }

    // #### METODO PRINCIPAL DE REGISTRO Y ALMACENAMIENTO DE DATOS EN FIREBASE RTDB ####
    fun createAccount(email: String?, password: String?, name: String?) {

        // ## Utilizamos el metodo createUser FirebaseAuth, y le agregamos un listener para ver si el registro fue exitoso
        auth!!.createUserWithEmailAndPassword(email!!, password!!).addOnCompleteListener(
            requireActivity()
        ) { task: Task<AuthResult?> ->

            // ## Si fue exitoso haremos lo siguiente:
            if (task.isSuccessful) {

                // ## Una vez generado el usuario capturamos su token (llamado UID - que es un identificador unico del usuario)
                val uid =
                    Objects.requireNonNull(auth!!.currentUser)?.uid

                // ## Creamos un objeto con el nombre y el email capturados a fin de guardarlos en la base de datos
                val user: Any = User2(name, email, "x-user")

                // ## Una vez obtenemos el uid lo usamos para guardar el usuario en la base de datos
                database!!.child("users/users").child(uid.toString()).setValue(user)

                // ## Indicamos a traves de un toast que el usuario fue creado exitosamente
                Toast.makeText(context, "Registro correcto, bienvenido", Toast.LENGTH_LONG)
                    .show()

                // ## Navegamos a la pagina de inicio
                val intent_ = Intent(context, DashboardUserActivity::class.java)
                startActivity(intent_)
            } else {
                val errorMessage =
                    Objects.requireNonNull(task.exception)
                        ?.localizedMessage
                Toast.makeText(context, "Hubo un error$errorMessage", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}