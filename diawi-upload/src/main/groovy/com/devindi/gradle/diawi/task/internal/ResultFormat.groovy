package com.devindi.gradle.diawi.task.internal

class ResultFormat {

    String format(String format, List<ReplacementItem> replacements) {
        replacements.forEach { item ->
            format = format.replace("{${item.target}}", item.replacement)
        }
        return format
    }
}

class ReplacementItem {
    String target
    String replacement

    ReplacementItem(String target, String replacement) {
        this.target = target
        this.replacement = replacement
    }
}
