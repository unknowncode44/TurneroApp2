package com.morellana.turneroapp.dashboarduser.model.professional

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.morellana.turneroapp.dashboarduser.dataclass.ProfessionalData

class ProfessionalRepo {
    fun getProfessionalData(): LiveData<MutableList<ProfessionalData>>{
        val dataMutable = MutableLiveData<MutableList<ProfessionalData>>()

        FirebaseDatabase.getInstance().getReference("users/professionals")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listData = mutableListOf<ProfessionalData>()
                    if (snapshot.exists()) {
                        for (professionalSnapshot in snapshot.children) {
                            val professionals =
                                professionalSnapshot.getValue(ProfessionalData::class.java)
                            listData.add(professionals!!)
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