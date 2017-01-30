/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sec.project.config;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import javax.annotation.PostConstruct;
import org.h2.tools.RunScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

/**
 *
 * @author kaisa
 */
@Service
public class CustomDatabaseService {

    @Autowired
    private SignupRepository signupRepository;
    
    @Autowired
    private PasswordEncoder pwe;
    
    private Connection connection;

    @PostConstruct
    public void init() {
        // Open connection to a database
        String databaseAddress = "jdbc:h2:file:./database";

        try {
            //connection without user details
            this.connection = DriverManager.getConnection(databaseAddress);

            //connect the sql sample database to connection instance
            RunScript.execute(this.connection, new FileReader("sql/mysqlsampledatabase.sql"));

            ResultSet resultSet = this.connection.createStatement().executeQuery("SELECT * FROM customers");

            System.out.println("Customers in database:"); //test
            while (resultSet.next()) {
                String name = resultSet.getString("customerName");
                String address = resultSet.getString("addressLine1");
                String pwd = name.substring(0, 4).toLowerCase()+address.substring(0, 4).toLowerCase();
                String card = resultSet.getString("creditCard");
                String city = resultSet.getString("city");
                if (card==null) {
                     signupRepository.save(new Signup(name, pwe.encode(pwd), address, city));
                } else signupRepository.save(new Signup(name, pwe.encode(pwd), address, city, card));
               
            }

        } catch (Throwable t) {
            System.err.println(t.getMessage());
        }

    }
    
    public Connection getConnection() {
        return this.connection;
    }

}
