package com.btg.challenge.orders.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("SwaggerConfig Unit Tests")
class SwaggerConfigTest {

    @InjectMocks
    private SwaggerConfig swaggerConfig;

    @Test
    @DisplayName("Should create OpenAPI bean with correct configuration")
    void shouldCreateOpenAPIBeanWithCorrectConfiguration() {
        // When
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Then
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("Orders Service API", openAPI.getInfo().getTitle());
        assertEquals("API for managing orders and customers in the BTG Challenge application", openAPI.getInfo().getDescription());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
    }

    @Test
    @DisplayName("Should create OpenAPI with non-null Info object")
    void shouldCreateOpenAPIWithNonNullInfoObject() {
        // When
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Then
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
    }

    @Test
    @DisplayName("Should create OpenAPI with all required Info fields populated")
    void shouldCreateOpenAPIWithAllRequiredInfoFieldsPopulated() {
        // When
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        Info info = openAPI.getInfo();

        // Then
        assertNotNull(info.getTitle());
        assertNotNull(info.getDescription());
        assertNotNull(info.getVersion());
        assertFalse(info.getTitle().trim().isEmpty());
        assertFalse(info.getDescription().trim().isEmpty());
        assertFalse(info.getVersion().trim().isEmpty());
    }

    @Test
    @DisplayName("Should create OpenAPI with consistent title across multiple calls")
    void shouldCreateOpenAPIWithConsistentTitleAcrossMultipleCalls() {
        // When
        OpenAPI openAPI1 = swaggerConfig.customOpenAPI();
        OpenAPI openAPI2 = swaggerConfig.customOpenAPI();

        // Then
        assertEquals(openAPI1.getInfo().getTitle(), openAPI2.getInfo().getTitle());
        assertEquals("Orders Service API", openAPI1.getInfo().getTitle());
        assertEquals("Orders Service API", openAPI2.getInfo().getTitle());
    }

    @Test
    @DisplayName("Should create OpenAPI with consistent description across multiple calls")
    void shouldCreateOpenAPIWithConsistentDescriptionAcrossMultipleCalls() {
        // When
        OpenAPI openAPI1 = swaggerConfig.customOpenAPI();
        OpenAPI openAPI2 = swaggerConfig.customOpenAPI();

        // Then
        assertEquals(openAPI1.getInfo().getDescription(), openAPI2.getInfo().getDescription());
        assertEquals("API for managing orders and customers in the BTG Challenge application", openAPI1.getInfo().getDescription());
    }

    @Test
    @DisplayName("Should create OpenAPI with consistent version across multiple calls")
    void shouldCreateOpenAPIWithConsistentVersionAcrossMultipleCalls() {
        // When
        OpenAPI openAPI1 = swaggerConfig.customOpenAPI();
        OpenAPI openAPI2 = swaggerConfig.customOpenAPI();

        // Then
        assertEquals(openAPI1.getInfo().getVersion(), openAPI2.getInfo().getVersion());
        assertEquals("1.0.0", openAPI1.getInfo().getVersion());
    }

    @Test
    @DisplayName("Should create new OpenAPI instance for each call")
    void shouldCreateNewOpenAPIInstanceForEachCall() {
        // When
        OpenAPI openAPI1 = swaggerConfig.customOpenAPI();
        OpenAPI openAPI2 = swaggerConfig.customOpenAPI();

        // Then
        assertNotSame(openAPI1, openAPI2);
        assertNotSame(openAPI1.getInfo(), openAPI2.getInfo());
    }

    @Test
    @DisplayName("Should create OpenAPI bean that is ready for Spring configuration")
    void shouldCreateOpenAPIBeanThatIsReadyForSpringConfiguration() {
        // When
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Then
        assertNotNull(openAPI);
        assertDoesNotThrow(() -> {
            openAPI.getInfo().getTitle();
            openAPI.getInfo().getDescription();
            openAPI.getInfo().getVersion();
        });
    }

}