package sec.project.config;

//omat importit
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.h2.tools.RunScript;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private Map<String, String> accountDetails;

    @PostConstruct
    public void init() {
        // Open connection to a database
        String databaseAddress = "jdbc:h2:file:./database";
        Connection connection;
        
        try {
            //connection without user details
            connection = DriverManager.getConnection(databaseAddress);
            
            //connect the MySql sample database to connection instance
            RunScript.execute(connection, new FileReader("sql/mysqlsampledatabase.sql"));
            
            this.accountDetails = new TreeMap<>();
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM customers");
            System.out.println("Customers in database:"); //test
            while (resultSet.next()) {
                String name = resultSet.getString("customerName");
                String address = resultSet.getString("addressLine1");
                accountDetails.put(name, address);
                
                System.out.println(name + "\t" + address);  //test
            }

        } catch (Throwable t) {
            System.err.println(t.getMessage());
        }
        
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        if (!this.accountDetails.containsKey(name)) {
            throw new UsernameNotFoundException("No such user: " + name);
        }

        return new org.springframework.security.core.userdetails.User(
                name,
                this.accountDetails.get(name),
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority("USER")));
    }
}
