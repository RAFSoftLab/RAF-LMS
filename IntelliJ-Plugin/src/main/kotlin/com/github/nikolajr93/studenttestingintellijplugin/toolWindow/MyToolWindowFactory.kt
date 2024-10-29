package com.github.nikolajr93.studenttestingintellijplugin.toolWindow


import com.github.nikolajr93.studenttestingintellijplugin.Config
import com.github.nikolajr93.studenttestingintellijplugin.GitServerHttpService
import com.github.nikolajr93.studenttestingintellijplugin.MyBundle
import com.github.nikolajr93.studenttestingintellijplugin.api.RafApiClient
import com.github.nikolajr93.studenttestingintellijplugin.api.Student
import com.github.nikolajr93.studenttestingintellijplugin.api.StudentInfoDto
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
    private lateinit var remoteStudentObject2: StudentInfoDto

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

        var uname = System.getenv("username");
//        MyBundle.username = uname
        MyBundle.computerName = System.getenv("computername")
        if(MyBundle.computerName.startsWith("u", true)){
            MyBundle.classroom = "Raf" + MyBundle.computerName.substring(2,3)
        }
        var firstDigitPos = MyBundle.username.indexOfFirst { it.isDigit() }
        var lastDigitPos = MyBundle.username.indexOfLast { it.isDigit() }
        MyBundle.builtStudentId = MyBundle.username.substring(lastDigitPos+1).uppercase(Locale.getDefault()) +
//                MyBundle.username.substring(firstDigitPos, lastDigitPos - 1) + "20" +
                MyBundle.username.substring(firstDigitPos, lastDigitPos - 1) + LocalDate.now().year/100 +
                MyBundle.username.substring(lastDigitPos -1, lastDigitPos +1)

//        var firstDigitPos1 = uname.indexOfFirst { it.isDigit() }
//        var lastDigitPos1 = uname.indexOfLast { it.isDigit() }
//        MyBundle.builtStudentId = uname.substring(lastDigitPos1+1).uppercase(Locale.getDefault()) +
////                MyBundle.username.substring(firstDigitPos, lastDigitPos - 1) + "20" +
//                uname.substring(firstDigitPos1, lastDigitPos1 - 1) + LocalDate.now().year/100 +
//                uname.substring(lastDigitPos1 -1, lastDigitPos1 +1)

//        studentReturnedJSONLocal = File(Config.STUDENT_INFO_FILE_PATH).readText()
//        localStudentObject = gson.fromJson(studentReturnedJSONLocal, Student::class.java)

//        Za sledecu verziju povuci indeks studenta iz environment variabli (user sadrzi indeks)
//        var studentReturnedString = RafApiClient.getStudent(MyBundle.studentId)
        var studentReturnedString = RafApiClient.getStudent(MyBundle.builtStudentId)
        MyBundle.returnedStudentString = studentReturnedString
//        remoteStudentObject1 = gson.fromJson(studentReturnedString, Student::class.java)
        remoteStudentObject2 = gson.fromJson(studentReturnedString, StudentInfoDto::class.java)
//        MyBundle.returnedStudent = remoteStudentObject1
        MyBundle.returnedStudent2 = remoteStudentObject2


        MyBundle.currUsername = System.getenv("username")
//        if(MyBundle.currUsername.contains("23")){
        if(MyBundle.currUsername.length > 3){
            MyBundle.username = MyBundle.currUsername
        }

    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow)

        this.contentFactory = ContentFactory.getInstance()

        // Get instance of ProjectManagerEx for project handling
        val projectManager = ProjectManager.getInstance()

        // Create a panel to hold all components
        mainPanel = JPanel(BorderLayout())

        this.afterClonedPanel = JPanel()
        afterClonedPanel.layout = BoxLayout(afterClonedPanel, BoxLayout.Y_AXIS)
        afterClonedPanel.border = BorderFactory.createEmptyBorder(10, 20, 10, 20)

        this.formPanel = JPanel(BorderLayout())
        formPanel.border = BorderFactory.createEmptyBorder(10, 20, 10, 20)

        this.fieldsPanel = JPanel(GridLayout(0,1))

        this.initialPanel = JPanel()
        initialPanel.layout = BoxLayout(initialPanel, BoxLayout.Y_AXIS)
        initialPanel.border = BorderFactory.createEmptyBorder(10, 20, 10, 20)

        // Add input fields labels
        studentsFirstNameTF = JTextField(20)
        studentsFirstNameTF.preferredSize = Dimension(300,24)

        studentsFirstNameTF.text = remoteStudentObject2.firstName
        fieldsPanel.add(makeField("First name:", studentsFirstNameTF))


        studentsLastNameTF = JTextField(20)
        studentsLastNameTF.preferredSize = Dimension(300, 24)
//        studentsLastNameTF.text = localStudentObject.lastName
        studentsLastNameTF.text = remoteStudentObject2.lastName
        fieldsPanel.add(makeField("Last name:", studentsLastNameTF))

        this.studentEnrollmentInfoPanel = JPanel(GridLayout(0,3))

        studentsStudyProgramTF = JTextField(20)
        studentsStudyProgramTF.preferredSize = Dimension(300, 24)

        studentsStudyProgramTF.text = remoteStudentObject2.studyProgramShort
        studentEnrollmentInfoPanel.add(makeField("Program:", studentsStudyProgramTF))

        studentsIndexNumberTF = JTextField(20)
        studentsIndexNumberTF.preferredSize = Dimension(300, 24)

        studentsIndexNumberTF.text = remoteStudentObject2.indexNumber.toString()
        studentEnrollmentInfoPanel.add(makeSmallField("Number:", studentsIndexNumberTF))

        studentsStartYearTF = JTextField(20)
        studentsStartYearTF.preferredSize = Dimension(300, 24)
        studentsStartYearTF.text = remoteStudentObject2.startYear
        studentEnrollmentInfoPanel.add(makeSmallField("Year:", studentsStartYearTF))

        fieldsPanel.add(studentEnrollmentInfoPanel)

        this.studentsTestSpecificPanel = JPanel(GridLayout(0,5))


        classroomNameTF = JTextField(20)
        classroomNameTF.preferredSize = Dimension(300, 24)

        classroomNameTF.text = MyBundle.classroom

        val classroomLabelText = JLabel("Classroom:")
        classroomLabelText.minimumSize = Dimension(20, classroomLabelText.minimumSize.height)
        classroomLabelText.preferredSize = Dimension(70, classroomLabelText.preferredSize.height)
        classroomLabelText.border = BorderFactory.createCompoundBorder(
                EmptyBorder(0, 0, 0, 10),  // Padding around the label
                EmptyBorder(0, 0, 0, 0))

        studentsTestSpecificPanel.add(classroomLabelText)
        studentsTestSpecificPanel.add(classroomNameTF)

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


        val assignmentChoices = arrayOf("Grupa 1", "Grupa 2", "Grupa 3", "Grupa 4")
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

//            studentsIndexCombined = studentsStudyProgramTF.getText().uppercase(Locale.getDefault()) + studentsIndexNumberTF.getText() + studentsStartYearTF.getText();
//
//            studentReturnedJSON = RafApiClient.getStudent(studentsIndexCombined)
//            studentReturnedJSONLocal = File(Config.STUDENT_INFO_FILE_PATH).readText()
////            localStudentObject = objectMapper.readValue(studentReturnedJSON, Student::class.java)
//            //  Ukloniti znakove naovdnika oko null vrednosti u JSON fajlu
////            localStudentObject = gson.fromJson(studentReturnedJSON, Student::class.java)
//            localStudentObject = gson.fromJson(studentReturnedJSONLocal, Student::class.java)


//            var isFirstNameTheSame = localStudentObject.firstName.equals(studentsFirstNameTF.getText(), true)
            var isFirstNameTheSame = remoteStudentObject2.firstName.equals(studentsFirstNameTF.getText(), true)
//            var isLastNameTheSame = localStudentObject.lastName.equals(studentsLastNameTF.getText(), true)
            var isLastNameTheSame = remoteStudentObject2.lastName.equals(studentsLastNameTF.getText(), true)

            var projectToOpen = "C:\\Projects\\GitTest"

            if(isFirstNameTheSame && isLastNameTheSame){
                val currentProject1 = project
                val studentInfoFilePath = Paths.get(currentProject1?.basePath, "..")
                val finalStudentInfoFilePath = studentInfoFilePath.resolve("studentInfo1.txt").toAbsolutePath().normalize()
                val finalStudentInfoFilePath2 = studentInfoFilePath.resolve("studentInfo2.txt").toAbsolutePath().normalize()
                val finalStudentTokenMessagePath = studentInfoFilePath.resolve("studentTokenMessage.txt").toAbsolutePath().normalize()
                val finalStudentTokenMessagePathForAfterReset = studentInfoFilePath.resolve("studentTokenMessageAfterReset.txt").toAbsolutePath().normalize()
                val finalStudentRepoAndForkMessagesPath = studentInfoFilePath.resolve("studentRepoAndForkMessages.txt").toAbsolutePath().normalize()
                Files.newBufferedWriter(finalStudentInfoFilePath).use { writer ->
                    writer.write(gson.toJson(remoteStudentObject2))
                }


                // Setting current project base path in MyBundle for future commits
                MyBundle.currentProjectBasePath = currentProject1?.basePath.toString()
//                Upisivanje dohvacenog studenta
                Files.newBufferedWriter(finalStudentInfoFilePath2).use { writer ->
                    writer.write(MyBundle.returnedStudentString)
                    writer.newLine()
                    writer.write(gson.toJson(MyBundle.returnedStudent2))
                    writer.newLine()
                    writer.write(MyBundle.builtStudentId)
                    writer.newLine()
                    writer.write(MyBundle.currentProjectBasePath)
                    writer.newLine()
                    writer.write(MyBundle.computerName)
                    writer.newLine()
                    writer.write(MyBundle.currUsername)
                }

                var tempStudentTokenMessageReturned = RafApiClient.authorizeStudent(MyBundle.builtStudentId)
                if(tempStudentTokenMessageReturned.contains("is already authorized")){
                    MyBundle.strictStudentToken = File(finalStudentTokenMessagePath.toUri()).readText()
                }else{
                    MyBundle.studentToken = tempStudentTokenMessageReturned
                    // Regular expression to extract the token value
                    val regex = Regex("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")
                    // Find the token value in the MyBundle.studentToken
                    val onlyTokenResult = regex.find(MyBundle.studentToken)
                    // If a match was found, get the value
                    val onlyTokenValue = onlyTokenResult?.value
                    if (onlyTokenValue != null) {
                        MyBundle.strictStudentToken = onlyTokenValue
                    }
                    Files.newBufferedWriter(finalStudentTokenMessagePath).use { writer ->
                        writer.write(MyBundle.strictStudentToken)
                    }
                }
                    MyBundle.examString = "OopZadatak" + (testGroupCB.selectedIndex + 1)
                    val testRepoMessageString = RafApiClient.getRepository(MyBundle.builtStudentId, MyBundle.strictStudentToken, MyBundle.examString)
                    val studentForkMessageString = RafApiClient.getFork(MyBundle.builtStudentId, MyBundle.strictStudentToken)
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
                    Files.newBufferedWriter(finalStudentRepoAndForkMessagesPath).use { writer ->
                        writer.write(MyBundle.testRepoMessage)
                        writer.newLine()
                        writer.write(MyBundle.studentForkMessage)
                        writer.newLine()
                        writer.write(MyBundle.testRepoPath)
                        writer.newLine()
                        writer.write(MyBundle.studentForkPath)
                        writer.newLine()
                        writer.write("Exam string: " + MyBundle.examString)
                    }

                // Run the clone operation in a worker thread to avoid blocking the UI.
                ApplicationManager.getApplication().executeOnPooledThread {
                    // Calls the 'cloneRepository' method which is a blocking operation

                    val currentProjectt = project
                    val projectDirr = currentProjectt?.basePath?.let { Paths.get(it) }
                    projectDirr?.toFile()?.listFiles()
//                            ?.filter { it.name != ".idea" }
                            ?.forEach { it.deleteRecursively() }

//                    Working lines:
                    isSuccess = GitServerHttpService.cloneRepositoryN(project.basePath!!, MyBundle.testRepoPath)
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
                val isPushSuccess = GitServerHttpService.pushToRepositoryN(
                        MyBundle.currentProjectBasePath,
                        MyBundle.builtStudentId,
                        MyBundle.username + " je predao rad."
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
        }

        fieldsPanel.add(signInButton)
        commitButton.isVisible = false
        if (MyBundle.repoCloned){
            commitButton.isVisible = true
        }
        fieldsPanel.add(commitButton)

        val checkProjectButton = JButton("Check Project")

        initialPanel.add(Label(System.getProperty("user.name")))
        initialPanel.add(checkProjectButton)

        mainPanel.add(fieldsPanel, BorderLayout.NORTH)

        // Add the panel to the ToolWindow
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(mainPanel, "", false)
        toolWindow.contentManager.addContent(content)

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
