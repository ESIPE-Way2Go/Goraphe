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
        var path =  System.getProperty("user.dir") + System.getProperty("file.separator")+ "scripts" + System.getProperty("file.separator") + "test.py";
        System.out.println(path);
        var command = "python3 " + path + " " + user + " " + simulation + "10";
        var p = System.getProperty("user.dir") + System.getProperty("file.separator")+ "scripts" + System.getProperty("file.separator") + "test.py";
        var  builder = new ProcessBuilder("python3", Path.of(p).toString(), user, simulation, "10");

        try {
            var process = builder.start();
            var res = new String(process.getInputStream().readAllBytes());
            int exitCode = process.waitFor();
//            System.out.println("result = " + res);
//            System.out.println("exitCode = " + exitCode);

            System.out.println("result"+res);
        } catch (IOException e) {
            throw new RuntimeException();
        } /*catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/ catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
