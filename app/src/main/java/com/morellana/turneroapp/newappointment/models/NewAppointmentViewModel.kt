package com.morellana.turneroapp.newappointment.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morellana.turneroapp.newappointment.dataclass.NewAppointmentProfessionalData

class NewAppointmentViewModel: ViewModel() {
    private val newAppointmentRepository = NewAppointmentRepository()

    fun fetchAppointmentSpeciality(): LiveData<MutableList<String>> {
        val mutableData = MutableLiveData<MutableList<String>>()
        newAppointmentRepository.getSpecialityData().observeForever {specialityList ->
            mutableData.value = specialityList
        }
        return mutableData
    }

    fun fetchAppointmentProfessionals(speciality: String): LiveData<MutableList<NewAppointmentProfessionalData>> {
        val mutableData = MutableLiveData<MutableList<NewAppointmentProfessionalData>>()
        newAppointmentRepository.getProfessionalUid(speciality).observeForever {professionalsList ->
            mutableData.value = professionalsList
        }
        return mutableData
    }
}