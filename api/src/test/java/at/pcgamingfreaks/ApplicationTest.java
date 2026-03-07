package at.pcgamingfreaks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@DisplayName("Application Tests")
class ApplicationTest {

    @Test
    @DisplayName("Application class should be instantiable")
    void application_shouldBeInstantiable() {
        assertDoesNotThrow(Application::new);
    }

    @Test
    @DisplayName("main() should call SpringApplication.run")
    void main_shouldCallSpringApplicationRun() {
        try (MockedStatic<SpringApplication> ignored = mockStatic(SpringApplication.class)) {
            ignored.when(() -> SpringApplication.run(any(Class.class), any(String[].class))).thenReturn(null);
            assertDoesNotThrow(() -> Application.main(new String[]{}));
            ignored.verify(() -> SpringApplication.run(any(Class.class), any(String[].class)));
        }
    }
}
