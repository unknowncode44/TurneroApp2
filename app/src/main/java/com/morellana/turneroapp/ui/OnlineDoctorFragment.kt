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
import com.morellana.turneroapp.adapters.OnlineDoctorsAdapter
import com.morellana.turneroapp.dataclass.OnlineDoctor

class OnlineDoctorFragment : Fragment() {
    private lateinit var dbRef : DatabaseReference

    lateinit var availableRecyclerView: RecyclerView

    lateinit var onlineArrayList: ArrayList<OnlineDoctor>

    lateinit var adapter: DoctorCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_online_doctor, container, false)



        getAvailableDoctor(view)

        return view
    }

    private fun getAvailableDoctor(view: View) {
        availableRecyclerView = view.findViewById(R.id.available_doctor_recycler)
        onlineArrayList = arrayListOf<OnlineDoctor>()
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