package com.devindi.gradle.diawi

import com.android.build.gradle.api.BaseVariant
import com.devindi.gradle.diawi.diawi.DiawiClient
import com.devindi.gradle.diawi.dsl.DiawiUploadExtension
import com.devindi.gradle.diawi.task.DiawiUploadTask
import com.devindi.gradle.diawi.task.internal.BlockingPollingService
import groovy.json.JsonSlurper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.gradle.api.*
import com.android.build.gradle.AppPlugin

class DiawiUploadPlugin implements Plugin<Project> {

	void apply(Project project) {

        def hasAppPlugin = project.plugins.find { p -> p instanceof AppPlugin }
        if (!hasAppPlugin) {
            throw new IllegalStateException("The 'com.android.application' plugin is required.")
        }

        def extension = project.extensions.create('diawi', DiawiUploadExtension)

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            void log(String message) {
                // Skip file content
                if(!message.contains("�")) {
                    project.logger.debug(message)
                }
            }
        })
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

        project.android.applicationVariants.all { BaseVariant variant ->
            def variantName = variant.name

            def diawiUploadTaskName = "diawiPublish$variantName"

            DiawiUploadTask diawiUploadTask = project.tasks.create(diawiUploadTaskName, DiawiUploadTask) as DiawiUploadTask
            diawiUploadTask.description = "Publish $variantName build to diawi"
            diawiUploadTask.group = 'Diawi'
            diawiUploadTask.variant = variant
            diawiUploadTask.uploadExtension = extension
            diawiUploadTask.client = new DiawiClient(client, new JsonSlurper())
            diawiUploadTask.pollingService = new BlockingPollingService()
        }
	}
}