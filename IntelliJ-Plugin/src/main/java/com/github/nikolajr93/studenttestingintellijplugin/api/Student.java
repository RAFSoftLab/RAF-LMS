package com.github.nikolajr93.studenttestingintellijplugin.api;

import java.time.LocalDateTime;
import java.util.Date;

public class Student {
    private String id;
    private String firstName;
    private String lastName;
    private Integer indexNumber;
    private String startYear;
    private String studiesGroup;
    private String taskGroup;
    private boolean taskCloned;
    private LocalDateTime taskClonedTime;
    private boolean taskSubmitted;
    private LocalDateTime taskSubmittedTime;
    private String major;
    private String studyProgram;
    private String classroom;
    private String forkName;

    public Student() {
    }

    public Student(
            String firstName,
            String lastName,
            Integer indexNumber,
            String startYear,
            String studiesGroup,
            String major,
            String studyProgram) {
        this.id = studyProgram+major+indexNumber+startYear;
        this.firstName = firstName;
        this.lastName = lastName;
        this.indexNumber = indexNumber;
        this.startYear = startYear;
        this.studiesGroup = studiesGroup;
        this.major = major;
        this.studyProgram = studyProgram;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getStartYear() {
        return startYear;
    }

    public String getStudyProgram() {
        return studyProgram;
    }

    public Integer getIndexNumber() {
        return indexNumber;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public void setStudyProgram(String studyProgram) {
        this.studyProgram = studyProgram;
    }

    public void setIndexNumber(Integer indexNumber) {
        this.indexNumber = indexNumber;
    }

    public String getStudiesGroup() {
        return studiesGroup;
    }

    public void setStudiesGroup(String studiesGroup) {
        this.studiesGroup = studiesGroup;
    }

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public boolean isTaskCloned() {
        return taskCloned;
    }

    public void setTaskCloned(boolean taskCloned) {
        this.taskCloned = taskCloned;
    }

    public LocalDateTime getTaskClonedTime() {
        return taskClonedTime;
    }

    public void setTaskClonedTime(LocalDateTime taskClonedTime) {
        this.taskClonedTime = taskClonedTime;
    }

    public boolean isTaskSubmitted() {
        return taskSubmitted;
    }

    public void setTaskSubmitted(boolean taskSubmitted) {
        this.taskSubmitted = taskSubmitted;
    }

    public LocalDateTime getTaskSubmittedTime() {
        return taskSubmittedTime;
    }

    public void setTaskSubmittedTime(LocalDateTime taskSubmittedTime) {
        this.taskSubmittedTime = taskSubmittedTime;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getForkName() {
        return forkName;
    }

    public void setForkName(String forkName) {
        this.forkName = forkName;
    }

    @Override
    public String toString() {
        String builder = "{\"id\" :" +
                "\"" + id + "\"" +
                ", \"firstName\" :" +
                "\"" + firstName + "\"" +
                ", \"lastName\" :" +
                "\"" + lastName + "\"" +
                ", \"indexNumber\" :" +
                "\"" + indexNumber + "\"" +
                ", \"startYear\" :" +
                "\"" + startYear + "\"" +
                ", \"studiesGroup\" :" +
                "\"" + studiesGroup + "\"" +
                ", \"taskGroup\" :" +
                "\"" + taskGroup + "\"" +
                ", \"taskCloned\" :" +
                "\"" + taskCloned + "\"" +
                ", \"taskClonedTime\" :" +
                "\"" + taskClonedTime + "\"" +
                ", \"taskSubmitted\" :" +
                "\"" + taskSubmitted + "\"" +
                ", \"taskSubmittedTime\" :" +
                "\"" + taskSubmittedTime + "\"" +
                ", \"major\" :" +
                "\"" + major + "\"" +
                ", \"studyProgram\" :" +
                "\"" + studyProgram + "\"" +
                ", \"classroom\" :" +
                "\"" + classroom + "\"" +
                ", \"forkName\" :" +
                "\"" + forkName + "\"" +
                "}";
        return builder;
    }
}
