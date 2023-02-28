package fr.esipe.way2go.dto.result.response;

public class ResultResponse {
    private String key;
    private String content;

    public ResultResponse(String key, String content) {
        this.key = key;
        this.content = content;
    }

    public String getKey() {
        return key;
    }

    public String getContent() {
        return content;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
