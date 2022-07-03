package com.dapper.matrixtestinkotlin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dapper.matrixtestinkotlin.databinding.FragmentOneBinding
import com.dapper.matrixtestinkotlin.utils.Sessions

class One : Fragment() {

    lateinit var binding: FragmentOneBinding
    lateinit var sessions: Sessions
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentOneBinding.inflate(layoutInflater)

        sessions = Sessions(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.latitude.text=sessions.getCurrentLatitude()
        binding.longitude.text=sessions.getCurrentLongitude()

    }

}