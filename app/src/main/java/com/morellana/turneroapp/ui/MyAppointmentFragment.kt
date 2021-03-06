package com.morellana.turneroapp.ui

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.graphics.alpha
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.morellana.turneroapp.R
import com.morellana.turneroapp.adapters.DoctorCardAdapter
import com.morellana.turneroapp.adapters.MyAppointmentsAdapter
import com.morellana.turneroapp.databinding.FragmentMyAppointmentBinding
import com.morellana.turneroapp.dataclass.Appointment
import com.morellana.turneroapp.dataclass.Profesional

class MyAppointmentFragment : Fragment() {

    //Binding
    private var _binding: FragmentMyAppointmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var appointmentArrayList: ArrayList<Appointment>
    private lateinit var appointmentRecyclerView: RecyclerView
    private lateinit var currentUserUid: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //lo que hacemos es animar el inflar y el desinflar
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_rigth)
        exitTransition = inflater.inflateTransition(R.transition.slide_left)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMyAppointmentBinding.inflate(inflater, container, false)

        val shimmer = binding.shimmerViewContainer

//        binding.shimmerViewContainer.startShimmer()

        auth = FirebaseAuth.getInstance()
        currentUserUid = auth.currentUser!!.uid
        appointmentRecyclerView = binding.myAppointments
        appointmentRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        appointmentRecyclerView.setHasFixedSize(true)
        appointmentArrayList = arrayListOf<Appointment>()


        getAppointmentData(currentUserUid)



        return binding.root
    }
    private fun getAppointmentData(uid: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("users/users/$uid/appointments")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    for (appointmentSnapshot in snapshot.children){
                        val appointments = appointmentSnapshot.getValue(Appointment::class.java)

                        appointmentArrayList.add(appointments!!)
                    }
//                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE

                    appointmentRecyclerView.adapter = MyAppointmentsAdapter(appointmentArrayList)
                }

                //Si no existe ningun snapshot, muestra el mensaje
                if (appointmentRecyclerView.adapter?.itemCount == 0){
                    Toast.makeText(context, "No hay turnos disponibles", Toast.LENGTH_SHORT).show()
//                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE
                    binding.nullAppointment.visibility = View.VISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}