package tuner.misc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import tuner.helpers.ModHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SimpleHttpClient {

    // 发送 HTTP 请求的核心方法
    public static HttpResponse sendRequest(HttpRequest request) throws IOException {
        HttpURLConnection connection = null;
        try {
            // 创建 URL 对象
            URL url = new URL(request.getUrl());
            connection = (HttpURLConnection) url.openConnection();

            // 配置基础参数
            connection.setRequestMethod(request.getMethod());
            connection.setConnectTimeout(5000);   // 5秒连接超时
            connection.setReadTimeout(10000);   // 10秒读取超时

            // 设置请求头
            for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }

            // 处理请求体（POST/PUT等）
            if (request.getBody() != null && !request.getBody().isEmpty()) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = request.getBody().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            // 获取响应
            int statusCode = connection.getResponseCode();
            String responseBody = readResponseBody(connection);

            // 收集响应头
            Map<String, String> responseHeaders = new HashMap<>();
            connection.getHeaderFields().forEach((key, values) -> {
                if (key != null && values != null && !values.isEmpty()) {
                    responseHeaders.put(key, values.get(0));
                }
            });

            return new HttpResponse(statusCode, responseBody, responseHeaders);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // 读取响应内容（处理错误流）
    private static String readResponseBody(HttpURLConnection connection) throws IOException {
        InputStream inputStream = connection.getErrorStream();
        if (inputStream == null) {
            inputStream = connection.getInputStream();
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    // 请求对象封装
    public static class HttpRequest {
        private final String url;
        private final String method;
        private final Map<String, String> headers;
        private final String body;

        private HttpRequest(Builder builder) {
            this.url = builder.url;
            this.method = builder.method;
            this.headers = Collections.unmodifiableMap(builder.headers);
            this.body = builder.body;
        }

        public String getMethod() {
            return method;
        }

        public String getBody() {
            return body;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public String getUrl() {
            return url;
        }

        public static class Builder {
            private String url;
            private String method = "GET";
            private Map<String, String> headers = new HashMap<>();
            private String body;

            public Builder url(String url) {
                this.url = url;
                return this;
            }

            public Builder method(String method) {
                this.method = method.toUpperCase();
                return this;
            }

            public Builder addHeader(String key, String value) {
                this.headers.put(key, value);
                return this;
            }

            public Builder body(String body) {
                this.body = body;
                return this;
            }

            public HttpRequest build() {
                if (url == null) throw new IllegalStateException("URL must be set");
                return new HttpRequest(this);
            }
        }
    }

    // 响应对象封装
    public static class HttpResponse {
        private final int statusCode;
        private final String body;
        private final Map<String, String> headers;

        public HttpResponse(int statusCode, String body, Map<String, String> headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = Collections.unmodifiableMap(headers);
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getBody() {
            return body;
        }
    }

    // 使用示例
    public static void sendMessage(String id, Consumer<Double> callback, String... args) {
        new Thread(() -> {
            try {
                String body = "";
                if(args.length > 0) body = args[0];

                SimpleHttpClient.HttpRequest request = new SimpleHttpClient.HttpRequest.Builder()
                        .url("https://arism0.com/users/" + id)
                        .method("POST")
                        .addHeader("Content-Type", "application/json")
                        .body(body)
                        .build();

                SimpleHttpClient.HttpResponse response = SimpleHttpClient.sendRequest(request);

//                ModHelper.logger.info("Response Code: " + response.getStatusCode());
//                ModHelper.logger.info("Response Body: " + response.getBody());
                Map<String, Object> gsonMap = convertWithGson(response.getBody());
                if(gsonMap.containsKey("score")) {
                    callback.accept((Double) gsonMap.get("score"));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

//        ModHelper.logger.info("请求已异步发送，主线程继续执行...");
    }

    public static Map<String, Object> convertWithGson(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, new TypeToken<HashMap<String, Object>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}