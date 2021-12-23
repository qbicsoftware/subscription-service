package life.qbic.subscriptions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import life.qbic.subscriptions.encryption.DecryptionException;
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
 * <p>Tests the behaviour of the {@code /subscription} endpoints</p>
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
  @DisplayName("encryptCancellationRequest works")
  void encryptCancellationRequestWorks() throws Exception {
    var payload = new CancellationRequest("QABCD", "test@user.id");
    var encrypted = "BStOJDfmn0ZyNceOPN3qU2xJw1mQfdbzY_a-uGt7Ae0=";
    Mockito.when(requestEncrypter.encryptCancellationRequest(payload)).thenReturn(encrypted);

    String json = String.format("{\"project\":\"%s\",\"userId\":\"%s\"}", payload.project(), payload.userId());

    mockMvc.perform(
        get("/subscription/cancel")
            .with(httpBasic("ChuckNorris","astrongpassphrase!"))
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding(StandardCharsets.UTF_8)
            .content(json)
        )
        .andExpect(status().is(200))
        .andExpect(content().string(encrypted));
  }

  @Test
  @DisplayName("encryptCancellationRequest does not work for incorrect input")
  void encryptCancellationRequestDoesNotWorkForIncorrectInput() throws Exception {
    var payload = new CancellationRequest("QABCD", "test@user.id");
    var encrypted = "BStOJDfmn0ZyNceOPN3qU2xJw1mQfdbzY_a-uGt7Ae0=";
    Mockito.when(requestEncrypter.encryptCancellationRequest(payload)).thenReturn(encrypted);

    String invalidUserId = String.format("{\"project\":\"%s\",\"user_id\":\"%s\"}", payload.project(), payload.userId());
    String invalidProject = String.format("{\"Project\":\"%s\",\"userId\":\"%s\"}", payload.project(), payload.userId());


    mockMvc.perform(
            get("/subscription/cancel")
                .with(httpBasic("ChuckNorris","astrongpassphrase!"))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(json)
        )
        .andExpect(status().is(400));

    mockMvc.perform(
            get("/subscription/cancel")
                .with(httpBasic("ChuckNorris","astrongpassphrase!"))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(invalidProject)
        )
        .andExpect(status().is(400));
  }

  @Test
  @DisplayName("decryptUnsubscriptionHash works")
  void decryptUnsubscriptionHashWorks() throws Exception {
    var payload = new CancellationRequest("QABCD", "test@user.id");
    var encrypted = "BStOJDfmn0ZyNceOPN3qU2xJw1mQfdbzY_a-uGt7Ae0=";
    Mockito.when(requestDecrypter.decryptCancellationRequest(encrypted)).thenReturn(payload);

    String json = String.format("{\"project\":\"%s\",\"userId\":\"%s\"}", payload.project(), payload.userId());

    mockMvc.perform(
            post("/subscription/cancel/{encrypted}", encrypted)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
        )
        .andExpect(status().is(202))
        .andExpect(content().string(json));
  }

  @Test
  @DisplayName("decryptCancellationRequest does not work for incorrect input")
  void decryptCancellationRequestDoesNotWorkForIncorrectInput() throws Exception {
    var encrypted = "SomeInvalidToken";
    Mockito.when(requestDecrypter.decryptCancellationRequest(encrypted))
        .thenThrow(DecryptionException.class);

    mockMvc
        .perform(
            post("/subscription/cancel/{encrypted}", encrypted)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
        .andExpect(status().is(400));
  }

  @Test
  @DisplayName("encryptCancellationRequest rejects unauthorized access")
  void encryptCancellationRequestRejectsUnauthorizedAccess() throws Exception {
    var payload = new CancellationRequest("QABCD", "test@user.id");
    var encrypted = "BStOJDfmn0ZyNceOPN3qU2xJw1mQfdbzY_a-uGt7Ae0=";
    Mockito.when(requestDecrypter.decryptCancellationRequest(encrypted)).thenReturn(payload);

    String json = String.format("{\"project\":\"%s\",\"userId\":\"%s\"}", payload.project(), payload.userId());

    mockMvc
        .perform(
            get("/subscription/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(json))
        .andDo(print())
        .andExpect(status().is(401));

    mockMvc
        .perform(
            get("/subscription/cancel")
                .with(httpBasic("wrongUser", "randompassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(json))
        .andDo(print())
        .andExpect(status().is(401));
  }

  @Test
  @DisplayName("UnprocessableEntity for erroneous SubscriptionRepository")
  void unprocessableEntityForErroneousSubscriptionRepository() throws Exception {
    var payload = new CancellationRequest("QABCD", "test@user.id");
    var encrypted = "BStOJDfmn0ZyNceOPN3qU2xJw1mQfdbzY_a-uGt7Ae0=";
    Mockito.when(requestDecrypter.decryptCancellationRequest(encrypted)).thenReturn(payload);
    Mockito.doThrow(new RuntimeException("Some test exception in subscription repo."))
        .when(subscriptionRepository).cancelSubscription(payload);

    mockMvc.perform(
            post("/subscription/cancel/{encrypted}", encrypted)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
        )
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @DisplayName("BadRequest for unprocessable input with valid format")
  void badRequestForUnprocessableInputWithValidFormat() throws Exception {

    var validButUnprocessableEntity = new CancellationRequest("some code", "some user id");
    Mockito.when(requestEncrypter.encryptCancellationRequest(validButUnprocessableEntity))
        .thenThrow(new EncryptionException());

    String validButUnprocessableJson =
        String.format(
            "{\"project\":\"%s\",\"userId\":\"%s\"}",
            validButUnprocessableEntity.project(), validButUnprocessableEntity.userId());

    mockMvc.perform(
            get("/subscription/cancel")
                .with(httpBasic("ChuckNorris","astrongpassphrase!"))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(validButUnprocessableJson)
        )
        .andExpect(status().isBadRequest());
  }
}
