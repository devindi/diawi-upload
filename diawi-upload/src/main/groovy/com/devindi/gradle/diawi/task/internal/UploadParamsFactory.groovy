package com.devindi.gradle.diawi.task.internal

import com.devindi.gradle.diawi.diawi.UploadParams
import com.devindi.gradle.diawi.dsl.DiawiUploadExtension
import com.google.common.base.Strings
import org.gradle.api.logging.Logger

class UploadParamsFactory {

    final Logger logger

    UploadParamsFactory(Logger logger) {
        this.logger = logger
    }

    UploadParams createParams(DiawiUploadExtension extension) {
        def token = extension.token
        def wallOfApps = extension.wallOfApps
        def password = extension.password
        def comment = extension.comment

        return new UploadParams(
                token,
                wallOfApps,
                password,
                comment,
                findCallbackUrl(extension),
                findCallbackEmails(extension)
        )
    }

    private String findCallbackUrl(DiawiUploadExtension extension) {
        def value = extension.callback.url
        def backport = extension.callbackUrl

        if (Strings.isNullOrEmpty(value) && Strings.isNullOrEmpty(backport)) {
            return null
        }

        if (!Strings.isNullOrEmpty(value) && Strings.isNullOrEmpty(backport)) {
            // new dsl used, ok
            return value
        }

        if (Strings.isNullOrEmpty(value) && !Strings.isNullOrEmpty(backport)) {
            logger.warn("Property 'diawi.callbackUrl' is deprecated and will be removed in future. Use diawi.callback.url instead")
            return backport
        }

        if (value != backport) {
            logger.warn("Property 'diawi.callbackUrl' is deprecated and will be removed in future. Use diawi.callback.url instead. Value of 'diawi.callbackUrl' ignored")
            return value
        }

        logger.error("Failed to resolve callback url")
        return null
    }

    private String findCallbackEmails(DiawiUploadExtension extension) {
        def value = extension.callback.emails.join(",")
        def backport = extension.callbackEmail

        if (Strings.isNullOrEmpty(value) && Strings.isNullOrEmpty(backport)) {
            return null
        }

        if (!Strings.isNullOrEmpty(value) && Strings.isNullOrEmpty(backport)) {
            // new dsl used, ok
            return value
        }

        if (Strings.isNullOrEmpty(value) && !Strings.isNullOrEmpty(backport)) {
            logger.warn("Property 'diawi.callbackEmail' is deprecated and will be removed in future. Use diawi.callback.emails instead")
            return backport
        }

        if (value != backport) {
            logger.warn("Property 'diawi.callbackEmail' is deprecated and will be removed in future. Use diawi.callback.emails instead. Value of 'diawi.callbackEmail' ignored")
            return value
        }

        logger.error("Failed to resolve callback emails")
        return null
    }
}
