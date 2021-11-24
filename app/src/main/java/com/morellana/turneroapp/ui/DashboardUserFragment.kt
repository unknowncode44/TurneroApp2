package com.morellana.turneroapp.ui

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.morellana.turneroapp.R
import com.morellana.turneroapp.SearchDoctor
import com.morellana.turneroapp.adapters.DoctorCardAdapter
import com.morellana.turneroapp.adapters.OnlineDoctorsAdapter
import com.morellana.turneroapp.adapters.SpecialityCardAdapter
import com.morellana.turneroapp.dataclass.OnlineDoctor
import com.morellana.turneroapp.dataclass.Profesional
import com.morellana.turneroapp.dataclass.Speciality
import com.morellana.turneroapp.databinding.FragmentDashboardUserBinding

class DashboardUserFragment : Fragment() {

    private var _binding: FragmentDashboardUserBinding? = null
    private val binding get() = _binding!!
    lateinit var dbRef : DatabaseReference
    lateinit var doctorCardsRecyclerView: RecyclerView
    lateinit var specialitiesRecyclerView: RecyclerView
    lateinit var availableRecyclerView: RecyclerView
    lateinit var professionalArrayList: ArrayList<Profesional>
    lateinit var specialitiesArrayList: ArrayList<Speciality>
    lateinit var onlineArrayList: ArrayList<OnlineDoctor>
    lateinit var adapter: DoctorCardAdapter

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
        _binding = FragmentDashboardUserBinding.inflate(inflater, container, false)

        //Comientza el efecto del shimmer
        binding.shimmerRecyclerDrCards.startShimmer()
        binding.shimmerAvailableDoctor.startShimmer()
        binding.shimmerSpecialities.startShimmer()

        binding.search.setOnClickListener {
            val searchIntent = Intent(context, SearchDoctor::class.java)
            startActivity(searchIntent)
        }

        binding.add.setOnClickListener {
            fragSelect(NewAppointment())
        }

        binding.manage.setOnClickListener {
            fragSelect(MakeAppointments())
        }

        doctorCardsRecyclerView = binding.recyclerDrCards
        doctorCardsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        doctorCardsRecyclerView.setHasFixedSize(true)

        specialitiesRecyclerView = binding.specialitiesRecyclerView
        specialitiesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        specialitiesRecyclerView.setHasFixedSize(true)

        availableRecyclerView = binding.availableDoctorRecycler
        availableRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        specialitiesRecyclerView.setHasFixedSize(true)


        specialitiesArrayList = arrayListOf<Speciality>()
        professionalArrayList = arrayListOf<Profesional>()
        onlineArrayList = arrayListOf<OnlineDoctor>()


        getProfesionalData()
        getSpecialityData()
        getAvailableDoctor()

        return binding.root
    }

    private fun getProfesionalData() {
        dbRef = FirebaseDatabase.getInstance().getReference("users/professionals")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (professionalSnapshot in snapshot.children){
                        val professionals = professionalSnapshot.getValue(Profesional::class.java)
                        professionalArrayList.add(professionals!!)
                    }
                    doctorCardsRecyclerView.adapter = DoctorCardAdapter(professionalArrayList)
                    binding.shimmerRecyclerDrCards.stopShimmer()
                    binding.shimmerRecyclerDrCards.visibility = View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
    private fun getSpecialityData() {
        dbRef = FirebaseDatabase.getInstance().getReference("specialities")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (specialitySnapshot in snapshot.children){
                        val specialities = specialitySnapshot.getValue(Speciality::class.java)
                        specialitiesArrayList.add(specialities!!)
                    }
                    specialitiesRecyclerView.adapter = SpecialityCardAdapter(specialitiesArrayList)
                    binding.shimmerSpecialities.stopShimmer()
                    binding.shimmerSpecialities.visibility = View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun getAvailableDoctor() {
        dbRef = FirebaseDatabase.getInstance().getReference("online")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (onlineSnapshot in snapshot.children){
                        val online = onlineSnapshot.getValue(OnlineDoctor::class.java)
                        onlineArrayList.add(online!!)
                    }
                    availableRecyclerView.adapter = OnlineDoctorsAdapter(onlineArrayList)
                    binding.shimmerAvailableDoctor.stopShimmer()
                    binding.shimmerAvailableDoctor.visibility = View.GONE

                }

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //agregando la extencion addToBackStack(null), al presionar back
    //vuelve al fragmento anterior
    private fun fragSelect(frag: Fragment) {
        val id: Int = (R.id.frag)
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(id, frag)?.addToBackStack(null)
        transaction?.commit()
    }

    override fun onPause() {



        super.onPause()
    }
}
