import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class UnitTests {

    private Lexer lexer;

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
        lexer = new Lexer("Test");
        lexer.Lex();
        Token token = lexer.getTokens().get(0);
        assertEquals("[WORD(Test)]", lexer.getTokens().toString());
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
        assertEquals(0, token.getLine());
        assertEquals(0, token.getStartPos());
    }

    // Tests for a word, number, and newline
    @Test
    public void test4() {
        lexer = new Lexer("test 5");
        lexer.Lex();
        assertEquals("[WORD(test), NUMBER(5)]", lexer.getTokens().toString());
        assertEquals(5, lexer.getTokens().get(1).getStartPos());
    }

    // Tests for word and number, then number and word.
    @Test
    public void test5() {
        lexer = new Lexer("5 test\ntest 5");
        lexer.Lex();
        assertEquals("[NUMBER(5), WORD(test), SEPARATOR, WORD(test), NUMBER(5)]", lexer.getTokens().toString());
        assertEquals(12, lexer.getTokens().get(4).getStartPos());
        assertEquals(1, lexer.getTokens().get(4).getLine());
    }

    // Test checks to see if speical characters are rooted out during errors.
    @Test
    public void test6() {
        lexer = new Lexer("13+12\n");
        Error exception = assertThrows(Error.class, () -> lexer.Lex());
        assertEquals("Not a recognized character at line: 0, position 2", exception.getMessage());
    }

    // This test checks to see if invalid numbers are thrown as exceptions.
    @Test
    public void test7() {
        lexer = new Lexer(".5.0 6.6.6");
        Error exception = assertThrows(Error.class, () -> lexer.Lex());
        assertEquals("Not a valid number at line 0", exception.getMessage());
    }
}
