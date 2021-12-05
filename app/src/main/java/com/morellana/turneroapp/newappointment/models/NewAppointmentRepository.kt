package com.morellana.turneroapp.newappointment.models

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.morellana.turneroapp.dashboarduser.dataclass.ProfessionalData
import com.morellana.turneroapp.newappointment.dataclass.NewAppointmentProfessionalData
import com.morellana.turneroapp.newappointment.dataclass.NewAppointmentSpecialtyData

class NewAppointmentRepository {
    var professionalData: ArrayList<NewAppointmentProfessionalData> = arrayListOf()

    fun getSpecialityData(): MutableLiveData<MutableList<String>> {
        val specialityDataMutable = MutableLiveData<MutableList<String>>()
        FirebaseDatabase.getInstance().getReference("specialities")
        .addValueEventListener(object: ValueEventListener {
            val specialityArrayName = mutableListOf<String>()
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (specialitySnapshot in snapshot.children){
                        val specialities = specialitySnapshot.getValue(NewAppointmentSpecialtyData::class.java)
                        specialityArrayName.add(specialities!!.name!!)
                    }
                }
                specialityDataMutable.value = specialityArrayName
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e ("ERROR", "Something went wrong")
            }
        })
        return specialityDataMutable
    }

    fun getProfessionalUid(speciality: String): MutableLiveData<MutableList<NewAppointmentProfessionalData>> {
        val professionalsInfoMutable = MutableLiveData<MutableList<NewAppointmentProfessionalData>>()
        val professionalsDataMutable = MutableLiveData<MutableList<String>>()
        val path = "specialities/$speciality/professionals" // creamos el path
        FirebaseDatabase.getInstance().getReference(path)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) { //hacemos snapshot a los profesionales de esa especialidad
                    if (snapshot.exists()){
                        for (uidSnapshot in snapshot.children){ // en este bucle:
                            val uid = uidSnapshot.getValue<String>() // obtendremos los valores uid dentro del nodo professionals
                            FirebaseDatabase.getInstance().getReference("users/professionals/$uid") // los buscaremos en el nodo user/professionals
                            .addValueEventListener(object: ValueEventListener {
                                val professionalInfoArray: ArrayList<NewAppointmentProfessionalData> = arrayListOf()
                                val professionalArrayName: ArrayList<String> = arrayListOf()
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    professionalInfoArray.clear()
                                    professionalArrayName.clear()
                                    if (snapshot.exists()){
                                        val professional: NewAppointmentProfessionalData? = snapshot.getValue(
                                            NewAppointmentProfessionalData::class.java)
                                        professionalInfoArray.add(professional!!)
                                        val name = professional.name.toString() // y obtendremos su nombre
                                        professionalArrayName.add(name) // lo anadiremos a este array
                                    }
                                    professionalsInfoMutable.value = professionalInfoArray
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })
                        }
//                        Log.d("SPECIALITY2", professionalArrayName.toString())

                    }

                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
            return professionalsInfoMutable
    }

    fun getProfessionalInfo(uid: String): MutableLiveData<MutableList<NewAppointmentProfessionalData>>{
        val professionalsInfoMutable = MutableLiveData<MutableList<NewAppointmentProfessionalData>>()
        FirebaseDatabase.getInstance().getReference("users/professionals/$uid")
            .addValueEventListener(object: ValueEventListener {
                val professionalArrayInfo: ArrayList<NewAppointmentProfessionalData> = arrayListOf()
                override fun onDataChange(snapshot: DataSnapshot) {
                    professionalData.clear()
                    if (snapshot.exists()){
                        val professionalInfo: NewAppointmentProfessionalData?  = snapshot.getValue(
                            NewAppointmentProfessionalData::class.java)
                        professionalData.add(professionalInfo!!)
                    }
                    professionalsInfoMutable.value = professionalArrayInfo
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        return professionalsInfoMutable
    }

}
