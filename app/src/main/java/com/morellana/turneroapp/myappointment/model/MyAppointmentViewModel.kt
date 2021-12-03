package com.morellana.turneroapp.myappointment.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morellana.turneroapp.myappointment.dataclass.AppointmentData

class MyAppointmentViewModel: ViewModel() {

    private val repo = MyAppointmentRepo()

    fun fetchAppointmentData(): LiveData<MutableList<AppointmentData>> {

        val mutableData = MutableLiveData<MutableList<AppointmentData>>()

        repo.getAppointmentData().observeForever { appointmentList ->

            mutableData.value = appointmentList

        }

        return mutableData

    }

}