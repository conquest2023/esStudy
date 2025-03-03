package es.board.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Enumeration;
import java.util.Map;

public class XssSafeHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final Map<String, String[]> sanitizedParams;

    public XssSafeHttpServletRequestWrapper(HttpServletRequest request, Map<String, String[]> sanitizedParams) {
        super(request);
        this.sanitizedParams = sanitizedParams;
    }

    @Override
    public String getParameter(String name) {
        String[] values = sanitizedParams.get(name);
        return (values != null && values.length > 0) ? values[0] : null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return sanitizedParams;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return java.util.Collections.enumeration(sanitizedParams.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return sanitizedParams.get(name);
    }
}