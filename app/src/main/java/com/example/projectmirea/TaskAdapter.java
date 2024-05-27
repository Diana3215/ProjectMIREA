package com.example.projectmirea;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<TaskReminder> currentList;
    private List<TaskReminder> todoList;
    private List<TaskReminder> inProgressList;
    private List<TaskReminder> doneList;
    private FirebaseFirestore db;
    private Context context;

    public TaskAdapter(List<TaskReminder> currentList, List<TaskReminder> todoList, List<TaskReminder> inProgressList, List<TaskReminder> doneList, FirebaseFirestore db, Context context) {
        this.currentList = currentList;
        this.todoList = todoList;
        this.inProgressList = inProgressList;
        this.doneList = doneList;
        this.db = db;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskReminder task = currentList.get(position);



        if (task.getStatus().equals("todo")) {
            holder.moveButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.task)));
        } else if (task.getStatus().equals("in_progress")) {
            holder.moveButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.todo)));
        } else if (task.getStatus().equals("done")) {
            holder.moveButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.done)));
        }
        holder.bind(task);

        holder.moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTask(task);
            }
        });
    }
    public void moveTask(TaskReminder task) {
        String status = task.getStatus();
        if (status.equals("todo")) {
            task.setStatus("in_progress");
            todoList.remove(task);
            inProgressList.add(task);
        } else if (status.equals("in_progress")) {
            task.setStatus("done");
            inProgressList.remove(task);
            doneList.add(task);
        } else if (status.equals("done")) {
            doneList.remove(task);
            currentList.remove(task);
            String taskId = task.getId();
            if (taskId != null) {
                db.collection("tasks").document(taskId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d("TaskAdapter", "Task deleted successfully");
                            notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("TaskAdapter", "Error deleting task", e);
                        });
            } else {
                Log.e("TaskAdapter", "Task ID is null");
            }
            return;
        }

        String taskId = task.getId();
        if (taskId != null) {
            db.collection("tasks").document(taskId)
                    .update("status", task.getStatus())
                    .addOnSuccessListener(aVoid -> {
                        Log.d("TaskAdapter", "Task status updated successfully");
                        notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TaskAdapter", "Error updating task status", e);
                    });
        } else {
            Log.e("TaskAdapter", "Task ID is null");
        }
    }

    @Override
    public int getItemCount() {
        return currentList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView taskName;
        private Button moveButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.task_text_view);
            moveButton = itemView.findViewById(R.id.movebut);
        }

        public void bind(TaskReminder task) {
            taskName.setText(task.getName());
            moveButton.setOnClickListener(v -> moveTask(task));
        }
    }
}