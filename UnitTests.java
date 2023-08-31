import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UnitTest {

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

    //Tests for word and number, then number and word.
    @Test
    public void test5() {
        lexer = new Lexer("5 test\ntest 5");
        lexer.Lex();
        assertEquals("[NUMBER(5), WORD(test), SEPARATOR, WORD(test), NUMBER(5)]", lexer.getTokens().toString());
        assertEquals(12, lexer.getTokens().get(4).getStartPos());
        assertEquals(1, lexer.getTokens().get(4).getLine());
    }

    //Test adds numbers separated by a symbol.
    @Test
    public void test6(){
        lexer = new Lexer("13+12\n");
        lexer.Lex();
        assertEquals("[NUMBER(13), NUMBER(12), SEPARATOR]", lexer.getTokens().toString());
    }

    //Test adds decimals and tests if it detects a number with .1
    @Test
    public void test7(){
        lexer = new Lexer(".5\n1.25+555");
        lexer.Lex();
        assertEquals("[NUMBER(0.5), SEPARATOR, NUMBER(1.25), NUMBER(555)]", lexer.getTokens().toString());
    }

    @Test
    public void test8(){
        lexer = new Lexer(".5.0");
        lexer.Lex();
        
    }
}
