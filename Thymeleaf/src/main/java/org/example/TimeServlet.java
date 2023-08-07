package org.example;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String resultTime = getDateTimeFromUserQuery(req, resp);
        Context context = new Context(req.getLocale(), Map.of("time", "resultTime"));
        context.setVariable("time", resultTime);

        engine.process("time", context, resp.getWriter());
        resp.getWriter().close();
    }

    private static String getDateTimeFromUserQuery(HttpServletRequest req, HttpServletResponse resp) {
        String zoneId = req.getParameter("zoneId");
        if (zoneId == null) {
            zoneId = getCookies(req);
        } else {
            zoneId = zoneId.replace(' ', '+');
            resp.addCookie(new Cookie("lastTimezone", zoneId));
        }

        return ZonedDateTime
                .now(ZoneId.of(zoneId))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss ")) + zoneId;
    }

    private static String getCookies(HttpServletRequest req) {
        String cookiesTime = req.getHeader("Cookie");

        Map<String, String> result = new HashMap<>();

        if (cookiesTime != null) {
            String[] separateCookies = cookiesTime.split(";");
            for (String pair : separateCookies) {
                String[] keyValue = pair.split("=");
                result.put(keyValue[0], keyValue[1]);
            }
        }
        return result.getOrDefault("lastTimezone", "UTC");
    }
}

/*
http://localhost:8080
http://localhost:8080/Thymeleaf/time
 */



