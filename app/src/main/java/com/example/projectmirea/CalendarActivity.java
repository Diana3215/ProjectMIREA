package com.example.projectmirea;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {
    // Переменные
    private FirebaseFirestore db;
    private EditText titleET;

    private CalendarView calendarView;
    private ArrayAdapter<String> remindersAdapter;
    private ArrayList<String> remindersList;


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.calendar);

        calendarView = findViewById(R.id.calendarView);
        titleET = findViewById(R.id.titleEditText);
        Button saveButton = findViewById(R.id.savebutton);
        Button taskButton = findViewById(R.id.taskbutton);
        Button profileButton = findViewById(R.id.profilebutton);

        remindersList = new ArrayList<>();
        remindersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, remindersList);
        ListView remindersLV = findViewById(R.id.reminderList);
        remindersLV.setAdapter(remindersAdapter);


        db = FirestoreHelper.getInstance();


        ListenerRegistration listenerRegistration = db.collection("reminders").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("CalendarActivity", "Ошибка получения данных: ", error);
                    return;
                }
                remindersList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    String title = doc.getString("title");
                    if (title != null) {
                        remindersList.add(title);
                    }
                }
                remindersAdapter.notifyDataSetChanged();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePickerDialog();
            }
        });
        taskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        remindersLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this);
                builder.setTitle("Удалить запись");
                builder.setMessage("Вы уверены, что хотите удалить запись?");

                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String removeItem = remindersList.get(position);

                        db.collection("reminders").whereEqualTo("title", removeItem)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                db.collection("reminders").document(document.getId()).delete();
                                            }
                                            remindersList.remove(position);
                                            remindersAdapter.notifyDataSetChanged();
                                            Toast.makeText(CalendarActivity.this, "Запись удалена", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(CalendarActivity.this, "Ошибка при удалении записи", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                });
                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.findViewById(R.id.reminderList).setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showDateTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                showTimePickerDialog(selectedDate);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog(final Calendar selectedDate) {
        int hour = selectedDate.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDate.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDate.set(Calendar.MINUTE, minute);
                saveReminder(selectedDate);
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void saveReminder(Calendar selectedDate) {
        String title = titleET.getText().toString();
        if (!title.isEmpty()) {
            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(selectedDate.getTime());
            Map<String, Object> reminder = new HashMap<>();
            reminder.put("title", title);
            reminder.put("date", date);

            db.collection("reminders")
                    .add(reminder)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(CalendarActivity.this, "Напоминание сохранено", Toast.LENGTH_SHORT).show();
                        titleET.setText("");
                    })
                    .addOnFailureListener(e -> Toast.makeText(CalendarActivity.this, "Ошибка при сохранении напоминания", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(CalendarActivity.this, "Введите заголовок", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    private String formatDateDMY(Calendar calendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    private String formatTime(Calendar calendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }
}
