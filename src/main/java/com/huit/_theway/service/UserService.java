package com.huit._theway.service;

import com.huit._theway.model.User;
import com.huit._theway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý logic cho người dùng
 * Mỗi thay đổi đều phải được chú thích bằng tiếng Việt
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(User user) {
        // Mã hóa mật khẩu trước khi lưu vào DB
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Gán vai trò mặc định là ROLE_USER nếu chưa có
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Collections.singleton("ROLE_USER"));
        }
        return userRepository.save(user);
    }

    public org.springframework.data.domain.Page<User> searchAndPaginate(String keyword, int page, int size) {
        List<User> all = userRepository.findAll();
        String lowerKeyword = keyword != null ? keyword.toLowerCase() : "";
        List<User> filtered = all.stream()
                .filter(u -> u.getId().toString().contains(lowerKeyword) || 
                             (u.getUsername() != null && u.getUsername().toLowerCase().contains(lowerKeyword)) ||
                             (u.getFullName() != null && u.getFullName().toLowerCase().contains(lowerKeyword)) ||
                             (u.getEmail() != null && u.getEmail().toLowerCase().contains(lowerKeyword)))
                .collect(java.util.stream.Collectors.toList());

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<User> pageContent = (start <= end && start < filtered.size()) ? filtered.subList(start, end) : new java.util.ArrayList<>();

        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, filtered.size());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setEnabled(!user.isEnabled());
            userRepository.save(user);
        }
    }

    @Transactional
    public void updateUserProfile(String username, String fullName, String phoneNumber, String address) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setFullName(fullName);
            user.setPhoneNumber(phoneNumber);
            user.setAddress(address);
            userRepository.save(user);
        }
    }

    @Transactional
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null && passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }
}
