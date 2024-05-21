package com.example.projectmirea;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends Fragment {
    private FirebaseFirestore db;
    private TaskAdapter adapter;
    private List<TaskReminder> taskList;
    private EditText editTextTask;
    private FrameLayout frameLayout;

    public TaskFragment() {
        // Required empty public constructor
    }
    public static TaskFragment newInstance(String param1, String param2) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_task, container, false);
        EdgeToEdge.enable(requireActivity());

        RecyclerView recyclerView = view.findViewById(R.id.recycle_view);
        Button addButton = view.findViewById(R.id.button);
        editTextTask = view.findViewById(R.id.editTextText);
        ProfilePoints UserProfile = new ProfilePoints();
        CheckBox checkBox = view.findViewById(R.id.checkBox1);


        db = ReminderFirestoreHelper.getInstance();

        taskList = new ArrayList<>();
        adapter = new TaskAdapter(taskList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v -> {
            String taskText = editTextTask.getText().toString();
            if (!taskText.isEmpty()) {
                TaskReminder newTask = new TaskReminder(taskText);
                taskList.add(newTask);
                adapter.notifyDataSetChanged();

                Map<String, Object> taskData = new HashMap<>();
                taskData.put("title", newTask.getName());

                db.collection("tasks")
                        .add(taskData)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("MainFragment", "TaskReminder added with ID: " + documentReference.getId());
                        })
                        .addOnFailureListener(e -> {
                            Log.e("MainFragment", "Error adding task", e);
                        });

                editTextTask.setText("");
            } else {
                TaskReminder newTask = new TaskReminder("Новая задача");
                taskList.add(newTask);
                adapter.notifyDataSetChanged();
            }
        });

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                int position = adapter.getCheckedPosition();
                if (position != RecyclerView.NO_POSITION) {
                    taskList.remove(position);
                    adapter.notifyItemRemoved(position);

                    String taskId = String.valueOf(adapter.getTaskItemId(position));
                    if (taskId != null) {
                        db.collection("tasks").document(taskId)
                                .delete()
                                .addOnSuccessListener(aVoid -> Log.d("MainFragment", "Task deleted successfully"))
                                .addOnFailureListener(e -> Log.e("MainFragment", "Error deleting task", e));
                    } else {
                        Log.e("MainFragment", "Error deleting task: taskId is null or empty");
                    }
                }
            }
        });




        return view;
    }



//    private void updateProgressBar(int points) {
//        ProgressBar progressBar = requireView().findViewById(R.id.progressBar);
//        progressBar.setProgress(points);
//    }


class ProfilePoints {
    public int points;
    public ProfilePoints() {
        this.points = 0;
    }
    public int getPoints() {
        return points;
    }
    public void addPoints(int pointsToAdd) {
        points += pointsToAdd;
    }
}
}