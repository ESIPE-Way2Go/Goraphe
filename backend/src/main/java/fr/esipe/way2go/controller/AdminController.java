package fr.esipe.way2go.controller;

import com.fasterxml.uuid.Generators;
import fr.esipe.way2go.configuration.WebSecurityConfiguration;
import fr.esipe.way2go.dao.InviteEntity;
import fr.esipe.way2go.dao.UserEntity;
import fr.esipe.way2go.dto.admin.UserBeforeInvitationRequest;
import fr.esipe.way2go.dto.invite.response.InvitationResponse;
import fr.esipe.way2go.dto.user.request.UpdatePasswordRequest;
import fr.esipe.way2go.dto.user.request.UserRequest;
import fr.esipe.way2go.dto.user.response.UserInfoResponse;
import fr.esipe.way2go.dto.user.response.UserResponse;
import fr.esipe.way2go.exception.user.UserEmailFound;
import fr.esipe.way2go.exception.user.WrongEmailFormatException;
import fr.esipe.way2go.exception.user.WrongPasswordFormatException;
import fr.esipe.way2go.exception.invite.InviteNotFoundException;
import fr.esipe.way2go.exception.user.UserNotFoundException;
import fr.esipe.way2go.exception.user.UserTokenNotFoundException;
import fr.esipe.way2go.exception.user.UsernameExistAlreadyException;
import fr.esipe.way2go.service.EmailService;
import fr.esipe.way2go.service.InviteService;
import fr.esipe.way2go.service.SimulationService;
import fr.esipe.way2go.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.annotation.security.PermitAll;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/administration")
public class AdminController {
    private EmailService emailSenderService;

    private UserService userService;
    private SimulationService simulationService;

    private InviteService inviteService;

    private WebSecurityConfiguration webSecurityConfiguration;

    @Autowired
    public AdminController(EmailService emailSenderService, UserService userService, SimulationService simulationService, InviteService inviteService, WebSecurityConfiguration webSecurityConfiguration) {
        this.emailSenderService = emailSenderService;
        this.userService = userService;
        this.inviteService = inviteService;
        this.webSecurityConfiguration = webSecurityConfiguration;
        this.simulationService = simulationService;
    }

    private void checkEmail(String email) {
        var regex = "^\\w[\\w.%+-]*@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!Pattern.compile(regex).matcher(email).matches()) throw new WrongEmailFormatException();
    }

    private void checkPassword(String password) {
        var regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";
        if (!password.matches(regex)) throw new WrongPasswordFormatException();
    }
    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    @PermitAll
    @GetMapping("/checkAccount/{token}")
    public ResponseEntity<UserResponse> checkAccount(@PathVariable String token) {
        var inviteEntityOptional = inviteService.findByToken(token);
        if (inviteEntityOptional.isEmpty())
            throw new InviteNotFoundException();
        var inviteEntity = inviteEntityOptional.get();
        return new ResponseEntity<>(new UserResponse(inviteEntity.getTargetEmail()), HttpStatus.OK);
    }

    @PermitAll
    @PutMapping("/createAccount")
    public ResponseEntity<Boolean> createAccount(@RequestBody UserRequest userRequest) {
        checkEmail(userRequest.getEmail());
        checkPassword(userRequest.getPassword());
        if (userRequest.getUsername().strip().isBlank()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        var user = userService.getUser(userRequest.getUsername());
        if (user.isPresent())
            throw new UsernameExistAlreadyException();

        var passwordEncoder = webSecurityConfiguration.passwordEncoder();
        var newUser =new UserEntity(userRequest.getUsername(), passwordEncoder.encode(userRequest.getPassword()), userRequest.getEmail(), "ROLE_USER");

        userService.saveUser(newUser);
        var invite = inviteService.findByEmail(userRequest.getEmail());
        if (invite.isPresent())
            inviteService.delete(invite.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PermitAll
    @GetMapping("/forgetPassword/{email}")
    public ResponseEntity<Object> sendForgotPassword(@PathVariable String email) {
        checkEmail(email);
        var userOptional = userService.getUserByEmail(email);
        if (userOptional.isEmpty())
            return new ResponseEntity<>(HttpStatus.OK);
        var user = userOptional.get();
        user.setToken(Generators.timeBasedGenerator().generate().toString());
        var userSave = userService.saveUser(user);
        emailSenderService.sendLinkForForgetPassword(userSave);
        return new ResponseEntity<>(HttpStatus.OK);
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
        checkPassword(updatePasswordRequest.getPassword());
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


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserInfoResponse>> getAllUsers() {
        var users = userService.getAllUsers();
        var usersInfo = new ArrayList<UserInfoResponse>();
        users.forEach(user -> usersInfo.add(new UserInfoResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole())));
        return new ResponseEntity<>(usersInfo, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        var userOptional = userService.getUserById(id);
        if (userOptional.isEmpty())
            throw new UserNotFoundException();

        var user = userOptional.get();
        var simulations = simulationService.getSimulationsOfUser(user);
        simulations.forEach(simulation -> simulationService.deleteSimulation(simulation));
        userService.deleteUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PermitAll
    @GetMapping("/invitations")
    public ResponseEntity<List<InvitationResponse>> getInvitations() {
        var invites = new ArrayList<InvitationResponse>();
        inviteService.getAll().forEach(invite -> invites.add(new InvitationResponse(invite.getInviteId(), invite.getTargetEmail(), invite.getMailSent(), invite.getMailCount())));
        return new ResponseEntity<>(invites, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/invitation/{id}")
    public ResponseEntity<Object> deleteInvitation(@PathVariable Long id) {
        var inviteEntityOptional = inviteService.findById(id);
        if (inviteEntityOptional.isEmpty())
            throw new InviteNotFoundException();

        inviteService.delete(inviteEntityOptional.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/invitation")
    public void sendInvitation(@RequestBody UserBeforeInvitationRequest userBeforeInvitationRequest) {
        var email = userBeforeInvitationRequest.getEmail();
        checkEmail(email);
        var user = userService.getUserByEmail(email);
        if (user.isPresent())
            throw new UserEmailFound(email);
        var inviteEntity = new InviteEntity(
                email, generateToken()
        );

        var saveInvite = inviteService.save(inviteEntity);
        emailSenderService.sendInvitation(email, saveInvite);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/invitation/{id}")
    public ResponseEntity<Object> reSendInvitation(@PathVariable Long id) {
        var inviteEntityOptional = inviteService.findById(id);
        if (inviteEntityOptional.isEmpty())
            throw new InviteNotFoundException();
        var invite = inviteEntityOptional.get();
        invite.setMailSent(Calendar.getInstance());
        invite.setMailCount(invite.getMailCount() + 1);
        invite.setToken(generateToken());
        inviteService.save(invite);
        emailSenderService.sendInvitation(invite.getTargetEmail(), invite);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

