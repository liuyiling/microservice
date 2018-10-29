package com.liuyiling.microservice.core.generator;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author liuyiling
 */
@SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
public class Generator {

    protected VelocityConfiguration config;

    public Generator() {
        config = new VelocityConfiguration();
    }

    public Generator(VelocityConfiguration config) {
        this.config = config;
    }

    public Writer createWriter(String targetDir, String path) {
        path = targetDir + path;
        File file = new File(path);
        String dir = file.getParent();
        File pd = new File(dir);
        if (!pd.exists()) {
            pd.mkdirs();
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return writer;
    }

    private VelocityEngine createVelocityEngine() {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        ve.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        ve.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, config.getTargetDir() + "com/xmair/core/template/");
        ve.init();
        return ve;
    }

    public void generateModel(String targetDir, Table table) {
        VelocityEngine velocityEngine = createVelocityEngine();
        VelocityContext context = createContext(table);
        Writer writer = createWriter(targetDir, config.getModelPackage().replace(".", "/")
                + "/" + table.getPojoBeanName() + ".java");

        Template t = velocityEngine.getTemplate("beanTemplate.vm");
        t.merge(context, writer);
        flushWriter(writer);
    }

    public void generateMapper(String targetDir, Table table) {
        VelocityEngine velocityEngine = createVelocityEngine();
        VelocityContext context = createContext(table);
        Writer writer = createWriter(targetDir, config.getMapperPackage().replace(".", "/")
                + "/" + table.getPojoBeanName() + "Mapper.java");
        Template t = velocityEngine.getTemplate("mapperTemplate.vm");
        t.merge(context, writer);
        flushWriter(writer);
    }

    private void flushWriter(Writer writer) {
        try {
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateXml(String targetDir, Table table) {
        VelocityEngine velocityEngine = createVelocityEngine();
        VelocityContext context = createContext(table);
        Writer writer = createWriter(targetDir, config.getXmlPackage()
                .replace(".", "/") + "/" + table.getPojoBeanName() + "Mapper.xml");
        Template t = velocityEngine.getTemplate("xmlTemplate.vm");
        t.merge(context, writer);
        flushWriter(writer);
    }

    private VelocityContext createContext(Table table) {
        Map map = BeanUtils.getValueMap(table);
        Map configMap = BeanUtils.getValueMap(config);
        configMap.put("author", System.getProperty("user.name"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        configMap.put("dateTime", sdf.format(new Date()));
        map.putAll(configMap);
        VelocityContext context = new VelocityContext(map);

        return context;
    }


}
