package com.devindi.gradle.diawi.task

import com.android.build.gradle.api.BaseVariant
import com.devindi.gradle.diawi.diawi.DiawiClient
import com.devindi.gradle.diawi.dsl.DiawiUploadExtension
import com.devindi.gradle.diawi.task.internal.BlockingPollingService
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class DiawiUploadTask extends DefaultTask {

    DiawiClient client
    BaseVariant variant
    DiawiUploadExtension uploadExtension
    BlockingPollingService pollingService = new BlockingPollingService()

    @TaskAction
    def upload() {
        def jobId = client.upload(variant.outputs[0].outputFile, uploadExtension)

        def link = pollingService.blockingGet(
                { client.checkJobStatus(uploadExtension.token, jobId) },
                1000,
                {
                    throw new IllegalArgumentException("Can't get link from diawi. Job id is $jobId Also you can check diawi dashboard https://dashboard.diawi.com/apps")
                })

        println "Build uploaded OK. Link is $link"
        if (uploadExtension.standardOutput) {
            uploadExtension.standardOutput.write(new Date().toString().bytes)
            uploadExtension.standardOutput.write(" - ".bytes)
            uploadExtension.standardOutput.write(variant.outputs[0].outputFile.name.bytes)
            uploadExtension.standardOutput.write(" - ".bytes)
            uploadExtension.standardOutput.write(link.bytes)
            uploadExtension.standardOutput.flush()
        }
    }
}