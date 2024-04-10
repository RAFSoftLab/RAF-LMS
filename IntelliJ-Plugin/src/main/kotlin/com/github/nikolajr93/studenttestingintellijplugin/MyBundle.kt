package com.github.nikolajr93.studenttestingintellijplugin

import com.github.nikolajr93.studenttestingintellijplugin.api.Student
import com.intellij.DynamicBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

@NonNls
private const val BUNDLE = "messages.MyBundle"

object MyBundle : DynamicBundle(BUNDLE) {

    @JvmStatic
    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getMessage(key, *params)

    @Suppress("unused")
    @JvmStatic
    fun messagePointer(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getLazyMessage(key, *params)

    var repoCloned = false
    var returnedStudent = Student()
    var returnedStudentString = ""
    var studentToken = ""
    var strictStudentToken = ""
    var strictStudentTokenAlternative = ""
    var examString = ""
//    Get the value from environment variables
    var studentId = "M512023"
    var testRepoMessage = ""
    var studentForkMessage = ""
    var testRepoPath = ""
    var studentForkPath = ""

    var username = "nredzic5123m"
    var builtStudentId = ""

    var computerName = ""
    var currUsername = ""

    var currentProjectBasePath = ""
}
