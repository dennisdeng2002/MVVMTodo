package com.codinginflow.mvvmtodo.ui.tasks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codinginflow.mvvmtodo.data.task.Task
import com.codinginflow.mvvmtodo.databinding.ItemTaskBinding

class TasksAdapter(
    private val itemClickListener: OnItemClickListener
) : ListAdapter<Task, TasksAdapter.TaskViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(inflater, parent, false)
        return TaskViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding,
        private val itemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val task = getItem(adapterPosition)
                itemClickListener.onItemClicked(task)
            }

            binding.checkBoxCompleted.setOnCheckedChangeListener { _, isChecked ->
                val task = getItem(adapterPosition)
                itemClickListener.onCheckBoxClicked(task.copy(completed = isChecked))
            }
        }

        fun bind(task: Task) {
            with(binding) {
                checkBoxCompleted.isChecked = task.completed
                textViewName.text = task.name
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(task: Task)
        fun onCheckBoxClicked(task: Task)
    }

    @Suppress("ClassName")
    object DIFF_CALLBACK : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(
            oldItem: Task,
            newItem: Task
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Task,
            newItem: Task
        ): Boolean = oldItem == newItem
    }
}
