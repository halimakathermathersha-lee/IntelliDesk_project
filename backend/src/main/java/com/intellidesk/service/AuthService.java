package com.intellidesk.service;
import com.intellidesk.dto.AuthRequest;
import com.intellidesk.entity.*;
import com.intellidesk.repository.*;
import com.intellidesk.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AuthService {
 private final UserRepository users;
 private final WorkerRepository workers;
 private final PasswordEncoder encoder;
 private final JwtService jwt;

 public AuthService(UserRepository users,WorkerRepository workers,PasswordEncoder encoder,JwtService jwt){
    this.users=users;this.workers=workers;this.encoder=encoder;this.jwt=jwt;}

 @Transactional
 public Map<String,Object> register(AuthRequest r) {
  if (r.getName() == null || r.getName().isBlank())
   throw new IllegalArgumentException("Name is required");
  if (r.getPassword() == null || r.getPassword().length() < 8)
   throw new IllegalArgumentException("Password must contain at least 8 characters");

  if (users.existsByEmail(r.getEmail()))
   throw new IllegalArgumentException("Email already registered");

  // Public registration cannot create admin accounts.
  Role role = "WORKER"
          .equalsIgnoreCase(r.getRole()) ? Role.WORKER
          : Role.USER;
  AppUser user = users.save(new AppUser(
          r.getName().trim(),
          r.getEmail().trim().toLowerCase(),
          encoder.encode(r.getPassword()),
          role
  ));

  if (role == Role.WORKER) {
   Worker worker = new Worker(user);

   String specialization = r.getSpecialization();

   if (specialization == null || specialization.isBlank()) {
    throw new IllegalArgumentException(
            "Please select your specialization"
    );
   }

   if ("Other".equalsIgnoreCase(specialization)) {
    throw new IllegalArgumentException(
            "Please enter your custom specialization"
    );
   }

   worker.setJobRole(specialization.trim());

   workers.save(worker);
  }

  return session(user);
 }

 public Map<String,Object> login(AuthRequest r){
  AppUser user=users.findByEmail(
          r.getEmail().trim().toLowerCase())
          .orElseThrow(()->new IllegalArgumentException("Invalid email or password"));
  if(!encoder.matches(r.getPassword(),user.getPassword()))
   throw new IllegalArgumentException("Invalid email or password");
  return session(user);
 }

 private Map<String,Object> session(AppUser u){
  return Map.of(
          "token",
          jwt.createToken(
                  u.getEmail(),u.getRole().name()),
          "id",u.getId(),
          "name",u.getName(),
          "email",u.getEmail(),
          "role",u.getRole().name());}
}
