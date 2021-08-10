package com.codinginflow.mvvmtodo.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.core.ext.onQueryTextChanged
import com.codinginflow.mvvmtodo.data.preferences.SortOrder
import com.codinginflow.mvvmtodo.data.task.Task
import com.codinginflow.mvvmtodo.databinding.FragmentTasksBinding
import com.codinginflow.mvvmtodo.ui.tasks.TasksViewModel.*
import com.codinginflow.mvvmtodo.ui.tasks.TasksViewModel.Event.*
import com.codinginflow.mvvmtodo.ui.tasks.adapter.TasksAdapter
import com.codinginflow.mvvmtodo.ui.tasks.adapter.TasksAdapter.OnItemClickListener
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), OnItemClickListener {

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
            adapter = TasksAdapter(this@TasksFragment)
            recyclerView.adapter = adapter
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )

            val itemTouchHelper = ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(
                    0,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                ) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean = false

                    override fun onSwiped(
                        viewHolder: RecyclerView.ViewHolder,
                        direction: Int
                    ) {
                        val task = adapter.currentList[viewHolder.adapterPosition]
                        viewModel.onTaskDeleted(task)
                    }
                }
            )

            itemTouchHelper.attachToRecyclerView(recyclerView)

            addTaskFab.setOnClickListener { viewModel.onAddNewTaskClicked() }
        }
    }

    private fun observeViewModel() {
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collect(::handleEvent)
        }
    }

    private fun handleEvent(event: Event) {
        when (event) {
            NavigateToAddTaskScreen -> {
                val directions = TasksFragmentDirections.tasksToAddEditTask(
                    -1,
                    getString(R.string.add_task)
                )

                findNavController().navigate(directions)
            }

            is NavigateToEditTaskScreen -> {
                val task = event.task
                val directions = TasksFragmentDirections.tasksToAddEditTask(
                    task.id,
                    getString(R.string.edit_task)
                )

                findNavController().navigate(directions)
            }

            is ShowUndoDeleteTaskMessage -> {
                Snackbar.make(requireView(), R.string.add_task, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.undo) { viewModel.onTaskInserted(event.task) }
                    .show()
            }
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

        val hideCompletedItem = menu.findItem(R.id.action_hide_completed_tasks)
        viewLifecycleOwner.lifecycleScope.launch {
            val hideCompleted = viewModel.preferences.first().hideCompleted
            hideCompletedItem.isChecked = hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.updateSortOrder(SortOrder.BY_NAME)
                true
            }

            R.id.action_sort_by_date_created -> {
                viewModel.updateSortOrder(SortOrder.BY_CREATED_AT)
                true
            }

            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                viewModel.updateHideCompleted(item.isChecked)
                true
            }

            R.id.action_delete_all_completed_tasks -> {
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClicked(task: Task) {
        viewModel.onTaskSelected(task)
    }

    override fun onCheckBoxClicked(task: Task) {
        viewModel.onTaskUpdated(task)
    }
}
