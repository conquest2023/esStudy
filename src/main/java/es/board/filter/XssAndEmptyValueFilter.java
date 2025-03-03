package es.board.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;

@Component
public class XssAndEmptyValueFilter  implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 🔥 XSS 필터링 적용
        Map<String, String[]> sanitizedParams = httpRequest.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> sanitizeArray(entry.getValue())
                ));

        XssSafeHttpServletRequestWrapper wrappedRequest = new XssSafeHttpServletRequestWrapper(httpRequest, sanitizedParams);
        chain.doFilter(wrappedRequest, response);
    }


    private String[] sanitizeArray(String[] values) {
        if (values == null) return null;
        return Arrays.stream(values)
                .map(value -> (value == null || value.trim().isEmpty()) ? null : Jsoup.clean(value, Safelist.basic()))
                .toArray(String[]::new);
    }
}
