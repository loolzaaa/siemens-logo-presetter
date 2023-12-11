package ru.loolzaaa.siemens.logopresetter.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.loolzaaa.siemens.logopresetter.LogoPresetter;
import ru.loolzaaa.siemens.logopresetter.config.AccessControlSettings;
import ru.loolzaaa.siemens.logopresetter.hardware.BaseHardware;
import ru.loolzaaa.siemens.logopresetter.scan.DeviceInfo;
import ru.loolzaaa.siemens.logopresetter.util.IPV4Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.*;

@RequiredArgsConstructor
public class PresetterHttpServer {

    private final LogoPresetter logoPresetter;

    public void start() throws IOException {
        final int port = 12000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this::start);
        server.createContext("/scan", exchange -> new HttpHandlerImpl(Constants.GET, this::scan).handle(exchange));
        server.createContext("/show", exchange -> new HttpHandlerImpl(Constants.GET, this::show).handle(exchange));
        server.createContext("/set", exchange -> new HttpHandlerImpl(Constants.POST, this::set, Constants.NO_CONTENT).handle(exchange));
        server.setExecutor(null);
        server.start();
        System.out.println("Server listening on " + port);
    }

    private void start(HttpExchange exchange) {
        if (Constants.GET.equals(exchange.getRequestMethod())) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            try (InputStream in = classLoader.getResourceAsStream("index.html")) {
                if (in == null) {
                    throw new RuntimeException("There is no resource index.html");
                }
                String response = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/html");
                exchange.sendResponseHeaders(Constants.OK, response.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(response.getBytes());
                output.flush();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    private String scan(Map<String, List<String>> params) {
        List<String> customHosts = params.get("customHost");
        List<DeviceInfo> devices = logoPresetter.scanAllDevices(customHosts == null ? null : customHosts.get(0));
        StringBuilder sb = new StringBuilder("[");
        for (DeviceInfo device : devices) {
            sb.append("{\"ip\":\"").append(device.getIp()).append("\",");
            sb.append("\"mask\":\"").append(device.getMask()).append("\",");
            sb.append("\"gateway\":\"").append(device.getGateway()).append("\"},");
        }
        if (sb.length() > 1) {
            return sb.replace(sb.length() - 1, sb.length(), "]").toString();
        } else {
            return "[]";
        }
    }

    private String show(Map<String, List<String>> params) {
        List<String> ips = params.get("ip");
        if (ips != null) {
            try {
                if (IPV4Utils.getInstance().formatToInt(ips.get(0)) == 0) {
                    throw new BadRequestException("Incorrect ip format");
                }
            } catch (IllegalArgumentException e) {
                throw new BadRequestException(e.getMessage());
            }
            List<Object> showDeviceDetails = logoPresetter.showDeviceDetails(ips.get(0));
            String firmware = (String) showDeviceDetails.get(0);
            BaseHardware.IPConfig ipConfig = (BaseHardware.IPConfig) showDeviceDetails.get(1);
            AccessControlSettings accessControlSettings = (AccessControlSettings) showDeviceDetails.get(2);
            return String.format("{\"firmware\":\"%s\",\"ip\":\"%s\",\"mask\":\"%s\",\"gateway\":\"%s\",\"s7\":\"%s\",\"modbus\":\"%s\"}",
                    firmware, ipConfig.ip, ipConfig.mask, ipConfig.gateway,
                    accessControlSettings.getS7Access().isEnabled(),
                    accessControlSettings.getModbusAccess().isEnabled());
        } else {
            throw new BadRequestException("You must specify ip query parameter for show command");
        }
    }

    private String set(Map<String, List<String>> params) {
        List<String> ips = params.get("ip");
        List<String> setIps = params.get("setIp");
        List<String> setMasks = params.get("setMask");
        List<String> setGateways = params.get("setGateway");
        if (ips != null && setIps != null && setMasks != null && setGateways != null) {
            try {
                if (IPV4Utils.getInstance().formatToInt(ips.get(0)) == 0) {
                    throw new BadRequestException("Incorrect ip format");
                }
                if (IPV4Utils.getInstance().formatToInt(setIps.get(0)) == 0) {
                    throw new BadRequestException("Incorrect new ip format");
                }
                if (IPV4Utils.getInstance().formatToInt(setMasks.get(0)) == 0) {
                    throw new BadRequestException("Incorrect mask format");
                }
                if (IPV4Utils.getInstance().formatToInt(setGateways.get(0)) == 0) {
                    throw new BadRequestException("Incorrect gateway format");
                }
            } catch (IllegalArgumentException e) {
                throw new BadRequestException(e.getMessage());
            }
            BaseHardware.IPConfig ipConfig = new BaseHardware.IPConfig();
            ipConfig.ip = setIps.get(0);
            ipConfig.mask = setMasks.get(0);
            ipConfig.gateway = setGateways.get(0);
            logoPresetter.setDeviceProperties(ips.get(0), ipConfig);
        } else {
            throw new BadRequestException("You must specify current ip and all new network parameters for set command");
        }
        return null;
    }

    private Map<String, List<String>> splitQueryToParams(String query) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyMap();
        }

        return Pattern.compile("&").splitAsStream(query)
                .map(s -> Arrays.copyOf(s.split("="), 2))
                .collect(groupingBy(s -> decode(s[0]), mapping(s -> decode(s[1]), toList())));
    }

    private Map<String, List<String>> splitBodyToParams(byte[] body) {
        return splitQueryToParams(new String(body, StandardCharsets.UTF_8));
    }

    private String decode(@NonNull String encoded) {
        return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
    }

    @RequiredArgsConstructor
    @AllArgsConstructor
    private class HttpHandlerImpl implements HttpHandler {

        @NonNull
        private final String httpMethod;
        @NonNull
        private final Function<Map<String, List<String>>, String> requestProcessor;

        private int httpCode = -1;

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (httpMethod.equals(exchange.getRequestMethod())) {
                Map<String, List<String>> params = httpMethod.equals(Constants.GET) ?
                        splitQueryToParams(exchange.getRequestURI().getRawQuery()) :
                        splitBodyToParams(exchange.getRequestBody().readAllBytes());
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                try {
                    String response = requestProcessor.apply(params);
                    exchange.sendResponseHeaders(
                            httpCode == -1 ? Constants.OK : httpCode,
                            httpCode == Constants.NO_CONTENT ? -1 : response.getBytes().length);
                    if (httpCode != Constants.NO_CONTENT) {
                        flush(exchange, response);
                    }
                } catch (BadRequestException e) {
                    String response = String.format("{\"error\":\"%s\"}", e.getMessage());
                    exchange.sendResponseHeaders(Constants.BAD_REQUEST, response.getBytes().length);
                    flush(exchange, response);
                } catch (Exception e) {
                    String response = String.format("{\"error\":\"%s\"}", e.getMessage());
                    exchange.sendResponseHeaders(Constants.INTERNAL_ERROR, response.getBytes().length);
                    flush(exchange, response);
                }
            } else {
                exchange.sendResponseHeaders(Constants.METHOD_NOT_ALLOWED, -1);
            }
            exchange.close();
        }

        private void flush(HttpExchange exchange, String response) throws IOException {
            OutputStream output = exchange.getResponseBody();
            output.write(response.getBytes());
            output.flush();
        }
    }
}
