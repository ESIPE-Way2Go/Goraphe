package fr.esipe.way2go;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
public class Way2GoApplication {
    public static void main(String[] args) {
        SpringApplication.run(Way2GoApplication.class, args);
       /* var command = "python3 //home/jeremy/Documents/ETUDE/ESIPE/INFO3/LastProject/AUTOROUTE/Goraphe/scripts/test.py 10 ";
        try {
            var process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
*/
    }

}