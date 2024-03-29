package com.example.book.service.impl;

import com.example.book.entity.AppUser;
import com.example.book.repository.UserRepository;
import com.example.book.repository.UserRoleRepository;
import com.example.book.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    UserRepository userRepository;


    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Override
    public AppUser findByName(String name) {
        return userRepository.findAppUserByName(name);
    }



    @Override
    public String existsByUserName(String username) throws MessagingException, UnsupportedEncodingException {
        String user = userRepository.existsByUserName(username);
        AppUser appUser = userRepository.findAppUserByName(username);
        if (user != null) {
            sendVerificationEmailForResetPassWord(username, appUser.getEmail());
        }
        return user;
    }

    @Override
    public void saveNewPassword(String password, String name) {
        userRepository.saveNewPassword(password, name);
    }


    public void sendVerificationEmailForResetPassWord(String userName, String email) throws MessagingException, UnsupportedEncodingException {
        String subject = "Hãy xác thực email của bạn";
        String mailContent = "";
        String confirmUrl = "http://localhost:4200/verify-reset-password/" + userName;


        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setFrom("sangnguyenjp97@gmail.com","CODE GYM");
        helper.setTo(email);
        helper.setSubject(subject);
        mailContent = "<p sytle='color:red;'>Xin chào " + userName + " ,<p>" + "<p> Nhấn vào link sau để xác thực email của bạn:</p>" +
                "<h3><a href='" + confirmUrl + "'>Link Xác thực( nhấn vào đây)!</a></h3>" +
                "<p>CODE GYM</p>";
        helper.setText(mailContent, true);
        javaMailSender.send(message);
    }


    @Override
    public List<AppUser> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void save(AppUser appUser) {
        userRepository.save(appUser.getUsername(), appUser.getPassword(), appUser.getEmail());
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void saveGmail(AppUser appUser) {
        if(userRepository.findAppUserByName(appUser.getUsername()) != null) {
            return;
        }
        appUser.setPassword(passwordEncoder().encode(appUser.getPassword()));
        userRepository.save(appUser);
        userRoleRepository.save(appUser.getId());
    }

    @Override
    public Integer findMaxId() {
        return userRepository.findMaxId();
    }

    @Override
    public Optional<AppUser> findById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public void edit(AppUser appUser) {
        userRepository.edit(appUser.getUsername(), appUser.getPassword(), appUser.getEmail(), appUser.getId());
    }

    @Override
    public void deleteUser(int id) {
        userRepository.deleteUser(id);
    }


    @Override
    public Boolean existsUsername(String username) {
        return username.equals(userRepository.existsUsername(username));
    }


    @Override
    public Boolean existsEmail(String email) {
        return email.equals(userRepository.existsEmail(email));
    }

    @Override
    public String sendEmail(String username) throws MessagingException, UnsupportedEncodingException {
        String user = userRepository.existsByUserName(username);
        AppUser appUser = userRepository.findAppUserByName(username);
        if (user != null) {
            send(username, appUser.getEmail());
        }
        return user;
    }

    public void send(String userName, String email) throws MessagingException, UnsupportedEncodingException {
        String subject = "Hãy xác thực email của bạn";
        String mailContent = "";
        String confirmUrl = "http://localhost:4200/verify-reset-password/" + userName;


        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setFrom("dotronghoang95@gmail.com","CODE GYM");
        helper.setTo(email);
        helper.setSubject(subject);
        mailContent = "<p sytle='color:red;'>Xin chào " + userName + " ,<p>" + "<p> Nhấn vào link sau để xác thực email của bạn:</p>" +
                "<h3><a href='" + confirmUrl + "'>Link Xác thực( nhấn vào đây)!</a></h3>" +
                "<p>CODE GYM</p>";
        helper.setText(mailContent, true);
        javaMailSender.send(message);
    }
}
