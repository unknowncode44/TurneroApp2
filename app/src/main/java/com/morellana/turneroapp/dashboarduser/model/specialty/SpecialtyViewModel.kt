package com.morellana.turneroapp.dashboarduser.model.specialty

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morellana.turneroapp.dashboarduser.dataclass.SpecialtyData

class SpecialtyViewModel: ViewModel() {
    private val specialtyRepo = SpecialtyRepo()
    fun fetchSpecialtyData(): LiveData<MutableList<SpecialtyData>>{
        val mutableData = MutableLiveData<MutableList<SpecialtyData>>()
        specialtyRepo.getSpecialtyData().observeForever { userList ->
            mutableData.value = userList
        }
        return mutableData
    }
}