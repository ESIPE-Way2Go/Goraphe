package fr.esipe.way2go.controller;

import com.fasterxml.uuid.Generators;
import fr.esipe.way2go.service.impl.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.security.PermitAll;
import java.util.Calendar;
import java.util.UUID;

@Controller
public class SimpleEmailExampleController {

    @Autowired
    private EmailServiceImpl emailSenderService;

    @PermitAll
    @ResponseBody
    @GetMapping("/sendSimpleEmail")
    public String sendSimpleEmail() {

        UUID timebaseUUID =  Generators.timeBasedGenerator().generate();      // it will geneate timebased UUID
        System.out.println("Time based UUID :"+timebaseUUID.toString());      // UUID string
        System.out.println("UUID version : "+timebaseUUID.version());         // 1 - version
        System.out.println("UUID Node : "+timebaseUUID.node());               // IEEE 802 address of mechine that generate value
        System.out.println("UUID Timestamp : "+timebaseUUID.timestamp());     // 60 bit timestamp

        var calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timebaseUUID.timestamp());
        System.out.println(calendar.getTime());
        /*
        //var uuid = UUID.randomUUID();
        var calendar = Calendar.getInstance();
     //   var test = UUID.fromString(uuid.toString());
       // calendar.setTimeInMillis();
        var uuid = Generators.timeBasedGenerator().generate();
        long timestamp = uuid.timestamp();
        var date = new Date((timestamp / 10000) - 12219292800000L);
        System.out.println(date);
    //    System.out.println(test.timestamp());
       // System.out.println(uuid.clockSequence());
       // System.out.println(UUID.randomUUID());
        //emailSenderService.sendInvitation();
       //*/
        return "Email Sent!";
    }

}

