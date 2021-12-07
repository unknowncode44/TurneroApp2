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
import com.morellana.turneroapp.R
import com.morellana.turneroapp.databinding.FragmentMyAppointmentBinding
import com.morellana.turneroapp.myappointment.model.MyAppointmentViewModel
import com.morellana.turneroapp.myappointment.adapter.MyAppointmentsAdapter
import com.morellana.turneroapp.newappointment.ui.NewAppointment

class MyAppointmentFragment : Fragment() {

    //Binding
    private var _binding: FragmentMyAppointmentBinding? = null
    private val binding get() = _binding!!

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
    ): View {

        _binding = FragmentMyAppointmentBinding.inflate(inflater, container, false)

        adapter = MyAppointmentsAdapter(requireContext())
        binding.myAppointments.layoutManager = LinearLayoutManager(context)
        binding.myAppointments.adapter = adapter

        observeData()

        binding.newAppontment.setOnClickListener {
            val id: Int = (R.id.frag)
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(id, NewAppointment())?.addToBackStack(null)
            transaction?.commit()
        }

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "FragmentLiveDataObserve")
    fun observeData(){
        binding.nullAppointment.visibility = View.GONE
        binding.shimmerViewContainer.startShimmer()
        viewModel.fetchAppointmentData().observe(this, Observer {
            binding.shimmerViewContainer.stopShimmer()
            binding.shimmerViewContainer.hideShimmer()
            binding.shimmerViewContainer.visibility = View.GONE
            adapter.setListData(it)
            adapter.notifyDataSetChanged()
            if (adapter.itemCount == 0){
                binding.nullAppointment.visibility = View.VISIBLE
                binding.myAppointments.visibility = View.INVISIBLE
            }
        })

    }
}