package com.github.nikolajr93.studenttestingintellijplugin.api;

public class ExamInfo {
    private String taskGroup;
    private String classroom;

    public ExamInfo() {}

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }
}
