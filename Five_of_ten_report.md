Here you can find details of which kind of security flaws this software includes and how I implemented them. There are also suggestions to prevent/fix given flaws.

_Citated_ descriptiond of flaws are from <a href=https://www.owasp.org/index.php/Top_10_2013-Top_10>.


__A1-Injection__

_"Injection flaws, such as SQL, OS, and LDAP injection occur when untrusted data is sent to an interpreter as part of a command or query. The attacker’s hostile data can trick the interpreter into executing unintended commands or accessing data without proper authorization."_

I implemented this flaw by careless input sanitations of SQL input Strings. In the source code an alternative way to make safe queries is in comments. Shortly, never use inlined String parameters, there are safe methods to add parameters to queries like PreparedStatement.setString(index,string).

You can find the flaw two ways:
1. Use flaw detector. At least OWASP ZAP did find this flaw just by running the site on localhost, giving the address and pressing Attack-button.
OR 
2. Make your own attack. I suggest this: Into the field where you can search shops by city, type this:
Pariisi' OR creditLimit>50000;--
You won't get any shops from "Pariisi" listed but instead all the shops with gratest credit limit.

-----------

__A3-Cross-Site Scripting (XSS)__
	
_"XSS flaws occur whenever an application takes untrusted data and sends it to a web browser without proper validation or escaping. XSS allows attackers to execute scripts in the victim’s browser which can hijack user sessions, deface web sites, or redirect the user to malicious sites."_

This flaw was very straightforward to implement. I just switched the type of text fields from text to utext in form.html. Luckily, by reversing this, XSS is not possible anymore.

You can see the flaw by typing <script>alert('Hey, script!');</script> to the name field and press "Join to..."-button.
The messagebox don't become visible straight away. Login as any user and click "LIST SHOPS". Now the script should be run. Also OWASP ZAP finds this flaw.


-----------

__A5-Security Misconfiguration__
	
_"Good security requires having a secure configuration defined and deployed for the application, frameworks, application server, web server, database server, and platform. Secure settings should be defined, implemented, and maintained, as defaults are often insecure. Additionally, software should be kept up to date."_

Well, in this crappy piece of code there is a lot of misconfigurations. But if we forget crashing, stupid redirects etc. for a while, there is some more "sophisticated" security misconfigurations too:

1. Out-dated/not recommended SHA-256-based StandardPasswordEncoder (http://docs.spring.io/spring-security/site/docs/current/apidocs/org/springframework/security/crypto/password/StandardPasswordEncoder.html). To fix this, switch to BCryptPasswordEncoder, which is safer. The encoder is defined in SecurityConfiguration-class.

2. Bad authentication management. Admin-page is clearly meant for admins, but security configuration aloows all authenticated users to get logged in. By adding "...antMatchers("/admin").hasAnyAuthority("ADMIN");" to SecurityConfiguration this problem is solved. There is also no authentication as default. It's a good practise to add  "http.authorizeRequests().anyRequest().authenticated()" to SecurityConfiguration.
I

-----------

__A6-Sensitive Data Exposure__	

_"Many web applications do not properly protect sensitive data, such as credit cards, tax IDs, and authentication credentials. Attackers may steal or modify such weakly protected data to conduct credit card fraud, identity theft, or other crimes. Sensitive data deserves extra protection such as encryption at rest or in transit, as well as special precautions when exchanged with the browser."_

ValentineHappiness-community has collected a nice database of it's members, and there is a lot of information of them. For example, creditcard numbers and credit limits of their membercards are stored without encryption.
This fault should correct by altering the code so that it encodes all sensitive information. The principle of encoding is easy to implement, like it were done with passwords.

-----------

__A8-Cross-Site Request Forgery (CSRF)__
	
_"A CSRF attack forces a logged-on victim’s browser to send a forged HTTP request, including the victim’s session cookie and any other automatically included authentication information, to a vulnerable web application. This allows the attacker to force the victim’s browser to generate requests the vulnerable application thinks are legitimate requests from the victim."_

This flaw was very easy to implement. Spring security enables CSRF shield (random tokens) by default, and I just needed to disable it. To fix flaw, one have to remove the row "http.csrf().disable();" from SecurityConfiguration.

In true life, CSRF is often disabled during development and testing - for example to use H2-console. An issue arises if itäs forgotten to enable before launching the production version.

-----------



