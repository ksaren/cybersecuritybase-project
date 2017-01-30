package sec.project.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Signup extends AbstractPersistable<Long> {

    @Column(unique = true)
    private String name;
    private String address;
    private String city = null;
    private String card;
    private String password;
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    

    public Signup() {
        super();
    }

    public Signup(String name, String pwd, String address, String city) {
        this();
        this.name = name;
        this.password = pwd;
        this.address = address;
        this.city = city;
        this.card = "NOT SET";
    }
    
    public Signup(String name, String pwd, String address, String city, String cc) {
        this();
        this.name = name;
        this.password = pwd;
        this.address = address;
        this.city = city;
        this.card = cc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    @Override
    public String toString() {
        return name + "\t" + address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPassword() {
         return this.password;
    }
    
       public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
           public String getCard() {
        return card;
    }

    public void setCard(String cc) {
        this.card = cc;
    }
}
