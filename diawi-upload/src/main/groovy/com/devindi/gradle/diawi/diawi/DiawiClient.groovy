package com.devindi.gradle.diawi.diawi

import groovy.json.JsonSlurper
import okhttp3.HttpUrl
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

class DiawiClient {

    private final OkHttpClient httpClient
    private final JsonSlurper slurper

    DiawiClient(OkHttpClient httpClient, JsonSlurper slurper) {
        this.httpClient = httpClient
        this.slurper = slurper
    }

    /**
     * Upload file to diawi.
     *
     * @param apkFile file to upload
     * @param params upload settings. Token and wallOfApps are mandatory
     * @return diawi file processing job identifier
     */
    String upload(File apkFile, UploadParams params) {
        if (!apkFile.exists()) {
            throw new FileNotFoundException(apkFile.absolutePath)
        }
        MultipartBody.Builder requestBody = new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("token", params.token)
            .addFormDataPart("wall_of_apps", params.wallOfApps ? '0' : '1')
        if (params.password != null) {
            requestBody.addFormDataPart("password", params.password)
        }
        if (params.comment != null) {
            requestBody.addFormDataPart("comment", params.comment)
        }
        if (params.callbackUrl != null) {
            requestBody.addFormDataPart("callback_url", params.callbackUrl)
        }
        if (params.callbackEmails != null) {
            requestBody.addFormDataPart("callback_email", params.callbackEmails)
        }
        requestBody.addFormDataPart("file", apkFile.name, RequestBody.create(null, apkFile))

        Request request = new Request.Builder()
                .url("https://upload.diawi.com/")
                .post(requestBody.build())
                .build()

        Response response = httpClient.newCall(request).execute()
        String responseBody = response.body().string()
        if (!response.isSuccessful()) {
            throw new IllegalStateException(responseBody)
        }
        def json = slurper.parseText(responseBody)
        if (json.status == 4000) {
            throw new IllegalStateException(responseBody)
        }
        return json.job
    }

    /**
     * Check file processing job.
     *
     * @param token diawi API token
     * @param jobId diawi file processing job identifier. You should call 'upload' to obtain this id
     * @return diawi's file hash or null when job isn't finished
     */
    String checkJobStatus(String token, String jobId) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://upload.diawi.com/status").newBuilder()
        urlBuilder.addQueryParameter("token", token)
        urlBuilder.addQueryParameter("job", jobId)
        String url = urlBuilder.build().toString()

        Request request = new Request.Builder()
                .url(url)
                .build()

        Response response = httpClient.newCall(request).execute()
        String responseBody = response.body().string()
        if (!response.isSuccessful()) {
            throw new IllegalStateException(responseBody)
        }
        def json = slurper.parseText(responseBody)
        if (json.status == 4000) {
            throw new IllegalStateException(responseBody)
        }
        return json.hash
    }
}
