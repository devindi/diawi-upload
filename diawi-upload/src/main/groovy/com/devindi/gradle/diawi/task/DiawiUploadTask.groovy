package com.devindi.gradle.diawi.task

import com.devindi.gradle.diawi.dsl.DiawiUploadExtension
import com.devindi.gradle.diawi.tools.PropertyHelper
import com.devindi.gradle.diawi.tools.Publisher
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.*
import org.gradle.api.tasks.StopActionException
import org.gradle.api.tasks.TaskAction

class DiawiUploadTask extends DefaultTask {

    ApplicationVariant variant
    DiawiUploadExtension uploadExtension

    @TaskAction
    def upload() {
        def token = uploadExtension.token
        if (!token) {
            token = PropertyHelper.readProperties(project.file('local.properties'))['diawi.token']
        }
        if (!token) {
            token = System.console().readLine('> Enter diawi token please: ')
        }

        def jobId = Publisher.publish(token, variant.outputs[0].outputFile)
        if (!jobId) {
            throw new StopActionException('APK uploading failed')
        }
        String link
        def counter = 0
        while (!link && counter < 10) {
            link = Publisher.checkURL(token, jobId)
            Thread.sleep(1000)
            counter++
        }
        if (link) {
            println "Build uploaded OK. Link is $link"
            if (uploadExtension.standardOutput) {
                uploadExtension.standardOutput.write(new Date().toString().bytes)
                uploadExtension.standardOutput.write(" - ".bytes)
                uploadExtension.standardOutput.write(variant.outputs[0].outputFile.name.bytes)
                uploadExtension.standardOutput.write(" - ".bytes)
                uploadExtension.standardOutput.write(link.bytes)
                uploadExtension.standardOutput.flush()
            }
        } else {
            throw new StopActionException('APK processing failed')
        }
    }
}