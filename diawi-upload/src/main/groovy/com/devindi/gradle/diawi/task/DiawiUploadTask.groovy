package com.devindi.gradle.diawi.task

import com.android.build.gradle.api.BaseVariant
import com.devindi.gradle.diawi.diawi.DiawiClient
import com.devindi.gradle.diawi.dsl.DiawiUploadExtension
import com.devindi.gradle.diawi.task.internal.BlockingPollingService
import com.devindi.gradle.diawi.task.internal.ReplacementItem
import com.devindi.gradle.diawi.task.internal.ResultFormat
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class DiawiUploadTask extends DefaultTask {

    DiawiClient client
    BaseVariant variant
    DiawiUploadExtension uploadExtension
    BlockingPollingService pollingService
    ResultFormat resultFormat

    @TaskAction
    def upload() {
        def jobId = client.upload(variant.outputs[0].outputFile, uploadExtension)

        def hash = pollingService.blockingGet(
                { client.checkJobStatus(uploadExtension.token, jobId) },
                1000,
                {
                    throw new IllegalArgumentException("Can't get link from diawi. Job id is $jobId Also you can check diawi dashboard https://dashboard.diawi.com/apps")
                })

        println "Build uploaded OK. Hash = $hash"

        def replacements = [
                new ReplacementItem("date", new Date().toString()),
                new ReplacementItem("hash", hash),
                new ReplacementItem("link", "https://install.diawi.com/$hash"),
                new ReplacementItem("file", variant.outputs[0].outputFile.name)
        ]
        def formattedResult = resultFormat.format(uploadExtension.resultFormat, replacements)

        if (uploadExtension.standardOutput) {
            uploadExtension.standardOutput.write(formattedResult.bytes)
            uploadExtension.standardOutput.flush()
        }
    }
}