package io.mateu.common.model.authentication;

import com.Ostermiller.util.MD5;
import com.Ostermiller.util.RandPass;
import com.google.common.base.Strings;
import io.mateu.common.model.common.File;
import io.mateu.common.model.util.EmailHelper;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import io.mateu.ui.mdd.server.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for users of our common. It can be an internal user or a user created for a customer or a supplier
 *
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Table(name = "_USER")
@Getter@Setter
public class User {

    @Tab("Info")
    @Embedded
    @Output
    private Audit audit = new Audit();

    /**
     * login must always be lowercase. It is the primary key.
     */
    @Id
    @ListColumn("Login")
    @NotNull
    private String login;

    public void setLogin(String login) {
        this.login = (login != null)?login.toLowerCase().trim():null;
    }

    @ListColumn("Name")
    @NotNull
    private String name;

    @ListColumn("Email")
    @NotNull
    private String email;

    @Ignored
    private String password = "1";

    public void setPassword(String password) {
        this.password = Helper.md5(password.toLowerCase().trim());
    }

    @ListColumn("Status")
    @NotNull
    private USER_STATUS status;

    private LocalDate expiryDate;

    @Output
    private LocalDateTime lastLogin;

    @Output
    private int failedLogins;

    @NotInList
    @ManyToOne
    private File photo;

    @TextArea
    private String comments;

    @Tab("Permissions")
    @OneToMany
    private List<Permission> permissions = new ArrayList<Permission>();


    @PrePersist
    public void resetPassword() {
        String password = new RandPass().getPass(6);
        //setPassword(MD5.getHashString(password));
        setPassword(Helper.md5(password.toLowerCase().trim()));
    }

    public void sendForgottenPasswordEmail() throws Throwable {
        if (Strings.isNullOrEmpty(getPassword())) throw new Exception("Missing password for user " + login);
        if (Strings.isNullOrEmpty(getEmail())) throw new Exception("Missing email for user " + login);
        if (USER_STATUS.INACTIVE.equals(getStatus())) throw new Exception("Deactivated user");
        EmailHelper.sendEmail(getEmail(), "Your password", getPassword(), true);
    }

    public boolean validatePassword(String text) {
        //return getPassword().equals(MD5.getHashString(text)) || getPassword().equals(text);
        return getPassword().equals(Helper.md5(text.toLowerCase().trim()));
    }

    @PostPersist
    public void post() {
        WorkflowEngine.add(new Runnable() {

            String xid = getLogin();

            @Override
            public void run() {

                try {
                    Helper.transact(new JPATransaction() {
                        @Override
                        public void run(EntityManager em) throws Throwable {
                            em.find(User.class, xid).sendWelcomeEmail();
                        }
                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            }
        });
    }

    public void sendWelcomeEmail() throws Throwable {
        if (Strings.isNullOrEmpty(getPassword())) throw new Exception("Missing password for user " + login);
        if (Strings.isNullOrEmpty(getEmail())) throw new Exception("Missing email for user " + login);
        if (USER_STATUS.INACTIVE.equals(getStatus())) throw new Exception("Deactivated user");
        EmailHelper.sendEmail(getEmail(), "Welcome " + getName(), "Your password is " + getPassword(), true);
    }




}
