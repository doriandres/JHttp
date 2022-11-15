import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JHttp {
    public static class Method {
        public static final String GET = "GET";
        public static final String POST = "POST";
        public static final String PUT = "PUT";
        public static final String PATCH = "PATCH";
        public static final String OPTIONS = "OPTIONS";
        public static final String CONNECT = "CONNECT";
    }

    public static class Response implements Closeable {
        private final HttpURLConnection connection;
        private final int status;
        private final Map<String, List<String>> headers;
        private final InputStream body;
        private String bodyAsString;

        private Response(HttpURLConnection connection) throws IOException {
            this.connection = connection;
            this.status = connection.getResponseCode();
            this.headers = connection.getHeaderFields();
            this.body = connection.getInputStream();
        }

        public int getStatus() {
            return status;
        }

        public Map<String, List<String>> getHeaders() {
            return headers;
        }

        public InputStream getBody() {
            return body;
        }

        public String getBodyAsString() {
            if (bodyAsString != null) {
                return bodyAsString;
            }
            bodyAsString = new BufferedReader(new InputStreamReader(body, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
            return bodyAsString;
        }

        public JSONObject getBodyAsJson() throws JSONException {
            return new JSONObject(getBodyAsString());
        }

        public JSONArray getBodyAsJsonArray() throws JSONException {
            return new JSONArray(getBodyAsString());
        }

        public void close() throws IOException {
            body.close();
            connection.disconnect();
        }
    }

    public static class Client {
        private Long defaultTimeoutInSeconds = 30L;

        public Long getDefaultTimeoutInSeconds() {
            return defaultTimeoutInSeconds;
        }

        public void setDefaultTimeoutInSeconds(Long defaultTimeoutInSeconds) {
            this.defaultTimeoutInSeconds = defaultTimeoutInSeconds;
        }

        public Client() {
        }

        public Client(Long defaultTimeoutInSeconds) {
            this.defaultTimeoutInSeconds = defaultTimeoutInSeconds;
        }

        public Response send(String method, String endpoint) throws ExecutionException, InterruptedException, TimeoutException {
            return send(method, endpoint, null, null, defaultTimeoutInSeconds);
        }

        public Response send(String method, String endpoint, Map<String, String> headers, String payload) throws ExecutionException, InterruptedException, TimeoutException {
            return send(method, endpoint, null, payload.getBytes(StandardCharsets.UTF_8), defaultTimeoutInSeconds);
        }

        public Response send(String method, String endpoint, Map<String, String> headers, byte[] payload, Long timeoutInSeconds) throws ExecutionException, InterruptedException, TimeoutException {

            ExecutorService executor = Executors.unconfigurableExecutorService(Executors.newSingleThreadExecutor());
            Future<Response> task = executor.submit(() -> {

                URL url = new URL(endpoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(true);
                connection.setRequestMethod(method);
                connection.setDoInput(true);

                if (headers != null && !headers.isEmpty()) {
                    for (Map.Entry<String, String> header : headers.entrySet()) {
                        connection.setRequestProperty(header.getKey(), header.getValue());
                    }
                }

                if (payload != null && payload.length > 0) {
                    connection.setDoOutput(true);
                    try (OutputStream os = connection.getOutputStream()) {
                        ;
                        os.write(payload, 0, payload.length);
                    }
                }

                return new JHttp.Response(connection);
            });

            return task.get(timeoutInSeconds, TimeUnit.SECONDS);
        }
    }
}
