package io.mateu.erp.server;

import com.google.common.base.Strings;
import com.rabbitmq.jms.admin.RMQConnectionFactory;
import com.rabbitmq.jms.admin.RMQDestination;
import io.mateu.common.model.authentication.Permission;
import io.mateu.common.model.authentication.USER_STATUS;
import io.mateu.common.model.authentication.User;
import io.mateu.common.model.config.AppConfig;
import io.mateu.erp.model.monitoring.Monitor;
import io.mateu.erp.model.population.Populator;
import io.mateu.common.model.util.EmailHelper;
import io.mateu.ui.core.server.BaseServerSideApp;
import io.mateu.ui.core.server.SQLTransaction;
import io.mateu.ui.core.server.ServerSideApp;
import io.mateu.ui.core.server.Utils;
import io.mateu.ui.core.shared.FileLocator;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by miguel on 3/1/17.
 */
public class ERPAtServerSide extends BaseServerSideApp implements ServerSideApp {

    static {
        if ("yes".equalsIgnoreCase(System.getProperty("monitor"))) {
            System.out.println("Starting monitor...");
            Monitor.monitor();
        } else {
            System.out.println("Monitor not needed.");
        }
    }


    private static long fileId;

    @Override
    public DataSource getJdbcDataSource()throws Throwable {
        return null;
    }

    @Override
    public Object[][] select(String sql)throws Throwable {
        return Helper.select(sql);
    }

    @Override
    public void execute(String sql)throws Throwable {
        Helper.execute(sql);
    }

    @Override
    public Object selectSingleValue(String sql)throws Throwable {
        return Helper.selectSingleValue(sql);
    }

    @Override
    public void update(String sql)throws Throwable {
        Helper.update(sql);
    }

    @Override
    public int getNumberOfRows(String sql) {
       return Helper.getNumberOfRows(sql);
    }

    @Override
    public Object[][] selectPage(String sql, int desdeFila, int numeroFilas)throws Throwable {
        return Helper.selectPage(sql, desdeFila, numeroFilas);
    }

    @Override
    public void transact(SQLTransaction t)throws Throwable {

        Helper.transact(t);

    }

    @Override
    public void notransact(SQLTransaction t)throws Throwable {
        Helper.notransact(t);
    }

    @Override
    public FileLocator upload(String fileName, byte[] bytes, boolean temporary)throws Throwable {

        long id = fileId++;
        String extension = ".tmp";
        if (fileName == null || "".equals(fileName.trim())) fileName = "" + id;
        if (fileName.lastIndexOf(".") < fileName.length() - 1) {
            extension = fileName.substring(fileName.lastIndexOf("."));
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }

        java.io.File temp = (System.getProperty("tmpdir") == null)? java.io.File.createTempFile(fileName, extension):new java.io.File(new java.io.File(System.getProperty("tmpdir")), fileName + extension);

        System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
        System.out.println("Temp file : " + temp.getAbsolutePath());

        if (true || !temp.exists()) {
            System.out.println("writing temp file to " + temp.getAbsolutePath());
            Utils.write(temp, bytes);
        } else {
            System.out.println("temp file already exists");
        }

        String baseUrl = System.getProperty("tmpurl");
        URL url = null;
        if (baseUrl == null) {
            url = temp.toURI().toURL();
        } else url = new URL(baseUrl + "/" + temp.getName());


        return new FileLocator(id, temp.getName(), url.toString(), temp.getAbsolutePath());
    }

    private static ConnectionFactory jmsConnectionFactory() {
        RMQConnectionFactory connectionFactory = new RMQConnectionFactory();
        connectionFactory.setUsername("tester");
        connectionFactory.setPassword("tester8912");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setHost("quon.mateu.io");
        return connectionFactory;
    }

    private static Destination jmsDestination() {
        RMQDestination jmsDestination = new RMQDestination();
        jmsDestination.setDestinationName("quo1");
        jmsDestination.setAmqpExchangeName("jms.durable.topic");
        jmsDestination.setAmqp(true);
        //jmsDestination.setAmqpQueueName("quo2");
        return jmsDestination;
    }

    @Override
    public UserData authenticate(String login, String password)throws Throwable {
        if (login == null || "".equals(login.trim()) || password == null || "".equals(password.trim())) throw new Exception("Username and password are required");


        UserData d = new UserData();
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em)throws Throwable {

                if (em.createQuery("select x.login from " + User.class.getName() + " x").getResultList().size() == 0) {
                    Populator.populate(AppConfig.class);
                }


                User u = em.find(User.class, login.toLowerCase().trim());
                if (u != null) {

                    if (u.getExpiryDate() != null && u.getExpiryDate().isBefore(LocalDate.now())) {
                        u.setStatus(USER_STATUS.EXPIRED);
                        em.getTransaction().commit();
                        em.getTransaction().begin();
                    }

                    if (USER_STATUS.INACTIVE.equals(u.getStatus())) throw new Exception("Deactivated user");
                    if (USER_STATUS.BLOCKED.equals(u.getStatus())) throw new Exception("Blocked user");
                    if (USER_STATUS.EXPIRED.equals(u.getStatus())) throw new Exception("Expired user");

                    if (u.getPassword() == null) throw new Exception("Missing password for user " + login);
                    if (!password.trim().equalsIgnoreCase(u.getPassword().trim())) {
                        u.setFailedLogins(u.getFailedLogins() + 1);
                        if (u.getFailedLogins() > 10) u.setStatus(USER_STATUS.BLOCKED);
                        em.getTransaction().commit();
                        throw new Exception("Wrong password");
                    }

                    d.setName(u.getName());
                    d.setEmail(u.getEmail());
                    d.setLogin(login);
                    u.setLastLogin(LocalDateTime.now());
                    u.setFailedLogins(0);
                    if (u instanceof io.mateu.erp.model.authentication.User) {
                        io.mateu.erp.model.authentication.User u2 = (io.mateu.erp.model.authentication.User) u;
                        if (u2.getActor() != null) d.set("agencyId", u2.getActor().getId());
                        if (u2.getOffice() != null) d.set("officeId", u2.getOffice().getId());
                    }
                    if (u.getPhoto() != null) d.setPhoto(u.getPhoto().toFileLocator().getUrl());
                    for (Permission p : u.getPermissions()) d.getPermissions().add(Math.toIntExact(p.getId()));
                } else throw new Exception("No user with login " + login);
            }
        });

        System.out.println("userdata=" + d);
        return d;
    }

    @Override
    public void forgotPassword(String login) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em)throws Throwable {

                if (em.createQuery("select x.login from " + User.class.getName() + " x").getResultList().size() == 0) {
                    Populator.populate(AppConfig.class);
                }


                User u = em.find(User.class, login.toLowerCase().trim());
                if (u != null) {
                    if (Strings.isNullOrEmpty(u.getPassword())) throw new Exception("Missing password for user " + login);
                    if (Strings.isNullOrEmpty(u.getEmail())) throw new Exception("Missing email for user " + login);
                    if (USER_STATUS.INACTIVE.equals(u.getStatus())) throw new Exception("Deactivated user");
                    EmailHelper.sendEmail(u.getEmail(), "Your password", u.getPassword(), true);
                } else throw new Exception("No user with login " + login);
            }
        });
    }

    @Override
    public void changePassword(String login, String oldPassword, String newPassword)throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em)throws Throwable {
                User u = em.find(User.class, login.toLowerCase().trim());
                if (u != null) {
                    if (!oldPassword.trim().equalsIgnoreCase(u.getPassword().trim())) throw new Exception("Wrong old password");
                    u.setPassword(newPassword);
                } else throw new Exception("No user with login " + login);
            }
        });
    }

    @Override
    public void updateProfile(String login, String name, String email, FileLocator foto)throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em)throws Throwable {
                User u = em.find(User.class, login.toLowerCase().trim());
                if (u != null) {
                    u.setName(name);
                    u.setEmail(email);
                } else throw new Exception("No user with login " + login);
            }
        });
    }

    @Override
    public UserData signUp(String s, String s1, String s2, String s3) throws Throwable {
        return null;
    }

    @Override
    public String recoverPassword(String s) throws Throwable {
        return null;
    }

    @Override
    public String getXslfoForListing()throws Throwable {
        String[] s = {""};
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em)throws Throwable {
                s[0] = AppConfig.get(em).getXslfoForList();
            }
        });
        return s[0];
    }

    @Override
    public void updateFoto(String login, FileLocator fileLocator)throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em)throws Throwable {
                User u = em.find(User.class, login.toLowerCase().trim());
                if (u != null) {
                    io.mateu.common.model.common.File p = u.getPhoto();
                    if (p == null) {
                        u.setPhoto(p = new io.mateu.common.model.common.File());
                        em.persist(p);
                    }
                    p.setName(fileLocator.getFileName());
                    p.setPath("");
                    p.setBytes(Utils.readBytes(fileLocator.getTmpPath()));
                } else throw new Exception("No user with login " + login);
            }
        });
    }
}
