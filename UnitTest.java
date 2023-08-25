import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class UnitTest {

    private Lexer lexer;

    /*
     * @Before
     * public void setUp() {
     * lexer = new Lexer("", 0, 0);
     * }
     */

    // Tests for empty cases
    @Test
    public void test0() {
        lexer = new Lexer("");
        lexer.Lex();
        assertEquals("[]", lexer.getTokens().toString());
    }

    // Tests for a word
    @Test
    public void test1() {
        lexer = new Lexer("example");
        lexer.Lex();
        Token token = lexer.getTokens().get(0);
        assertEquals("[WORD(example)]", lexer.getTokens().toString());
        assertEquals(0, token.getLine());
        assertEquals(0, token.getStartPos());
    }

    // Tests for a number
    @Test
    public void test2() {

        lexer = new Lexer("123");
        lexer.Lex();
        Token token = lexer.getTokens().get(0);
        assertEquals("[NUMBER(123)]", lexer.getTokens().toString());
        assertEquals(0, token.getLine());
        assertEquals(0, token.getStartPos());
    }

    // Tests for a newline
    @Test
    public void test3() {
        lexer = new Lexer("\n");
        lexer.Lex();
        Token token = lexer.getTokens().get(0);
        assertEquals("[SEPARATOR]", lexer.getTokens().toString());
        assertEquals(1, token.getLine());
        assertEquals(0, token.getStartPos());
    }

    // Tests for a word, number, and newline
    @Test
    public void test4() {
        lexer = new Lexer("test 5");
        lexer.Lex();
        assertEquals("[WORD(test), NUMBER(5)]", lexer.getTokens().toString());
    }

    //Tests for word and number, then number and word.
    public void test5() {
        lexer = new Lexer("5 test\ntest 5");
        lexer.Lex();
        assertEquals("[NUMBER(5), WORD(test)], SEPARATOR, WORD[test], NUMBER[5]", lexer.getTokens().toString());

    }

}
