package es.board.util;

import org.jsoup.nodes.Element;

public  class ResizeImage {

    public static String pickSrc(Element img) {
        String src = img.attr("src");
        if (src == null || src.isBlank()) src = img.attr("data-src");
        if ((src == null || src.isBlank()) && img.hasAttr("srcset")) {
            String[] parts = img.attr("srcset").split(",");
            src = parts[parts.length - 1].trim().split(" ")[0];
        }
        return src;
    }

    public static boolean isAcceptable(String url) {
        if (url == null || url.isBlank()) return false;
        if (url.startsWith("blob:") || url.startsWith("data:")) return false;
        if (!(url.startsWith("http://") || url.startsWith("https://"))) return false;
        // 필요시 화이트리스트(CloudFront/S3 전용)
        // return url.contains("cloudfront.net") || url.contains("s3.amazonaws.com") || url.contains("cdn.workly.info");
        return true;
    }

    public static Integer widthFrom(Element img) {
        Integer w = parseIntSafe(img.attr("width"));
        if (w != null) return w;
        String style = img.attr("style");
        if (style != null && style.contains("width")) {
            String m = style.replaceAll(".*width\\s*:\\s*([0-9]+).*", "$1");
            return parseIntSafe(m);
        }
        return null;
    }

    public static  Integer heightFrom(Element img) {
        Integer h = parseIntSafe(img.attr("height"));
        if (h != null) return h;
        String style = img.attr("style");
        if (style != null && style.contains("height")) {
            String m = style.replaceAll(".*height\\s*:\\s*([0-9]+).*", "$1");
            return parseIntSafe(m);
        }
        return null;
    }

    private static Integer parseIntSafe(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.valueOf(s.replaceAll("[^0-9]", "")); }
        catch (Exception e) { return null; }
    }

    public String stripExt(String filename) {
        int i = filename.lastIndexOf('.');
        return (i > 0) ? filename.substring(0, i) : filename;
    }


}
