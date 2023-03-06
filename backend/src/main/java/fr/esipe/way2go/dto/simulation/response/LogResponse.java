package fr.esipe.way2go.dto.simulation.response;

import fr.esipe.way2go.utils.StatusScript;

public class LogResponse {
    private StatusScript status;
    private String scriptName;
    private String[] content;

    public LogResponse() {
    }

    public LogResponse(StatusScript status, String scriptName, String[] content) {
        this.status = status;
        this.scriptName = scriptName;
        this.content = content;
    }

    public StatusScript getStatus() {
        return status;
    }

    public String getScriptName() {
        return scriptName;
    }

    public String[] getContent() {
        return content;
    }

    public void setStatus(StatusScript status) {
        this.status = status;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public void setContent(String[] content) {
        this.content = content;
    }
}
