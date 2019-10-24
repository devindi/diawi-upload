package com.devindi.gradle.diawi.dsl

class DiawiUploadExtension {
    String token
    boolean wallOfApps
    String password
    String comment
    OutputExtension output = new OutputExtension()
    CallbackExtension callback = new CallbackExtension()

    // those props will be removed at version 2.0
    @Deprecated
    String callbackEmail
    @Deprecated
    String callbackUrl
}
