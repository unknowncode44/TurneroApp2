package com.morellana.turneroapp.myappointment.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.morellana.turneroapp.R
import com.morellana.turneroapp.databinding.FragmentMyAppointmentBinding
import com.morellana.turneroapp.myappointment.model.MyAppointmentViewModel
import com.morellana.turneroapp.myappointment.adapter.MyAppointmentsAdapter
import com.morellana.turneroapp.myappointment.dataclass.AppointmentData

class MyAppointmentFragment : Fragment() {

    //Binding
    private var _binding: FragmentMyAppointmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbRef: DatabaseReference
    private lateinit var appointmentDataArrayList: ArrayList<AppointmentData>
    private lateinit var appointmentRecyclerView: RecyclerView
    private lateinit var currentUserUid: String

    //View Model
    private lateinit var adapter: MyAppointmentsAdapter
    private val viewModel by lazy { ViewModelProviders.of(this).get(MyAppointmentViewModel::class.java) }


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

        adapter = MyAppointmentsAdapter(requireContext())
        binding.myAppointments.layoutManager = LinearLayoutManager(context)
        binding.myAppointments.adapter = adapter

        observeData()

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "FragmentLiveDataObserve")
    fun observeData(){
        binding.shimmerViewContainer.startShimmer()
        viewModel.fetchAppointmentData().observe(this, Observer {
            binding.shimmerViewContainer.stopShimmer()
            binding.shimmerViewContainer.hideShimmer()
            binding.shimmerViewContainer.visibility = View.GONE
            adapter.setListData(it)
            adapter.notifyDataSetChanged()
        })
    }

    //Funcionalidad descartada; se aplica en la clase "Repo" de esta actividad

//    private fun getAppointmentData(uid: String) {
//        dbRef = FirebaseDatabase.getInstance().getReference("users/users/$uid/appointments")
//        dbRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()){
//
//                    for (appointmentSnapshot in snapshot.children){
//                        val appointments = appointmentSnapshot.getValue(AppointmentData::class.java)
//
//                        appointmentDataArrayList.add(appointments!!)
//                    }
////                    binding.shimmerViewContainer.stopShimmer()
//                    binding.shimmerViewContainer.visibility = View.GONE
//
//                    appointmentRecyclerView.adapter = MyAppointmentsAdapter(appointmentDataArrayList)
//                }
//
//                //Si no existe ningun snapshot, muestra el mensaje
//                if (appointmentRecyclerView.adapter?.itemCount == 0){
//                    Toast.makeText(context, "No hay turnos disponibles", Toast.LENGTH_SHORT).show()
////                    binding.shimmerViewContainer.stopShimmer()
//                    binding.shimmerViewContainer.visibility = View.GONE
//                    binding.nullAppointment.visibility = View.VISIBLE
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
//    }

}