package es.board.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientIpResolver {
    public String resolve(HttpServletRequest req) {
        String ip = header(req, "CF-Connecting-IP");
        if (isEmpty(ip)) ip = firstXff(req.getHeader("X-Forwarded-For"));
        if (isEmpty(ip)) ip = req.getRemoteAddr();
        return ip;
    }
    private String firstXff(String xff) {
        if (isEmpty(xff)) return null;
        return xff.split(",")[0].trim();
    }
    private boolean isEmpty(String s){ return s==null||s.isEmpty()||"unknown".equalsIgnoreCase(s);}
    private String header(HttpServletRequest r, String name){ String v=r.getHeader(name); return isEmpty(v)?null:v; }
}