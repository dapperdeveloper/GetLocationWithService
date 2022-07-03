package com.dapper.matrixtestinkotlin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.dapper.matrixtestinkotlin.R
import com.dapper.matrixtestinkotlin.databinding.FragmentHomeBinding
import com.dapper.matrixtestinkotlin.utils.Sessions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class Home : Fragment() {

    lateinit var binding: FragmentHomeBinding
    var fragment: Fragment? = null
    lateinit var sessions: Sessions
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        sessions = Sessions(context)
        fragment = One()
        replaceFragment(fragment as One)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        fragment = One()
                    }
                    1 -> {
                        fragment = Two()
                    }
                    2 -> {
                        fragment = Three()
                    }
                }

                val fm = activity!!.supportFragmentManager
                val ft = fm.beginTransaction()
                fragment?.let { ft.replace(R.id.frameLayout, it) }
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ft.commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }

    private fun replaceFragment(fragment: Fragment){
        if (fragment != null){
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.commit()
        }
    }


}