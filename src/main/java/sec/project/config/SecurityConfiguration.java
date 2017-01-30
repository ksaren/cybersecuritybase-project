package sec.project.config;

import org.h2.server.web.WebServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
        //Disabled CSRF to allow Cross Site Request Forgery 
        //Flaw A8 from OWASP Top Ten list
        http.csrf().disable();

        http.headers().frameOptions().sameOrigin();
        http.authorizeRequests()
                .antMatchers("/h2-console/*").permitAll()
                // Misconfigured security settings, flaw A5. Should be added ".anyRequest().authenticated" 
                //to ensure there is no sites accidentally  available without authentication. Also,
                .antMatchers("/form").permitAll()
                .antMatchers("/admin").authenticated()
                //instead of above, should be ".antMatchers("/admin").hasAnyAuthority("ADMIN");"
                .anyRequest().permitAll();
                
                          
        http.formLogin().permitAll();
        http.logout().permitAll();
    
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
               //another misconfiguration; very easily guessable default admin in production version
        auth.inMemoryAuthentication()
                .withUser("admin").password("1234").roles("ADMIN");
        
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    
    }

    //I've replaced the recommended BCryptEncoder by SHA-256 based StandardPasswordEncoder,
    //which is not recommended for any new software. 
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder();
    }
    
    @Bean
    ServletRegistrationBean h2servletRegistration() {
        ServletRegistrationBean srb = new ServletRegistrationBean(new WebServlet());
        srb.addUrlMappings("/console/*");
        return srb;
    }
}

