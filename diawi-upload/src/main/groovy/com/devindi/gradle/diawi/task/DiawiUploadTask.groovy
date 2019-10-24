package com.devindi.gradle.diawi.task

import com.android.build.gradle.api.BaseVariant
import com.devindi.gradle.diawi.diawi.DiawiClient
import com.devindi.gradle.diawi.diawi.UploadParams
import com.devindi.gradle.diawi.dsl.DiawiUploadExtension
import com.devindi.gradle.diawi.task.internal.BlockingPollingService
import com.devindi.gradle.diawi.task.internal.ReplacementItem
import com.devindi.gradle.diawi.task.internal.ResultFormat
import com.devindi.gradle.diawi.task.internal.UploadParamsFactory
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class DiawiUploadTask extends DefaultTask {

    DiawiClient client
    BaseVariant variant
    DiawiUploadExtension uploadExtension
    BlockingPollingService pollingService
    ResultFormat resultFormat
    UploadParamsFactory paramsFactory

    @SuppressWarnings("unused")
    @TaskAction
    def upload() {
        UploadParams params = paramsFactory.createParams(uploadExtension)

        def jobId = client.upload(variant.outputs[0].outputFile, params)

        def hash = pollingService.blockingGet(
                { client.checkJobStatus(uploadExtension.token, jobId) },
                1000,
                {
                    throw new IllegalArgumentException("Can't get link from diawi. Job id is $jobId Also you can check diawi dashboard https://dashboard.diawi.com/apps")
                })

        handleDiawiHash(hash)
    }

    private void handleDiawiHash(String hash) {
        println "Build uploaded OK. Hash = $hash"

        String outputFormat = uploadExtension.output.format
        OutputStream outputStream = uploadExtension.output.stream

        List<ReplacementItem> replacements = [
                new ReplacementItem("date", new Date().toString()),
                new ReplacementItem("hash", hash),
                new ReplacementItem("link", "https://install.diawi.com/$hash"),
                new ReplacementItem("file", variant.outputs[0].outputFile.name),
                new ReplacementItem("badge_url", "https://api.diawi.com/badge/$hash/available"),
                new ReplacementItem("badge_html", "<a href=\"https://i.diawi.com/$hash\" title=\"Diawi link\"><img src=\"https://api.diawi.com/badge/$hash/available\" alt=\"Diawi link\"/></a>"),
                new ReplacementItem("badge_md", "[![Diawi link](https://api.diawi.com/badge/$hash/available)](https://i.diawi.com/$hash)")
        ]

        def formattedOutput = resultFormat.format(outputFormat, replacements)

        outputStream.write(formattedOutput.bytes)
        outputStream.flush()
    }
}