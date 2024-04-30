package com.example.projectmirea;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // ПЕРЕМЕННЫЕ


    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private Button addButton;
    private EditText editTextTask;
    public ImageView calendar;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //АЙДИШНИКИ
        recyclerView = findViewById(R.id.recycler_view);
        addButton = findViewById(R.id.taskbutton);
        editTextTask = findViewById(R.id.edit_text_task);
        calendar = findViewById(R.id.calendar);






        // создание списка с задачами
        taskList = new ArrayList<>();
        // Создание и установка адаптера
        adapter = new TaskAdapter(taskList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskText = editTextTask.getText().toString();
                if(!taskText.isEmpty()){
                    Task newTask = new Task(taskText);
                    taskList.add(newTask);
                    adapter.notifyDataSetChanged();
                    editTextTask.setText("");
                }
                Task newTask = new Task("Новая задача");
                taskList.add(newTask);
                adapter.notifyDataSetChanged();
            }

        });




        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCalendar = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intentCalendar);
            }
        });

    }

    }
