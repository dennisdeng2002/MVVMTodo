package com.codinginflow.mvvmtodo.ui.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.core.constants.ADD_EDIT_REQUEST_KEY
import com.codinginflow.mvvmtodo.core.constants.ADD_EDIT_RESULT_KEY
import com.codinginflow.mvvmtodo.databinding.FragmentAddEditTaskBinding
import com.codinginflow.mvvmtodo.ui.addedittask.AddEditTaskViewModel.Event.NavigateBackWithResult
import com.codinginflow.mvvmtodo.ui.addedittask.AddEditTaskViewModel.Event.ShowInvalidInputMessage
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.DateFormat

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) {

    private val viewModel: AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddEditTaskBinding.bind(view)
        setUp(binding)
        observeViewModel(binding)
    }

    private fun setUp(binding: FragmentAddEditTaskBinding) {
        with(binding) {
            editTextTaskName.addTextChangedListener {
                viewModel.setName(it.toString())
            }

            checkBoxImportant.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setImportant(isChecked)
            }

            fabSaveTask.setOnClickListener {
                viewModel.onSaveClicked()
            }
        }
    }

    private fun observeViewModel(binding: FragmentAddEditTaskBinding) {
        viewModel.task.observe(viewLifecycleOwner) { task ->
            with(binding) {
                editTextTaskName.setText(task.name)
                checkBoxImportant.isChecked = task.important
                textViewDateCreated.text = getString(
                    R.string.created_template,
                    DateFormat.getDateInstance().format(task.createdAt)
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collect(::handleEvent)
        }
    }

    private fun handleEvent(event: AddEditTaskViewModel.Event) {
        when (event) {
            is NavigateBackWithResult -> {
                setFragmentResult(
                    ADD_EDIT_REQUEST_KEY,
                    bundleOf(ADD_EDIT_RESULT_KEY to event.result)
                )

                findNavController().popBackStack()
            }

            is ShowInvalidInputMessage -> {
                Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
