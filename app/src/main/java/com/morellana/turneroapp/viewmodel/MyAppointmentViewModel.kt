package com.morellana.turneroapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morellana.turneroapp.dataclass.Appointment
import com.morellana.turneroapp.repo.Repo

class MyAppointmentViewModel: ViewModel() {

    private val repo = Repo()

    fun fetchMyAppointmentData(): LiveData<MutableList<Appointment>>{

        val mutableData = MutableLiveData<MutableList<Appointment>>()
        repo.getAppointmentData().observeForever { userList ->

            mutableData.value = userList

        }

        return mutableData

    }

}