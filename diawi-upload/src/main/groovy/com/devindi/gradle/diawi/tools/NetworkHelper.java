package com.devindi.gradle.diawi.tools;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public abstract class NetworkHelper {

    protected static String HOST = "https://upload.diawi.com/";
    private static final String boundary =  "*****";
    private static final String crlf = "\r\n";
    private static final String twoHyphens = "--";

    public static RequestResult sendPost(String token, String url, File file) throws IOException {
        ALog.debug("POST request %s / %s. \n", HOST + url, file.getAbsolutePath());
        URL requestUrl = new URL(HOST + url);
        HttpsURLConnection connection = (HttpsURLConnection) requestUrl.openConnection();
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestProperty(
                "Content-Type", "multipart/form-data;boundary=" + boundary);

        DataOutputStream request = new DataOutputStream(connection.getOutputStream());

        request.writeBytes(twoHyphens + boundary + crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"token\""+ crlf);
        request.writeBytes("Content-Type: text/plain; charset=UTF-8" + crlf);
        request.writeBytes(crlf);
        request.writeBytes(token+ crlf);
        request.flush();

        String fileName = file.getName();
        request.writeBytes(twoHyphens + boundary + crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" +
                fileName + "\"" + crlf);
        request.writeBytes(crlf);

        byte[] bytes = Files.readAllBytes(file.toPath());
        request.write(bytes);

        request.flush();

        request.writeBytes(crlf);
        request.writeBytes(twoHyphens + boundary +
                twoHyphens + crlf);

        request.flush();
        request.close();

        return getRequestResult(connection);
    }

    public static RequestResult sendGet(String token, String url) throws IOException {
        ALog.debug("GET request %s.", HOST + url);
        URL requestUrl = new URL(HOST + url);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod("GET");
        if (token != null)
            connection.addRequestProperty("Authorization", token);
        return getRequestResult(connection);
    }

    private static RequestResult getRequestResult(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();

        InputStream inputStream;
        if (responseCode < 400)
            inputStream = connection.getInputStream();
        else
            inputStream = connection.getErrorStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        String inputLine;
        StringBuilder response = new StringBuilder();

        ALog.debug("-----------------------------------");
        ALog.debug("PATH " + connection.getURL().getPath());
        ALog.debug("CODE = " + responseCode);
        ALog.debug("BODY = ");

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            ALog.debug(inputLine);
        }
        in.close();

        inputLine = response.toString();

        ALog.debug("-----------------------------------");
        return new RequestResult(responseCode, inputLine);
    }

    public static class RequestResult {
        public int code;
        public String responseString;

        private RequestResult(int code, String responseString) {
            this.code = code;
            this.responseString = responseString;
        }

        @Override
        public String toString() {
            return "RequestResult{" +
                    "code=" + code +
                    ", responseString='" + responseString + '\'' +
                    '}';
        }
    }
}
