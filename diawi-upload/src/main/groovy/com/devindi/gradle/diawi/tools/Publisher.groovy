package com.devindi.gradle.diawi.tools

import groovy.json.JsonSlurper

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

class Publisher {

    //Workaround to avoid SSLHandshakeException
    static void hackSecurity() throws Exception {
        TrustManager[] trustManagers = [
                new X509TrustManager(){
                    @Override
                    void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    X509Certificate[] getAcceptedIssuers() {
                        return null
                    }
                }
        ]
        SSLContext context = SSLContext.getInstance("SSL")
        context.init(null, trustManagers, new SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(context.socketFactory)
    }

    static String publish(String token, File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("Can't find file: ${file.path}")
        }

        def post
        try {
            post = NetworkHelper.sendPost(token, "", file)
            if (post.code != 200) {
                throw new IllegalArgumentException(post.responseString)
            }
            def slurper = new JsonSlurper()
            def result = slurper.parseText(post.responseString)
            def jobId = result.job
            if (!jobId) {
                throw new IllegalStateException("Can't parse job id. Response is ${post.responseString}")
            }
            return jobId
        } catch (SSLHandshakeException ex) {
            throw new IllegalArgumentException("Can't connect to https://upload.diawi.com", ex)
        }
    }

    static String checkURL(String token, String jobId) {
        try {
            def result = NetworkHelper.sendGet("", "status?token=$token&job=$jobId")
            if (result.code != 200) {
                throw new IllegalArgumentException(result.responseString)
            }
            def slurper = new JsonSlurper()
            def json = slurper.parseText(result.responseString)
            if (json.status == 4000) {
                throw new IllegalArgumentException(result.responseString)
            }
            return json.link
        } catch (SSLHandshakeException ex) {
            throw new IllegalArgumentException("Can't connect to https://upload.diawi.com", ex)
        }
    }
}
