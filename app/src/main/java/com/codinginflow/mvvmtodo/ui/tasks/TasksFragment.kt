package com.codinginflow.mvvmtodo.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.core.ext.onQueryTextChanged
import com.codinginflow.mvvmtodo.databinding.FragmentTasksBinding
import com.codinginflow.mvvmtodo.ui.tasks.adapter.TasksAdapter
import com.codinginflow.mvvmtodo.ui.tasks.enums.SortOrder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private lateinit var adapter: TasksAdapter
    private val viewModel: TasksViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        val binding = FragmentTasksBinding.bind(view)
        setUp(binding)
        observeViewModel()
    }

    private fun setUp(binding: FragmentTasksBinding) {
        with(binding) {
            adapter = TasksAdapter()
            recyclerView.adapter = adapter
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun observeViewModel() {
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_tasks, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                true
            }

            R.id.action_sort_by_name -> {
                viewModel.sortOrder.value = SortOrder.BY_NAME
                true
            }

            R.id.action_sort_by_date_created -> {
                viewModel.sortOrder.value = SortOrder.BY_CREATED_AT
                true
            }

            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                viewModel.hideCompleted.value = item.isChecked
                true
            }

            R.id.action_delete_all_completed_tasks -> {
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
