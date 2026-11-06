package com.intellidesk.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public class AuthRequest {
 @Email @NotBlank private String email;
 @NotBlank private String password;
 private String name;
 private String role;
 private String specialization;

 public String getEmail(){
  return email;
 }
 public void setEmail(String v){email=v;}
 public String getPassword(){return password;} public void setPassword(String v){password=v;}
 public String getName(){return name;} public void setName(String v){name=v;}
 public String getRole(){return role;} public void setRole(String v){role=v;}

 public String getSpecialization() {
  return specialization;
 }

 public void setSpecialization(String v) {
  specialization = v;
 }
}
