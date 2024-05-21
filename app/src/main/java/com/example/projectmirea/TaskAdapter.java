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


    private List<TaskReminder> taskReminderList;
    private Context context;
    private int checkedPosition = RecyclerView.NO_POSITION;


    public TaskAdapter(List<TaskReminder> taskReminderList, Context context){
        this.taskReminderList = taskReminderList;
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
        TaskReminder taskReminder = taskReminderList.get(position);
        holder.taskTextView.setText(taskReminder.getName());

        // Устанавливаем состояние чекбокса на основе позиции элемента
        holder.checkBoxTask.setChecked(position == checkedPosition);

        // Устанавливаем слушатель изменения состояния чекбокса
        holder.checkBoxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Сохраняем позицию выбранного элемента
                checkedPosition = holder.getAdapterPosition();
            }
        });
    }


    @Override
    public int getItemCount() {
        return taskReminderList.size();
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
    public int getCheckedPosition() {
        return checkedPosition;
    }

    public String getTaskItemId(int position) {
        if (position != RecyclerView.NO_POSITION) {
            TaskReminder task = taskReminderList.get(position);
            return task.getId();
        } else {
            return null;
        }
    }


}

