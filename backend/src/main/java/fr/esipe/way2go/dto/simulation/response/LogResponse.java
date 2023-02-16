package fr.esipe.way2go.dto.simulation.response;

public class LogResponse {
    private String status;
    private String scriptName;
    private String[] content;

    public LogResponse() {
    }

    public LogResponse(String status, String scriptName, String[] content) {
        this.status = status;
        this.scriptName = scriptName;
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public String getScriptName() {
        return scriptName;
    }

    public String[] getContent() {
        return content;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public void setContent(String[] content) {
        this.content = content;
    }
}
