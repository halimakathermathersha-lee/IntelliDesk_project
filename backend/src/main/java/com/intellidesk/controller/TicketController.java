package com.intellidesk.controller;
import com.intellidesk.dto.UpdateRequest;
import com.intellidesk.entity.AppUser;
import com.intellidesk.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController @RequestMapping("/api/tickets")
public class TicketController {
 private final TicketService service;
 public TicketController(TicketService service){this.service=service;}
 private AppUser me(Authentication a){return service.current(a.getName());}
 @GetMapping public ResponseEntity<?> list(Authentication a){return ResponseEntity.ok(service.list(me(a)));}
 @GetMapping("/{id}") public ResponseEntity<?> detail(@PathVariable Long id,Authentication a){return safe(()->service.detail(id,me(a)));}
 @PostMapping public ResponseEntity<?> create(@RequestParam String description,@RequestParam(required=false) MultipartFile attachment,Authentication a){
  try{return ResponseEntity.ok(service.create(description,attachment==null?null:attachment.getOriginalFilename(),attachment==null?null:attachment.getContentType(),attachment==null?0:attachment.getSize(),me(a)));}
  catch(Exception e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()==null?"Could not create ticket":e.getMessage()));}
 }
 @PostMapping("/{id}/updates") public ResponseEntity<?> update(@PathVariable Long id,@Valid @RequestBody UpdateRequest r,Authentication a){return safe(()->service.addUpdate(id,r.getMessage(),me(a)));}
 @PostMapping("/{id}/resolve") public ResponseEntity<?> resolve(@PathVariable Long id,@RequestBody UpdateRequest r,Authentication a){return safe(()->service.resolve(id,r.getResolution(),me(a)));}
 @PostMapping("/{id}/confirm") public ResponseEntity<?> confirm(@PathVariable Long id,Authentication a){return safe(()->service.confirm(id,me(a)));}
 @PostMapping("/{id}/reopen") public ResponseEntity<?> reopen(@PathVariable Long id,Authentication a){return safe(()->service.reopen(id,me(a)));}
 @PostMapping("/{id}/escalate") public ResponseEntity<?> escalate(@PathVariable Long id,Authentication a){return safe(()->service.escalate(id,me(a)));}

 @PostMapping("/{id}/assign")
 public ResponseEntity<?> assignWorker(
         @PathVariable Long id,
         @RequestBody Map<String, Long> body,
         Authentication a) {

  return safe(() ->
          service.assignWorker(
                  id,
                  body.get("workerId"),
                  me(a)
          )
  );
 }
 private ResponseEntity<?> safe(java.util.function.Supplier<Object> action){try{return ResponseEntity.ok(action.get());}catch(org.springframework.security.access.AccessDeniedException e){return ResponseEntity.status(403).body(Map.of("message",e.getMessage()));}catch(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()));}}
}
