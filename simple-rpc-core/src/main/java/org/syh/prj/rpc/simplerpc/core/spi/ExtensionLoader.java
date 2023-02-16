package org.syh.prj.rpc.simplerpc.core.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtensionLoader {
    private String EXTENSION_LOADER_DIR_PREFIX = "META-INF/simple-rpc/";
    private Map<String, LinkedHashMap<String, Class<?>>> extensionLoaderClassMap = new ConcurrentHashMap<>();

    public void loadExtension(Class clazz) throws IOException, ClassNotFoundException {
        if (clazz == null) {
            throw new IllegalArgumentException("class is null!");
        }
        String spiFilePath = EXTENSION_LOADER_DIR_PREFIX + clazz.getName();
        Enumeration<URL> enumeration = this.getClass().getClassLoader().getResources(spiFilePath);
        while (enumeration.hasMoreElements()) {
            URL url = enumeration.nextElement();
            InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            LinkedHashMap<String, Class<?>> classMap = new LinkedHashMap<>();
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                String[] lineArr = line.split("=");
                String implClassName = lineArr[0];
                String interfaceName = lineArr[1];
                classMap.put(implClassName, Class.forName(interfaceName));
            }

            extensionLoaderClassMap.computeIfAbsent(
                clazz.getName(),
                param -> new LinkedHashMap<>()
            ).putAll(classMap);
        }
    }

    public <T> Class<?> getActualClass(Class<T> clazz, String type) throws IOException, ClassNotFoundException {
        if (!extensionLoaderClassMap.containsKey(clazz.getName())) {
            loadExtension(clazz);
        }
        return extensionLoaderClassMap.get(clazz.getName()).get(type);
    }

    public <T> List<Class<?>> getActualClassList(Class<T> clazz) throws IOException, ClassNotFoundException {
        if (!extensionLoaderClassMap.containsKey(clazz.getName())) {
            loadExtension(clazz);
        }
        return new ArrayList<>(extensionLoaderClassMap.get(clazz.getName()).values());
    }
}
