package life.qbic.subscriptions.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private static final String REALM="QBIC";

  @Autowired
  public void configureGlobalSecurity(
      AuthenticationManagerBuilder auth,
      @Value("${service.officer.name}") String officerName,
      @Value("${service.officer.password}") String officerPassword) throws Exception {
    auth.inMemoryAuthentication()
        .withUser(officerName).password("{noop}"+officerPassword).roles("OFFICER");

  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers(HttpMethod.GET, "/subscription/cancel").hasRole("OFFICER")
        .and().httpBasic().realmName(REALM).authenticationEntryPoint(getBasicAuthEntryPoint())
        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//We don't need sessions to be created.
  }

  @Bean
  public QbicBasicAuthenticationEntryPoint getBasicAuthEntryPoint(){
    return new QbicBasicAuthenticationEntryPoint();
  }

  /* To allow Pre-flight [OPTIONS] request from browser */
  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
  }


}
