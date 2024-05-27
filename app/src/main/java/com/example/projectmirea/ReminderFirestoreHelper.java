package com.example.projectmirea;

import android.annotation.SuppressLint;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ReminderFirestoreHelper {
    private static final String COLLECTION_NAME = "tasks";
    private FirebaseFirestore db;
    private static FirebaseFirestore instance;

    public ReminderFirestoreHelper() {
        // Инициализация Firestore
        db = FirebaseFirestore.getInstance();
    }
    @SuppressLint("VisibleForTests")
    public static synchronized FirebaseFirestore getInstance() {
        if (instance == null) {
            instance = FirebaseFirestore.getInstance();
        }
        return instance;
    }

    public void addReminder(String title, OnCompleteListener<DocumentReference> listener) {
        Map<String, Object> task = new HashMap<>();
        task.put("title", title);

        db.collection(COLLECTION_NAME)
                .add(task)
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

}

