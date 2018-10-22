package com.devindi.gradle.diawi.task.internal

import java.util.concurrent.Callable

class BlockingPollingService {

    String blockingGet(Callable<String> action, long interval, Runnable failCallback) {
        String result = action.call()
        def counter = 0
        while (result == null && counter < 10) {
            Thread.sleep(interval)
            counter++
            result = action.call()
        }
        if (result == null) {
            failCallback.run()
        }
        return result
    }
}
