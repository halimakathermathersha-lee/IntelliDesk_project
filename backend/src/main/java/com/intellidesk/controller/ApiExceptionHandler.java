package com.intellidesk.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(AccessDeniedException.class) public ResponseEntity<?> forbidden(Exception e){return ResponseEntity.status(403).body(Map.of("message","You are not allowed to perform this action."));}
 @ExceptionHandler(Exception.class) public ResponseEntity<?> error(Exception e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()==null?"Unexpected error":e.getMessage()));}
}
