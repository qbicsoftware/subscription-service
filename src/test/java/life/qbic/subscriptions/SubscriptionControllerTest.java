package life.qbic.subscriptions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import life.qbic.subscriptions.encryption.RequestDecrypter;
import life.qbic.subscriptions.encryption.RequestEncrypter;
import life.qbic.subscriptions.subscriptions.CancellationRequest;
import life.qbic.subscriptions.subscriptions.SubscriptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
@WebMvcTest(controllers = SubscriptionController.class)
class SubscriptionControllerTest {

  @MockBean
  SubscriptionRepository subscriptionRepository;
  @MockBean
  RequestDecrypter requestDecrypter;
  @MockBean
  RequestEncrypter requestEncrypter;

  @Autowired
  MockMvc mockMvc;

  @Test
  @DisplayName("get cancellation request for correct input")
  void getCancellationRequestForCorrectInput() throws Exception {
    var payload = new CancellationRequest("QABCD", "test@user.id");
    var encrypted = "BStOJDfmn0ZyNceOPN3qU2xJw1mQfdbzY_a-uGt7Ae0=";
    Mockito.when(requestEncrypter.encryptCancellationRequest(payload)).thenReturn(encrypted);

    String json = String.format("{\"project\":\"%s\", \"userId\":\"%s\"}", payload.project(), payload.userId());

    mockMvc.perform(
        get("/subscription/cancel")
            .with(httpBasic("ChuckNorris","astrongpassphrase!"))
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding(StandardCharsets.UTF_8)
            .content(json)
        )
        .andDo(print())
        .andExpect(status().is(200))
        .andExpect(content().string(encrypted));
  }
}