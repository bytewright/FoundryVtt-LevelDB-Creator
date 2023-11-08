package org.bytewright.foundrytools.json.pojo;

public class JournalPageText  extends BaseFoundryVttObject{
    Integer format;
    String content;
    String markdown;

    public Integer getFormat() {
        return format;
    }

    public void setFormat(Integer format) {
        this.format = format;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }
}
