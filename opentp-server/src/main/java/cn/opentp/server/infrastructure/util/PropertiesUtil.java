package cn.opentp.server.infrastructure.util;

import cn.opentp.server.infrastructure.command.CommandOptions;
import cn.opentp.server.Environment;
import cn.opentp.server.infrastructure.exception.ResourceLoadException;
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
    private static final String CONFIG_SPLITTER = ".";
    private static final String CONFIG_DO_SPLITTER = "\\.";


    public static boolean loadCmdProps(Environment properties, String[] args) {

        CommandLineParser parser = new DefaultParser();
        Options options = CommandOptions.opentpOption();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.getOptionValue("h") != null) {
                CommandOptions.printHelp(options);
                return false;
            }

            String reportPort = cmd.getOptionValue("rp");
            if (reportPort != null) properties.setReceivePort(Integer.parseInt(reportPort));

            String httpPort = cmd.getOptionValue("hp");
            if (httpPort != null) properties.setHttpPort(Integer.parseInt(httpPort));

            String transportPort = cmd.getOptionValue("gp");
            if (transportPort != null) properties.setTransportPort(Integer.parseInt(transportPort));

            return true;
        } catch (ParseException exp) {
            CommandOptions.printHelp(options);
            return false;
        }
    }

    public static void loadProps(ClassLoader classLoader, Environment environment, String fileName) {

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

                String realKey = key;
                if (key.contains(CONFIG_SPLITTER)) {
                    realKey = buildCamelKey(key);
                }

                Field field = null;
                try {
                    field = Environment.class.getDeclaredField(realKey);
                    field.setAccessible(true);
                    String name = field.getType().getName();
                    if (name.equals("int")) {
                        field.setInt(environment, Integer.parseInt(value));
                    } else {
                        field.set(environment, String.valueOf(value));
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    log.error("", e);
                }
            }
        } catch (IOException e) {
            throw new ResourceLoadException("加载资源失败", e);
        }
    }

    private static String buildCamelKey(String key) {
        String[] keys = key.split(CONFIG_DO_SPLITTER, -1);

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
