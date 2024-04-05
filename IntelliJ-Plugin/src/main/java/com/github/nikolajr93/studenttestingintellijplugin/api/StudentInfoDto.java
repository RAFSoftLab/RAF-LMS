package com.github.nikolajr93.studenttestingintellijplugin.api;

public class StudentInfoDto {
    private String firstName;
    private String lastName;
    private Integer indexNumber;
    private String startYear;
    private String studyProgramShort; // RN, RI - iz indeksa

    public StudentInfoDto(String firstName,
                          String lastName,
                          Integer indexNumber,
                          String startYear,
                          String studyProgramShort
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.indexNumber = indexNumber;
        this.startYear = startYear;
        this.studyProgramShort = studyProgramShort;
    }
}
