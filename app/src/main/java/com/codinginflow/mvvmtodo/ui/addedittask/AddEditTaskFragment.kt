package com.codinginflow.mvvmtodo.ui.addedittask

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.databinding.FragmentAddEditTaskBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) {

    private val viewModel: AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddEditTaskBinding.bind(view)
        observeViewModel(binding)
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
    }
}
