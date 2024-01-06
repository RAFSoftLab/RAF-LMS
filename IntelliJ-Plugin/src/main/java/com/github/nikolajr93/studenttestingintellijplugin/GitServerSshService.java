package com.github.nikolajr93.studenttestingintellijplugin;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GitServerSshService {
    private static final String FILE_TO_COMMIT = "sample.txt";

    public static void main(String[] args) {
        cloneRepository();
//        createSampleFile();
//        pushToRepository();

        try {
            // Open the Git repository
            try (Git git = Git.open(new File(Config.SSH_LOCAL_PATH_5))) {
//                CreateBranchCommand createBranchCommand = git.branchCreate();
//                createBranchCommand.call();
                // Create or modify files in the working directory
                createOrUpdateFile(FILE_TO_COMMIT, "Hello, JGit!");

                // Stage the changes
                git.add().addFilepattern(FILE_TO_COMMIT).call();

                // Create a commit
                git.commit().setMessage("Add sample.txt commit").call();

                // Push the commit to the remote repository
                pushToRepository(git);
            }

            System.out.println("Commit and push completed successfully.");
        } catch (IOException | GitAPIException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void createOrUpdateFile(String filePath, String content) throws IOException {
        // Implement your logic to create or update the file
        // For simplicity, this example writes content to the file
        try (java.io.FileWriter writer = new java.io.FileWriter(Config.SSH_LOCAL_PATH_5 + File.separator + filePath)) {
            writer.write(content);
        }
    }

    private static void pushToRepository(Git git) throws GitAPIException {
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


    public static void cloneRepository() {
        try {
//            File sshDir = new File(FS.DETECTED.userHome(), "/.ssh");
//            SshdSessionFactory sshSessionFactory = new SshdSessionFactoryBuilder()
//                    .setPreferredAuthentications("publickey")
//                    .setHomeDirectory(FS.DETECTED.userHome())
//                    .setSshDirectory(sshDir)
//                    .build(null);
            SshSessionFactory sshSessionFactory = SshSessionFactory.getInstance();
//            System.err.println(sshSessionFactory.toString());
//            System.err.println("test");
//            System.out.println(sshSessionFactory.toString());
            CloneCommand cloneCommand = Git.cloneRepository()
                    .setURI(Config.SSH_REPO_URL)
                    .setDirectory(new File(Config.SSH_LOCAL_PATH_5))
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                            Config.SERVER_USERNAME,
                            Config.SERVER_PASSWORD))
                    .setTransportConfigCallback(transport -> {
                        SshTransport sshTransport = (SshTransport) transport;
                        sshTransport.setSshSessionFactory(sshSessionFactory);
                    });
            cloneCommand.call();

            System.out.println("Repository cloned successfully.");
        } catch (GitAPIException e) {
            System.err.println("Error cloning repository: " + e.getMessage());
            System.err.println("Error 2 cloning repository: " + e.getLocalizedMessage());
            System.err.println("Error 3 cloning repository: " + e.toString());
        }
    }

    public static void createSampleFile() {
        try (Git git = Git.open(new File(Config.SSH_LOCAL_PATH_5))) {
            // Create a sample file
            File sampleFile = new File(Config.SSH_LOCAL_PATH_5, "sample.txt");
            try (FileWriter writer = new FileWriter(sampleFile)) {
                writer.write("This is a sample file.");
            }

            // Add the sample file to the staging area
            AddCommand addCommand = git.add();
            addCommand.addFilepattern("sample.txt").call();

            // Commit the changes
            CommitCommand commitCommand = git.commit();
            commitCommand.setMessage("Add sample file").call();

            System.out.println("Sample file created and committed.");
        } catch (IOException | GitAPIException e) {
            System.err.println("Error creating sample file: " + e.getMessage());
        }
    }

    public static void pushToRepository() {
        try (Git git = Git.open(new File(Config.SSH_LOCAL_PATH_5))) {
            SshSessionFactory sshSessionFactory = SshSessionFactory.getInstance();
            PushCommand pushCommand = git
                    .push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                            Config.SERVER_USERNAME,
                            Config.SERVER_PASSWORD))
                    .setTransportConfigCallback(transport -> {
                        SshTransport sshTransport = (SshTransport) transport;
                        sshTransport.setSshSessionFactory(sshSessionFactory);
                    });
            pushCommand.call();

            System.out.println("Push to repository completed successfully.");
        } catch (GitAPIException | IOException e) {
            System.err.println("Error pushing to repository: " + e.getMessage());
        }
    }
}
