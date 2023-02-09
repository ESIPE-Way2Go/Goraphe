package fr.esipe.way2go.service.impl;

import fr.esipe.way2go.service.ScriptPythonService;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Path;
@Service
public class ScriptPythonServiceImpl implements ScriptPythonService {
    /**
     *
     * @param user : identifiant de la simulation
     * @param simulation : id of the simulation
     * @param nameScript : name of the script
     */
    public void executeScript(String user, String simulation, String nameScript) {
        var path = System.getProperty("user.dir") + System.getProperty("file.separator")+ "scripts" + System.getProperty("file.separator") + "test.py";
        var builder = new ProcessBuilder("python3", Path.of(path).toString(), user, simulation, "10");

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
