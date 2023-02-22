package fr.esipe.way2go.dto.user.response;

public class UserInfoResponse {
    private Long id;
    private String name;
    private String mail;
    private String role;

    public UserInfoResponse(Long id, String name, String mail, String role) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public String getRole() {
        return role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
