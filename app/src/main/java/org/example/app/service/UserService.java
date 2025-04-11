package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.dto.AuthDTO;
import org.example.app.dto.user.UserCreateDTO;
import org.example.app.dto.user.UserDTO;
import org.example.app.dto.user.UserUpdateDTO;
import org.example.app.exception.ResourceNotFoundException;
import org.example.app.mapper.UserMapper;
import org.example.app.model.User;
import org.example.app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDTO> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::map)
                .collect(Collectors.toList());
    }

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        return userMapper.map(user);
    }

    public UserDTO login(AuthDTO authDTO) {
        User user = userRepository.findByEmail(authDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + authDTO.getEmail()));
        return userMapper.map(user);
    }

    public UserDTO create(UserCreateDTO dto) {
        User user = userMapper.map(dto);

        Long id = userRepository.save(user).getId();
        user.setId(id);

        return userMapper.map(user);
    }

    public UserDTO update(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        userMapper.update(dto, user);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        userRepository.delete(user);
    }
}
