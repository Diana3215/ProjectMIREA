package com.example.projectmirea;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{


    private List<Task> taskList;
    private Context context;

    public TaskAdapter(List<Task> taskList, Context context){
        this.taskList = taskList;
        this.context = context;
    }
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskTextView.setText(task.getName());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTextView;
        CheckBox checkBoxTask;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxTask = itemView.findViewById(R.id.checkbox);
            taskTextView = itemView.findViewById(R.id.task_text_view);
        }
    }

}

