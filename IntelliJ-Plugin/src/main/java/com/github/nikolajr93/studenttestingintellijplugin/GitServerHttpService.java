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
                            Config.SERVER_GIT_USERNAME,
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
                        Config.SERVER_GIT_USERNAME,
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
                    Config.SERVER_GIT_USERNAME,
                    Config.SERVER_PASSWORD));
            pushCommand.call();

            System.out.println("Push to repository completed successfully.");
        } catch (GitAPIException | IOException e) {
            System.err.println("Error pushing to repository: " + e.getMessage());
        }
    }

    /**
     * Creates a student-specific fork of a repository and clones it locally.
     *
     * @param sourceRepoUrl The source repository URL
     *                      Example: ""http://abc@100.000.000.00/OOP/2024_25/Prvi_ispit/15"
     * @param studentId The unique student identifier
     *                 Example: "Petar_Petrovic_M_41_23"
     * @param localPath The local directory path where the repository should be cloned
     *                 Example: "C:\\Projects\\GitTest800"
     */
    public static void forkAndCloneRepository(String sourceRepoUrl, String studentId, String localPath) {
        try {
            String forkUrl = constructForkUrl(sourceRepoUrl, studentId);
            System.out.println("Fork URL: " + forkUrl);

            String forkPath = getForkPathFromUrl(forkUrl);

            System.out.println("Creating student directory at: " + forkPath);

            String host = Config.SERVER_HOST;
            int port = 22;
            String username = Config.SERVER_USERNAME;
            String password = Config.SERVER_PASSWORD;

            // Execute the script
            String output = RemoteScriptExecutor.runRemoteScript(
                    host, port, username, password,
                    Config.REMOTE_SCRIPT_2,
                    forkPath + " " + sourceRepoUrl
            );
            System.out.println("Script output:\n" + output);

            System.out.println("Cloning to local machine...");
            Git.cloneRepository()
                    .setURI(forkUrl)
                    .setDirectory(new File(localPath))
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                            Config.SERVER_GIT_USERNAME,
                            Config.SERVER_PASSWORD))
                    .call();

            System.out.println("Repository cloned locally to: " + localPath);

        } catch (Exception e) {
            System.err.println("Error in fork and clone operation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Constructs the URL for the student's fork.
     */
    private static String constructForkUrl(String sourceRepoUrl, String studentId) {
        // Remove trailing slash if present
        sourceRepoUrl = sourceRepoUrl.replaceAll("/$", "");
        return sourceRepoUrl + "/Studentska_resenja/" + studentId;
    }

    /**
     * Extracts the filesystem path from a Git URL.
     */
    private static String getForkPathFromUrl(String url) {
        // Convert URL format to filesystem path
        // Add /srv/git prefix and remove the http part
        // Example: "http://abc@100.000.000.00/OOP/2024_25/..." -> "/srv/git/OOP/2024_25/..."
        return "/srv/git" + url.replaceAll("http://[^/]+", "");
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
                            Config.SERVER_GIT_USERNAME,
                            Config.SERVER_PASSWORD))
                    .call();

            System.out.println("Changes pushed to the main branch of the forked repository.");
        } catch (GitAPIException | IOException e) {
            System.err.println("Error pushing changes to forked repository: " + e.getMessage());
        }
    }
}
