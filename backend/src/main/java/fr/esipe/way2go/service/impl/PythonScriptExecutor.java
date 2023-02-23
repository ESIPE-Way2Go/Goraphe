package fr.esipe.way2go.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PythonScriptExecutor {

    public static void main(String[] args) throws IOException {

        // First Python script
        ProcessBuilder pb1 = new ProcessBuilder("python3", "script1.py");
        Process process1 = pb1.start();

        BufferedReader stdout1 = new BufferedReader(new InputStreamReader(process1.getInputStream()));
        String line;
        StringBuilder output1 = new StringBuilder();
        while ((line = stdout1.readLine()) != null) {
            output1.append(line);
        }

        // Second Python script
        ProcessBuilder pb2 = new ProcessBuilder("python3", "script2.py", output1.toString());
        Process process2 = pb2.start();

        BufferedReader stdout2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
        StringBuilder output2 = new StringBuilder();
        while ((line = stdout2.readLine()) != null) {
            output2.append(line);
        }

        // Third Python script
        ProcessBuilder pb3 = new ProcessBuilder("python3", "script3.py", output2.toString());
        Process process3 = pb3.start();

        BufferedReader stdout3 = new BufferedReader(new InputStreamReader(process3.getInputStream()));
        StringBuilder output3 = new StringBuilder();
        while ((line = stdout3.readLine()) != null) {
            output3.append(line);
        }

        System.out.println(output3.toString());
    }
}
