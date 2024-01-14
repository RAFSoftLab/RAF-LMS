package com.github.nikolajr93.studenttestingintellijplugin.api;

import org.junit.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RafApiIntegrationTests {

    public static final String DUMMY_ID = "MSI10002050";
    public static final Student MOCK_STUDENT = new Student(
            "Foo",
            "Bar",
            1000,
            "2050",
            "120",
            "SI",
            "M");

    @Test
    public void getStudents_returnsStudents() throws IOException {
        // Arrange
        RafApiClient.createStudent(MOCK_STUDENT);

        // Act
        String students = RafApiClient.getStudents();

        // Assert
        assertTrue(students.length() > 0);
        RafApiClient.deleteStudent(DUMMY_ID);
    }

    @Test
    public void getStudent_returnsStudentById() throws IOException {
        // Arrange
        RafApiClient.createStudent(MOCK_STUDENT);

        // Act
        var student = RafApiClient.getStudent(DUMMY_ID);

        // Assert
        assertNotNull(student);
        RafApiClient.deleteStudent(DUMMY_ID);
    }

    @Test
    public void createStudent_createsStudentSuccessfully() throws IOException {
        // Arrange & Act
        var student = RafApiClient.createStudent(MOCK_STUDENT);

        // Assert
        assertNotNull(student);
        RafApiClient.deleteStudent(DUMMY_ID);
    }

    @Test
    public void deleteStudent_deletesStudentSuccessfully() throws IOException {
        // Arrange
        RafApiClient.createStudent(MOCK_STUDENT);
        var responseString = String.format("Student with id: %s doesn't exist in the db", DUMMY_ID) ;

        // Act
        RafApiClient.deleteStudent(DUMMY_ID);
        var response = RafApiClient.getStudent(DUMMY_ID);


        // Assert
        assertTrue(response.equals(responseString));
    }
}
