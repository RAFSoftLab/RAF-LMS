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

    public static boolean pushToRepository(String path, String branchName, String message) {
        // Open the Git repository
        try (Git git = Git.open(new File(path))) {
            // Stage the changes
            git.add().addFilepattern(".").call();
            var branchCommand = git.branchCreate();
            branchCommand.setName(branchName);
            branchCommand.call();

            // Create a commit
            git.commit().setMessage(message).call();

            // Push the commit to the remote repository
            gitPushToRepository(git);

            System.out.println("Commit and push completed successfully.");
            return true;
        } catch (IOException | GitAPIException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return false;
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
