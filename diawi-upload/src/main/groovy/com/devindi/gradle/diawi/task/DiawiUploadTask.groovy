package com.devindi.gradle.diawi.task

import com.devindi.gradle.diawi.dsl.DiawiUploadExtension
import com.devindi.gradle.diawi.tools.PropertyHelper
import com.devindi.gradle.diawi.tools.Publisher
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.*
import org.gradle.api.tasks.TaskAction

class DiawiUploadTask extends DefaultTask {

    ApplicationVariant variant
    DiawiUploadExtension uploadExtension

    @TaskAction
    def upload() {
        String token = uploadExtension.token
        if (!token) {
            token = PropertyHelper.readProperties(project.file('local.properties'))['diawi.token']
        }
        if (!token) {
            token = System.console().readLine('>Define diawi token with diawi.token in the local.properties file or type it here: ')
        }


        Publisher.hackSecurity()

        def jobId = Publisher.publish(token, variant.outputs[0].outputFile, uploadExtension.password, uploadExtension.comment, uploadExtension.callbackUrl, uploadExtension.callbackEmail, uploadExtension.wallOfApps)
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
            throw new IllegalArgumentException("Can't get link from diawi. Job id is $jobId Also you can check diawi dashboard https://dashboard.diawi.com/apps")
        }
    }
}