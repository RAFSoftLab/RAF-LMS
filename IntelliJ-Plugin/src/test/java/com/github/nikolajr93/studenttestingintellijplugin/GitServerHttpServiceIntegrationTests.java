package com.github.nikolajr93.studenttestingintellijplugin;

import org.junit.Test;

import static com.github.nikolajr93.studenttestingintellijplugin.GitServerSshService.*;
import static com.github.nikolajr93.studenttestingintellijplugin.GitServerSshService.cloneRepository2;
import static org.junit.Assert.assertEquals;

public class GitServerHttpServiceIntegrationTests {
    private static final String FILE_TO_COMMIT = "proba.txt";

    @Test
    public void cloneRepositoryTest() {
        // Arrange and Act
        var repositoryIsCloned = cloneRepository2(Config.SSH_LOCAL_PATH_1);
//        var repositoryIsCloned = GitServerSshService.cloneRepository3(Config.SSH_LOCAL_PATH_1);
//        var repositoryIsCloned = cloneRepository(Config.SSH_LOCAL_PATH_1);
//        var repositoryIsCloned = GitServerHttpService.cloneRepository();

        // Assert
        assertEquals(repositoryIsCloned, true);
    }

    @Test
    public void cloneAndPushToRepositoryTest() {
        // Arrange and Act
        var repositoryIsCloned = cloneRepository(Config.SSH_LOCAL_PATH_1);
        var fileIsPushedToRepo = pushToRepository(
                Config.SSH_LOCAL_PATH_1,
                "main",
                "Initial commit");

        // Assert
        assertEquals(fileIsPushedToRepo, true);
    }
}