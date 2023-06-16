import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {
            HttpServer server = Main.makeServer();
            server.start();
            initRoutes(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initRoutes(HttpServer server) {
        server.createContext("/", Main::handleRequest);
        server.createContext("/apps", Main::handleAppsRequest);
        server.createContext("/apps/profile", Main::handleProfileRequest);
        server.createContext("/index.html", Main::handleFileRequest);
        server.createContext("/styles.css", Main::handleFileRequest);
    }

    private static HttpServer makeServer() throws IOException {
        String host = "localhost";
        InetSocketAddress address = new InetSocketAddress(host, 9889);

        String msg = "запускаем сервер по адресу" + " http://%s:%s/%n";
        System.out.printf(msg, address.getHostName(), address.getPort());
        HttpServer server = HttpServer.create(address, 50);
        System.out.println("  -> удачно!");
        return server;
    }

    private static void handleRequest(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");

            int responseCode = 200;
            int length = 0;
            exchange.sendResponseHeaders(responseCode, length);
            try (PrintWriter writer = getWriterFrom(exchange)) {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String ctxPath = exchange.getHttpContext().getPath();
                writer.println("<html><body>");
                write(writer, "HTTP Метод", method);
                write(writer, "Запрос", uri.toString());
                write(writer, "Обработан через", ctxPath);
                writeHeaders(writer, "Заголовки запроса", exchange.getRequestHeaders());
                writeData(writer, exchange);
                writer.println("</body></html>");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleAppsRequest(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");

            int responseCode = 200;
            int length = 0;
            exchange.sendResponseHeaders(responseCode, length);
            try (PrintWriter writer = getWriterFrom(exchange)) {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String ctxPath = exchange.getHttpContext().getPath();
                writer.println("<html><body>");
                write(writer, "HTTP Метод", method);
                write(writer, "Запрос", uri.toString());
                write(writer, "Обработан через", ctxPath);
                writeHeaders(writer, "Заголовки запроса", exchange.getRequestHeaders());
                writeData(writer, exchange);
                writer.println("</body></html>");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleProfileRequest(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");

            int responseCode = 200;
            int length = 0;
            exchange.sendResponseHeaders(responseCode, length);
            try (PrintWriter writer = getWriterFrom(exchange)) {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String ctxPath = exchange.getHttpContext().getPath();
                writer.println("<html><body>");
                write(writer, "HTTP Метод", method);
                write(writer, "Запрос", uri.toString());
                write(writer, "Обработан через", ctxPath);
                writeHeaders(writer, "Заголовки запроса", exchange.getRequestHeaders());
                writeData(writer, exchange);
                writer.println("</body></html>");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleFileRequest(HttpExchange exchange) {
        try {
            String requestPath = exchange.getRequestURI().getPath();
            String filePath = "htmls" + requestPath;

            File file = new File(filePath);
            if (file.exists()) {
                byte[] fileBytes = readFileBytes(file);
                exchange.getResponseHeaders().add("Content-Type", getContentType(file));
                exchange.sendResponseHeaders(200, fileBytes.length);
                OutputStream responseBody = exchange.getResponseBody();
                responseBody.write(fileBytes);
                responseBody.close();
            } else {
                response404(exchange);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] readFileBytes(File file) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (InputStream input = new FileInputStream(file)) {
            byte[] data = new byte[4096];
            int nRead;
            while ((nRead = input.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
        }
        return buffer.toByteArray();
    }

    private static String getContentType(File file) {
        String fileName = file.getName();
        if (fileName.endsWith(".html")) {
            return "text/html; charset=utf-8";
        } else if (fileName.endsWith(".css")) {
            return "text/css; charset=utf-8";
        }
        return "application/octet-stream";
    }

    private static void response404(HttpExchange exchange) throws IOException {
        String response = "404 (Not Found)";
        exchange.sendResponseHeaders(404, response.length());
        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(response.getBytes());
        responseBody.close();
    }

    private static PrintWriter getWriterFrom(HttpExchange exchange) {
        OutputStream output = exchange.getResponseBody();
        Charset charset = StandardCharsets.UTF_8;
        return new PrintWriter(output, false, charset);
    }

    private static void write(Writer writer, String msg, String method) {
        String data = String.format("%s: %s\n\n", msg, method);
        try {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeHeaders(Writer writer, String type, Headers headers) {
        write(writer, type, "");
        headers.forEach((k, v) -> write(writer, "\t" + k, v.toString()));
    }

    private static BufferedReader getReader(HttpExchange exchange) {
        InputStream input = exchange.getRequestBody();
        Charset charset = StandardCharsets.UTF_8;
        InputStreamReader isr = new InputStreamReader(input, charset);
        return new BufferedReader(isr);
    }

    private static void writeData(Writer writer, HttpExchange exchange) {
        try (BufferedReader reader = getReader(exchange)) {
            if (!reader.ready()) {
                return;
            }
            write(writer, "Блок данных", "");
            reader.lines().forEach(e -> write(writer, "\t", e));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
