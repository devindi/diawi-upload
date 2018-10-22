package com.devindi.gradle.diawi.dsl

class DiawiUploadExtension {
    String token
    OutputStream standardOutput
    boolean wallOfApps
    String password
    String comment
    String callbackEmail
    String callbackUrl
    String resultFormat = "{file} uploaded at {date}. Diawi hash: {hash}"
}
