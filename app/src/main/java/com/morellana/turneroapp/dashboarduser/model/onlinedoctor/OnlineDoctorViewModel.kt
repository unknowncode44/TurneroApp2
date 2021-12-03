package com.morellana.turneroapp.dashboarduser.model.onlinedoctor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morellana.turneroapp.dashboarduser.dataclass.OnlineDoctorData
import com.morellana.turneroapp.dashboarduser.model.onlinedoctor.OnlineDoctorRepo

class OnlineDoctorViewModel: ViewModel() {
    private val onlineDoctorRepo = OnlineDoctorRepo()
    fun fetchOnlineDoctorData(): LiveData<MutableList<OnlineDoctorData>> {
        val mutableData = MutableLiveData<MutableList<OnlineDoctorData>>()
        onlineDoctorRepo.getOnlineDoctorData().observeForever{ userList ->
            mutableData.value = userList
        }
        return mutableData
    }
}