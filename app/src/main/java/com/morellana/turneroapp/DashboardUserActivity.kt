package com.morellana.turneroapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.morellana.turneroapp.adapters.DoctorCardAdapter
import com.morellana.turneroapp.adapters.OnlineDoctorsAdapter
import com.morellana.turneroapp.adapters.SpecialityCardAdapter
import com.morellana.turneroapp.databinding.ActivityDashboardUserBinding
import com.morellana.turneroapp.dataclass.OnlineDoctor
import com.morellana.turneroapp.dataclass.Profesional
import com.morellana.turneroapp.dataclass.Speciality

class DashboardUserActivity : AppCompatActivity(){
    lateinit var dbRef : DatabaseReference
    lateinit var doctorCardsRecyclerView: RecyclerView
    lateinit var specialitiesRecyclerView: RecyclerView
    lateinit var availableRecyclerView: RecyclerView
    lateinit var professionalArrayList: ArrayList<Profesional>
    lateinit var specialitiesArrayList: ArrayList<Speciality>
    lateinit var onlineArrayList: ArrayList<OnlineDoctor>
    lateinit var adapter: DoctorCardAdapter
    private lateinit var binding: ActivityDashboardUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar()

        val ab: ActionBar? = supportActionBar
        if(ab != null){
            ab.setHomeAsUpIndicator(R.drawable.nav_icon)
            ab.setDisplayHomeAsUpEnabled(true)
            ab.title = "Nombre de Usuario"
        }

        //doctorCardsRecyclerView = findViewById(R.id.recycler_dr_cards)
        //doctorCardsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        //doctorCardsRecyclerView.setHasFixedSize(true)

        //specialitiesRecyclerView = findViewById(R.id.specialitiesRecyclerView)
        //specialitiesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        //specialitiesRecyclerView.setHasFixedSize(true)

        //availableRecyclerView = findViewById(R.id.available_doctor_recycler)
        //availableRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        //specialitiesRecyclerView.setHasFixedSize(true)


        //specialitiesArrayList = arrayListOf<Speciality>()
        //professionalArrayList = arrayListOf<Profesional>()
        // onlineArrayList = arrayListOf<OnlineDoctor>()

        //getProfesionalData()
        //getSpecialityData()
        //getAvailableDoctor()


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
                }
            }
            override fun onCancelled(error: DatabaseError) {

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
                        if (online != null) {
                            onlineArrayList.add(online)
                        }
                    }
                    availableRecyclerView.adapter = OnlineDoctorsAdapter(onlineArrayList)
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}