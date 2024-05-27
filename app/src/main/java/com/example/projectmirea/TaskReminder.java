package com.example.projectmirea;

public class TaskReminder {
    private String status;
    private String name;
    private String id;
    public String getId() {
        return id;
    }
    public TaskReminder() {

    }

    public TaskReminder(String name){
        this.name = name;
        this.status = "todo";
    }
    public String getName(){
        return name;
    }
    public void setId(String id){
        this.id = id;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
