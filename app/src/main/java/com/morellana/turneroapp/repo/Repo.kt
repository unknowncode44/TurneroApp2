package com.morellana.turneroapp.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.morellana.turneroapp.dataclass.Appointment

class Repo {

    fun getAppointmentData(): LiveData<MutableList<Appointment>> {

        val dataMutable = MutableLiveData<MutableList<Appointment>>()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseDatabase.getInstance().getReference("users/users/$uid/appointments").addValueEventListener (object : ValueEventListener {

            val listData = mutableListOf<Appointment>()

            override fun onDataChange(snapshot: DataSnapshot) {
               if (snapshot.exists()){
                   for (appointmentSnapshot in snapshot.children){

                       val appointments = appointmentSnapshot.getValue(Appointment::class.java)

                       listData.add(appointments!!)

                   }
                   dataMutable.value = listData
               }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        return dataMutable

    }
}