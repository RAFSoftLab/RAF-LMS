package com.github.nikolajr93.studenttestingintellijplugin.api;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class RafApiIntegrationTests {

    public static final String DUMMY_ID = "M10002050";
    public static final Student MOCK_STUDENT = new Student(
            "Foo",
            "Bar",
            1000,
            "2050",
            "120",
            "M");

//    private StudentsControllerClient client;

    @BeforeEach
    public void setUp() {
//        client = new StudentsControllerClient();
    }

    @Test
    public void getStudents_returnsStudents() throws IOException {
        // Arrange
//        RafApiClient.createStudent(MOCK_STUDENT);

        // Act
        String students = RafApiClient.getStudents();

        // Assert
        assertTrue(students.length() > 0);
//        RafApiClient.deleteStudent(DUMMY_ID);
    }

    @Test
    public void authorizeStudent_returnsToken() {
        // Act
        String tokenMessage = RafApiClient.authorizeStudent("M312023");

        // Assert
        assertTrue(tokenMessage.length() > 0);
    }

    @Test
    public void getRepository_returnsRepository() {
        // Act
        String repositoryMessage = RafApiClient.getRepository("M312023",
                "26ba21e2-465c-461d-b6f7-1471538c1700",
                "OopZadatak1");

        // Assert
        assertTrue(repositoryMessage.length() > 0);
    }

    @Test
    public void getFork_returnsFork() throws IOException {
        // Act
        String forkMessage = RafApiClient.getFork("M312023",
                "26ba21e2-465c-461d-b6f7-1471538c1700");

        // Assert
        assertTrue(forkMessage.length() > 0);
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

//    @Test
//    public void taskIsCloned_clonedTaskSuccessfully() throws IOException {
//        // Arrange
//        RafApiClient.createStudent(MOCK_STUDENT);
//        var examInfo = new ExamInfo("21", "RAF10");
//
//        // Act
//        var student = RafApiClient.taskIsCloned(DUMMY_ID, examInfo);
//
//        // Assert
//        assertNotNull(student);
//        RafApiClient.deleteStudent(DUMMY_ID);
//    }

    @Test
    public void taskIsSubmitted_submitsTaskSuccessfully() throws IOException {
        // Arrange
        RafApiClient.createStudent(MOCK_STUDENT);
        var taskSubmissionInfo = new TaskSubmissionInfo("MSI312024-fork");

        // Act
        var student = RafApiClient.taskIsSubmitted(DUMMY_ID, taskSubmissionInfo);

        // Assert
        assertNotNull(student);
        RafApiClient.deleteStudent(DUMMY_ID);    }

//    @Test
//    public void deleteStudent_deletesStudentSuccessfully() throws IOException {
//        // Arrange
//        RafApiClient.createStudent(MOCK_STUDENT);
//        var responseString = String.format("Student with id: %s doesn't exist in the db", DUMMY_ID) ;
//
//        // Act
//        RafApiClient.deleteStudent(DUMMY_ID);
//        var response = RafApiClient.getStudent(DUMMY_ID);
//
//
//        // Assert
//        assertTrue(response.equals(responseString));
//    }

//    @Test
//    public void authorizeStudent_AuthorizesStudent() {
//        // Arrange
//        RafApiClient.createStudent(MOCK_STUDENT);
//
//        // Act
//        var response = client.authorizeStudent(DUMMY_ID);
//
//        //Assert
//        assertEquals(200, response.getStatusCodeValue());
//    }
}
