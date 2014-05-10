package webapplication;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
        protected final void configure(final HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .anyRequest().authenticated();
        http
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .permitAll();
    }

    @Override
    protected final void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser("ruben").password("4227492").roles("USER")
                .and()
                .withUser("mathijs").password("4237676").roles("USER")
                .and()
                .withUser("jasper").password("4212584").roles("USER")
                .and()
                .withUser("robbert").password("1527118").roles("USER")
                .and()
                .withUser("willemjan").password("4141040").roles("USER");
    }
}