package com.morellana.turneroapp.dashboarduser.model.specialty

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.morellana.turneroapp.dashboarduser.adapter.SpecialtyAdapter
import com.morellana.turneroapp.dashboarduser.dataclass.SpecialtyData

class SpecialtyRepo {
    fun getSpecialtyData(): LiveData<MutableList<SpecialtyData>>{
        val dataMutable = MutableLiveData<MutableList<SpecialtyData>>()
        FirebaseDatabase.getInstance().getReference("specialities")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listData = mutableListOf<SpecialtyData>()
                    if (snapshot.exists()){
                        for (specialitySnapshot in snapshot.children){
                            val specialities = specialitySnapshot.getValue(SpecialtyData::class.java)
                            listData.add(specialities!!)
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