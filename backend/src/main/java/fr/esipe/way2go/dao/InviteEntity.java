package fr.esipe.way2go.dao;

import fr.esipe.way2go.dao.converter.CalendarConverter;
import javax.persistence.*;
import java.sql.Date;
import java.util.Calendar;

@Entity
@Table(name = "invite", schema = "public", catalog = "goraphe")
public class InviteEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "invite_id")
    private Long inviteId;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private UserEntity user;

    @Column(name = "target_email")
    private String targetEmail;

    @Column(name = "status")
    private String status;

    @Column(name = "first_mail_sent")
    @Convert(converter = CalendarConverter.class)
    private Calendar firstMailSent;

    @Column(name = "last_mail_sent")
    private Date lastMailSent;

    @Column(name = "mail_count", nullable = false)
    private Integer mailCount = 0;

    @Column(name = "token", nullable = false)
    private String token;

    public InviteEntity() {
    }

    public InviteEntity(UserEntity userEntity, String token) {
        this.user = userEntity;
        this.token = token;
        firstMailSent = Calendar.getInstance();
        this.mailCount += 1;
    }

    /**
     * Returns this invite's ID.
     *
     * @return inviteId This invite's ID. (Long)
     */
    public Long getInviteId() {
        return inviteId;
    }

    /**
     * Sets a new ID for this invite.
     *
     * @param inviteId This invite's new ID. (Long)
     */
    public void setInviteId(Long inviteId) {
        this.inviteId = inviteId;
    }

    /**
     * Returns this invite's receiver user.
     *
     * @return user This invite's receiver user. (UserEntity)
     */
    public UserEntity getUser() {
        return user;
    }

    /**
     * Sets a new ID for this invite's receiver user.
     *
     * @param user This new invite's receiver user. (UserEntity)
     */
    public void setUser(UserEntity user) {
        this.user = user;
    }

    /**
     * Returns this invite's targeted email.
     *
     * @return inviteId This invite's targeted email. (String)
     */
    public String getTargetEmail() {
        return targetEmail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Sets a new targeted email for this invite.
     *
     * @param targetEmail This invite's new targeted email. (String)
     */
    public void setTargetEmail(String targetEmail) {
        this.targetEmail = targetEmail;
    }

    /**
     * Returns this invite's status.
     *
     * @return inviteId This invite's status. (String)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets a new status for this invite.
     *
     * @param status This invite's new status. (String)
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns this invite's first mail sent date.
     *
     * @return firstMailSent This invite's first mail sent date. (Date)
     */
    public Calendar getFirstMailSent() {
        return firstMailSent;
    }

    /**
     * Sets a new date for this invite's first mail sent.
     *
     * @param firstMailSent The new date for this invite's first mail sent. (Date)
     */
    public void setFirstMailSent(Calendar firstMailSent) {
        this.firstMailSent = firstMailSent;
    }

    /**
     * Returns this invite's last mail sent date.
     *
     * @return lastMailSent This invite's last mail sent date. (Date)
     */
    public Date getLastMailSent() {
        return lastMailSent;
    }

    /**
     * Sets a new date for this invite's last mail sent.
     *
     * @param lastMailSent The new date for this invite's last mail sent. (Date)
     */
    public void setLastMailSent(Date lastMailSent) {
        this.lastMailSent = lastMailSent;
    }

    /**
     * Returns this invite's mail count sent.
     *
     * @return mailCount This invite's mail count sent. (Integer)
     */
    public Integer getMailCount() {
        return mailCount;
    }

    /**
     * Sets a new mail count sent for this invite.
     *
     * @param mailCount This invite's new mail count sent. (Integer)
     */
    public void setMailCount(Integer mailCount) {
        this.mailCount = mailCount;
    }
}
