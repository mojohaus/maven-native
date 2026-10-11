package org.codehaus.mojo.natives.msvc;

import org.codehaus.plexus.util.Os;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RegQueryTest {
    @Test
    void testRegQuery() {
        if (Os.isFamily("windows")) {
            String value = RegQuery.getValue(
                    "REG_SZ", "HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion", "ProgramFilesDir");
            assertNotNull(value);
        }
    }
}
