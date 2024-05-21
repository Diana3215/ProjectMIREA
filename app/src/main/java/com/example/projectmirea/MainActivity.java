package com.example.projectmirea;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    // ПЕРЕМЕННЫЕ

    private TaskAdapter adapter;
    public ImageView calendar;
    public  ImageView profile;
    public  ImageView menu;

    public MainActivity() {
    }

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
        calendar = findViewById(R.id.calendar);
        profile = findViewById(R.id.profile);
        menu = findViewById(R.id.menu);
        ProfilePoints UserProfile = new ProfilePoints();
        CalendarFragment1 calendarFragment1 = new CalendarFragment1();
        TaskFragment taskFragment = new TaskFragment();

        FirebaseFirestore db = ReminderFirestoreHelper.getInstance();


        if (savedInstanceState == null) {
            setNewFragment(taskFragment);
        }

        calendar.setOnClickListener(v -> setNewFragment(calendarFragment1));
        menu.setOnClickListener(v -> setNewFragment(taskFragment));


    }

    private void setNewFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.addToBackStack(null);
        ft.commit();

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
