package mk.finki.ukim.lost_and_found.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;

public class AuthConfig {

    public HttpSecurity authorize(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        .anyRequest().authenticated()
                )
                .logout(LogoutConfigurer::permitAll);
    }

}
