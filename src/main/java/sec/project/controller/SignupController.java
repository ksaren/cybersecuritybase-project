package sec.project.controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.config.CustomDatabaseService;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

@Controller
public class SignupController {

    @Autowired
    private SignupRepository signupRepository;

    @Autowired
    private CustomDatabaseService cds;

    @Autowired
    private PasswordEncoder pwe;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm(Model model, @RequestParam(required = false) String content) {
        return "form";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String loadAdmin(Model model, Authentication auth, @RequestParam(required = false) String content) {
        return "admin";
    }

    @RequestMapping(value = "/welcome", method = RequestMethod.POST)
    public String submitForm(HttpServletRequest request, @RequestParam String name, @RequestParam String password, @RequestParam String address, @RequestParam String city, @RequestParam(required = false) String card) throws SQLException {

        PreparedStatement pst = cds.getConnection().prepareStatement("INSERT INTO customers (customerName,addressLine1,city,creditCard) VALUES (?,?,?,?);");
        pst.setString(1, name);
        pst.setString(2, address);
        pst.setString(3, city);
        if (request.getParameterMap().containsKey("card")) {
            pst.setString(4, card);
            signupRepository.save(new Signup(name, pwe.encode(password), address, city, card));
        } else {
            pst.setString(4, null);
            signupRepository.save(new Signup(name, pwe.encode(password), address, city));
        }
        pst.executeUpdate();
        return "welcome";
    }

    @RequestMapping(value = "/cities", method = RequestMethod.POST)
    public String searchByCity(Model model, @RequestParam String city) throws SQLException {

        //Here we have a reason for flaw A1 Injection. Prepared statement should be used with parameters.
        String statement = "SELECT * FROM customers WHERE city='" + city + "';";
        PreparedStatement pst = cds.getConnection().prepareStatement(statement);

        //Safe way to do:
        // PreparedStatement pst = cds.getConnection().prepareStatement("SELECT * FROM customers WHERE city=?");
        // pst.setString(1, city);     
        ResultSet inCity = pst.executeQuery();
        ArrayList<String> cust = new ArrayList();

        while (inCity.next()) {
            cust.add(inCity.getString("customerName"));
        }

        model.addAttribute("cities", cust);
        return "form";

    }

    @RequestMapping(value = "/signups", method = RequestMethod.GET)
    public String showSignUps(Model model) {
        model.addAttribute("signups", signupRepository.findAll());
        return "admin";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/form";
    }
}
