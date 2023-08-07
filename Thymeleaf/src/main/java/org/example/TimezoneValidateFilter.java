package org.example;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;

@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        res.setContentType("text/html; charset=utf-8");

        String zoneId  = req.getParameter("zoneId");
        if (zoneId == null || validateTimeZone(zoneId.replace(' ', '+'))) {
            chain.doFilter(req, res);
        } else {
            res.getWriter().write("Wrong time zone");
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().close();
        }
    }

    private boolean validateTimeZone(String timezone) {
        try {
            ZoneId.of(timezone);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }
}






