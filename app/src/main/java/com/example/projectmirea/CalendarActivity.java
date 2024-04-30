package com.example.projectmirea;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {
    // Переменные
    private EditText titleET;
    private ReminderCalendarSQL RCSQL;
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

        remindersList= new ArrayList<>();
        remindersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, remindersList);// побаловаться
        ListView remindersLV = findViewById(R.id.reminedrList);
        remindersLV.setAdapter(remindersAdapter);

        RCSQL = new ReminderCalendarSQL(this);



//        saveButton.setOnClickListener(v -> saveReminder());
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePickerDialog();
                saveReminder();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });



        

        }

    private void showDateTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
            }
        }, year, month, day);
        datePickerDialog.show();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar selectedTime = Calendar.getInstance();
                selectedTime.set(hour, minutes);
            }
        }, hour, minutes, true);
        timePickerDialog.show();
    }

    private void saveReminder(){
            SQLiteDatabase db = RCSQL.getWritableDatabase();

            String title = titleET.getText().toString().trim();

            long selectedDateMillis = calendarView.getDate();

            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTimeInMillis(selectedDateMillis);

            String selectedDateStr = formatDate(selectedDateMillis);

            ContentValues values = new ContentValues();
            values.put(ReminderContract.ReminderEntry.COLUMN_TITLE, title);
            values.put(ReminderContract.ReminderEntry.COLUMN_DATE, selectedDateStr);

            long newRowId = db.insert(ReminderContract.ReminderEntry.TABLE_NAME, null, values);

            if (newRowId == -1) {
                Toast.makeText(this, "Ошибка при сохранении напоминания", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Напоминание сохранено", Toast.LENGTH_SHORT).show();
                String reminder = title + " " + formatDateDMY(selectedDate) + " " + formateTime(selectedDate);
                remindersList.add(reminder);
                runOnUiThread(() -> remindersAdapter.notifyDataSetChanged());
            }
//            if (remindersList == null){
//                Log.e("Reminder", "remindersList is null");
//            }
//            if(remindersAdapter == null){
//                Log.e("Reminder", "remindersAdapter is null");
//            }
        }
    private String formatDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
    private String formatDateDMY(Calendar calendar){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }
    private String formateTime(Calendar calendar){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }
}


