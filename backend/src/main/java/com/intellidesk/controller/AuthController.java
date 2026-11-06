package com.intellidesk.controller;
import com.intellidesk.dto.AuthRequest;
import com.intellidesk.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/auth")
public class AuthController {

 private final AuthService auth;
 public AuthController(AuthService auth){
  this.auth=auth;}

 @PostMapping("/register")
 public ResponseEntity<?> register(@Valid @RequestBody AuthRequest request)
 {
  try{
   return ResponseEntity.ok(auth.register(request));
  }
  catch(IllegalArgumentException e){
   return ResponseEntity.badRequest()
           .body(Map.of("message",e.getMessage()));
  }}

 @PostMapping("/login")
 public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request){
  try{
   return ResponseEntity.ok(auth.login(request));
  }
  catch(IllegalArgumentException e){
   return ResponseEntity.status(401)
           .body(Map.of("message",e.getMessage()));
  }}
}
