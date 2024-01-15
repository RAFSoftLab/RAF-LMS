package com.github.nikolajr93.studenttestingintellijplugin;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;


public class GitServerHttpService {

    public static void main(String[] args) {
       cloneRepository(Config.SSH_LOCAL_PATH_1);
       pushToRepository(Config.SSH_LOCAL_PATH_1, "test-branch","This is test commit");
    }

    public static void cloneRepository(String path) {
        try {
            Git.cloneRepository()
                    .setURI(Config.HTTP_REPO_URL)
                    .setDirectory(new File(path))
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                            Config.SERVER_USERNAME,
                            Config.SERVER_PASSWORD))
                    .call();

            System.out.println("Repository cloned successfully.");
        } catch (GitAPIException e) {
            System.err.println("Error cloning repository: " + e.getMessage());
        }
    }

    public static void pushToRepository(String path, String branchName, String message) {
        try (Git git = Git.open(new File(path))) {
            // Add the sample file to the staging area
            git.add().addFilepattern(".").call();
            var branchCommand = git.branchCreate();
            branchCommand.setName(branchName);
            branchCommand.call();


            // Create a commit
            git.commit().setMessage(message).call();

            // Push to the repository
            PushCommand pushCommand = git.push();
            pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                    Config.SERVER_USERNAME,
                    Config.SERVER_PASSWORD));
            pushCommand.call();

            System.out.println("Push to repository completed successfully.");
        } catch (GitAPIException | IOException e) {
            System.err.println("Error pushing to repository: " + e.getMessage());
        }
    }
}
