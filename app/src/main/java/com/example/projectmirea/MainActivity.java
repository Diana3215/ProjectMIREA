package com.example.projectmirea;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
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


    private TaskAdapter adapter;
    private List<Task> taskList;
    private EditText editTextTask;
    public ImageView calendar;
    public  ImageView profile;
    private CheckBox checkBox;

    @SuppressLint({"MissingInflatedId", "WrongViewCast", "NotifyDataSetChanged"})
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
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        Button addButton = findViewById(R.id.taskbutton);
        editTextTask = findViewById(R.id.edit_text_task);
        calendar = findViewById(R.id.calendar);
        profile = findViewById(R.id.profile);
        ProfilePoints UserProfile = new ProfilePoints();
        CheckBox checkBox = findViewById(R.id.checkbox);




        // создание списка с задачами
        taskList = new ArrayList<>();
        // Создание и установка адаптера
        adapter = new TaskAdapter(taskList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        addButton.setOnClickListener(v -> {
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
        });

        checkBox.setOnClickListener(v -> {
            if (checkBox.isChecked()) {
                UserProfile.addPoints(1);
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("progress", UserProfile.getPoints());
                startActivity(intent);

            }
        });



        calendar.setOnClickListener(v -> {
            Intent intentCalendar = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intentCalendar);
        });
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });


    }

    }
class ProfilePoints{
    public int points;
    public ProfilePoints(){
        this.points = 0;
    }
    public int getPoints(){
        return points;
    }
    public void addPoints(int pointsToAdd){
        points += pointsToAdd;
    }
}
