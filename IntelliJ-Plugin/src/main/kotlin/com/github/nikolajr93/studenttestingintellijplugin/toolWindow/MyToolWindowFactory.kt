package com.github.nikolajr93.studenttestingintellijplugin.toolWindow


import com.github.nikolajr93.studenttestingintellijplugin.Config
import com.github.nikolajr93.studenttestingintellijplugin.GitServerHttpService
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
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.Label
import com.intellij.ui.content.ContentFactory
import java.awt.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.util.*
import javax.swing.*
import javax.swing.border.EmptyBorder
import kotlin.properties.Delegates


class MyToolWindowFactory : ToolWindowFactory {

    private lateinit var studentsFirstNameTF: JTextField
    private lateinit var studentsLastNameTF: JTextField
    private lateinit var studentsStudyProgramTF: JTextField
    private lateinit var studentsIndexNumberTF: JTextField
    private lateinit var studentsStartYearTF: JTextField
    private lateinit var studentsTaskGroupTF: JTextField
    private lateinit var classroomNameTF: JTextField
    private lateinit var outputArea: JTextArea
    private lateinit var cloningReportArea: JTextArea
    private lateinit var testGroupCB: JComboBox<Any>
    private lateinit var subjectCB: JComboBox<Any>

    private lateinit var studentsIndexCombined: String
    private lateinit var studentReturnedJSON: String
    private lateinit var studentReturnedJSONLocal: String
    private lateinit var localStudentObject: Student
    private lateinit var remoteStudentObject1: Student

//    private lateinit var objectMapper: ObjectMapper
    private lateinit var gson: Gson


    private lateinit var mainPanel: JPanel
    private lateinit var initialPanel: JPanel
    private lateinit var formPanel: JPanel
    private lateinit var studentEnrollmentInfoPanel: JPanel
    private lateinit var studentsTestSpecificPanel: JPanel
    private lateinit var comboBoxPanel: JPanel
    private lateinit var fieldsPanel: JPanel
    private lateinit var afterClonedPanel: JPanel
    private lateinit var toolWindow: ToolWindow
    private lateinit var contentFactory: ContentFactory

//    private lateinit var connection: MessageBusConnection

    private lateinit var studentIndex: String
    private var isSuccess by Delegates.notNull<Boolean>()

    init {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
        // Gson added
        gson = Gson()

        isSuccess = false

        studentReturnedJSONLocal = File(Config.STUDENT_INFO_FILE_PATH).readText()
        localStudentObject = gson.fromJson(studentReturnedJSONLocal, Student::class.java)

//        Za sledecu verziju povuci indeks studenta iz environment variabli (user sadrzi indeks)
        var studentReturnedString = RafApiClient.getStudent(MyBundle.studentId)
        MyBundle.returnedStudentString = studentReturnedString
        remoteStudentObject1 = gson.fromJson(studentReturnedString, Student::class.java)
        MyBundle.returnedStudent = remoteStudentObject1
//        MyBundle.studentId = "M532023"

        MyBundle.currUsername = System.getenv("username")
        if(MyBundle.currUsername.contains("23")){
            MyBundle.username = MyBundle.currUsername
        }
//        MyBundle.username = System.getenv("username")
        MyBundle.computerName = System.getenv("computername")
        var firstDigitPos = MyBundle.username.indexOfFirst { it.isDigit() }
        var lastDigitPos = MyBundle.username.indexOfLast { it.isDigit() }
        MyBundle.builtStudentId = MyBundle.username.substring(lastDigitPos+1).uppercase(Locale.getDefault()) +
//                MyBundle.username.substring(firstDigitPos, lastDigitPos - 1) + "20" +
                MyBundle.username.substring(firstDigitPos, lastDigitPos - 1) + LocalDate.now().year/100 +
                MyBundle.username.substring(lastDigitPos -1, lastDigitPos +1)
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
//        this.formPanel = JPanel()
//        formPanel.layout = BoxLayout(formPanel, BoxLayout.Y_AXIS)
        this.formPanel = JPanel(BorderLayout())
        formPanel.border = BorderFactory.createEmptyBorder(10, 20, 10, 20)

        this.fieldsPanel = JPanel(GridLayout(0,1))

        this.initialPanel = JPanel()
        initialPanel.layout = BoxLayout(initialPanel, BoxLayout.Y_AXIS)
        initialPanel.border = BorderFactory.createEmptyBorder(10, 20, 10, 20)

        // Add input fields labels
        studentsFirstNameTF = JTextField(20)
        studentsFirstNameTF.preferredSize = Dimension(300,24)
//        formPanel.add(makeField("Student Index:", studentIndexInput))
        studentsFirstNameTF.text = localStudentObject.firstName
        fieldsPanel.add(makeField("First name:", studentsFirstNameTF))
//        fieldsPanel.add(JLabel("Student Index:"))
//        fieldsPanel.add(studentIndexInput)

        studentsLastNameTF = JTextField(20)
        studentsLastNameTF.preferredSize = Dimension(300, 24)
        studentsLastNameTF.text = localStudentObject.lastName
        fieldsPanel.add(makeField("Last name:", studentsLastNameTF))

        this.studentEnrollmentInfoPanel = JPanel(GridLayout(0,3))

        studentsStudyProgramTF = JTextField(20)
        studentsStudyProgramTF.preferredSize = Dimension(300, 24)
//        fieldsPanel.add(makeField("Study program:", studentsLastNameTF))
        studentsStudyProgramTF.text = localStudentObject.studyProgram
        studentEnrollmentInfoPanel.add(makeField("Program:", studentsStudyProgramTF))
//        studentEnrollmentInfoPanel.add(makeSmallField("Program:", studentsStudyProgramTF))

        studentsIndexNumberTF = JTextField(20)
        studentsIndexNumberTF.preferredSize = Dimension(300, 24)
//        fieldsPanel.add(makeField("Index number:", studentsIndexNumberTF))
        studentsIndexNumberTF.text = localStudentObject.indexNumber.toString()
        studentEnrollmentInfoPanel.add(makeSmallField("Number:", studentsIndexNumberTF))
//        studentEnrollmentInfoPanel.add(makeSmallField("No.", studentsIndexNumberTF))

//        fieldsPanel.add(studentEnrollmentInfoPanel)

        studentsStartYearTF = JTextField(20)
        studentsStartYearTF.preferredSize = Dimension(300, 24)
        studentsStartYearTF.text = localStudentObject.startYear
//        fieldsPanel.add(makeField("Enrollment year:", studentsStartYearTF))
        studentEnrollmentInfoPanel.add(makeSmallField("Year:", studentsStartYearTF))
//        studentEnrollmentInfoPanel.add(makeSmallField("Year:", studentsStartYearTF))

        fieldsPanel.add(studentEnrollmentInfoPanel)

//        this.studentsTestSpecificPanel = JPanel(GridLayout(0,3))
//        this.studentsTestSpecificPanel = JPanel(GridLayout(0,4))
        this.studentsTestSpecificPanel = JPanel(GridLayout(0,5))
//        this.studentsTestSpecificPanel = JPanel(GridBagLayout())


        classroomNameTF = JTextField(20)
        classroomNameTF.preferredSize = Dimension(300, 24)
        classroomNameTF.text = localStudentObject.classroom
//        studentsTestSpecificPanel.add(makeField("Classroom:", classroomNameTF))
//        studentsTestSpecificPanel.add()


        val classroomLabelText = JLabel("Classroom:")
        classroomLabelText.minimumSize = Dimension(20, classroomLabelText.minimumSize.height)
        classroomLabelText.preferredSize = Dimension(70, classroomLabelText.preferredSize.height)
        classroomLabelText.border = BorderFactory.createCompoundBorder(
                EmptyBorder(0, 0, 0, 10),  // Padding around the label
                EmptyBorder(0, 0, 0, 0))

        studentsTestSpecificPanel.add(classroomLabelText)
        studentsTestSpecificPanel.add(classroomNameTF)

//        this.comboBoxPanel = JPanel(GridLayout(0, 2))

//        val testGroupLabelText = JLabel("Test group:")
        val testGroupLabelText = JLabel("Asgmt. grp:")
        testGroupLabelText.minimumSize = Dimension(20, testGroupLabelText.minimumSize.height)
        testGroupLabelText.preferredSize = Dimension(70, testGroupLabelText.preferredSize.height)
        testGroupLabelText.border = BorderFactory.createCompoundBorder(
                EmptyBorder(0, 0, 0, 10),  // Padding around the label
                EmptyBorder(0, 0, 0, 0))

//        val subjectLabelText = JLabel("Subject:")
        val subjectLabelText = JLabel("Asgmt:")
        subjectLabelText.minimumSize = Dimension(20, subjectLabelText.minimumSize.height)
        subjectLabelText.preferredSize = Dimension(70, subjectLabelText.preferredSize.height)
        subjectLabelText.border = BorderFactory.createCompoundBorder(
                EmptyBorder(0, 0, 0, 10),  // Padding around the label
                EmptyBorder(0, 0, 0, 0))

        val assignmentChoices = arrayOf("Grupa 1", "Grupa 2", "Grupa 3", "Grupa 4", "Grupa 5", "Grupa 6")
        val subjectChoices = arrayOf("OOP", "VP", "NVP", "MSA", "SK", "TS")
        // Create a JComboBox with the choices
        testGroupCB = JComboBox(assignmentChoices)
        subjectCB = JComboBox(subjectChoices)
        subjectCB.isEnabled = false

        studentsTestSpecificPanel.add(subjectLabelText)

        studentsTestSpecificPanel.add(subjectCB)


//        studentsTestSpecificPanel.add(testGroupLabelText)
        studentsTestSpecificPanel.add(testGroupCB)

        fieldsPanel.add(studentsTestSpecificPanel)


        outputArea = JTextArea();
        cloningReportArea = JTextArea();

        val commitButton = JButton("Commit")

        // Create a Sign in button and add it to the panel
        val signInButton = JButton("Begin")
        signInButton.addActionListener {

            studentsIndexCombined = studentsStudyProgramTF.getText().uppercase(Locale.getDefault()) + studentsIndexNumberTF.getText() + studentsStartYearTF.getText();

            studentReturnedJSON = RafApiClient.getStudent(studentsIndexCombined)
//            studentReturnedJSONLocal = File(Config.STUDENT_INFO_FILE_PATH).readText()
////            localStudentObject = objectMapper.readValue(studentReturnedJSON, Student::class.java)
//            //  Ukloniti znakove naovdnika oko null vrednosti u JSON fajlu
////            localStudentObject = gson.fromJson(studentReturnedJSON, Student::class.java)
//            localStudentObject = gson.fromJson(studentReturnedJSONLocal, Student::class.java)


            var isFirstNameTheSame = localStudentObject.firstName.equals(studentsFirstNameTF.getText(), true)
            var isLastNameTheSame = localStudentObject.lastName.equals(studentsLastNameTF.getText(), true)

            var projectToOpen = "C:\\Projects\\GitTest"

            if(isFirstNameTheSame && isLastNameTheSame){
//                localStudentObject.classroom = classroomNameTF.getText()
                localStudentObject.taskGroup = testGroupCB.selectedItem.toString()
                val studentInfoFilePath = Paths.get(Config.STUDENT_INFO_FILE_PATH1)
                val studentInfoFilePath2 = Paths.get(Config.STUDENT_INFO_FILE_PATH2)
                val studentTokenMessagePath = Paths.get(Config.STUDENT_TOKEN_MESSAGE_PATH)
                val studentTokenMessagePathForAfterReset = Paths.get(Config.STUDENT_TOKEN_MESSAGE_PATH_AFTER_RESET)
                val studentRepoAndForkMessagesPath = Paths.get(Config.STUDENT_REPO_AND_FORK_MESSAGES_PATH)
                Files.newBufferedWriter(studentInfoFilePath).use { writer ->
//                    writer.write(studentReturnedJSON)
                    writer.write(gson.toJson(localStudentObject))
//                    writer.newLine()
                }
                val currentProject1 = project

                // Setting current project base path in MyBundle for future commits
                MyBundle.currentProjectBasePath = currentProject1?.basePath.toString()
//                Upisivanje dohvacenog studenta
                Files.newBufferedWriter(studentInfoFilePath2).use { writer ->
//                    writer.write(studentReturnedJSON)
                    writer.write(MyBundle.returnedStudentString)
                    writer.newLine()
                    writer.write(gson.toJson(MyBundle.returnedStudent))
                    writer.newLine()
                    writer.write(MyBundle.builtStudentId)
                    writer.newLine()
                    writer.write(MyBundle.currentProjectBasePath)
                    writer.newLine()
                    writer.write(MyBundle.computerName)
                    writer.newLine()
                    writer.write(MyBundle.currUsername)
                }

                var tempStudentTokenMessageReturned = RafApiClient.authorizeStudent(MyBundle.studentId)
                if(!tempStudentTokenMessageReturned.contains("is already authorized")){
                    MyBundle.studentToken = tempStudentTokenMessageReturned
//                    Regex for UUID parsing
                    // Regular expression to extract the token value
                    val regex = Regex("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")
                    // Find the token value in the MyBundle.studentToken
                    val onlyTokenResult = regex.find(MyBundle.studentToken)
                    // If a match was found, get the value
                    val onlyTokenValue = onlyTokenResult?.value
                    if (onlyTokenValue != null) {
                        MyBundle.strictStudentToken = onlyTokenValue
                    }
                    // Alternative parsing
                    val jsonString = MyBundle.studentToken
                    val decodedString = jsonString.replace("\\\"", "\"")
                    val tokenT = decodedString.split("\"value\":\"")[1].split("\"")[0]
                    MyBundle.strictStudentTokenAlternative = tokenT
//                  Upisivanje dohvacene poruke sa studentskim tokenom
                    Files.newBufferedWriter(studentTokenMessagePath).use { writer ->
                        writer.write(MyBundle.studentToken)
                        writer.newLine()
                        writer.write(MyBundle.strictStudentToken)
                        writer.newLine()
                        writer.write(MyBundle.strictStudentTokenAlternative)
                    }
                    MyBundle.examString = "OopZadatak" + (testGroupCB.selectedIndex + 1)
                    val testRepoMessageString = RafApiClient.getRepository(MyBundle.studentId, MyBundle.strictStudentToken, MyBundle.examString)
                    val studentForkMessageString = RafApiClient.getFork(MyBundle.studentId, MyBundle.strictStudentToken)
                    MyBundle.testRepoMessage = testRepoMessageString
                    MyBundle.studentForkMessage = studentForkMessageString
//                    Test repo path parsing
                    val repoPathString = MyBundle.testRepoMessage
                    val decodedRepoPathString = repoPathString.replace("\\\"", "\"")
                    val repoPathValue = decodedRepoPathString.split("\"message\":\"\"")[1].dropLast(3)
                    MyBundle.testRepoPath = repoPathValue
//                    Test repo path parsing
                    val forkPathString = MyBundle.studentForkMessage
                    val decodedForkPathString = forkPathString.replace("\\\"", "\"")
                    val forkPathValue = decodedForkPathString.split("\"message\":\"\"")[1].dropLast(3)
                    MyBundle.studentForkPath = forkPathValue
                    Files.newBufferedWriter(studentRepoAndForkMessagesPath).use { writer ->
                        writer.write(MyBundle.testRepoMessage)
                        writer.newLine()
                        writer.write(MyBundle.studentForkMessage)
                        writer.newLine()
                        writer.write(MyBundle.testRepoPath)
                        writer.newLine()
                        writer.write(MyBundle.studentForkPath)
                    }
                }else{
                    Files.newBufferedWriter(studentTokenMessagePathForAfterReset).use { writer ->
                        writer.write(MyBundle.studentToken)
//                    writer.newLine()
                    }
                }

                // Run the clone operation in a worker thread to avoid blocking the UI.
                ApplicationManager.getApplication().executeOnPooledThread {
                    // Calls the 'cloneRepository' method which is a blocking operation
//                    val isSuccess = GitServerSshService.cloneRepository(Config.SSH_LOCAL_PATH_1)
//                    val isSuccess = GitServerSshService.cloneRepository2(Config.SSH_LOCAL_PATH_1)
//                    isSuccess = GitServerHttpService.cloneRepository(Config.SSH_LOCAL_PATH_1)
//                    Liniju ispod ukloniti i zameniti linijom iznad kada Git proradi
//                    isSuccess = GitServerHttpService.cloneRepository(Config.SSH_LOCAL_PATH_2)

                    val currentProjectt = project
                    val projectDirr = currentProjectt?.basePath?.let { Paths.get(it) }
                    projectDirr?.toFile()?.listFiles()
//                            ?.filter { it.name != ".idea" }
                            ?.forEach { it.deleteRecursively() }

//                    Working lines:
//                    isSuccess = GitServerHttpService.cloneRepository(Config.SSH_LOCAL_PATH_1)
                    isSuccess = GitServerHttpService.cloneRepository(project.basePath!!)
//                    Liniju ispod ukloniti kada Git proradi:
//                    isSuccess = true;
                    // `invokeLater` schedules this task to run on the Event Dispatch Thread (EDT).
                    ApplicationManager.getApplication().invokeLater {
                        cloningReportArea.text = if (isSuccess) "Repository cloned successfully." else "Failed to clone repository."
                        if(isSuccess){
                            studentsFirstNameTF.isEnabled = false
                            studentsLastNameTF.isEnabled = false
                            studentsStudyProgramTF.isEnabled = false
                            studentsIndexNumberTF.isEnabled = false
                            studentsStartYearTF.isEnabled = false
                            classroomNameTF.isEnabled = false
                            testGroupCB.isEnabled = false
                            projectToOpen = Config.SSH_LOCAL_PATH_1;
                            //  Opening the new project
//                            ApplicationManager.getApplication().invokeLater(Runnable {
//                                ApplicationManager.getApplication().runReadAction {
//                                    // Get the currently opened projects
//                                    val openProjects = projectManager.openProjects
////                    val openProjects = projectManagerEx.openProjects
//
//                                    if (openProjects.none { it.projectFilePath == projectToOpen }) {
//                                        projectManager.loadAndOpenProject(projectToOpen)
////                        projectManagerEx.openProject(Paths.get(projectToOpen), OpenProjectTask(forceOpenInNewFrame = false))
//                                    }
//                                }
//                            })

                            //  Overwriting the opened project
                            ApplicationManager.getApplication().executeOnPooledThread {
//                                val isSuccess = GitServerHttpService.cloneRepository(Config.SSH_LOCAL_PATH_1)
                                if(isSuccess){
                                    MyBundle.repoCloned = true
                                    commitButton.isVisible = true
                                    signInButton.isVisible = false
                                    ApplicationManager.getApplication().invokeLater(Runnable {
//                                        Two project directories cloning solution:

//                                        val currentProject = project
//                                        val projectDir = currentProject?.basePath?.let { Paths.get(it) }
//                                        projectDir?.toFile()?.listFiles()
//                                                ?.filter { it.name != ".idea" }
//                                                ?.forEach { it.deleteRecursively() }
//                                        val sourceDir = Paths.get(Config.SSH_LOCAL_PATH_1)
//                                        sourceDir.toFile().listFiles()
//                                                ?.filter { it.name != ".git" }
////                                                ?.forEach { it.copyRecursively(Paths.get(projectDir.toString(), it.name).toFile()) }
//                                                ?.forEach { it.copyRecursively(Paths.get(projectDir.toString(), it.name).toFile(), overwrite = true) }


//                                        // Setting current project base path in MyBundle for future commits
//                                        MyBundle.currentProjectBasePath = currentProject?.basePath.toString()
                                        // Refresh Virtual File System
                                        val virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(project.basePath!!)
                                        virtualFile?.refresh(false, true)
                                    })
                                }
                            }
                        }else{
                            val openProjects = projectManager.openProjects
                            if (openProjects.none { it.projectFilePath == projectToOpen }) {
                                projectManager.loadAndOpenProject(projectToOpen)
                            }
                        }
                    }
                }
            }else{
                val labelTryAgain = JLabel("Wrong information, plase try again.")
                fieldsPanel.add(labelTryAgain)
            }


        }

//        val commitButton = JButton("Commit")
        commitButton.preferredSize = Dimension(100, 30)
        commitButton.maximumSize = Dimension(100, 30)
        commitButton.addActionListener {
            // Get the current project
            val currentProject = ProjectManager.getInstance().openProjects[0]

            // Save all open files
            FileDocumentManager.getInstance().saveAllDocuments()

            // Start a background thread for the blocking operations
            ApplicationManager.getApplication().executeOnPooledThread {
                // Use the project base path as the repository path. Replace "newBranch" with the desired branch name.
//                val isSuccess = GitServerSshService.pushToRepository2(
//                Dodati kredencijale u commit msg
//                Staviti Forkstring kao naziv brancha
                val isPushSuccess = GitServerHttpService.pushToRepository(
//                        Config.SSH_LOCAL_PATH_1,
                        MyBundle.currentProjectBasePath,
                        "student1Branch",
                        "Initial commit"
                )

                // If the push operation is successful, close and dispose of the project
                if (isPushSuccess) {
                    val confirmationDialog = JOptionPane.showConfirmDialog(
                            null,
                            "Da li ste sigurni da zelite da predate rad?",
                            "Potvrda o predaji rada",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    )
                    // check if user clicked yes
                    if (confirmationDialog == JOptionPane.YES_OPTION) {
                        ApplicationManager.getApplication().invokeLater {
                            ProjectManager.getInstance().closeAndDispose(currentProject)
                        }
                    }
//                    ApplicationManager.getApplication().invokeLater {
//                        ProjectManager.getInstance().closeAndDispose(currentProject)
//                    }
                } else {
                    // Handle failure - this prints an error to the console, but you'll likely want to replace this with your own error handling
                    println("Failed to push changes to new branch.")
                    //  Dodato samo radi demonstracije
                    val confirmationDialog = JOptionPane.showConfirmDialog(
                            null,
                            "Da li ste sigurni da zelite da predate rad?",
                            "Potvrda o predaji rada",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    )
                    // check if user clicked yes
                    if (confirmationDialog == JOptionPane.YES_OPTION) {
                        ApplicationManager.getApplication().invokeLater {
                            ProjectManager.getInstance().closeAndDispose(currentProject)
                        }
                    }
                }
            }

//            // Close and dispose of the project (Deprecated)
//            ApplicationManager.getApplication().invokeLater {
//                ProjectManager.getInstance().closeAndDispose(currentProject)
//            }
        }

        fieldsPanel.add(signInButton)
        commitButton.isVisible = false
        if (MyBundle.repoCloned){
            commitButton.isVisible = true
        }
        fieldsPanel.add(commitButton)
        //Staro
//        formPanel.add(commitButton)
//        formPanel.add(fieldsPanel, BorderLayout.NORTH)

//        afterClonedPanel.add(commitButton)

        val checkProjectButton = JButton("Check Project")

        initialPanel.add(Label(System.getProperty("user.name")))
        initialPanel.add(checkProjectButton)
        //Saro, sa init panelom
//        mainPanel.add(initialPanel, BorderLayout.NORTH)

        mainPanel.add(fieldsPanel, BorderLayout.NORTH)

        // Add the panel to the ToolWindow
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(mainPanel, "", false)
        toolWindow.contentManager.addContent(content)

//        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
//        toolWindow.contentManager.addContent(content)
    }

    private fun makeField(name: String, field: JTextField): JPanel {

        val labelText = JLabel(name)
        labelText.minimumSize = Dimension(20, labelText.minimumSize.height)
        labelText.preferredSize = Dimension(70, labelText.preferredSize.height)
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

    private fun makeSmallField(name: String, field: JTextField): JPanel {

        val labelText = JLabel(name)
        labelText.minimumSize = Dimension(20, labelText.minimumSize.height)
        labelText.preferredSize = Dimension(60, labelText.preferredSize.height)
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
