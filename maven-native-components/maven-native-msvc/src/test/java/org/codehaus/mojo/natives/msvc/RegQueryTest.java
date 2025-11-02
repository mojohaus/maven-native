package org.codehaus.mojo.natives.msvc;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.Os;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RegQueryTest extends PlexusTestCase {
    @Test
    void regQuery() {
        if (Os.isFamily("windows")) {
            String value = RegQuery.getValue(
                    "REG_SZ", "HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion", "ProgramFilesDir");
            assertNotNull(value);
        }
    }
}
