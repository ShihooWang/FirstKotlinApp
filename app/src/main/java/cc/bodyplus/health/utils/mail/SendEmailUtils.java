package cc.bodyplus.health.utils.mail;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;


/**
 * 邮件发送
 *
 * @author stoneyang
 */
public class SendEmailUtils {

    public static SendEmailUtils mSendEmailUtils;

    // 发送邮件的服务器的IP和端口
    private String mailServerHost = "s******";

    private String mailServerPort = "**";

    // 邮件发送者的地址
    private String mFromAddress = "******";

    // 邮件接收者的地址

    private String[] mCCAddress = {"shihu.wang@bodyplus.cc"};

    // 登陆邮件发送服务器的用户名和密码
    private String userName = "******";

    private String password = "******";

    private StringBuffer sendtext = new StringBuffer();

    private SendEmailUtils() {

    }

    /**
     * 获取实例
     *
     * @return
     */
    public static SendEmailUtils getInstance() {
        if (mSendEmailUtils == null) {
            mSendEmailUtils = new SendEmailUtils();
        }
        return mSendEmailUtils;
    }

    public class PassAuthenticator extends Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            String username = userName;
            String pwd = password;

            return new PasswordAuthentication(username, pwd);
        }
    }

    /**
     * 设置发送的内容
     *
     * @param text
     */
    public void setSendText(String text) {
        sendtext.append(text);
    }

    public void sendEmail(final String FileName) {

        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    Properties props = new Properties();
                    props.put("mail.smtp.protocol", "smtp");
                    props.put("mail.smtp.auth", "true"); // 设置要验证
                    props.put("mail.smtp.host", mailServerHost); // 设置host
                    props.put("mail.smtp.port", mailServerPort); // 设置端口

                    PassAuthenticator pass = new PassAuthenticator(); // 获取帐号密码
                    Session session = Session.getInstance(props, pass); // 获取验证会话
                    // 配置发送及接收邮箱
                    InternetAddress fromAddress, toAddress, ccAddress[];
                    fromAddress = new InternetAddress(mFromAddress);
//                    toAddress = new InternetAddress(mToAddress);
                    ccAddress = new InternetAddress[mCCAddress.length];
                    for (int i = 0; i < mCCAddress.length; i++) {
                        ccAddress[i] = new InternetAddress(mCCAddress[i]);
                    }

                    // 配置发送信息
                    MimeMessage message = new MimeMessage(session);
                    // message.setContent(sendtext, "");
                    // message.setFileName(FileName);
                    message.setSentDate(new Date());
                    // message.setText(sendtext);

                    message.setSubject("Health Error Log");
                    message.setFrom(fromAddress);
//                    message.addRecipient(javax.mail.Message.RecipientType.TO, toAddress);
                    for (int i = 0; i < ccAddress.length; i++) {
                        message.addRecipient(javax.mail.Message.RecipientType.CC, ccAddress[i]);
                    }

                    // DataHandler handler = new DataHandler(new
                    // ByteArrayDataSource(sendtext.getBytes(), "text/plain"));
                    // message.setDataHandler(handler);

                    FileReader Reader = new FileReader(FileName);
                    BufferedReader br = new BufferedReader(Reader);
                    String text;
                    while ((text = br.readLine()) != null) {
                        sendtext.append(text + "\n");
                    }
                    Reader.close();
                    br.close();
                    // attachPart.setDataHandler(new DataHandler(fds));

                    MimeBodyPart attachPart1 = new MimeBodyPart();
                    DataHandler handler = new DataHandler(new ByteArrayDataSource(sendtext.toString().getBytes(),
                            "text/plain"));
                    attachPart1.setDataHandler(handler);

                    // attachPart.setFileName(fds.getName());
                    MimeMultipart allMultipart = new MimeMultipart("mixed"); // 附件
                    // allMultipart.addBodyPart(attachPart);//添加
                    allMultipart.addBodyPart(attachPart1);

                    message.setContent(allMultipart);
                    message.saveChanges();

                    Transport.send(message);

                } catch (MessagingException e) {
//                    Log.i("Msg", e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                File mFile = new File(FileName);// 发送成功删除文件
                if (mFile.exists()) {
                    mFile.delete();
                }

            }
        }.start();
    }

}
