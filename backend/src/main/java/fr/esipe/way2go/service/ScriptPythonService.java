package fr.esipe.way2go.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public interface ScriptPythonService {

    void executeScript(String user, String simulation, String script);

    /**
     * Get the content of the files
     * @param path of the file
     * @return the content on file in the string
     */
    default String readFile(String path) {
        var logs = new StringBuilder();
        try (var br = new BufferedReader(new FileReader(path))){
            var line = "";
            while ((line = br.readLine()) != null) {
                logs.append(line).append("\n");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return logs.toString();
    }
}
