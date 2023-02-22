package fr.esipe.way2go.controller;

import com.fasterxml.uuid.Generators;
import fr.esipe.way2go.configuration.WebSecurityConfiguration;
import fr.esipe.way2go.dao.InviteEntity;
import fr.esipe.way2go.dao.UserEntity;
import fr.esipe.way2go.dto.admin.UserBeforeInvitationRequest;
import fr.esipe.way2go.dto.user.request.UpdatePasswordRequest;
import fr.esipe.way2go.dto.user.request.UserRequest;
import fr.esipe.way2go.dto.user.response.UserResponse;
import fr.esipe.way2go.exception.EmailFormatWrongException;
import fr.esipe.way2go.exception.UserEmailFound;
import fr.esipe.way2go.exception.invite.InviteNotFoundException;
import fr.esipe.way2go.exception.user.UserNotFoundException;
import fr.esipe.way2go.exception.user.UserTokenNotFoundException;
import fr.esipe.way2go.exception.user.UsernameExistAlreadyException;
import fr.esipe.way2go.service.EmailService;
import fr.esipe.way2go.service.InviteService;
import fr.esipe.way2go.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/administration")
public class AdminController {
    private EmailService emailSenderService;

    private UserService userService;

    private InviteService inviteService;

    private WebSecurityConfiguration webSecurityConfiguration;

    @Autowired
    public AdminController(EmailService emailSenderService, UserService userService, InviteService inviteService, WebSecurityConfiguration webSecurityConfiguration) {
        this.emailSenderService = emailSenderService;
        this.userService = userService;
        this.inviteService = inviteService;
        this.webSecurityConfiguration = webSecurityConfiguration;
    }

    @PermitAll
    //  @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/invitation")
    public void sendInvitation(HttpServletRequest request, @RequestBody UserBeforeInvitationRequest userBeforeInvitationRequest) {
        var email = userBeforeInvitationRequest.getEmail();
        checkEmail(email);
        var user = userService.getUserByEmail(email);
        if (user.isPresent())
            throw new UserEmailFound(email);
        var inviteEntity = new InviteEntity(
                userService.saveUser(new UserEntity(email, userBeforeInvitationRequest.getRole())),
                Generators.timeBasedGenerator().generate().toString()
        );

        var saveInvite = inviteService.save(inviteEntity);
        emailSenderService.sendInvitation(email, saveInvite);
    }


    private void checkEmail(String email) {
        var regex = "^(.+)@(\\S+)$";
        var result = Pattern.compile(regex).matcher(email).matches();
        if (!result)
            throw new EmailFormatWrongException();
    }

    @PermitAll
    @GetMapping("/checkAccount/{token}")
    public ResponseEntity<UserResponse> checkAccount(@PathVariable String token) {
        var inviteEntityOptional = inviteService.findByToken(token);
        if (inviteEntityOptional.isEmpty())
            throw new InviteNotFoundException();

        var inviteEntity = inviteEntityOptional.get();
        var user = inviteEntity.getUser();
        return new ResponseEntity<>(new UserResponse(user.getEmail()), HttpStatus.OK);
    }

    @PermitAll
    @PutMapping("/createAccount")
    public ResponseEntity<Boolean> createAccount(@RequestBody UserRequest userRequest) {
        var user = userService.getUser(userRequest.getUsername());
        if (user.isPresent())
            throw new UsernameExistAlreadyException();

        var userSave = userService.getUserByEmail(userRequest.getEmail());
        if (userSave.isEmpty())
            throw new UserNotFoundException();
        var userGet = userSave.get();

        var passwordEncoder = webSecurityConfiguration.passwordEncoder();

        userGet.setUsername(userRequest.getUsername());
        userGet.setPassword(passwordEncoder.encode(userGet.getPassword()));
        userService.saveUser(userGet);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PermitAll
    @GetMapping("/forgetPassword/{email}")
    public void sendForgotPassword(@PathVariable String email) {
        var userOptional = userService.getUserByEmail(email);
        if (userOptional.isEmpty())
            throw new UserNotFoundException();
        var user = userOptional.get();
        user.setToken(Generators.timeBasedGenerator().generate().toString());
        var userSave = userService.saveUser(user);
        emailSenderService.sendLinkForForgetPassword(userSave);
    }

    @PermitAll
    @GetMapping("/checkModifyPassword/{token}")
    public ResponseEntity<Object> checkModifyPassword(@PathVariable String token) {
        var user = userService.getUserByToken(token);
        if (user.isEmpty())
            throw new UserTokenNotFoundException();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PermitAll
    @PutMapping("/updatePassword/")
    public ResponseEntity<Boolean> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest) {
        var userEntityOptional = userService.getUserByToken(updatePasswordRequest.getToken());
        if (userEntityOptional.isEmpty())
            throw new UserTokenNotFoundException();
        var userEntity = userEntityOptional.get();
        var passwordEncoder = webSecurityConfiguration.passwordEncoder();
        userEntity.setPassword(passwordEncoder.encode(updatePasswordRequest.getPassword()));
        userEntity.setToken(null);
        userService.saveUser(userEntity);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PermitAll
    @ResponseBody
    @GetMapping("/sendSimpleEmail")
    public String sendSimpleEmail(HttpServletRequest request) {
        System.out.println(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort());

        UUID timebaseUUID = Generators.timeBasedGenerator().generate();      // it will geneate timebased UUID
        System.out.println("Time based UUID :" + timebaseUUID.toString());      // UUID string
        System.out.println("UUID version : " + timebaseUUID.version());         // 1 - version
        System.out.println("UUID Node : " + timebaseUUID.node());               // IEEE 802 address of mechine that generate value
        System.out.println("UUID Timestamp : " + timebaseUUID.timestamp());     // 60 bit timestamp

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

