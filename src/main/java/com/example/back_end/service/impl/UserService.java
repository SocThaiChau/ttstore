package com.example.back_end.service.impl;

import com.example.back_end.config.ConvertToDate;
import com.example.back_end.exception.UserException;
import com.example.back_end.exception.NotFoundException;
import com.example.back_end.model.entity.EmailDetails;
import com.example.back_end.model.entity.Role;
import com.example.back_end.model.entity.Roles;
import com.example.back_end.model.entity.User;
import com.example.back_end.model.mapper.UserMapper;
import com.example.back_end.model.request.UserRequest;
import com.example.back_end.repository.EmailService;
import com.example.back_end.repository.RoleRepository;
import com.example.back_end.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.persistence.criteria.CriteriaBuilder;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {
   @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

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
            user.setCheckPassword(false);

            Optional<Role> customerRoleOptional = roleRepository.findByRoles(Roles.CUSTOMER);
            if (customerRoleOptional.isPresent()) {
                user.setRole(customerRoleOptional.get());
            } else {
                return "Role CUSTOMER không tồn tại";
            }

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

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }

    public User getByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()-> new NotFoundException("Không tìm email: "+ email));
    }

    public void updateUserPassword(Long id, String password){
        User oldUser = getUserById(id);
        if(oldUser == null){
            throw new NotFoundException("Không tìm thấy người dùng: "+ id);
        }
//        oldUser.setPassword(new BCryptPasswordEncoder().encode(password));
        oldUser.setPassword(password);
        userRepository.save(oldUser);
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(()-> new NotFoundException("Không tìm thấy người dùng: "+ id));
    }

    public User getUserById(Integer id) throws UserException {
        return userRepository.findById(Long.valueOf(id)).orElseThrow(()->new UserException("UserNotFound!"));
    }

    public String checkDuplicatePhone(User user){
        if(userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()){
            return "This phone number is already used by another user!";
        }
        return null;
    }
    public String checkDuplicateEmail(User user){
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            return "This email is already used by another user!";
        }
        return null;
    }
    public boolean isEnabled(Integer id) throws UserException {
        return getUserById(id).isEnabled();
    }
    public  User updateUser(UserRequest user , Integer id) throws ExecutionControl.UserException, UserException {
        User exUser = getUserById(id);

        exUser.setAvatarUrl(user.getAvatarUrl());
        exUser.setName(user.getName());
        exUser.setPhoneNumber(user.getPhoneNumber());

        try{
            userRepository.save(exUser);
        }catch(Exception e){
            return null;
        }
        return exUser;
    }
    public long getcountUser(){
        return userRepository.count();
    }

    public String updatePassword(UserRequest userRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            User oldUser = userRepository.findById(currentUser.getId()).get();
            oldUser.setPassword(userRequest.getPassword());
            oldUser.setCheckPassword(true);
            userRepository.save(oldUser);
            return "Update password Successfully";


        } catch (Exception e) {
            return "Error while updating password!!!";
        }
    }

    public String checkPassword() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            User oldUser = userRepository.findById(currentUser.getId()).get();

            if (oldUser.getCheckPassword().equals(false)) {
                return "";
            } else {
                return "Password Checked";
            }

        } catch (Exception e) {
            return "Error while updating password!!!";
        }
    }
}
