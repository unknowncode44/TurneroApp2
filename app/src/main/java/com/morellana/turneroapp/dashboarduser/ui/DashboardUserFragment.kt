package com.morellana.turneroapp.dashboarduser.ui

import android.annotation.SuppressLint
import android.content.Intent
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
import com.morellana.turneroapp.SearchDoctor
import com.morellana.turneroapp.dashboarduser.adapter.ProfessionalAdapter
import com.morellana.turneroapp.dashboarduser.adapter.OnlineDoctorsAdapter
import com.morellana.turneroapp.dashboarduser.adapter.SpecialtyAdapter
import com.morellana.turneroapp.dashboarduser.dataclass.SpecialtyData
import com.morellana.turneroapp.dashboarduser.model.onlinedoctor.OnlineDoctorViewModel
import com.morellana.turneroapp.dashboarduser.model.professional.ProfessionalViewModel
import com.morellana.turneroapp.dashboarduser.model.specialty.SpecialtyViewModel
import com.morellana.turneroapp.databinding.FragmentDashboardUserBinding
import com.morellana.turneroapp.ui.MakeAppointments
import com.morellana.turneroapp.ui.NewAppointment

class DashboardUserFragment : Fragment() {

    private var _binding: FragmentDashboardUserBinding? = null
    private val binding get() = _binding!!
    lateinit var specialitiesRecyclerView: RecyclerView

    //View Model *Online Doctor*
    private lateinit var onlineDoctorAdapter: OnlineDoctorsAdapter
    private val onlineDoctorViewModel by lazy { ViewModelProviders.of(this).get(
        OnlineDoctorViewModel::class.java) }

    //View Model *Professional Data*
    private lateinit var professionalAdapter: ProfessionalAdapter
    private val professionalViewModel by lazy { ViewModelProviders.of(this). get(
        ProfessionalViewModel::class.java) }

    //View Model *Specialties*
    private lateinit var specialtiesAdapter: SpecialtyAdapter
    private val specialtiesViewModel by lazy { ViewModelProviders.of(this).get(
        SpecialtyViewModel::class.java) }

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

        //Professional Data
        professionalAdapter = ProfessionalAdapter(requireContext())
        binding.professionalDoctorRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.professionalDoctorRecycler.setHasFixedSize(true)
        binding.professionalDoctorRecycler.adapter = professionalAdapter
        observeProfessionalData()

        //Specialties
        specialtiesAdapter = SpecialtyAdapter(requireContext())
        binding.specialtiesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.specialtiesRecyclerView.setHasFixedSize(true)
        binding.specialtiesRecyclerView.adapter = specialtiesAdapter
        observeSpecialtyData()

        //Online Doctor
        onlineDoctorAdapter = OnlineDoctorsAdapter(requireContext())
        binding.onlineDoctorRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.onlineDoctorRecycler.setHasFixedSize(true)
        binding.onlineDoctorRecycler.adapter = onlineDoctorAdapter
        observeOnlineDoctorData()

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

        return binding.root
    }

    @SuppressLint("FragmentLiveDataObserve", "NotifyDataSetChanged")
    private fun observeProfessionalData() {
        binding.shimmerProfessional.startShimmer()
        professionalViewModel.fetchProfessionalData().observe(this, Observer {
            binding.shimmerProfessional.stopShimmer()
            binding.shimmerProfessional.hideShimmer()
            binding.shimmerProfessional.visibility = View.GONE
            professionalAdapter.setListData(it)
            professionalAdapter.notifyDataSetChanged()
        })
    }

    @SuppressLint("FragmentLiveDataObserve", "NotifyDataSetChanged")
    private fun observeSpecialtyData() {
        binding.shimmerSpecialities.startShimmer()
        specialtiesViewModel.fetchSpecialtyData().observe(this, Observer {
            binding.shimmerSpecialities.stopShimmer()
            binding.shimmerSpecialities.hideShimmer()
            binding.shimmerSpecialities.visibility = View.GONE
            specialtiesAdapter.setListData(it)
            specialtiesAdapter.notifyDataSetChanged()
        })
    }

    @SuppressLint("FragmentLiveDataObserve", "NotifyDataSetChanged")
    fun observeOnlineDoctorData(){
        binding.shimmerOnlineDoctor.startShimmer()
        onlineDoctorViewModel.fetchOnlineDoctorData().observe(this, Observer {
            binding.shimmerOnlineDoctor.stopShimmer()
            binding.shimmerOnlineDoctor.hideShimmer()
            binding.shimmerOnlineDoctor.visibility = View.GONE
            onlineDoctorAdapter.setListData(it)
            onlineDoctorAdapter.notifyDataSetChanged()
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
}