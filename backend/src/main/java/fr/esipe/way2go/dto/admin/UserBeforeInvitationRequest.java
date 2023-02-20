package fr.esipe.way2go.dto.admin;

public class UserBeforeInvitationRequest {
    private String email;
    private String role;

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
