package com.morellana.turneroapp.newappointment.dataclass

data class NewAppointmentProfessionalData(var name: String = "", var id: String= "", var atteDays: AtteDays = AtteDays(
    vie = false,
    lun = false,
    sab = false,
    dom = false,
    jue = false,
    mar = false,
    mie = false))
