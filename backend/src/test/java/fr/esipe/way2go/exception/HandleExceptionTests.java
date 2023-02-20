package fr.esipe.way2go.exception;

import fr.esipe.way2go.Way2GoApplication;
import fr.esipe.way2go.exception.user.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(classes = Way2GoApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK

)
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class HandleExceptionTests {

    @Nested
    @DisplayName("Simulation Forbidden Exception")
    class SimulationForbiddenExceptionTest {
        @Test
         void returnStatus() {
            // Arrange
            SimulationForbidden ex = new SimulationForbidden();
            ControllerAdvisor advisor = new ControllerAdvisor();
            // Act
            ResponseEntity<Object> result = advisor.handleSimulationForbidden(ex);
            // Assert
            assertThat(result.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
        }

        @Test
         void returnBody() {
            // Arrange
            SimulationForbidden ex = new SimulationForbidden();
            ControllerAdvisor advisor = new ControllerAdvisor();

            // Act
            ResponseEntity<Object> result = advisor.handleSimulationForbidden(ex);

            // Assert
            Map<String, Object> body = (Map<String, Object>) result.getBody();
            assert body != null;
            assertThat(body.containsKey("timestamp"), equalTo(true));
            assertThat(body.containsKey("message"), equalTo(true));
            assertThat(body.get("message"), equalTo(ex.getMessage()));
        }
    }

    @Nested
    @DisplayName("Simulation Not Found Exception")
    class SimulationNotFoundExceptionTest {
        @Test
         void returnStatus() {
            // Arrange
            SimulationNotFoundException ex = new SimulationNotFoundException(1L);
            ControllerAdvisor advisor = new ControllerAdvisor();
            // Act
            ResponseEntity<Object> result = advisor.handleSimulationNotFound(ex);
            // Assert
            assertThat(result.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        }

        @Test
         void returnBody() {
            // Arrange
            SimulationNotFoundException ex = new SimulationNotFoundException(1L);
            ControllerAdvisor advisor = new ControllerAdvisor();

            // Act
            ResponseEntity<Object> result = advisor.handleSimulationNotFound(ex);

            // Assert
            Map<String, Object> body = (Map<String, Object>) result.getBody();
            assert body != null;
            assertThat(body.containsKey("timestamp"), equalTo(true));
            assertThat(body.containsKey("message"), equalTo(true));
            assertThat(body.get("message"), equalTo(ex.getMessage()));
        }
    }

    @Nested
    @DisplayName("User Not Found Exception")
    class UserNotFoundExceptionTest {
        @Test
         void returnStatus() {
            // Arrange
            UserNotFoundException ex = new UserNotFoundException(1L);
            ControllerAdvisor advisor = new ControllerAdvisor();
            // Act
            ResponseEntity<Object> result = advisor.handleUserNotFound(ex);
            // Assert
            assertThat(result.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        }

        @Test
         void returnBody() {
            // Arrange
            UserNotFoundException ex = new UserNotFoundException(1L);
            ControllerAdvisor advisor = new ControllerAdvisor();

            // Act
            ResponseEntity<Object> result = advisor.handleUserNotFound(ex);

            // Assert
            Map<String, Object> body = (Map<String, Object>) result.getBody();
            assert body != null;
            assertThat(body.containsKey("timestamp"), equalTo(true));
            assertThat(body.containsKey("message"), equalTo(true));
            assertThat(body.get("message"), equalTo(ex.getMessage()));
        }
    }




}
