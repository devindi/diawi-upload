package com.devindi.gradle.diawi.task.internal

import com.devindi.gradle.diawi.dsl.DiawiUploadExtension
import org.junit.Assert
import org.junit.Test

@SuppressWarnings("GrDeprecatedAPIUsage")
class UploadParamsFactoryTest {

    @Test
    void callbackUrlNewDsl() {
        DiawiUploadExtension extension = createExtension()
        extension.callback.url = "http://example.com"

        def params = UploadParamsFactory.createParams(extension)

        Assert.assertEquals("http://example.com", params.callbackUrl)
    }

    @Test
    void callbackEmailNewDsl() {
        DiawiUploadExtension extension = createExtension()
        extension.callback.emails = ["me@example.com"]

        def params = UploadParamsFactory.createParams(extension)

        Assert.assertEquals("me@example.com", params.callbackEmails)
    }

    private static def createExtension() {
        def extension = new DiawiUploadExtension()
        extension.token = "123"
        return extension
    }
}
