package com.example.projectmirea;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FirebaseFirestore db;
    private EditText titleET;
    private CalendarView calendarView;
    private ArrayAdapter<String> remindersAdapter;
    private ArrayList<String> remindersList;

    public CalendarFragment1() {
        // Required empty public constructor
    }

    public static CalendarFragment1 newInstance(String param1, String param2) {
        CalendarFragment1 fragment = new CalendarFragment1();
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

        View view = inflater.inflate(R.layout.calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        titleET = view.findViewById(R.id.titleEditText);
        Button saveButton = view.findViewById(R.id.savebutton);
        Button taskButton = view.findViewById(R.id.taskbutton);
        Button profileButton = view.findViewById(R.id.profilebutton);

        remindersList = new ArrayList<>();
        remindersAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, remindersList);
        ListView remindersLV = view.findViewById(R.id.reminderList);
        remindersLV.setAdapter(remindersAdapter);

        db = FirestoreHelper.getInstance();

        ListenerRegistration listenerRegistration = db.collection("reminders").addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("CalendarFragment2", "Ошибка получения данных: ", error);
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
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

//        profileButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ProfileActivity.class);
//                startActivity(intent);
//            }
//        });

        remindersLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                                            Toast.makeText(getContext(), "Запись удалена", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getContext(), "Ошибка при удалении записи", Toast.LENGTH_LONG).show();
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

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.findViewById(R.id.reminderList).setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        return view;
    }

    private void showDateTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
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
                        Toast.makeText(getContext(), "Напоминание сохранено", Toast.LENGTH_SHORT).show();
                        titleET.setText("");
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Ошибка при сохранении напоминания", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "Введите заголовок", Toast.LENGTH_SHORT).show();
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