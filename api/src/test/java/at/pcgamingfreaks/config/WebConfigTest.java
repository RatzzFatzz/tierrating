package at.pcgamingfreaks.config;

import at.pcgamingfreaks.mapper.converter.StringToContentTypeConverter;
import at.pcgamingfreaks.mapper.converter.StringToServiceConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebConfig Tests")
class WebConfigTest {

    @Test
    @DisplayName("addFormatters() should register both converters")
    void addFormatters_shouldRegisterBothConverters() {
        WebConfig webConfig = new WebConfig();
        FormatterRegistry registry = mock(FormatterRegistry.class);
        ArgumentCaptor<Converter<?, ?>> captor = ArgumentCaptor.forClass(Converter.class);

        webConfig.addFormatters(registry);

        verify(registry, times(2)).addConverter(captor.capture());
        List<Converter<?, ?>> registeredConverters = captor.getAllValues();
        assertEquals(2, registeredConverters.size());
        assertTrue(registeredConverters.stream().anyMatch(c -> c instanceof StringToServiceConverter));
        assertTrue(registeredConverters.stream().anyMatch(c -> c instanceof StringToContentTypeConverter));
    }
}
