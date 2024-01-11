package com.github.nikolajr93.studenttestingintellijplugin.api;

public class Student {
    private Integer id;
    private String firstName;
    private String lastName;
    private String startYear;
    private String studyProgram;
    private Integer indexNumber;

    public Student(Integer id, String firstName, String lastName, String startYear, String studyProgram, Integer indexNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.startYear = startYear;
        this.studyProgram = studyProgram;
        this.indexNumber = indexNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getStudyProgram() {
        return studyProgram;
    }

    public void setStudyProgram(String studyProgram) {
        this.studyProgram = studyProgram;
    }

    public Integer getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(Integer indexNumber) {
        this.indexNumber = indexNumber;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", startYear='" + startYear + '\'' +
                ", studyProgram='" + studyProgram + '\'' +
                ", indexNumber=" + indexNumber +
                '}';
    }
}
