package life.qbic.subscriptions.security;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

public class QbicBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

  private static final Logger log = getLogger(QbicBasicAuthenticationEntryPoint.class);

  @Override
  public void commence(final HttpServletRequest request,
      final HttpServletResponse response,
      final AuthenticationException authException) throws IOException {
    //Authentication failed, send error response.
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");

    PrintWriter writer = response.getWriter();
    log.error(authException.getMessage());
    writer.println("HTTP Status 401 : Full authentication required.");
  }

  @Override
  public void afterPropertiesSet() {
    setRealmName("QBIC");
    super.afterPropertiesSet();
  }

}
