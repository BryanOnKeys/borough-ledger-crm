package com.boroughledger.crm.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DemoViewerPasswordTest {
    @Test
    void publicDemoPasswordMatchesTheFlywayBcryptHash() {
        // Esta contraseña es pública a propósito: pertenece al usuario VIEWER del Post-it.
        assertTrue(new BCryptPasswordEncoder().matches(
            "demo123456789",
            "$2y$12$pWKPjmjEqaUtkX/YjcIjX.8/e6JN/FlqLRTAYPpRHHYHXZQ.TdAgG"
        ));
    }
}
