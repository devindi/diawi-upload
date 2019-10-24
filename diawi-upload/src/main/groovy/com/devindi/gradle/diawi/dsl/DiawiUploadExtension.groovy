package com.devindi.gradle.diawi.dsl

class DiawiUploadExtension {
    String token
    boolean wallOfApps
    String password
    String comment
    OutputExtension output = new OutputExtension()
    CallbackExtension callback = new CallbackExtension()
}
