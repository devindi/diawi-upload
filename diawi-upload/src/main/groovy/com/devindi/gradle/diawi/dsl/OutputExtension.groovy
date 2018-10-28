package com.devindi.gradle.diawi.dsl

class OutputExtension {
    OutputStream stream = System.out
    String format = "{file} uploaded at {date}. Diawi hash: {hash}"
}
