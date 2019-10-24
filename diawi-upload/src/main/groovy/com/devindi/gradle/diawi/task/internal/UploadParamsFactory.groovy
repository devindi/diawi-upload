package com.devindi.gradle.diawi.task.internal

import com.devindi.gradle.diawi.diawi.UploadParams
import com.devindi.gradle.diawi.dsl.DiawiUploadExtension

class UploadParamsFactory {

    static UploadParams createParams(DiawiUploadExtension extension) {
        def token = extension.token
        def wallOfApps = extension.wallOfApps
        def password = extension.password
        def comment = extension.comment

        return new UploadParams(
                token,
                wallOfApps,
                password,
                comment,
                extension.callback.url,
                extension.callback.emails.join(",")
        )
    }
}
