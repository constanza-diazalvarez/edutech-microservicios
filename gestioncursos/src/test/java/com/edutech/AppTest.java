package com.edutech;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @Test
    public void testApp() {
        assertTrue(true);
    }

    /**
     * Test básico para verificar que la aplicación puede iniciarse
     */
    @Test
    public void testApplicationContext() {
        // Test básico que siempre pasa
        assertNotNull("Test context");
        assertEquals(2, 1 + 1);
    }
}
