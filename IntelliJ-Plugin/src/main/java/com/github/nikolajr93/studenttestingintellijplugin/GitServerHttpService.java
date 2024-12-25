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

    /**
     * Clones a Git repository from a specified remote URL to a local directory.
     *
     * @param path     The local directory path where the repository will be cloned.
     *                 Example: "/path/to/local/directory"
     * @param taskPath The task-specific repository path appended to the base URL defined in {@code Config.HTTP_REPO_URL}.
     *                 Example: "my-repo.git"
     * @throws GitAPIException If an error occurs during the cloning process.
     * @throws IOException      If an I/O error occurs while accessing the directory.
     */
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

    /**
     * Pushes local changes to the specified branch of a Git repository.
     * If the branch does not exist, it creates the branch before pushing changes.
     *
     * @param path    The local directory path of the Git repository.
     *                Example: "/path/to/local/repository"
     * @param branchName The branch name to which changes should be pushed. If the branch does not exist,
     *                   it will be created locally.
     *                   Example: "feature-branch"
     * @param message The commit message to associate with the changes being pushed.
     *                Example: "Initial commit for feature development"
     * @throws GitAPIException If an error occurs during Git operations such as branch creation, pull, commit, or push.
     * @throws IOException      If an I/O error occurs while accessing the local repository.
     */
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

    /**
     * Clones a repository from the source directory and creates a forked repository in the destination directory.
     *
     * @param sourceDirPath The source directory path of the original repository.
     *                      Example: "/srv/git/OOP/2024_25/Prvi ispit"
     * @param destinationDirPath The destination directory path for the forked repository.
     *                           Example: "/srv/git/OOP/2024_25/Prvi ispit/Studentska_resenja"
     * @param name The name to give the forked repository.
     *             Example: Petar_Petrovic_M_41_23
     */
    public static void cloneAndForkRepository(String sourceDirPath, String destinationDirPath, String name) {
        try {
            // Clone repository from source
            Git.cloneRepository()
                    .setURI(sourceDirPath)
                    .setDirectory(new File(destinationDirPath + File.separator + name))
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                            Config.SERVER_USERNAME,
                            Config.SERVER_PASSWORD))
                    .call();

            System.out.println("Repository cloned and forked successfully at: " +
                    destinationDirPath + File.separator + name);
        } catch (GitAPIException e) {
            System.err.println("Error cloning and forking repository: " + e.getMessage());
        }
    }

    /**
     * Pushes changes to the main branch of the forked repository.
     *
     * @param repoPath The path to the forked repository.
     * @param commitMessage The commit message for the changes.
     */
    public static void pushChangesToForkedRepo(String repoPath, String commitMessage) {
        try (Git git = Git.open(new File(repoPath))) {
            // Checkout the main branch (assumes "main" exists)
            git.checkout().setName("main").call();

            // Add, commit, and push changes
            git.add().addFilepattern(".").call();
            git.commit().setMessage(commitMessage).call();

            git.push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                            Config.SERVER_USERNAME,
                            Config.SERVER_PASSWORD))
                    .call();

            System.out.println("Changes pushed to the main branch of the forked repository.");
        } catch (GitAPIException | IOException e) {
            System.err.println("Error pushing changes to forked repository: " + e.getMessage());
        }
    }
}
