package com.github.nikolajr93.studenttestingintellijplugin;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class GitServerHttpService {

    public static void main(String[] args) {
       cloneRepository();
       pushToRepository();
    }

    public static void cloneRepository() {
        try {
            Git.cloneRepository()
                    .setURI(Config.HTTP_REPO_URL)
                    .setDirectory(new File(Config.HTTP_LOCAL_PATH_1))
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                            Config.SERVER_USERNAME,
                            Config.SERVER_PASSWORD))
                    .call();

            System.out.println("Repository cloned successfully.");
        } catch (GitAPIException e) {
            System.err.println("Error cloning repository: " + e.getMessage());
            System.err.println("Error 2 cloning repository: " + e.getLocalizedMessage());
            System.err.println("Error 3 cloning repository: " + e.toString());
        }
    }

    public static void pushToRepository() {
        try (Git git = Git.open(new File(Config.HTTP_LOCAL_PATH_1))) {
            // Generate a sample file
            createSampleFile(git);

            // Add the sample file to the staging area
            AddCommand addCommand = git.add();
            addCommand.addFilepattern("sample.txt").call();

            // Commit the changes
            CommitCommand commitCommand = git.commit();
            commitCommand.setMessage("Add sample file").call();

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

    private static void createSampleFile(Git git) throws IOException {
        // Create a sample file
        File sampleFile = new File(Config.HTTP_LOCAL_PATH_1, "sample.txt");
        try (FileWriter writer = new FileWriter(sampleFile)) {
            writer.write("This is a sample file.");
        }
    }
}
