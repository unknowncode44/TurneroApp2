package com.morellana.turneroapp.dataclass

import android.text.Layout

data class Appointment(
    var professional: String? = null,
    var speciality: String? = null,
    var date: String? = null,
    var time: String? = null,
    var place: String? = null,
    var visibility: Boolean = false
)
