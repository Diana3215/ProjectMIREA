package com.example.projectmirea.DatabaseHelpers;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class GlobalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}