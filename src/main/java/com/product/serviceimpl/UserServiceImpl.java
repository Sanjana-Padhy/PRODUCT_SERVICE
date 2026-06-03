package com.product.serviceimpl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.product.dto.AddUserDto;
import com.product.dto.EmailOtpVerifyDto;
import com.product.entity.User;
import com.product.events.SimpleMessageEvent;
import com.product.mapping.ModelMapper;
import com.product.repository.UserRepository;
import com.product.service.MailService;
import com.product.service.UserService;
import com.product.util.EmailMessageBuilderUtil;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private EmailMessageBuilderUtil emailMessageBuilder;

    @Autowired
    private Random random;

    @Autowired
    private MailService mailService;

    @Autowired
    @Qualifier("otpholder")
    private Map<String, Object[]> otpholder;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepo;

    // 🔥 VERY IMPORTANT
    @Autowired
    private PasswordEncoder passwordEncoder;

    // ===============================
    // 🔹 STEP 1: SEND OTP
    // ===============================
    @Override
    public String initiateUserVerificationService(AddUserDto dto) {

        // ✅ Check if already exists
        if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists with this email");
        }

        Integer otp = random.nextInt(100000, 999999);

        String otpMessage =
                emailMessageBuilder.otpMessageBuilder(dto.getName(), otp);

        // 📧 Send email (event)
        SimpleMessageEvent emailEvent = SimpleMessageEvent.builder()
                .receiverEmail(dto.getEmail())
                .message(otpMessage)
                .subject("Products alert : OTP")
                .build();

        eventPublisher.publishEvent(emailEvent);

        // 🧠 Store OTP in memory
        Object[] tempData = {
                otp + "",
                LocalDateTime.now().plusMinutes(2),
                dto
        };

        otpholder.put(dto.getEmail(), tempData);

        return "OTP sent to email: " + dto.getEmail();
    }

    // ===============================
    // 🔹 STEP 2: VERIFY OTP + SAVE USER
    // ===============================
    @Override
    public String finalUserVerificationService(EmailOtpVerifyDto dto) {

        Object[] tempUserData = otpholder.get(dto.getEmail());

        if (tempUserData == null) {
            throw new RuntimeException("Invalid email or OTP expired");
        }

        LocalDateTime expiry = (LocalDateTime) tempUserData[1];

        if (LocalDateTime.now().isAfter(expiry)) {
            otpholder.remove(dto.getEmail());
            throw new RuntimeException("OTP expired");
        }

        String savedOtp = (String) tempUserData[0];

        if (!savedOtp.equals(dto.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        // ✅ Get original user data
        AddUserDto userDto = (AddUserDto) tempUserData[2];

        User user = modelMapper.addUserDtoToUserEntity(userDto);

        // 🔥🔥🔥 MOST IMPORTANT FIX
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // ✅ Default role
        user.setRole("USER");

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepo.save(user);

        // 🧹 remove OTP after success
        otpholder.remove(dto.getEmail());

        // 📧 Send success email
        String message =
                emailMessageBuilder.userRegisterMessageBuilder(userDto.getName());

        SimpleMessageEvent event = SimpleMessageEvent.builder()
                .receiverEmail(dto.getEmail())
                .message(message)
                .subject("User Registration Successful")
                .build();

        eventPublisher.publishEvent(event);

        return "User registered successfully";
    }
}