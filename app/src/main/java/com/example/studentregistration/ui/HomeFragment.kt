package com.example.studentregistration.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.studentregistration.R
import com.example.studentregistration.databinding.FragmentHomeBinding
import com.example.studentregistration.viewmodel.StudentViewModel

/** HOME PAGE - navigates to the Registration and Search pages. */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddStudent.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_registrationFragment)
        }
        binding.btnFindStudent.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        // LiveData from Room: the label updates by itself whenever a student is added.
        viewModel.studentCount.observe(viewLifecycleOwner) { count ->
            binding.tvCount.text = getString(R.string.registered_count, count)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
