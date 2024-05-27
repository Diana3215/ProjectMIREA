package com.example.projectmirea;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TaskFragment extends Fragment {
    private FirebaseFirestore db;
    private TaskAdapter todoAdapter, inProgressAdapter, doneAdapter;
    private List<TaskReminder> todoList, inProgressList, doneList;
    private EditText editTextTask;

    public TaskFragment() {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        EdgeToEdge.enable(requireActivity());

        RecyclerView todoRecyclerView = view.findViewById(R.id.todo);
        RecyclerView inProgressRecyclerView = view.findViewById(R.id.inprocess);
        RecyclerView doneRecyclerView = view.findViewById(R.id.done);
        Button addButton = view.findViewById(R.id.button);
        editTextTask = view.findViewById(R.id.editTextText);

        db = FirebaseFirestore.getInstance();

        todoList = new ArrayList<>();
        inProgressList = new ArrayList<>();
        doneList = new ArrayList<>();

        todoAdapter = new TaskAdapter(todoList, todoList, inProgressList, doneList, db, requireContext());
        inProgressAdapter = new TaskAdapter(inProgressList, todoList, inProgressList, doneList, db, requireContext());
        doneAdapter = new TaskAdapter(doneList, todoList, inProgressList, doneList, db, requireContext());

        todoRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        todoRecyclerView.setAdapter(todoAdapter);

        inProgressRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        inProgressRecyclerView.setAdapter(inProgressAdapter);

        doneRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        doneRecyclerView.setAdapter(doneAdapter);

        addButton.setOnClickListener(v -> {
            String taskText = editTextTask.getText().toString();
            if (!taskText.isEmpty()) {
                TaskReminder newTask = new TaskReminder(taskText);
                todoList.add(newTask);
                todoAdapter.notifyDataSetChanged();

                Map<String, Object> taskData = new HashMap<>();
                taskData.put("title", newTask.getName());
                taskData.put("status", "todo");

                db.collection("tasks")
                        .add(taskData)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("TaskFragment", "Task added with ID: " + documentReference.getId());
                            newTask.setId(documentReference.getId()); // Set the document ID
                        })
                        .addOnFailureListener(e -> {
                            Log.e("TaskFragment", "Error adding task", e);
                        });

                editTextTask.setText("");
            } else {
                TaskReminder newTask = new TaskReminder("New Task");
                todoList.add(newTask);
                todoAdapter.notifyDataSetChanged();
            }
        });



        loadTasksFromFirestore();

        return view;
    }

    private void loadTasksFromFirestore() {
        db.collection("tasks")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        String status = documentSnapshot.getString("status");
                        String title = documentSnapshot.getString("title");


                        Log.d("TaskFragment", "Retrieved task: Title - " + title + ", Status - " + status);


                        TaskReminder task = new TaskReminder(title);
                        task.setStatus(status);


                        switch (status) {
                            case "todo":
                                todoList.add(task);
                                break;
                            case "in_progress":
                                inProgressList.add(task);
                                break;
                            case "done":
                                doneList.add(task);
                                break;
                        }
                        String taskId = documentSnapshot.getId();
                        if (taskId != null) {
                            task.setId(taskId);
                        } else {
                            Log.e("TaskFragment", "Document ID is null");
                        }
                    }


                    todoAdapter.notifyDataSetChanged();
                    inProgressAdapter.notifyDataSetChanged();
                    doneAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("TaskFragment", "Error loading tasks from Firestore", e);
                });
    }
}
