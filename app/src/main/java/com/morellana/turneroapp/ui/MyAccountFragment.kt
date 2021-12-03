package com.morellana.turneroapp.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.morellana.turneroapp.R
import com.morellana.turneroapp.SplashActivity
import com.morellana.turneroapp.databinding.FragmentMyAccountBinding
import com.morellana.turneroapp.dataclass.UserInfo
import com.morellana.turneroapp.dialogs.DialogMessageSimple
import android.content.Context
import android.content.ContextWrapper
import android.media.ThumbnailUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import com.theartofdev.edmodo.cropper.CropImage
import java.io.*


//Implementamos la clase para el paso de datos
class MyAccountFragment : Fragment(), DialogMessageSimple.Data {

    private var _binding: FragmentMyAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    //Image Crop
    //-------------------------------------------------------------------------------------
    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(16, 16)
                .getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent).uri
        }

    }
    //-------------------------------------------------------------------------------------

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //lo que hacemos es animar el inflar y el desinflar
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_rigth)
        exitTransition = inflater.inflateTransition(R.transition.slide_left)
    }

    @SuppressLint("UseCompatLoadingForColorStateLists", "WrongThread")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentMyAccountBinding.inflate(inflater, container, false)

        //Creamos la instancia
        auth = FirebaseAuth.getInstance()

        //Obtenemos la id de usuario
        val user: String = auth.currentUser!!.uid

        //Sacamos los datos de la DB
        getInfo(user)
        pickImgFromLocal(requireContext())

        //Escondemos el contenedor
        binding.containerGone.isVisible = false
        //Desplazamos el boton
        binding.ok.translationX = -500f

        //Abrimos el menu de cambio de datos
        binding.data.setOnClickListener {
            when (binding.data.text){
                "Modificar datos" ->{
                    modify(user)
                }
                "Volver" ->{
                    back(user)
                }
            }
        }

        //Instanciamos la clase para mostrarla
        val dialog = DialogMessageSimple()
        //El boton de logout y su doble funcion
        binding.logout.setOnClickListener {
            when (binding.logout.text){
                "Cerrar Sesion" -> {
                    logOut()
                }
                "Cambiar contraseña" -> {
                    when (binding.pass.isEnabled){
                        true -> {
                            //Comparamos que la contraseña cumpla las condiciones
                            if (binding.pass.text!!.length >= 6) {
                                if (binding.pass.text.toString() == binding.repPass.text.toString()){
                                    newPass(user)
                                } else {
                                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, "Contraseña muy corta", Toast.LENGTH_LONG).show()
                            }
                        }
                        false -> {
                            dialog.dialogMessageSimple(
                                context,
                                this)
                        }
                    }
                }
            }
        }

        //Modificamos los datos del usuario
        binding.ok.setOnClickListener {
            //Llamar a cuadro de dialogo
            updateData(user,
                binding.email.text.toString(),
                binding.lastName.text.toString(),
                binding.name.text.toString(),
                binding.pass.text.toString(),
                binding.phone.text.toString())
            back(user)
        }

        //Image Crop
        //-------------------------------------------------------------------------------------
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract){
            it?.let { uri ->
                //Cargamos la imagen por su URI - Uniform Resource Identifier,
                // “Identificador uniforme de recursos” (ubicacion)
                binding.imageProfile.setImageURI(uri)
                uploadImage(uri)
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
                saveToInternalStorage(bitmap, requireContext())
            }
        }
        //-------------------------------------------------------------------------------------

        //Cargamos una foto de la galeria
        binding.add.setOnClickListener {

            //Image Crop
            cropActivityResultLauncher.launch(null)

        }

        return binding.root
    }

    //Sube la imagen original al servidor
    private fun uploadImage(path: Uri?) {
        val user: String = auth.currentUser?.uid.toString()
        val storage = FirebaseStorage.getInstance().getReference("users")
        //Obtenemos la imagen de la ubicacion
        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, path)
        val baos = ByteArrayOutputStream()
        //Convertimos la imagen a byte
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        //La almacenamos
        val data = baos.toByteArray()
        val imageNoResized = storage.child(user)
        val uploadTask = imageNoResized.putBytes(data)
        //La subimos
        uploadTask.addOnSuccessListener{
            Toast.makeText(context, "Joya", Toast.LENGTH_SHORT).show()
        } .addOnFailureListener {
            Toast.makeText(context, "Mal", Toast.LENGTH_SHORT).show()
        }
    }

    //Guarda la imagen en el almacenamiento interno (thumbnail)
    private fun saveToInternalStorage(bitmap: Bitmap, context: Context): String {
        val name = auth.currentUser?.uid
        //Obtenemos la direccion para guardar la imagen
        val cw = ContextWrapper(context.applicationContext)
        // path: /data/data/yourapp/app_data/.thumbnails
        val directory = cw.getDir(".thumbnails", Context.MODE_PRIVATE)
        // Creamos la direccion
        Log.d("PATH", directory.path)
        val mypath = File(directory, "$name.jpg")

        //Creamos una imagen thumbnail para facilitar la carga
        val thumbImage: Bitmap = ThumbnailUtils.extractThumbnail(
            bitmap,
            500,
            500
        )

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            thumbImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.absolutePath
    }

    //Obtenemos la imagen creada tipo thumbnail, para mejorar el tiempo de carga
    private fun pickImgFromLocal(context: Context){

        val name = auth.currentUser?.uid
        val cw = ContextWrapper(context.applicationContext)
        val directory = cw.getDir(".thumbnails", Context.MODE_PRIVATE)
        Log.d("PATH", directory.path)
        val mypath = File(directory, "$name.jpg")
        val bitmap = BitmapFactory.decodeFile(mypath.absolutePath)
        if (bitmap != null){
            binding.imageProfile.setImageBitmap(bitmap)
        } else {
            pickImgFromServer()
        }
    }

    //Carga la imagen desde el servidor (Su utilidad es para cuando abramos la imagen)
    private fun pickImgFromServer() {
        val user: String = auth.currentUser?.uid.toString()
        val storageRef = FirebaseStorage.getInstance().reference.child("users/" + user + "noresized")
        //Creamos un archivo temporal
        val localFile = File.createTempFile("tempImage", "jpg")
        //Cargamos la imagen del storage en ese archivo temporal
        storageRef.getFile(localFile).addOnSuccessListener {
            //Lo convertimos a bitmap
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
        } .addOnFailureListener {
            Toast.makeText(context, "Todo mal", Toast.LENGTH_LONG).show()
        }
    }

    private fun logOut(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, SplashActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun newPass(uid: String){
        val user = FirebaseAuth.getInstance().currentUser
        val newPassword = binding.pass.text.toString()
        user!!.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User password updated.")
                    Toast.makeText(context, "Se Cambio la Contraseña con exito", Toast.LENGTH_LONG)
                        .show()
                    binding.repPass.setText("")
                    db.child("pass").setValue(binding.pass.text.toString())
                    binding.pass.isEnabled = false
                    binding.repPass.isEnabled = false
                }
            }
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun modify(uid: String){
        binding.ok.animate().translationX(0f).alpha(1F).setDuration(500).setStartDelay(300).start()
        binding.containerGone.isVisible = true
        binding.data.text = "Volver"
        binding.data.backgroundTintList = resources.getColorStateList(R.color.colorAccent)
        binding.logout.backgroundTintList = resources.getColorStateList(R.color.Secondary)
        binding.logout.text = "Cambiar contraseña"
        enabled()
        //Cuando finaliza la tarea, trae de nuevo los datos
        getInfo(uid)
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun back(uid: String){
        binding.ok.animate().translationX(-500f).alpha(0F).setDuration(500).setStartDelay(300).start()
        binding.containerGone.isVisible = false
        binding.data.text = "Modificar datos"
        binding.data.backgroundTintList = resources.getColorStateList(R.color.Secondary)
        binding.logout.backgroundTintList = resources.getColorStateList(R.color.colorAccent)
        binding.logout.text = "Cerrar Sesion"
        noEnabled()


        //Si se cancela la tarea, pero se modificaron sin aceptar los cambios, pone los datos
        //correspondientes
        getInfo(uid)
    }

    private fun enabled(){
        binding.name.isEnabled = true
        binding.lastName.isEnabled = true
        binding.phone.isEnabled = true
        binding.email.isEnabled = true
    }

    private fun noEnabled(){
        binding.name.isEnabled = false
        binding.lastName.isEnabled = false
        binding.phone.isEnabled = false
        binding.email.isEnabled = false
        binding.pass.isEnabled = false
        binding.repPass.isEnabled = false
    }

    private fun getInfo(uid: String){

        db = FirebaseDatabase.getInstance().getReference("users/users/$uid/data")
        db.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val name: String = snapshot.child("name").value.toString()
                    binding.name.setText(name)
                    val lastName: String = snapshot.child("lastName").value.toString()
                    binding.lastName.setText(lastName)
                    val phone: String = snapshot.child("phone").value.toString()
                    binding.phone.setText(phone)
                    val email: String = snapshot.child("email").value.toString()
                    binding.email.setText(email)
                    val pass: String = snapshot.child("pass").value.toString()
                    binding.pass.setText(pass)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        if (binding.name.text == null){
            binding.name.setText("")
        }
        if (binding.lastName.text == null){
            binding.lastName.setText("")
        }
        if (binding.phone.text == null){
            binding.phone.setText("")
        }
        if (binding.email.text == null){
            binding.email.setText("")
        }
        if (binding.pass.text == null){
            binding.pass.setText("")
        }
    }

    private fun updateData(uid: String, email: String, lastName: String, name: String, pass: String, phone: String){
        val data: Any = UserInfo(email, lastName, name, pass, phone)
        db.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(context, "Se actualizaron los datos", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(context, "Error al actualizar datos!", Toast.LENGTH_LONG).show()
                Log.e("Firebase", "Error updating data", it)
            }
    }

    //Funcion para pasar los datos al cuadro de dialogo
    override fun password(pass: String) {
        if (binding.pass.text.toString() == pass){
            //Mostrar la contraseña
            binding.pass.isEnabled = true
            binding.repPass.isEnabled = true
        } else {
            Toast.makeText(context, "La contraseña no coincide", Toast.LENGTH_LONG).show()
        }
        super.password(pass)
    }
}