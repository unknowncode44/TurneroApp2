package com.morellana.turneroapp.dashboarduser.model.professional

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morellana.turneroapp.dashboarduser.dataclass.ProfessionalData
import com.morellana.turneroapp.dashboarduser.model.professional.ProfessionalRepo

class ProfessionalViewModel: ViewModel() {
    private val professionalRepo = ProfessionalRepo()
    fun fetchProfessionalData(): LiveData<MutableList<ProfessionalData>> {
        val mutableData = MutableLiveData<MutableList<ProfessionalData>>()
        professionalRepo.getProfessionalData().observeForever{ userList ->
            mutableData.value = userList
        }
        return mutableData
    }

}