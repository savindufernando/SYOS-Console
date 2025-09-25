package app;

import org.junit.jupiter.api.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private String runWithInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Main.main(new String[]{}); // ✅ call static main instead of new Main().start()
        return outContent.toString();
    }

    @Test
    void choosingExitOption_ShouldPrintGoodbye() {
        // Option 3 = Exit
        String output = runWithInput("3\n");

        assertTrue(output.contains("Thank you for visiting SYOS"), "Exit message should appear");
    }



}
