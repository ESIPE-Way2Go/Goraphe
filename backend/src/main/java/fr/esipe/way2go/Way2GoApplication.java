package fr.esipe.way2go;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

//@SpringBootApplication
public class Way2GoApplication {
    public static void main(String[] args) {
//        SpringApplication.run(Way2GoApplication.class, args);
        var p = System.getProperty("user.dir") + System.getProperty("file.separator")+ "scripts" + System.getProperty("file.separator") + "test.py";
        //var p = new ClassPathResource("test.py").getPath();
        System.out.println(p);
        var builder = new ProcessBuilder("python3", Path.of(p).toString(), "10");
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();
            var res = new String(process.getInputStream().readAllBytes());
            int exitCode = process.waitFor();
//            System.out.println("result = " + res);
//            System.out.println("exitCode = " + exitCode);

            System.out.println("result"+res);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}