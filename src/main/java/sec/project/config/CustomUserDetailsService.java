package sec.project.config;


import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    
    @Autowired
    private SignupRepository signupRepository;
    
   

    @PostConstruct
    public void init() {
        
        Signup admin = new Signup();
        admin.setName("admin");
        admin.setPassword("admin");
        signupRepository.save(admin);
             
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Signup signup = null;
        for (Signup s:this.signupRepository.findAll()) {
         if (s.getName().equals(name)) {
            signup = s;
            break;
         }
        }
        if (signup==null) {
            throw new UsernameNotFoundException("No such user: " + name);
        }

        return new org.springframework.security.core.userdetails.User(
                signup.getName(),
                signup.getPassword(),
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority("USER")));
    }
    
}
