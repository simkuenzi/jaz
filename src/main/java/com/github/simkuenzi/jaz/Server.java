package com.github.simkuenzi.jaz;

import io.javalin.Javalin;
import io.javalin.core.compression.CompressionStrategy;
import io.javalin.http.Context;
import io.javalin.plugin.rendering.FileRenderer;
import io.javalin.plugin.rendering.JavalinRenderer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Server {

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getProperty("com.github.simkuenzi.http.port", "9000"));
        String context = System.getProperty("com.github.simkuenzi.http.context", "/jaz");

        JavalinRenderer.register(renderer(), ".html");

        Javalin.create(config -> {
            config.contextPath = context;
            config.addStaticFiles("com/github/simkuenzi/jaz/static/");

            // Got those errors on the apache proxy with compression enabled. Related to the Issue below?
            // AH01435: Charset null not supported.  Consider aliasing it?, referer: http://pi/one-egg/
            // AH01436: No usable charset information; using configuration default, referer: http://pi/one-egg/
            config.compressionStrategy(CompressionStrategy.NONE);
        })

        // Workaround for https://github.com/tipsy/javalin/issues/1016
        // Aside from mangled up characters the wrong encoding caused apache proxy to fail on style.css.
        // Apache error log: AH01385: Zlib error -2 flushing zlib output buffer ((null))
        .before(ctx -> {
            if (ctx.res.getCharacterEncoding().equals("utf-8")) {
                ctx.res.setCharacterEncoding(StandardCharsets.UTF_8.name());
            }
        })
        .start(port)

        .get("/", ctx -> ctx.render("home.html", model(ctx)));
    }

    private static Map<String, Object> model(Context ctx) throws IOException {
        Map<String, Object> vars = new HashMap<>();
        Properties versionProps = new Properties();
        versionProps.load(Server.class.getResourceAsStream("version.properties"));
        vars.put("version", versionProps.getProperty("version"));
        return vars;
    }

    private static FileRenderer renderer() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/com/github/simkuenzi/jaz/templates/");
        templateResolver.setCacheable(false);
        templateResolver.setForceTemplateMode(true);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return (filePath, model, context) -> {
            WebContext thymeleafContext = new WebContext(context.req, context.res, context.req.getServletContext(), context.req.getLocale());
            thymeleafContext.setVariables(model);
            return templateEngine.process(filePath, thymeleafContext);
        };
    }
}
