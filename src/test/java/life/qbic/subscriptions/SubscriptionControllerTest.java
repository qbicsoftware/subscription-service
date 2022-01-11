package life.qbic.subscriptions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import life.qbic.subscriptions.encryption.DecryptionException;
import life.qbic.subscriptions.encryption.EncryptionException;
import life.qbic.subscriptions.encryption.RequestDecrypter;
import life.qbic.subscriptions.encryption.RequestEncrypter;
import life.qbic.subscriptions.subscriptions.CancellationRequest;
import life.qbic.subscriptions.subscriptions.SubscriptionRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Tests the behaviour of the {@code /subscriptions} endpoints */
@WebMvcTest(controllers = SubscriptionController.class)
class SubscriptionControllerTest {

  @MockBean SubscriptionRepository subscriptionRepository;
  @MockBean RequestDecrypter requestDecrypter;
  @MockBean RequestEncrypter requestEncrypter;

  @Autowired MockMvc mockMvc;

  @ParameterizedTest
  @CsvSource(value = {"project, user_id", "Project, userId"})
  @DisplayName("When invalid input is provided, POST /subscriptions/tokens responds BAD_REQUEST")
  void whenInvalidInputIsProvidedPostSubscriptionsTokensRespondsBadRequest(
      String invalidProjectTag, String invalidUserTag) throws Exception {
    String invalidObject =
        String.format(
            "{\"%s\":\"validProject\",\"%s\":\"validUserId\"}", invalidProjectTag, invalidUserTag);
    mockMvc
        .perform(
            post("/subscriptions/tokens")
                .with(httpBasic("ChuckNorris", "astrongpassphrase!"))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(invalidObject))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("When valid input is provided, DELETE /subscriptions responds NO_CONTENT")
  void whenValidInputIsProvidedDeleteSubscriptionsRespondsNoContent() throws Exception {
    var payload = new CancellationRequest("validProject", "validUserId");
    var encrypted = "validToken";
    Mockito.when(requestDecrypter.decryptCancellationRequest(encrypted)).thenReturn(payload);
    mockMvc.perform(delete("/subscriptions/{token}", encrypted)).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("When valid input is provided, POST /subscriptions/tokens responds OK")
  void whenValidInputIsProvidedPostSubscriptionsTokensRespondsOk() throws Exception {
    var payload = new CancellationRequest("validProject", "validUserId");
    var encrypted = "validToken";
    Mockito.when(requestEncrypter.encryptCancellationRequest(payload)).thenReturn(encrypted);

    String json =
        String.format(
            "{\"project\":\"%s\",\"userId\":\"%s\"}", payload.project(), payload.userId());

    mockMvc
        .perform(
            post("/subscriptions/tokens")
                .with(httpBasic("ChuckNorris", "astrongpassphrase!"))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(json))
        .andExpect(status().isOk())
        .andExpect(content().string(encrypted));
  }

  @Disabled("Disabled until authorization is tackled")
  @Test
  @DisplayName("When authorization is missing, DELETE /subscriptions responds UNAUTHORIZED")
  void whenAuthorizationIsMissingDeleteSubscriptionsRespondsUnauthorized() throws Exception {
    // currently, this might fail as providing no authenticate header passes.
    // Probably problem with auth not with this endpoint
    mockMvc.perform(delete("/subscriptions/{token}", "someValidToken")).andExpect(status().isUnauthorized());
  }

  @Disabled("Disabled until authorization is tackled")
  @Test
  @DisplayName("When authorization is missing, POST /subscriptions/tokens responds UNAUTHORIZED")
  void whenAuthorizationIsMissingPostSubscriptionsTokensRespondsUnauthorized() throws Exception {
    // currently, this might fail as providing no authenticate header passes.
    // Probably problem with auth not with this endpoint
    mockMvc.perform(post("/subscriptions/tokens")).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName(
      "When authorization credentials are wrong, DELETE /subscriptions responds UNAUTHORIZED")
  void whenAuthorizationCredentialsAreWrongDeleteSubscriptionsRespondsUnauthorized()
      throws Exception {
    mockMvc
        .perform(
            delete("/subscriptions/{token}", "someValidToken")
                .with(httpBasic("wrongUser", "wrongPassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName(
      "When authorization credentials are wrong, POST /subscriptions/tokens responds UNAUTHORIZED")
  void whenAuthorizationCredentialsAreWrongPostSubscriptionsTokensRespondsUnauthorized()
      throws Exception {
    mockMvc
        .perform(post("/subscriptions/tokens").with(httpBasic("wrongUser", "wrongPasswd")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("When encryption fails, POST /subscriptions/tokens responds BAD_REQUEST")
  void whenEncryptionFailsPostSubscriptionsTokensRespondsBadRequest() throws Exception {
    var validEntity = new CancellationRequest("some code", "some user id");
    Mockito.when(requestEncrypter.encryptCancellationRequest(validEntity))
        .thenThrow(new EncryptionException());

    String validObject =
        String.format(
            "{\"project\":\"%s\",\"userId\":\"%s\"}", validEntity.project(), validEntity.userId());

    mockMvc
        .perform(
            post("/subscriptions/tokens")
                .with(httpBasic("ChuckNorris", "astrongpassphrase!"))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(validObject))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("When decryption fails, DELETE /subscriptions responds BAD_REQUEST")
  void whenDecryptionFailsDeleteSubscriptionsRespondsBadRequest() throws Exception {
    var validButUnprocessableToken = "validButUnprocessableToken";
    Mockito.when(requestDecrypter.decryptCancellationRequest(validButUnprocessableToken))
        .thenThrow(new DecryptionException());

    mockMvc
        .perform(delete("/subscriptions/{token}", validButUnprocessableToken))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("When data access fails, DELETE /subscriptions responds UNPROCESSABLE_ENTITY")
  void whenDataAccessFailsDeleteSubscriptionsRespondsUnprocessableEntity() throws Exception {
    var payload = new CancellationRequest("validProject", "validUserId");
    var encrypted = "validToken";
    Mockito.when(requestDecrypter.decryptCancellationRequest(encrypted)).thenReturn(payload);
    Mockito.doThrow(new RuntimeException("Some test exception in subscription repo."))
        .when(subscriptionRepository)
        .cancelSubscription(payload);

    mockMvc
        .perform(
            delete("/subscriptions/{token}", encrypted)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
        .andExpect(status().isUnprocessableEntity());
  }
}
