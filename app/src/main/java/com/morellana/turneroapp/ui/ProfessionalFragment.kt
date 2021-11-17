package com.morellana.turneroapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.morellana.turneroapp.R
import com.morellana.turneroapp.adapters.DoctorCardAdapter
import com.morellana.turneroapp.dataclass.Profesional

class ProfessionalFragment : Fragment() {

    private lateinit var dbRef : DatabaseReference

    lateinit var professionalArrayList: ArrayList<Profesional>

    lateinit var doctorCardsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_professional, container, false)

        doctorCardsRecyclerView = view.findViewById(R.id.recycler_dr_cards)

        professionalArrayList = arrayListOf<Profesional>()

        getProfesionalData()

        return view
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
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}