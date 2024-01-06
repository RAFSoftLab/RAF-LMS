package com.github.nikolajr93.studenttestingintellijplugin;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class GitServerHttpServiceIntegrationTests {
    private static final String FILE_TO_COMMIT = "proba.txt";

    @Test
    public void cloneRepositoryTest() {
        // Arrange and Act
        var repositoryIsCloned = cloneRepository(Config.HTTP_LOCAL_PATH_3);

        // Assert
        assertEquals(repositoryIsCloned, true);
    }

    @Test
    public void cloneAndPushToRepositoryTest() {
        // Arrange and Act
        var repositoryIsCloned = cloneRepository(Config.HTTP_LOCAL_PATH_2);
        var fileIsPushedToRepo = pushToRepository(Config.HTTP_LOCAL_PATH_2);

        // Assert
        assertEquals(fileIsPushedToRepo, true);
    }

    public static boolean cloneRepository(String path) {
        try {
            SshSessionFactory sshSessionFactory = SshSessionFactory.getInstance();
            File testFolder = new File(path);

            CloneCommand cloneCommand = Git.cloneRepository()
                    .setURI(Config.SSH_REPO_URL)
                    .setDirectory(testFolder)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                            Config.SERVER_USERNAME,
                            Config.SERVER_PASSWORD))
                    .setTransportConfigCallback(transport -> {
                        SshTransport sshTransport = (SshTransport) transport;
                        sshTransport.setSshSessionFactory(sshSessionFactory);
                    });
            cloneCommand.call();

            System.out.println("Repository cloned successfully.");
            return true;
        } catch (GitAPIException e) {
            System.err.println("Error cloning repository: " + e.getMessage());
            return false;
        }
    }

    private boolean pushToRepository(String path) {
        try {
            // Open the Git repository
            try (Git git = Git.open(new File(path))) {
                // Create or modify files in the working directory
                createOrUpdateFile(FILE_TO_COMMIT, "Hello, ovo je proba!", path);

                // Stage the changes
                git.add().addFilepattern(FILE_TO_COMMIT).call();

                // Create a commit
                git.commit().setMessage("Add proba.txt commit").call();

                // Push the commit to the remote repository
                gitPushToRepository(git);
            }

            System.out.println("Commit and push completed successfully.");
            return true;
        } catch (IOException | GitAPIException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }

    private static void createOrUpdateFile(String filePath, String content, String path) throws IOException {
        // Implement your logic to create or update the file
        // For simplicity, this example writes content to the file
        try (java.io.FileWriter writer = new java.io.FileWriter(path + File.separator + filePath)) {
            writer.write(content);
        }
    }

    private static void gitPushToRepository(Git git) throws GitAPIException {
        // Configure credentials (replace with your username and password)
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(
                Config.SERVER_USERNAME,
                Config.SERVER_PASSWORD);

        // Push to the remote repository
        PushCommand pushCommand = git.push();
        pushCommand.setCredentialsProvider(credentialsProvider);
        SshSessionFactory sshSessionFactory = SshSessionFactory.getInstance();
        pushCommand.setTransportConfigCallback(transport -> {
            SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(sshSessionFactory);
        });
        pushCommand.call();
    }
}


//            // Clean up resources, such as deleting the test folder
//            boolean result = true;
//            if (testFolder.exists()) {
//                result = deleteFolder(testFolder);
//            }


//            try {
//                FileUtils.forceDelete(testFolder);
////                // Execute the command to delete the folder and its contents
////                deleteFolderWithGit(testFolderPath);
////                System.out.println("Folder deleted successfully.");
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.err.println("Failed to delete the folder.");
//            }

//            if (result) {
//                System.out.println("Test folder deleted successfully.");
//            }

//    private static boolean deleteFolder(File folder) {
//        File[] files = folder.listFiles();
//        if (files != null) {
//            for (File file : files) {
//                if (file.isDirectory()) {
//                    deleteFolder(file);
//                } else {
//                    if (!file.delete()) {
//                        return false;
//                    }
//                }
//            }
//        }
//        return folder.delete();
//    }

//    private static void deleteFolderWithGit(String folderPath) throws IOException, InterruptedException {
//        // Use the ProcessBuilder to execute a command
//        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "rd", "/s", "/q", folderPath);
//        processBuilder.redirectErrorStream(true);
//
//        Process process = processBuilder.start();
//        int exitCode = process.waitFor();
//
//        if (exitCode != 0) {
//            // Handle the case where the process did not exit successfully
//            throw new IOException("Failed to delete the folder. Exit code: " + exitCode);
//        }
//    }