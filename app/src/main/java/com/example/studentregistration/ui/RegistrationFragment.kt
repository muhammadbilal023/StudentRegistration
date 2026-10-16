package com.example.studentregistration.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.studentregistration.R
import com.example.studentregistration.databinding.FragmentRegistrationBinding
import com.example.studentregistration.viewmodel.RegisterState
import com.example.studentregistration.viewmodel.StudentViewModel

/** REGISTRATION PAGE - form (name, course, year) that stores a student in Room. */
class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            viewModel.register(
                name = binding.etName.text.toString(),
                course = binding.etCourse.text.toString(),
                yearText = binding.etYear.text.toString()
            )
        }

        viewModel.registerState.observe(viewLifecycleOwner) { render(it) }
    }

    private fun render(state: RegisterState) {
        // Clear old errors first so only the current ones are displayed.
        binding.tilName.error = null
        binding.tilCourse.error = null
        binding.tilYear.error = null

        when (state) {
            is RegisterState.Invalid -> {
                binding.tilName.error = state.nameError
                binding.tilCourse.error = state.courseError
                binding.tilYear.error = state.yearError // e.g. "Year must be a whole number from 1 to 4"
                binding.tvResult.visibility = View.GONE
                binding.btnSearchThis.visibility = View.GONE
            }
            is RegisterState.Success -> {
                binding.etName.text?.clear()
                binding.etCourse.text?.clear()
                binding.etYear.text?.clear()
                binding.tvResult.text = getString(R.string.reg_success, state.studentId)
                binding.tvResult.visibility = View.VISIBLE
                binding.btnSearchThis.visibility = View.VISIBLE

                // Navigate WITH DATA: pass the new student's ID to the Search page in a Bundle.
                binding.btnSearchThis.setOnClickListener {
                    findNavController().navigate(
                        R.id.action_registrationFragment_to_searchFragment,
                        bundleOf(SearchFragment.ARG_STUDENT_ID to state.studentId)
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
