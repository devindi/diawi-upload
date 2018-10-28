package com.devindi.gradle.diawi.diawi

import com.google.common.base.Strings

class UploadParams {

    final String token
    final boolean wallOfApps
    final String password
    final String comment
    final String callbackUrl
    final String callbackEmails

    UploadParams(String token, boolean wallOfApps, String password, String comment, String callbackUrl, String callbackEmails) {
        if (Strings.isNullOrEmpty(token)) {
            throw new IllegalArgumentException("API token is missing")
        }
        this.token = token
        this.wallOfApps = wallOfApps
        this.password = password
        this.comment = comment
        this.callbackUrl = callbackUrl
        this.callbackEmails = callbackEmails
    }
}
