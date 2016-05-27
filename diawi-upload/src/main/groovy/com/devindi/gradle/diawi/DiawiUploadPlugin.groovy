package com.devindi.gradle.diawi

import com.devindi.gradle.diawi.dsl.DiawiUploadExtension
import com.devindi.gradle.diawi.task.DiawiUploadTask
import org.apache.commons.lang.StringUtils
import org.gradle.api.*
import com.android.build.gradle.AppPlugin

class DiawiUploadPlugin implements Plugin<Project> {

	void apply(Project project) {

        def hasAppPlugin = project.plugins.find { p -> p instanceof AppPlugin }
        if (!hasAppPlugin) {
            throw new IllegalStateException("The 'com.android.application' plugin is required.")
        }

        def extension = project.extensions.create('diawi', DiawiUploadExtension)

        project.android.applicationVariants.all { variant ->

            def buildTypeName = variant.buildType.name.capitalize()

            def productFlavorNames = variant.productFlavors.collect { it.name.capitalize() }
            if (productFlavorNames.isEmpty()) {
                productFlavorNames = [""]
            }
            def productFlavorName = productFlavorNames.join('')

            def variationName = "${productFlavorName}${buildTypeName}"

            def diawiUploadTaskName = "diawiPublish${variationName}"

            def diawiUploadTask = project.tasks.create(diawiUploadTaskName, DiawiUploadTask)
            diawiUploadTask.description "Publish $variationName build to diawi"
            diawiUploadTask.group 'Diawi'
            diawiUploadTask.variant variant
            diawiUploadTask.uploadExtension extension
        }
	}
}