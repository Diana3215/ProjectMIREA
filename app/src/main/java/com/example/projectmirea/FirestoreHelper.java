package com.example.projectmirea;


import android.annotation.SuppressLint;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {
    private static final String COLLECTION_NAME = "reminders";
    private FirebaseFirestore db;
    private static FirebaseFirestore instance;

    public FirestoreHelper() {
        // Инициализация Firestore
        db = FirestoreHelper.getInstance();
    }

    @SuppressLint("VisibleForTests")
    public static synchronized FirebaseFirestore getInstance() {
        if (instance == null) {
            instance = FirebaseFirestore.getInstance();
        }
        return instance;
    }

    public void addReminder(String title, String date, OnCompleteListener<DocumentReference> listener) {
        Map<String, Object> reminder = new HashMap<>();
        reminder.put("title", title);
        reminder.put("date", date);

        db.collection(COLLECTION_NAME)
                .add(reminder)
                .addOnCompleteListener(listener);
    }

    public void deleteReminder(String title, OnCompleteListener<QuerySnapshot> listener) {
        db.collection(COLLECTION_NAME)
                .whereEqualTo("title", title)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                    }
                    listener.onComplete(task);
                });
    }
    public void createCollection(String collectionName) {
        // firestore
        Map<String, Object> data = new HashMap<>();
        db.collection(collectionName)
                .add(data)
                .addOnSuccessListener(documentReference -> System.out.println("Коллекция " + collectionName + " успешно создана"))
                .addOnFailureListener(e -> System.out.println("Ошибка при создании коллекции " + collectionName + ": " + e));
    }


}

