package es.board.domain;

import es.board.controller.record.NormalizedContent;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

@Service
public class NormalizeService {

    public NormalizedContent normalize(String rawHtml, String title) {
        var whitelist = Safelist.relaxed()
                .addTags("p","br","ul","ol","li","b","i","strong","em","blockquote","h1","h2","h3","code","pre")
                .removeAttributes(":all", "style", "class", "id");
        var cleanedHtml = Jsoup.clean(rawHtml, whitelist);

        var doc = Jsoup.parse(cleanedHtml);
        String text = doc.text().replaceAll("\\s+", " ").trim();

        var links = doc.select("a[href]").stream()
                .map(e -> e.attr("abs:href").isBlank() ? e.attr("href") : e.attr("abs:href"))
                .distinct().limit(100).toList();

        int len = text.length();
        int tokens = estimateTokens(title, text);
        String hash = "sha256:" + DigestUtils.sha256Hex(title + "\n" + text);

        String excerpt = text.length() > 300 ? text.substring(0, 300) + "..." : text;

        return new NormalizedContent(cleanedHtml, text, excerpt, links, len, tokens, hash);
    }

    public static int estimateTokens(String title, String text) {
        String s = (title == null ? "" : title) + " " + (text == null ? "" : text);
        int len = s.length();

        int cjk = 0;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (isCJK(ch)) cjk++;
        }
        double cjkRatio = len == 0 ? 0.0 : (double) cjk / len;

        double tokens;
        if (cjkRatio > 0.3) {
            tokens = len * 1.0;            // 한글/중문 위주
        } else {
            tokens = Math.ceil(len / 4.0); // 영문 위주
        }

        tokens *= 1.05;

        return (int) Math.ceil(tokens);
    }

    private static boolean isCJK(char ch) {
        Character.UnicodeBlock b = Character.UnicodeBlock.of(ch);
        return b == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || b == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || b == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || b == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || b == Character.UnicodeBlock.HIRAGANA
                || b == Character.UnicodeBlock.KATAKANA
                || b == Character.UnicodeBlock.HANGUL_SYLLABLES
                || b == Character.UnicodeBlock.HANGUL_JAMO
                || b == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO;
    }

}
