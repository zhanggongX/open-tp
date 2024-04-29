package cn.opentp.server;

import cn.opentp.server.command.CommandOptions;
import cn.opentp.server.configuration.Configuration;
import cn.opentp.server.configuration.OpentpProperties;
import cn.opentp.server.constant.Constant;
import cn.opentp.server.report.ReportServer;
import cn.opentp.server.rest.RestServer;
import cn.opentp.server.rest.controller.FaviconHttpHandler;
import cn.opentp.server.rest.controller.HttpHandler;
import cn.opentp.server.rest.controller.OpentpHttpHandler;
import cn.opentp.server.rocksdb.OpentpRocksDB;
import cn.opentp.server.transport.TransportServer;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

public class OpentpBootstrap {

    private static final Logger log = LoggerFactory.getLogger(OpentpBootstrap.class);

    private OpentpBootstrap() {

    }

    public static void main(String[] args) throws InterruptedException {

        // 全局唯一配置实例
        Configuration configuration = Configuration.configuration();

        // 加载配置
        loadProps(configuration.properties(), Constant.DEFAULT_CONFIG_FILE);

        // 加载参数配置
        boolean loadSuccess = loadCmdProps(configuration.properties(), args);
        if (!loadSuccess) return;

        startServers();
    }

    private static void startServers() {
        Configuration configuration = Configuration.configuration();

        OpentpProperties properties = configuration.properties();
        ReportServer reportServer = new ReportServer();
        reportServer.start(properties.getReportServerPort());

        RestServer restServer = new RestServer();
        restServer.start(properties.getHttpServerPort());

        TransportServer transportServer = new TransportServer();
        transportServer.start(properties.getTransportServerPort());

        Map<String, HttpHandler> endPoints = Configuration.configuration().endPoints();
        // 添加 handler
        endPoints.put("favicon.ico", new FaviconHttpHandler());
        endPoints.put("opentp", new OpentpHttpHandler());

        ShutdownHook shutdownHook = new ShutdownHook();
        shutdownHook.add(restServer);
        shutdownHook.add(reportServer);
        shutdownHook.add(transportServer);
        shutdownHook.add(OpentpRocksDB.rocksDB());

        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    private static boolean loadCmdProps(OpentpProperties properties, String[] args) {

        CommandLineParser parser = new DefaultParser();
        Options options = CommandOptions.opentpOption();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.getOptionValue("h") != null) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("opentp", options);
                return false;
            }

            String reportPort = cmd.getOptionValue("rp");
            if (reportPort != null) properties.setReportServerPort(Integer.parseInt(reportPort));

            String httpPort = cmd.getOptionValue("hp");
            if (httpPort != null) properties.setHttpServerPort(Integer.parseInt(httpPort));

            String transportPort = cmd.getOptionValue("tp");
            if (transportPort != null) properties.setTransportServerPort(Integer.parseInt(transportPort));
            return true;
        } catch (ParseException exp) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("opentp", options);
            return false;
        }
    }

    private static void loadProps(OpentpProperties properties, String fileName) {

        URL resource = loadResource(fileName);
        if (resource == null) {
            return;
        }

        try (InputStreamReader reader = new InputStreamReader(resource.openStream(), StandardCharsets.UTF_8)) {
            Properties tempProps = new Properties();
            tempProps.load(reader);
            System.out.println(1);
            for (Map.Entry<Object, Object> entry : tempProps.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();

                if (key.contains("-")) {
                    String camelKey = buildCamelKey(key);

                    Field field = null;
                    try {
                        field = OpentpProperties.class.getDeclaredField(camelKey);
                        field.setAccessible(true);
                        String name = field.getType().getName();
                        if (name.equals("int")) {
                            field.setInt(properties, Integer.parseInt(value));
                        } else if (name.equals("java.lang.String")) {
                            field.set(camelKey, value);
                        }
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        log.error("", e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String buildCamelKey(String key) {
        String[] keys = key.split("-");

        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(keys[0]);
        for (int i = 1; i < keys.length; i++) {
            if (keys[i].length() > 1) {
                keyBuilder.append(keys[i].substring(0, 1).toUpperCase()).append(keys[i].substring(1));
            } else {
                keyBuilder.append(keys[i].toUpperCase());
            }
        }
        return keyBuilder.toString();
    }

    private static URL loadResource(String fileName) {
        Configuration configuration = Configuration.configuration();
        return configuration.appClassLoader().getResource(fileName);
    }
}
