package ru.sigmadigital.pantus.api;

import android.os.Build;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

public class Sender {

    public static BaseResponse sendGet(String url, Map<String, String> headers) {
        if (Network.debug) {
            System.out.println("do GET " + url);
        }

        if (url != null) {
            if (isHttps(url)) {
                return sendGetHttps(url, headers);
            } else if (isHttp(url)) {
                return sendGetHttp(url, headers);
            }
        }
        return null;
    }

    public static ByteArrayOutputStream loadData(String url, Map<String, String> headers) {
        if (Network.debug) {
            System.out.println("do GET " + url);
        }

        if (url != null) {
            if (isHttps(url)) {
                return loadDataHttps(url, headers);
            } else if (isHttp(url)) {
                return loadDataHttp(url, headers);
            }
        }
        return null;
    }

    public static BaseResponse sendPost(String url, String data, Map<String, String> headers) {
        if (Network.debug) {
            System.out.println("do POST " + url + " data=" + data);
        }
        if (url != null) {
            if (isHttps(url)) {
                return sendPostHttps(url, data, headers);
            } else if (isHttp(url)) {
                return sendPostHttp(url, data, headers);
            }
        }
        return null;
    }

    public static BaseResponse sendPost(String url, byte[] data, Map<String, String> headers) {
        if (Network.debug) {
            System.out.println("do POST " + url + " data size=" + data.length);
        }
        if (url != null) {
            if (isHttps(url)) {
                return sendPostHttps(url, data, headers);
            } else if (isHttp(url)) {
                return sendPostHttp(url, data, headers);
            }
        }
        return null;
    }

    public static BaseResponse sendPost(String url, InputStream data, Map<String, String> headers) {
        if (Network.debug) {
            System.out.println("do POST " + url);
        }
        if (url != null) {
            if (isHttps(url)) {
                return sendPostHttps(url, data, headers);
            } else if (isHttp(url)) {
                return sendPostHttp(url, data, headers);
            }
        }
        return null;
    }

    private static boolean isHttps(String url) {
        if (url != null) {
            return url.startsWith("https://");
        }
        return false;
    }

    private static boolean isHttp(String url) {
        if (url != null) {
            return url.startsWith("http://");
        }
        return false;
    }

    private static void setHeaders(URLConnection connection, Map<String, String> headers) {
        if (connection != null) {
            if (headers != null) {
                if (headers.containsKey("content-type") && headers.get("content-type").toLowerCase().equals("application/json")) {
                    connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                }
                for (String key : headers.keySet()) {
                    connection.addRequestProperty(key, headers.get(key));
                }
            }
        }
    }

    public static String readStream(InputStream is, boolean gzip) throws IOException {
        if (is != null) {
            InputStream in;
            if (gzip) {
                in = new GZIPInputStream(is);
            } else {
                in = is;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String inputLine;
            StringBuilder sb = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(inputLine);
            }
            in.close();

            return sb.toString();
        }
        return null;
    }

    public static ByteArrayOutputStream readStreamToBytes(InputStream is, boolean gzip) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (is != null) {
            InputStream in;
            if (gzip) {
                in = new GZIPInputStream(is);
            } else {
                in = is;
            }

            int r;
            byte[] buf = new byte[1024];
            while ((r = in.read(buf)) > 0) {
                baos.write(buf, 0, r);
            }
            in.close();
        }
        return baos;
    }

    private static void writeData(OutputStream os, byte[] data) throws IOException {
        if (os != null && data != null) {
            os.write(data, 0, data.length);
            os.flush();
            os.close();
        }
    }

    private static void writeData(OutputStream os, InputStream data) throws IOException {
        if (os != null && data != null) {
            byte[] rd = new byte[1024];
            int r;
            while ((r = data.read(rd, 0, rd.length)) != -1) {
                os.write(rd, 0, r);
            }
            os.flush();
            os.close();
            data.close();
        }
    }

    private static InputStream getInputStream(HttpURLConnection connection) throws IOException {
        int code = connection.getResponseCode();
        if (code == 200) {
            return connection.getInputStream();
        } else {
            return connection.getErrorStream();
        }
    }

    private static boolean isGzip(HttpURLConnection con) {
        return (con.getHeaderField("Content-Encoding") != null && con.getHeaderField("Content-Encoding").equals("gzip"));
    }

    private static BaseResponse sendPostHttp(String url, byte[] data, Map<String, String> headers) {
        try {
            URL u = new URL(url);
            HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("POST");
            setHeaders(con, headers);
            con.setDoOutput(true);
            writeData(con.getOutputStream(), data);
            int code = con.getResponseCode();
            InputStream is = getInputStream(con);
            BaseResponse resp = new BaseResponse(code);
            resp.setData(readStream(is, isGzip(con)));
            if (Network.debug) {
                System.out.println("date=" + resp.getData());
            }
            is.close();
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static BaseResponse sendPostHttps(String url, String data, Map<String, String> headers) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return sendPostHttps(url, data.getBytes(StandardCharsets.UTF_8), headers);
        } else {
            try {
                return sendPostHttps(url, data.getBytes("utf-8"), headers);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private static BaseResponse sendPostHttp(String url, String data, Map<String, String> headers) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return sendPostHttp(url, data.getBytes(StandardCharsets.UTF_8), headers);
        } else {
            try {
                return sendPostHttp(url, data.getBytes("utf-8"), headers);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private static BaseResponse sendPostHttp(String url, InputStream data, Map<String, String> headers) {
        try {
            URL u = new URL(url);
            HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("POST");
            setHeaders(con, headers);
            con.setDoOutput(true);
            writeData(con.getOutputStream(), data);
            int code = con.getResponseCode();
            InputStream is = getInputStream(con);
            BaseResponse resp = new BaseResponse(code);
            resp.setData(readStream(is, isGzip(con)));
            if (Network.debug) {
                System.out.println("date=" + resp.getData());
            }
            is.close();
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static BaseResponse sendPostHttps(String url, InputStream data, Map<String, String> headers) {
        try {
            URL u = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) u.openConnection();
            con.setRequestMethod("POST");
            setHeaders(con, headers);
            con.setDoOutput(true);
            writeData(con.getOutputStream(), data);
            int code = con.getResponseCode();
            InputStream is = getInputStream(con);
            BaseResponse resp = new BaseResponse(code);
            resp.setData(readStream(is, isGzip(con)));
            if (Network.debug) {
                System.out.println("date=" + resp.getData());
            }
            is.close();
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static BaseResponse sendPostHttps(String url, byte[] data, Map<String, String> headers) {
        try {
            URL u = new URL(url);
            if (Network.debug) {
                System.out.println("do POST " + url + " data size=" + data.length);
            }
            HttpsURLConnection con = (HttpsURLConnection) u.openConnection();
            con.setRequestMethod("POST");
            setHeaders(con, headers);
            con.setDoOutput(true);
            writeData(con.getOutputStream(), data);
            int code = con.getResponseCode();
            InputStream is = getInputStream(con);
            BaseResponse resp = new BaseResponse(code);
            resp.setData(readStream(is, isGzip(con)));
            if (Network.debug) {
                System.out.println("date=" + resp.getData());
            }
            is.close();
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static BaseResponse sendGetHttp(String url, Map<String, String> headers) {
        try {
            URL u = new URL(url);
            HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("GET");
            setHeaders(con, headers);
            int code = con.getResponseCode();
            InputStream is = getInputStream(con);
            BaseResponse resp = new BaseResponse(code);
            resp.setData(readStream(is, isGzip(con)));
            if (Network.debug) {
                System.out.println("date=" + resp.getData());
            }
            is.close();
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static BaseResponse sendGetHttps(String url, Map<String, String> headers) {
        try {
            URL u = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) u.openConnection();
            con.setRequestMethod("GET");
            setHeaders(con, headers);
            int code = con.getResponseCode();
            InputStream is = getInputStream(con);
            BaseResponse resp = new BaseResponse(code);
            resp.setData(readStream(is, isGzip(con)));
            if (Network.debug) {
                System.out.println("date=" + resp.getData());
            }
            is.close();
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ByteArrayOutputStream loadDataHttps(String url, Map<String, String> headers) {
        try {
            URL u = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) u.openConnection();
            con.setRequestMethod("GET");
            setHeaders(con, headers);
            InputStream is = getInputStream(con);
            ByteArrayOutputStream baos = readStreamToBytes(is, isGzip(con));
            if (Network.debug) {
                System.out.println("date=" + baos.size());
            }
            is.close();
            return baos;
        } catch (Exception e) {
            e.printStackTrace();
            return new ByteArrayOutputStream();
        }
    }

    private static ByteArrayOutputStream loadDataHttp(String url, Map<String, String> headers) {
        try {
            URL u = new URL(url);
            HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("GET");
            setHeaders(con, headers);
            InputStream is = getInputStream(con);
            ByteArrayOutputStream baos = readStreamToBytes(is, isGzip(con));
            if (Network.debug) {
                System.out.println("date=" + baos.size());
            }
            is.close();
            return baos;
        } catch (Exception e) {
            e.printStackTrace();
            return new ByteArrayOutputStream();
        }
    }
}
