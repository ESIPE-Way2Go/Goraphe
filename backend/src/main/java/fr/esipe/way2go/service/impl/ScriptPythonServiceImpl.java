package fr.esipe.way2go.service.impl;

import java.io.IOException;
public class ScriptPythonServiceImpl {
    /**
     *
     * @param user : identifiant de la simulation
     * @param simulation : id of the simulation
     * @param nameScript : name of the script
     */
    public static void executeScript(String user, String simulation, String nameScript) {
        System.out.println("python3 script/" + nameScript + ".py " + user + " " + simulation + " 10 ");
        var  builder = new ProcessBuilder("python3", "script/" + nameScript + ".py", user, simulation, "10");
        try {
            builder.start();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
