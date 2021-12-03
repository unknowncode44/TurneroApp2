package com.morellana.turneroapp.myappointment.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.morellana.turneroapp.myappointment.dataclass.AppointmentData

class MyAppointmentRepo {

    fun getAppointmentData(): MutableLiveData<MutableList<AppointmentData>> {

        val dataMutable = MutableLiveData<MutableList<AppointmentData>>()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseDatabase.getInstance().getReference("users/users/$uid/appointments")
            .addValueEventListener(object : ValueEventListener {
                val listData = mutableListOf<AppointmentData>()
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){

                        for (appointmentSnapshot in snapshot.children){
                            val appointments = appointmentSnapshot.getValue(AppointmentData::class.java)

                            listData.add(appointments!!)
                        }
                    }
                    dataMutable.value = listData
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e ("ERROR", "Something went wrong")
                }

            })
        return dataMutable
    }
}