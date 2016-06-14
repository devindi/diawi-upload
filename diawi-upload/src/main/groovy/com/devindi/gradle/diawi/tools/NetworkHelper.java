package com.devindi.gradle.diawi.tools;

import org.gradle.api.Nullable;

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

    public static RequestResult sendPost(String token, File file, boolean wallOfApps, @Nullable String password, @Nullable String comment, @Nullable String callbackUrl, @Nullable String callbackEmail) throws IOException {
        ALog.debug("POST request %s / %s. \n", HOST, file.getAbsolutePath());
        URL requestUrl = new URL(HOST);
        HttpsURLConnection connection = (HttpsURLConnection) requestUrl.openConnection();
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestProperty(
                "Content-Type", "multipart/form-data;boundary=" + boundary);

        DataOutputStream request = new DataOutputStream(connection.getOutputStream());

        addStringPart(request, "token", token);
        addStringPart(request, "wall_of_apps", wallOfApps ? "1" : "0");
        if (password != null)
            addStringPart(request, "password", password);
        if (comment != null)
            addStringPart(request, "comment", comment);
        if (callbackUrl != null)
            addStringPart(request, "callback_url", callbackUrl);
        if (callbackEmail != null)
            addStringPart(request, "callback_email", callbackEmail);


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

    public static RequestResult sendGet(String url) throws IOException {
        ALog.debug("GET request %s.", HOST + url);
        URL requestUrl = new URL(HOST + url);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod("GET");
        return getRequestResult(connection);
    }

    private static DataOutputStream addStringPart(DataOutputStream stream, String name, String data) throws IOException {
        stream.writeBytes(twoHyphens + boundary + crlf);
        stream.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"" + crlf);
        stream.writeBytes("Content-Type: text/plain; charset=UTF-8" + crlf);
        stream.writeBytes(crlf);
        stream.writeBytes(data + crlf);
        stream.flush();
        return stream;
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
