package com.dapper.matrixtestinkotlin.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dapper.matrixtestinkotlin.R
import com.dapper.matrixtestinkotlin.databinding.FragmentThreeBinding
import com.dapper.matrixtestinkotlin.utils.Sessions


class Three : Fragment() {

    lateinit var binding: FragmentThreeBinding
    lateinit var sessions: Sessions
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentThreeBinding.inflate(layoutInflater)

        sessions = Sessions(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.latitude.text=sessions.getCurrentLatitude()
        binding.longitude.text=sessions.getCurrentLongitude()

    }

}