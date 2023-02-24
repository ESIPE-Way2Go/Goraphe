package fr.esipe.way2go.dto.invite.response;

import java.util.Calendar;

public class InvitationResponse {
    private Long id;
    private String email;
    private Calendar sendDate;
    private int mailSendCount;

    public InvitationResponse(Long id, String email, Calendar sendDate, int mailSendCount) {
        this.id = id;
        this.email = email;
        this.sendDate = sendDate;
        this.mailSendCount = mailSendCount;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Calendar getSendDate() {
        return sendDate;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSendDate(Calendar sendDate) {
        this.sendDate = sendDate;
    }


}
