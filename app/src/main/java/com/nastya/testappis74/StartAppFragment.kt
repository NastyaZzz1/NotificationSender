package com.nastya.testappis74

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.nastya.testappis74.databinding.FragmentStartAppBinding


class StartAppFragment : Fragment() {
    private var _binding: FragmentStartAppBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartAppBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.sendMesBtn.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_startAppFragment_to_createNotifiFragment)
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}