package com.intellidesk.config;
import com.intellidesk.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

 @Configuration
   public class SecurityConfig {

       private final JwtFilter jwtFilter;
            public SecurityConfig(JwtFilter jwtFilter){
             this.jwtFilter=jwtFilter;
            }


       @Bean
       PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
       }

       @Bean
       SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
            http.csrf(csrf->csrf.disable())
                  .cors(Customizer.withDefaults())
                  .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(a -> a
                            .requestMatchers("/api/auth/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                            .anyRequest().authenticated()
                    )
                  .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
       }

     @Bean
     CorsConfigurationSource corsConfigurationSource() {

         CorsConfiguration c = new CorsConfiguration();

         c.setAllowedOrigins(List.of(
                 "http://localhost:5173",
                 "https://intelli-desk-project.vercel.app"
         ));

         c.setAllowedMethods(List.of(
                 "GET", "POST", "PUT", "PATCH",
                 "DELETE", "OPTIONS"
         ));

         c.setAllowedHeaders(List.of(
                 "Authorization",
                 "Content-Type"
         ));

         c.setAllowCredentials(true);

         UrlBasedCorsConfigurationSource source =
                 new UrlBasedCorsConfigurationSource();

         source.registerCorsConfiguration("/**", c);

         return source;
     }

 }
