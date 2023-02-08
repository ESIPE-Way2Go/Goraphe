package fr.esipe.way2go.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class ScriptPythonServiceImpl {
    /**
     *
     * @param user : identifiant de la simulation
     * @param simulation : id of the simulation
     * @param nameScript : name of the script
     */
    public static void executeScript(String user, String simulation, String nameScript) {
        var p = System.getProperty("user.dir") + System.getProperty("file.separator")+ "scripts" + System.getProperty("file.separator") + "test.py";
        var  builder = new ProcessBuilder("python3", Path.of(p).toString(), user, simulation, "10");

        try {
            var process = builder.start();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
