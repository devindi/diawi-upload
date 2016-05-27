package com.devindi.gradle.diawi.tools

import groovy.json.JsonSlurper
import org.gradle.api.tasks.StopActionException

import javax.net.ssl.SSLHandshakeException

class Publisher {

    static String publish(String token, File file) {
        if (!file.exists()) {
            throw new StopActionException("Cant find file: ${file.path}")
        }

        def post
        try {
            post = NetworkHelper.sendPost(token, "", file)
            if (post.code != 200) {
                throw new StopActionException(post.responseString)
            }
            def slurper = new JsonSlurper()
            def result = slurper.parseText(post.responseString)
            return result.job
        } catch (SSLHandshakeException ex) {
            //sudo keytool -import -alias diawi -keystore /usr/lib/jvm/jdk1.8.0/jre/lib/security/cacerts -file /home/devindi/upload.diawi.com
            throw new StopActionException('Install SSL certificate for upload.diawi.com')
        }
    }

    static String checkURL(String token, String jobId) {
        try {
            def result = NetworkHelper.sendGet("", "status?token=$token&job=$jobId")
            if (result.code != 200) {
                throw new StopActionException(result.responseString)
            }
            def slurper = new JsonSlurper()
            def json = slurper.parseText(result.responseString)
            if (json.status == 4000) {
                throw new StopActionException(result.responseString)
            }
            return json.link
        } catch (SSLHandshakeException ex) {
            throw new StopActionException('Install SSL certificate for upload.diawi.com')
        }
    }
}
