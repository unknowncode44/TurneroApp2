package com.morellana.turneroapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
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
import android.content.pm.PackageManager
import android.media.ThumbnailUtils
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.*

//Creamos las variables para navegar entre los resultados de las actividades
const val GALLERY = 3000
const val CAMERA = 3001
//Variable de permisos
private const val PERMISSION_REQUEST = 10

//Implementamos la clase para el paso de datos
class MyAccountFragment : Fragment(), DialogMessageSimple.Data {

    private var _binding: FragmentMyAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth

    //La lista de los permisos que maneja la APP
    private var permission = arrayOf(android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private var imageUri: Uri? = null

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
    ): View {
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
                getText(R.string.log_out) -> {
                    logOut()
                }
                getText(R.string.change_password) -> {
                    when (binding.pass.isEnabled){
                        true -> {
                            //Comparamos que la contraseña cumpla las condiciones
                            if (binding.pass.text!!.length >= 6) {
                                if (binding.pass.text.toString() == binding.repPass.text.toString()){
                                    newPass()
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
            updateData(
                binding.email.text.toString(),
                binding.lastName.text.toString(),
                binding.name.text.toString(),
                binding.pass.text.toString(),
                binding.phone.text.toString())
            back(user)
        }

        //Cargamos una foto de la galeria
        binding.add.setOnClickListener {
            if (checkPermission(requireContext(), permission)){
                        Log.i("PERMISOS", "TODOS LOS PERMISOS ACEPTADOS")
                Toast.makeText(context, "asfdaf", Toast.LENGTH_LONG).show()
                        showPictureDialog()
                    } else {
                //Caso contrario, pide los permisos nuevamente
                requestPermissions(permission, PERMISSION_REQUEST)
                Log.e("PERMISOS", "SE PIDEN LOS PERMISOS NUEVAMENTE")
            }
        }

        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST){
            var allSuccess = true
            for (i in permissions.indices){
                if (grantResults[i] == PackageManager.PERMISSION_DENIED){
                    allSuccess = false
                    var requestAgain = shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain){
                        Toast.makeText(context, getText(R.string.denied_permission), Toast.LENGTH_LONG).show()
                    } else {
                        Log.e("PERMISOS",
                            getText(R.string.denied_permission).toString()
                        )
                        Toast.makeText(context, getText(R.string.permission_denied_need_config), Toast.LENGTH_LONG).show()
                    }
                }
            }
            if (allSuccess){
                showPictureDialog()
            }
        }
    }

    @SuppressLint("WrongConstant")
    fun checkPermission(context: Context, permissionArray: Array<String>): Boolean{
        var allSuccess = true
        for (i in permissionArray.indices){
            if (activity?.checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED){
                allSuccess = false
            }
        }
        return allSuccess
    }

    //Funcion para abrir la galeria y obtener la foto

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {
                //Corresponde al CropImage Activity ---------------------------------
                CropImage.activity(imageUri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setGuidelines(CropImageView.Guidelines.OFF)
                    .setAspectRatio(16, 16)
                    .setAutoZoomEnabled(false)
                    .start(requireContext(), this@MyAccountFragment)
                //------------------------------------------------------------------
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                //Corresponde al CropImage Activity ---------------------------------
                CropImage.activity(data?.data)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setGuidelines(CropImageView.Guidelines.OFF)
                    .setAspectRatio(16, 16)
                    .setAutoZoomEnabled(false)
                    .start(requireContext(), this@MyAccountFragment)
                //------------------------------------------------------------------
            }

        }

        //Corresponde al CropImage Activity ------------------------------------------------------------------
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            try {
                val resultUri = result.uri //Tomamos el resultado de la imagen, lo pasamos a uri
                val bitmap = requireActivity().contentResolver.openInputStream(resultUri)
                val photoBitmap = BitmapFactory.decodeStream(bitmap)
                binding.imageProfile.setImageBitmap(photoBitmap)
                uploadImage(resultUri)
                val bitmap2: Bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, resultUri)
                saveToInternalStorage(bitmap2, requireContext())
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        else {
            return
        }
        //---------------------------------------------------------------------------------------------------
    }

    //El dialogo para seleccionar la imagen
    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(context)
        pictureDialog.setTitle("Selección")
        val pictureDialogItems = arrayOf("Desde Galeria", "Desde Cámara")
        pictureDialog.setItems(pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallary() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) //Instanciamos el intent para tomar del almacenamiento
        intent.type = "image/*" //Los tipos de valores que pude tomar
        requireActivity().startActivityFromFragment(this, intent, GALLERY) //Lanzamos la actividad
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(ACTION_IMAGE_CAPTURE) //Instanciamos el intent de la camara
        val title = "image.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, title)
            put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera")
        }
        imageUri = activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) //Nos pide unos valores que se cargan en Values
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri) //Ponemos la imagen en un extra
        requireActivity().startActivityFromFragment(this, intent, CAMERA) //Lanzamos la actividad
    }

    //Sube la imagen original al servidor
    private fun uploadImage(path: Uri?) {
        val user: String = auth.currentUser?.uid.toString()
        val storage = FirebaseStorage.getInstance().getReference("users")
        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, path) //Obtenemos la imagen de la ubicacion que pasamos anteriormente
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) //Convertimos la imagen a byte
        val data = baos.toByteArray() //La almacenamos
        val imageNoResized = storage.child(user)
        val uploadTask = imageNoResized.putBytes(data)
        //La subimos
        uploadTask.addOnSuccessListener{
            TODO("Buscar la forma de colocar un cartel de carga")
        } .addOnFailureListener {
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
        val name = auth.currentUser?.uid //Le colocamos el nombre del usuario a la imagen en miniatura
        val cw = ContextWrapper(context.applicationContext)
        val directory = cw.getDir(".thumbnails", Context.MODE_PRIVATE) //Asignamos un directorio en los archivos de la app
        Log.d("PATH", directory.path) //Mostramos el directorio en el Logcat
        val mypath = File(directory, "$name.jpg") //Creamos el archivo temporal
        val bitmap = BitmapFactory.decodeFile(mypath.absolutePath) //Colocamos la imagen en el archivo
        if (bitmap != null){
            binding.imageProfile.setImageBitmap(bitmap) //Si la imagen existe, se coloca
        } else {
            pickImgFromServer() //Caso contrario, se saca de la bd
        }
    }

    //Carga la imagen desde el servidor (Su utilidad es para cuando abramos la imagen)
    private fun pickImgFromServer() {
        val user: String = auth.currentUser?.uid.toString()
        val storageRef = FirebaseStorage.getInstance().reference.child("users/$user") //Le asignamos como nombre la uid del usuario
        val localFile = File.createTempFile("tempImage", "jpg") //Creamos un archivo temporal
        //Cargamos la imagen del storage en ese archivo temporal
        storageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath) //Lo convertimos a bitmap
            saveToInternalStorage(bitmap, requireContext()) //Lo guardamos en el almacenamiento interno como thumbnail
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

    private fun newPass(){
        val user = FirebaseAuth.getInstance().currentUser
        val newPassword = binding.pass.text.toString()
        user!!.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User password updated.")
                    Toast.makeText(context, getText(R.string.password_changed), Toast.LENGTH_LONG)
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
        binding.data.text = getText(R.string.back)
        binding.data.backgroundTintList = resources.getColorStateList(R.color.colorAccent)
        binding.logout.backgroundTintList = resources.getColorStateList(R.color.Secondary)
        binding.logout.text = getText(R.string.change_password)
        enabled()
        //Cuando finaliza la tarea, trae de nuevo los datos
        getInfo(uid)
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun back(uid: String){
        binding.ok.animate().translationX(-500f).alpha(0F).setDuration(500).setStartDelay(300).start()
        binding.containerGone.isVisible = false
        binding.data.text = getText(R.string.modify_data)
        binding.data.backgroundTintList = resources.getColorStateList(R.color.Secondary)
        binding.logout.backgroundTintList = resources.getColorStateList(R.color.colorAccent)
        binding.logout.text = getText(R.string.log_out)
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

    private fun updateData(email: String, lastName: String, name: String, pass: String, phone: String){
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