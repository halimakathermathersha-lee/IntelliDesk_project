package com.intellidesk.ai;
import org.springframework.stereotype.Service;
import java.util.Locale;

@Service
public class ClassificationService {

 public record Prediction(String title,String category,String priority,String severity,String requiredRole,String requiredSkills,String provider){}


 // Honest fallback: keyword rules, not a real AI model. Provider is kept separate for later integration.
 public Prediction classify(String description){
  String d=description.toLowerCase(Locale.ROOT);

  String category="GENERAL", title="General support request", role="General Support", skills="troubleshooting";

  if(containsAny(d,"payroll","salary","payment","invoice")){
   category="APPLICATION";title="Payroll or payment application issue";
   role="Application Support";skills="payroll,application support,sql";
  }
  else if(containsAny(d,"laptop","keyboard","monitor","printer","hardware")){
   category="HARDWARE";title="Hardware support required";
   role="IT Hardware Support";
   skills="hardware,device troubleshooting";
  }
  else if(containsAny(d,"password","login","account","access","locked out")){
   category="ACCESS";title="Account or access issue";
   role="Identity and Access Support";
   skills="account access,password reset";
  }
  else if(containsAny(d,"network","wifi","internet","vpn")){
   category="NETWORK";
   title="Network connectivity issue";
   role="Network Support";
   skills="networking,vpn";
  }
  else if(containsAny(d,"email","outlook","mailbox")){
   category="EMAIL";
   title="Email service issue";
   role="Email Support";
   skills="email administration";
  }

  boolean critical=containsAny(d,"all employees","company-wide","production down","security breach","data breach","critical outage");

  boolean high=containsAny(d,"not working","unavailable","delayed","multiple employees","30 employees","urgent","cannot access");

  String priority=critical?"CRITICAL":high?"HIGH":containsAny(d,"slow","intermittent","sometimes")?"MEDIUM":"LOW";
  String severity=critical?"SEV1":high?"SEV2":priority.equals("MEDIUM")?"SEV3":"SEV4";

  if(critical)
   title="Critical service disruption";
  else if(category.equals("APPLICATION")&&high)
   title="Business application unavailable";
  return new Prediction(title,category,priority,severity,role,skills,"RULE_BASED_FALLBACK");
 }
 private boolean containsAny(String text,String... words){
  for(String word:words)
   if(text.contains(word))
    return true;return false;
 }
}
