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
import life.qbic.subscriptions.encryption.EncryptionException;
import life.qbic.subscriptions.encryption.RequestDecrypter;
import life.qbic.subscriptions.encryption.RequestEncrypter;
import life.qbic.subscriptions.subscriptions.CancellationRequest;
import life.qbic.subscriptions.subscriptions.SubscriptionRepository;
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

/** Tests the behaviour of the {@code /subscription} endpoints */
@WebMvcTest(controllers = SubscriptionController.class)
class SubscriptionControllerTest {

  @MockBean SubscriptionRepository subscriptionRepository;
  @MockBean RequestDecrypter requestDecrypter;
  @MockBean RequestEncrypter requestEncrypter;

  @Autowired MockMvc mockMvc;

  @ParameterizedTest
  @CsvSource(value = {"project, user_id", "Project, userId"})
  @DisplayName("When invalid input is provided, GET /cancel responds BAD_REQUEST")
  void whenInvalidInputIsProvidedGetCancelRespondsBadRequest(
      String invalidProjectTag, String invalidUserTag) throws Exception {
    String invalidObject =
        String.format(
            "{\"%s\":\"validProject\",\"%s\":\"validUserId\"}", invalidProjectTag, invalidUserTag);
    mockMvc
        .perform(
            get("/subscription/cancel")
                .with(httpBasic("ChuckNorris", "astrongpassphrase!"))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(invalidObject))
        .andExpect(status().is(400));
  }

  @ParameterizedTest
  @CsvSource(value = {"project, user_id", "Project, userId"})
  @DisplayName("When invalid input is provided, POST /cancel/token/generate responds BAD_REQUEST")
  void whenInvalidInputIsProvidedPostCancelTokenGenerateRespondsBadRequest(
      String invalidProjectTag, String invalidUserTag) throws Exception {
    String invalidObject =
        String.format(
            "{\"%s\":\"validProject\",\"%s\":\"validUserId\"}", invalidProjectTag, invalidUserTag);
    mockMvc
        .perform(
            post("/subscription/cancel/token/generate")
                .with(httpBasic("ChuckNorris", "astrongpassphrase!"))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(invalidObject))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("When valid input is provided, GET /cancel responds OK")
  void whenValidInputIsProvidedGetCancelRespondsOk() throws Exception {
    var payload = new CancellationRequest("validProject", "validUserId");
    var encrypted = "validToken";
    Mockito.when(requestEncrypter.encryptCancellationRequest(payload)).thenReturn(encrypted);

    String json =
        String.format(
            "{\"project\":\"%s\",\"userId\":\"%s\"}", payload.project(), payload.userId());

    mockMvc
        .perform(
            get("/subscription/cancel")
                .with(httpBasic("ChuckNorris", "astrongpassphrase!"))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(json))
        .andExpect(status().isOk())
        .andExpect(content().string(encrypted));
  }

  @Test
  @DisplayName("When valid input is provided, POST /cancel/token/generate responds OK")
  void whenValidInputIsProvidedPostCancelTokenGenerateRespondsOk() throws Exception {
    var payload = new CancellationRequest("validProject", "validEmail");
    var encrypted = "thisIsAValidToken";
    Mockito.when(requestEncrypter.encryptCancellationRequest(payload)).thenReturn(encrypted);

    String json =
        String.format(
            "{\"project\":\"%s\",\"userId\":\"%s\"}", payload.project(), payload.userId());

    mockMvc
        .perform(
            post("/subscription/cancel/token/generate")
                .with(httpBasic("ChuckNorris", "astrongpassphrase!"))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(json))
        .andExpect(status().isOk())
        .andExpect(content().string(encrypted));
  }

  @Test
  @DisplayName("When valid input is provided, POST /cancel responds ACCEPTED")
  void whenValidInputIsProvidedPostCancelRespondsAccepted() throws Exception {
    var payload = new CancellationRequest("validProject", "validUserId");
    var encrypted = "validToken";
    Mockito.when(requestDecrypter.decryptCancellationRequest(encrypted)).thenReturn(payload);

    String json =
        String.format(
            "{\"project\":\"%s\",\"userId\":\"%s\"}", payload.project(), payload.userId());

    mockMvc
        .perform(
            post("/subscription/cancel/{encrypted}", encrypted)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
        .andExpect(status().isAccepted())
        .andExpect(content().string(json));
  }

  @Test
  @DisplayName("When authorization is missing, GET /cancel responds UNAUTHORIZED")
  void whenAuthorizationIsMissingGetCancelRespondsUnauthorized() throws Exception {
    var payload = new CancellationRequest("validProject", "validUserId");
    String json =
        String.format(
            "{\"project\":\"%s\",\"userId\":\"%s\"}", payload.project(), payload.userId());

    mockMvc
        .perform(
            get("/subscription/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(json))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("When authorization is missing, POST /cancel/token/generate responds UNAUTHORIZED")
  void whenAuthorizationIsMissingPostCancelTokenGenerateRespondsUnauthorized() throws Exception {
    var payload = new CancellationRequest("validProject", "validUserId");
    String json =
        String.format(
            "{\"project\":\"%s\",\"userId\":\"%s\"}", payload.project(), payload.userId());

    mockMvc
        .perform(
            post("/subscription/cancel/token/generate")
                .with(httpBasic("wrongUser", "wrongPassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(json))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("When authorization credentials are wrong, GET /cancel responds UNAUTHORIZED")
  void whenAuthorizationCredentialsAreWrongGetCancelRespondsUnauthorized() throws Exception {
    var payload = new CancellationRequest("validProject", "validUserId");
    String json =
        String.format(
            "{\"project\":\"%s\",\"userId\":\"%s\"}", payload.project(), payload.userId());
    mockMvc
        .perform(
            get("/subscription/cancel")
                .with(httpBasic("wrongUser", "wrongPassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(json))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("When authorization credentials are wrong, POST /cancel/token/generate responds UNAUTHORIZED")
  void whenAuthorizationCredentialsAreWrongPostCancelTokenGenerateRespondsUnauthorized() throws Exception {
    var payload = new CancellationRequest("validProject", "validUserId");
    String json =
        String.format(
            "{\"project\":\"%s\",\"userId\":\"%s\"}", payload.project(), payload.userId());

    mockMvc
        .perform(
            post("/subscription/cancel/token/generate")
                .with(httpBasic("wrongUser", "wrongPassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(json))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("When encryption fails, GET /cancel responds BAD_REQUEST")
  void whenEncryptionFailsGetCancelRespondsBadRequest() throws Exception {
    var validEntity = new CancellationRequest("some code", "some user id");
    Mockito.when(requestEncrypter.encryptCancellationRequest(validEntity))
        .thenThrow(new EncryptionException());

    String validObject =
        String.format(
            "{\"project\":\"%s\",\"userId\":\"%s\"}", validEntity.project(), validEntity.userId());

    mockMvc
        .perform(
            get("/subscription/cancel")
                .with(httpBasic("ChuckNorris", "astrongpassphrase!"))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(validObject))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("When encryption fails, POST 'cancel/token/generate' responds BAD_REQUEST")
  void whenEncryptionFailsPostCancelTokenGenerateRespondsBadRequest() throws Exception {
    var validEntity = new CancellationRequest("some code", "some user id");
    Mockito.when(requestEncrypter.encryptCancellationRequest(validEntity))
        .thenThrow(new EncryptionException());

    String validObject =
        String.format(
            "{\"project\":\"%s\",\"userId\":\"%s\"}", validEntity.project(), validEntity.userId());

    mockMvc
        .perform(
            post("/subscription/cancel/token/generate")
                .with(httpBasic("ChuckNorris", "astrongpassphrase!"))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(validObject))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("When decryption fails, POST /cancel responds BAD_REQUEST")
  void whenDecryptionFailsPostCancelRespondsBadRequest() throws Exception {
    var validButUnprocessableToken = "validButUnprocessableToken";
    Mockito.when(requestDecrypter.decryptCancellationRequest(validButUnprocessableToken))
        .thenThrow(new DecryptionException());

    mockMvc
        .perform(
            post("/subscription/cancel/{encrypted}", validButUnprocessableToken)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("When data access fails, POST /cancel responds UNPROCESSABLE_ENTITY")
  void whenDataAccessFailsPostCancelRespondsUnprocessableEntity() throws Exception {
    var payload = new CancellationRequest("validProject", "validUserId");
    var encrypted = "validToken";
    Mockito.when(requestDecrypter.decryptCancellationRequest(encrypted)).thenReturn(payload);
    Mockito.doThrow(new RuntimeException("Some test exception in subscription repo."))
        .when(subscriptionRepository)
        .cancelSubscription(payload);

    mockMvc
        .perform(
            post("/subscription/cancel/{encrypted}", encrypted)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
        .andExpect(status().isUnprocessableEntity());
  }
}
