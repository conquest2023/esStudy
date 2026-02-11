export function htmlToText(html) {
    if (!html) return "";
    const doc = new DOMParser().parseFromString(html, "text/html");

    // <br> → \n
    doc.querySelectorAll("br").forEach(br => br.replaceWith("\n"));

    // <p> → 내용 + \n\n
    doc.querySelectorAll("p").forEach(p => {
        p.replaceWith(p.textContent + "\n\n");
    });

    // 나머지는 textContent로
    return (doc.body.textContent || "")
        .replace(/\u00A0/g, " ")
        .replace(/\n{3,}/g, "\n\n")
        .trim();
}

export function textToHtml(text) {
    if (!text) return "";
    // 줄바꿈 기준으로 문단 만들기
    const paras = text
        .split(/\n{2,}/)
        .map(p => p.trim())
        .filter(Boolean);

    return paras.map(p => `<p>${escapeHtml(p).replace(/\n/g, "<br>")}</p>`).join("");
}

function escapeHtml(str) {
    return str
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
