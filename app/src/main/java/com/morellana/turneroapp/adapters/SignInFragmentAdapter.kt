package com.morellana.turneroapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.morellana.turneroapp.TabLoginFragment
import com.morellana.turneroapp.TabRegisterFragment

class SignInFragmentAdapter(fragmentManager: FragmentManager?, lifecycle: Lifecycle) :
    FragmentStateAdapter(
        fragmentManager!!, lifecycle
    ) {
    override fun createFragment(position: Int): Fragment {
//        creamos un switch para pasar entre tabs
        return if (position == 1) {
            TabRegisterFragment()
        } else TabLoginFragment()
    }

    override fun getItemCount(): Int {
        return 2
    }
}