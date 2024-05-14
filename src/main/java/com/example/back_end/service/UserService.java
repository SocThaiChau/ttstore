package com.example.back_end.service;

import com.example.back_end.config.ConvertToDate;
import com.example.back_end.model.entity.EmailDetails;
import com.example.back_end.model.entity.User;
import com.example.back_end.model.mapper.UserMapper;
import com.example.back_end.model.request.UserRequest;
import com.example.back_end.repository.EmailService;
import com.example.back_end.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {
   @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailService emailService;

    public List<User> findAll() {
        try {
            List<User> users = userRepository.findAll();
            return new ArrayList<>(users);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public String createUser(UserRequest userRequest) {
        try {
            User oldUser = userRepository.findByEmail(userRequest.getEmail()).orElse(null);
            if(oldUser!=null)
            {
                return "Email đã tồn tại";
            }
            User user = new User();
            user = userMapper.toEntity(userRequest);

            LocalDateTime localDateTime  = LocalDateTime.now();
            Date createdDate = ConvertToDate.convertToDateViaSqlTimestamp(localDateTime);
            user.setCreateDate(createdDate);
            String password = RandomStringUtils.randomAlphanumeric(8);
            user.setPassword(password);
            user.setCreatedBy(userRepository.findById(userRequest.getCreatedByUserId()).orElse(null));
            userRepository.save(user);

            EmailDetails emailDetails = new EmailDetails();
            emailDetails.setSubject("Thông báo: Tài khoản của bạn đã được tạo thành công");
            emailDetails.setRecipient(user.getEmail());
            emailDetails.setMsgBody("Chào "+userRequest.getName() +
                    ",\n Chúng tôi rất vui thông báo rằng tài khoản của bạn đã được tạo thành công. Dưới đây là thông tin tài khoản của bạn:\n"
                    + "\n Tên đăng nhập: "+userRequest.getEmail()
                    + "\n Mật khẩu: "+password
                    + "\nTrân trọng.\n");
            emailService.sendSimpleMail(emailDetails);

            return "Create user Successfully...";
        }catch (Exception e)
        {
            return "Error while creating user!!!";
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Email not found"));
    }
}
