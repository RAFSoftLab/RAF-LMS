package com.github.nikolajr93.studenttestingintellijplugin.toolWindow

import com.github.nikolajr93.studenttestingintellijplugin.Config
import com.github.nikolajr93.studenttestingintellijplugin.GitServerSshService
import com.github.nikolajr93.studenttestingintellijplugin.MyBundle
import com.github.nikolajr93.studenttestingintellijplugin.api.RafApiClient
import com.github.nikolajr93.studenttestingintellijplugin.api.Student
import com.github.nikolajr93.studenttestingintellijplugin.services.MyProjectService
import com.google.gson.Gson
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*
import javax.swing.border.EmptyBorder


class MyToolWindowFactory : ToolWindowFactory {

    private lateinit var studentIndexInput: JTextField
    private lateinit var usernameInput: JTextField
    private lateinit var passwordInput: JPasswordField
    private lateinit var outputArea: JTextArea
    private lateinit var cloningReportArea: JTextArea

    private lateinit var mainPanel: JPanel
    private lateinit var initialPanel: JPanel
    private lateinit var formPanel: JPanel
    private lateinit var afterClonedPanel: JPanel
    private lateinit var toolWindow: ToolWindow
    private lateinit var contentFactory: ContentFactory

//    private lateinit var connection: MessageBusConnection

    private lateinit var studentIndex: String
    private lateinit var username: String
    private lateinit var password: String

    init {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow)

        this.contentFactory = ContentFactory.getInstance()

        // Get instance of ProjectManagerEx for project handling
        val projectManager = ProjectManager.getInstance()

        // Create a panel to hold all components
        mainPanel = JPanel(BorderLayout())
//        var mainPanel = JPanel(CardLayout())

//        val afterClonedPanel = JPanel()
        this.afterClonedPanel = JPanel()
        afterClonedPanel.layout = BoxLayout(afterClonedPanel, BoxLayout.Y_AXIS)
        afterClonedPanel.border = BorderFactory.createEmptyBorder(10, 20, 10, 20)

//        val formPanel = JPanel()
        this.formPanel = JPanel()
        formPanel.layout = BoxLayout(formPanel, BoxLayout.Y_AXIS)
        formPanel.border = BorderFactory.createEmptyBorder(10, 20, 10, 20)

        this.initialPanel = JPanel()
        initialPanel.layout = BoxLayout(initialPanel, BoxLayout.Y_AXIS)
        initialPanel.border = BorderFactory.createEmptyBorder(10, 20, 10, 20)

        // Add input fields labels
        studentIndexInput = JTextField(20)
        formPanel.add(makeField("Student Index:", studentIndexInput))

        usernameInput = JTextField(20)
        formPanel.add(makeField("Username:", usernameInput))

        passwordInput = JPasswordField(20)
        formPanel.add(makeField("Password:", passwordInput))

        outputArea = JTextArea();
        cloningReportArea = JTextArea();

        // Create a Sign in button and add it to the panel
        val signInButton = JButton("Sign in")
        signInButton.addActionListener {
            studentIndex = studentIndexInput.text
            username = usernameInput.text
            password = String(passwordInput.password)
            // Clear the input fields
            studentIndexInput.text = ""
            usernameInput.text = ""
            passwordInput.text = ""

            //  Open the project from the file system
            var projectToOpen = "C:\\Users\\P53\\Documents\\RAF\\NVPTest\\restDemo"
            // Get instance of ProjectManagerEx for project handling
//            val projectManager = ProjectManager.getInstance() // Moved to above the method
//            val projectManagerEx = ProjectManagerEx.getInstanceEx()

            //  Clears old content
            toolWindow.contentManager.removeAllContents(true)

            //  Prints info about the student with ID = 1
            // Call the getStudent method from the RafApiClient Java class
            val studentJson = RafApiClient.getStudent("1")

            // Parse the returned JSON into a Student object
            val student = Gson().fromJson(studentJson, Student::class.java)

            // Display the student information in the plugin window
            outputArea.text = "Student info: \n ${student}"

            // Create and add a commit button to the ToolWindow
            val cloneRepoButton = JButton("Clone repo")
            // Create an ActionListener for the cloneRepoButton
            cloneRepoButton.addActionListener {
                // Run the clone operation in a worker thread to avoid blocking the UI.
                ApplicationManager.getApplication().executeOnPooledThread {
                    // Calls the 'cloneRepository' method which is a blocking operation
//                    val isSuccess = GitServerSshService.cloneRepository(Config.SSH_LOCAL_PATH_1)
                    val isSuccess = GitServerSshService.cloneRepository2(Config.SSH_LOCAL_PATH_1)
                    // `invokeLater` schedules this task to run on the Event Dispatch Thread (EDT).
                    ApplicationManager.getApplication().invokeLater {
                        cloningReportArea.text = if (isSuccess) "Repository cloned successfully." else "Failed to clone repository."
                        if(isSuccess){
                            projectToOpen = Config.SSH_LOCAL_PATH_1;
                            ApplicationManager.getApplication().invokeLater(Runnable {
                                ApplicationManager.getApplication().runReadAction {
                                    // Get the currently opened projects
                                    val openProjects = projectManager.openProjects
//                    val openProjects = projectManagerEx.openProjects

                                    if (openProjects.none { it.projectFilePath == projectToOpen }) {
                                        projectManager.loadAndOpenProject(projectToOpen)
//                        projectManagerEx.openProject(Paths.get(projectToOpen), OpenProjectTask(forceOpenInNewFrame = false))
                                    }
                                }
                            })
                        }
                    }
                }
            }

            val newPanel = JPanel()
            newPanel.add(cloneRepoButton)
            newPanel.add(outputArea)
            newPanel.add(cloningReportArea)

            val contentFactory = ContentFactory.getInstance()
            val content = contentFactory.createContent(newPanel, "", false)
            toolWindow.contentManager.addContent(content)

        }

        formPanel.add(signInButton)

        val commitButton = JButton("Commit")
        commitButton.addActionListener {
            // Get the current project
            val currentProject = ProjectManager.getInstance().openProjects[0]

            // Save all open files
            FileDocumentManager.getInstance().saveAllDocuments()

            // Start a background thread for the blocking operations
            ApplicationManager.getApplication().executeOnPooledThread {
                // Use the project base path as the repository path. Replace "newBranch" with the desired branch name.
                val isSuccess = GitServerSshService.pushToRepository2(
                        Config.SSH_LOCAL_PATH_1,
                        "student1Branch",
                        "Initial commit"
                )

                // If the push operation is successful, close and dispose of the project
                if (isSuccess) {
                    ApplicationManager.getApplication().invokeLater {
                        ProjectManager.getInstance().closeAndDispose(currentProject)
                    }
                } else {
                    // Handle failure - this prints an error to the console, but you'll likely want to replace this with your own error handling
                    println("Failed to push changes to new branch.")
                }
            }

//            // Close and dispose of the project (Deprecated)
//            ApplicationManager.getApplication().invokeLater {
//                ProjectManager.getInstance().closeAndDispose(currentProject)
//            }
        }

        afterClonedPanel.add(commitButton)

        val checkProjectButton = JButton("Check Project")
        checkProjectButton.addActionListener {
            toolWindow.contentManager.removeAllContents(true)
//            if (project.basePath == Config.SSH_LOCAL_PATH_1) {
//            if (project.basePath?.contains("GitTest") == true) {
//            if (ProjectManager.getInstance().openProjects.any { it.basePath == Config.SSH_LOCAL_PATH_1 }) {
//            if (ProjectManager.getInstance().openProjects.any { it.basePath?.contains(Config.SSH_LOCAL_PATH_1) == true }) {
            if (ProjectManager.getInstance().openProjects.any { it.basePath?.contains("GitTest") == true }) {
//                mainPanel.add(afterClonedPanel)
                val studentJson = RafApiClient.getStudent("1")
                val student = Gson().fromJson(studentJson, Student::class.java)
                // Display the student information in the plugin window
                outputArea.text = "Student info: \n ${student}"
                afterClonedPanel.add(outputArea)
                // Add the panel to the ToolWindow
                val contentFactory = ContentFactory.getInstance()
                val content = contentFactory.createContent(afterClonedPanel, "", false)
                toolWindow.contentManager.addContent(content)
            }
            else {
//                mainPanel.add(formPanel)
                // Add the panel to the ToolWindow
                val contentFactory = ContentFactory.getInstance()
                val content = contentFactory.createContent(formPanel, "", false)
                toolWindow.contentManager.addContent(content)
            }

        }

        initialPanel.add(checkProjectButton)
        mainPanel.add(initialPanel)

        // Add the panel to the ToolWindow
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(mainPanel, "", false)
        toolWindow.contentManager.addContent(content)

//        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
//        toolWindow.contentManager.addContent(content)
    }

    private fun makeField(name: String, field: JTextField): JPanel {

        val labelText = JLabel(name)
        labelText.minimumSize = Dimension(100, labelText.minimumSize.height)
        labelText.preferredSize = Dimension(100, labelText.preferredSize.height)
        labelText.border = BorderFactory.createCompoundBorder(
                EmptyBorder(0, 0, 0, 10),  // Padding around the label
                EmptyBorder(0, 0, 0, 0))

        val labelPanel = JPanel()
        labelPanel.layout = BoxLayout(labelPanel, BoxLayout.X_AXIS)
        labelPanel.add(labelText)
        labelPanel.add(Box.createHorizontalGlue())  // This will take all extra space

        val fieldPanel = JPanel(BorderLayout())
        fieldPanel.add(labelPanel, BorderLayout.WEST)
        fieldPanel.add(field, BorderLayout.CENTER)

        return fieldPanel
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(toolWindow: ToolWindow) {

        private val service = toolWindow.project.service<MyProjectService>()

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            val label = JBLabel(MyBundle.message("randomLabel", "?"))

            add(label)
            add(JButton(MyBundle.message("shuffle")).apply {
                addActionListener {
                    label.text = MyBundle.message("randomLabel", service.getRandomNumber())
                }
            })
        }
    }
}
