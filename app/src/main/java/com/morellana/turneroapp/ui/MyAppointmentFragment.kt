package com.morellana.turneroapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.alpha
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.morellana.turneroapp.R
import com.morellana.turneroapp.adapters.DoctorCardAdapter
import com.morellana.turneroapp.adapters.MyAppointmentsAdapter
import com.morellana.turneroapp.dataclass.Appointment
import com.morellana.turneroapp.dataclass.Profesional

class MyAppointmentFragment : Fragment() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var appointmentArrayList: ArrayList<Appointment>
    private lateinit var appointmentRecyclerView: RecyclerView
    private lateinit var currentUserUid: String
    private lateinit var mShimmerFrameLayout: ShimmerFrameLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view: View = inflater.inflate(R.layout.fragment_my_appointment, container, false)

        mShimmerFrameLayout = view.findViewById(R.id.shimmerViewContainer)
        mShimmerFrameLayout.startShimmerAnimation()

        auth = FirebaseAuth.getInstance()
        currentUserUid = auth.currentUser!!.uid
        appointmentRecyclerView = view.findViewById(R.id.myAppointments)
        appointmentRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        appointmentRecyclerView.setHasFixedSize(true)
        appointmentArrayList = arrayListOf<Appointment>()


        getAppointmentData(currentUserUid)


        return view
    }
    override fun onResume(){
        super.onResume();
        mShimmerFrameLayout.startShimmerAnimation()
    }
    override fun onPause(){
        mShimmerFrameLayout.stopShimmerAnimation()
        super.onPause()

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
                    mShimmerFrameLayout.stopShimmerAnimation();

                    appointmentRecyclerView.adapter = MyAppointmentsAdapter(appointmentArrayList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}