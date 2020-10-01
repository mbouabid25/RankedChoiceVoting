import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RankedChoiceVoteTest {

    @BeforeEach
    void setUp() throws Exception {
    }

    @Test
    public void testRankedChoiceVoting()
    {
      PrintStream originalOut = System.out;
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(bos));

      // action
      String[] args = new String[1];
      args[0] = "Test1.txt";
      RankedChoiceVoting.main(args);

      // assertion
      assertEquals("Winner is Beyonce\n", bos.toString());

      // undo the binding in System
      System.setOut(originalOut);
    }
}
