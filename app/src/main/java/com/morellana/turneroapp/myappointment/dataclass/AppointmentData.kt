package com.morellana.turneroapp.myappointment.dataclass

import android.text.Layout

data class AppointmentData(
    var professional: String? = null,
    var speciality: String? = null,
    var date: String? = null,
    var time: String? = null,
    var place: String? = null,
    var visibility: Boolean = false
)
