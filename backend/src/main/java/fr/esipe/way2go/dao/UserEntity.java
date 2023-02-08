package fr.esipe.way2go.dao;

import javax.persistence.*;

@Entity
@Table(name = "user", schema = "public", catalog = "compte")
@Inheritance(strategy = InheritanceType.JOINED)
public class UserEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "role", nullable = false)
    private String role;

    /**
     * Returns this user's ID.
     *
     * @return userId This user's ID. (Long)
     */
    public Long getId() {
        return userId;
    }

    /**
     * Sets a new ID for this user.
     *
     * @param userId This user's new ID. (Long)
     */
    public void setId(Long userId) {
        this.userId = userId;
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
     * Returns this user's email address.
     *
     * @return email This user's email address. (String)
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets a new email address for this user.
     *
     * @param email This user's new email address. (String)
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns this user's role.
     *
     * @return role This user's role. (String)
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets a new role for this user.
     *
     * @param role This user's new role. (String)
     */
    public void setRole(String role) {
        this.role = role;
    }
}