package fr.esipe.way2go.dao;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user", schema = "public", catalog = "compte")
@Inheritance(strategy = InheritanceType.JOINED)
public class UserEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "firstname", nullable = false)
    private String firstname;

    @Column(name = "mail", nullable = false)
    private String mail;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "role", nullable = false)
    private String role;

    /**
     * Returns this user's ID.
     *
     * @return id This user's ID. (Long)
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets a new ID for this user.
     *
     * @param id This user's new ID. (Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns this user's username.
     *
     * @return username this user's username. (String)
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets a new username for this user.
     *
     * @param username This user's new username. (String)
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns this user's name.
     *
     * @return name This user's name (String)
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for this user.
     *
     * @param name This user's new name. (String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns this user's first name.
     *
     * @return This user's first name (String)
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Sets a new first name for this user.
     *
     * @param firstname This user's new first name.
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Returns this user's email address.
     *
     * @return mail This user's email address. (String)
     */
    public String getMail() {
        return mail;
    }

    /**
     * Sets a new email address for this user.
     *
     * @param mail This user's new email address. (String)
     */
    public void setMail(String mail) {
        this.mail = mail;
    }

    /**
     * Returns this user's password.
     *
     * @return password This user's password. (String)
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets a new password for this user.
     *
     * @param password This user's new password. (String)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns this user's type.
     *
     * @return type This user's type. (String)
     */
    public String getType() {
        return type;
    }

    /**
     * Sets a new type for this user.
     *
     * @param type This user's new type. (String)
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    public String getRole() {
        return role;
    }

    /**
     *
     * @param role
     */
    public void setRole(String role) {

        this.role = role;
    }
}