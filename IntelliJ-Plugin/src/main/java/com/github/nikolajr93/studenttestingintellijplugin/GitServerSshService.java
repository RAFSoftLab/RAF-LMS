package com.github.nikolajr93.studenttestingintellijplugin;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;

public class GitServerSshService {
    private static final String FILE_TO_COMMIT = "sample.txt";

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

    public static boolean pushToRepository(String path) {
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
