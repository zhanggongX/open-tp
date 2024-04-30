package cn.opentp.server.util;

import cn.opentp.server.command.CommandOptions;
import cn.opentp.server.configuration.OpentpProperties;
import cn.opentp.server.exception.ResourceLoadException;
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

public class PropertiesUtil {

    private static final Logger log = LoggerFactory.getLogger(PropertiesUtil.class);

    public static boolean loadCmdProps(OpentpProperties properties, String[] args) {

        CommandLineParser parser = new DefaultParser();
        Options options = CommandOptions.opentpOption();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.getOptionValue("h") != null) {
                CommandOptions.printHelp(options);
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
            CommandOptions.printHelp(options);
            return false;
        }
    }

    public static void loadProps(ClassLoader classLoader, OpentpProperties properties, String fileName) {

        URL resource = ResourceUtil.loadResource(classLoader, fileName);
        if (resource == null) {
            return;
        }

        try (InputStreamReader reader = new InputStreamReader(resource.openStream(), StandardCharsets.UTF_8)) {
            Properties tempProps = new Properties();
            tempProps.load(reader);

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
            throw new ResourceLoadException("加载资源失败", e);
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
}
