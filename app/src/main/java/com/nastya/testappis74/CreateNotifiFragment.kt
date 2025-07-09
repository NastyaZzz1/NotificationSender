package com.nastya.testappis74

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.nastya.testappis74.databinding.FragmentCreateNotifiBinding
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class CreateNotifiFragment : Fragment() {
    private var _binding: FragmentCreateNotifiBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreateNotifiViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateNotifiBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.sendMesBtn.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_createNotifiFragment_to_startAppFragment)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(NotificationApi::class.java)
        viewModel = ViewModelProvider(this, CreateNotifiViewModelFactory(api))
            .get(CreateNotifiViewModel::class.java)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.sendMesBtn.setOnClickListener {
            val title = binding.titleNotifi.text.toString()
            val message = binding.textNotifi.text.toString()

            if (title.isBlank() || message.isBlank()) {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.sendNotification(title, message)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.notificationState.collect { state ->
                when (state) {
                    is CreateNotifiViewModel.NotificationState.Idle -> {

                    }
                    is CreateNotifiViewModel.NotificationState.Loading -> {
                        binding.sendMesBtn.isEnabled = false
                    }
                    is CreateNotifiViewModel.NotificationState.Success -> {
                        binding.sendMesBtn.isEnabled = true
                        Toast.makeText(
                            context,
                            "Уведомление отправлено!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is CreateNotifiViewModel.NotificationState.Error -> {
                        binding.sendMesBtn.isEnabled = true
                        Toast.makeText(
                            context,
                            state.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}