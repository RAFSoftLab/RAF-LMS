package com.github.nikolajr93.studenttestingintellijplugin;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;


public class GitServerHttpService {

    public static void cloneRepository(String path, String taskPath) {
        try {
            Git.cloneRepository()
                    .setURI(Config.HTTP_REPO_URL + taskPath)
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
            boolean branchExists = false;
            for (Ref ref : git.branchList().call()) {
                if (ref.getName().endsWith(branchName)) {
                    branchExists = true;
                    break;
                }
            }

            if (!branchExists) {
                    git.checkout()
                            .setCreateBranch(true)
                            .setName(branchName)
                            .call();
            } else {
                    git.checkout()
                            .setName(branchName)
                            .call();

                PullResult result = git
                        .pull()
                        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                        Config.SERVER_USERNAME,
                        Config.SERVER_PASSWORD))
                        .call();
                if(result.isSuccessful()) {
                    System.out.println("Repository successfully updated");
                } else {
                    System.out.println("Pull failed, check conflicts and repository status");
                }
            }

            git.add().addFilepattern(".").call();

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
