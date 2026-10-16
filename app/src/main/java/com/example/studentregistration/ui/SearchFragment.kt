package com.example.studentregistration.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.studentregistration.R
import com.example.studentregistration.databinding.FragmentSearchBinding
import com.example.studentregistration.viewmodel.SearchState
import com.example.studentregistration.viewmodel.StudentViewModel

/** STUDENT SEARCH PAGE - find a student by ID; shows name + course or an error message. */
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSearch.setOnClickListener { search() }
        binding.etStudentId.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search()
                true
            } else false
        }

        viewModel.searchState.observe(viewLifecycleOwner) { render(it) }

        // Data received from the Registration page (-1 means "opened from Home, nothing passed").
        val passedId = arguments?.getInt(ARG_STUDENT_ID, -1) ?: -1
        if (passedId >= 0 && savedInstanceState == null) {
            binding.etStudentId.setText(passedId.toString())
            search()
        }
    }

    private fun search() = viewModel.searchStudent(binding.etStudentId.text.toString())

    private fun render(state: SearchState) {
        binding.tilStudentId.error = null
        binding.cardResult.visibility = View.GONE
        binding.tvError.visibility = View.GONE

        when (state) {
            is SearchState.Found -> {
                val s = state.student
                binding.tvResultId.text = getString(R.string.label_id, s.studentId)
                binding.tvResultName.text = getString(R.string.label_name, s.name)
                binding.tvResultCourse.text = getString(R.string.label_course, s.course)
                binding.tvResultYear.text = getString(R.string.label_year, s.year)
                binding.cardResult.visibility = View.VISIBLE
            }
            is SearchState.NotFound -> {
                binding.tvError.text = getString(R.string.error_not_found, state.id)
                binding.tvError.visibility = View.VISIBLE
            }
            is SearchState.InvalidInput -> {
                binding.tilStudentId.error = getString(R.string.error_id_invalid)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /** Must match the argument name declared in nav_graph.xml. */
        const val ARG_STUDENT_ID = "studentId"
    }
}
