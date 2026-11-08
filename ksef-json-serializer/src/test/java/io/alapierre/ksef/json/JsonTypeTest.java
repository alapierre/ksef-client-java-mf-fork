package io.alapierre.ksef.json;

import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;

public class JsonTypeTest {
    @Test
    public void typeTokenRejectsUnresolvedTypes() {
        assertEquals(String.class, JsonType.of(String.class).getType());
        assertThrows(IllegalArgumentException.class, () -> new JsonType() { });
        assertThrows(IllegalArgumentException.class, JsonTypeTest::unresolvedType);
        assertThrows(IllegalArgumentException.class, () -> new JsonType<List<?>>() { });
    }

    private static <T> JsonType<List<T>> unresolvedType() {
        return new JsonType<List<T>>() { };
    }
}
