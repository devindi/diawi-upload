package com.devindi.gradle.diawi.tools

public class PropertyHelper {
    public static Properties readProperties(File file) {
        Properties props = new Properties()
        props.load(new FileInputStream(file))
        return props
    }
}
