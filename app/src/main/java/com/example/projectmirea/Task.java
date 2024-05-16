package com.example.projectmirea;

import android.util.Log;

public class Task {
    private String name;

    public Task(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public boolean isSuccessful() {
        Log.i("Sucsess", "Нет ошибок");
        return true;
    }
}
