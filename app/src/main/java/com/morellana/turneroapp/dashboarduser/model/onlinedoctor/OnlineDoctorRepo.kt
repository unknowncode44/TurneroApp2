package com.morellana.turneroapp.dashboarduser.model.onlinedoctor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.morellana.turneroapp.dashboarduser.dataclass.OnlineDoctorData

class OnlineDoctorRepo {
    fun getOnlineDoctorData(): LiveData<MutableList<OnlineDoctorData>> {

        val dataMutable = MutableLiveData<MutableList<OnlineDoctorData>>()

        FirebaseDatabase.getInstance().getReference("online").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listData = mutableListOf<OnlineDoctorData>()
                if (snapshot.exists()) {
                    for (onlineSnapshot in snapshot.children) {
                        val online = onlineSnapshot.getValue(OnlineDoctorData::class.java)
                        listData.add(online!!)
                    }
                }
                dataMutable.value = listData
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return dataMutable
    }
}