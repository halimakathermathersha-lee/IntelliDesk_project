package com.intellidesk.dto;
import jakarta.validation.constraints.NotBlank;
public class UpdateRequest {

 @NotBlank
 private String message;
 private String resolution;


 public String getMessage(){return message;}
 public void setMessage(String v){message=v;}
 public String getResolution(){return resolution;}
 public void setResolution(String v){resolution=v;}
}
