package com.devindi.gradle.diawi.task.internal

import com.devindi.gradle.diawi.dsl.DiawiUploadExtension
import org.gradle.api.logging.Logger
import org.junit.Assert
import org.junit.Test

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.*

@SuppressWarnings("GrDeprecatedAPIUsage")
class UploadParamsFactoryTest {

    def loggerMock = mock(Logger.class)
    def factory = new UploadParamsFactory(loggerMock)

    @Test
    void callbackUrlNewDsl() {
        DiawiUploadExtension extension = createExtension()
        extension.callback.url = "http://example.com"

        def params = factory.createParams(extension)

        Assert.assertEquals("http://example.com", params.callbackUrl)
        verifyNoMoreInteractions(loggerMock)
    }

    @Test
    void callbackUrlOldDsl() {
        DiawiUploadExtension extension = createExtension()
        extension.callbackUrl = "http://example.com"

        def params = factory.createParams(extension)

        Assert.assertEquals("http://example.com", params.callbackUrl)
        verify(loggerMock).warn(any(String.class))
    }

    @Test
    void newDslPreferredUrl() {
        DiawiUploadExtension extension = createExtension()
        extension.callbackUrl = "http://old.example.com"
        extension.callback.url = "http://new.example.com"

        def params = factory.createParams(extension)

        Assert.assertEquals("http://new.example.com", params.callbackUrl)
        verify(loggerMock).warn(any(String.class))
    }

    @Test(expected = IllegalArgumentException.class)
    void tokenRequiredException() {
        DiawiUploadExtension extension = new DiawiUploadExtension()

        factory.createParams(extension)
    }

    @Test
    void callbackEmailNewDsl() {
        DiawiUploadExtension extension = createExtension()
        extension.callback.emails = ["me@example.com"]

        def params = factory.createParams(extension)

        Assert.assertEquals("me@example.com", params.callbackEmails)
        verifyNoMoreInteractions(loggerMock)
    }

    @Test
    void callbackEmailOldDsl() {
        DiawiUploadExtension extension = createExtension()
        extension.callbackEmail = "me@example.com"

        def params = factory.createParams(extension)

        Assert.assertEquals("me@example.com", params.callbackEmails)
        verify(loggerMock).warn(any(String.class))
    }

    @Test
    void newDslPreferredEmail() {
        DiawiUploadExtension extension = createExtension()
        extension.callbackEmail = ["me@old.example.com"]
        extension.callback.emails = ["me@new.example.com"]

        def params = factory.createParams(extension)

        Assert.assertEquals("me@new.example.com", params.callbackEmails)
        verify(loggerMock).warn(any(String.class))
    }

    @Test
    void noLogsForEmpty() {
        DiawiUploadExtension extension = createExtension()

        factory.createParams(extension)

        verifyNoMoreInteractions(loggerMock)
    }

    private static def createExtension() {
        def extension = new DiawiUploadExtension()
        extension.token = "123"
        return extension
    }
}
