import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class LexerTest {

    private Lexer lexer;

    /*
     * @Before
     * public void setUp() {
     * lexer = new Lexer("", 0, 0);
     * }
     */
    @Test
    public void test1() {
        lexer = new Lexer("example", 0, 0);
        lexer.Lex();
        Token token = lexer.tokens.get(0);
        assertEquals("[WORD(example)]", lexer.tokens);
        assertEquals(0, token.getLine());
        assertEquals(0, token.getPos());
    }

    @Test
    public void test2() {

        lexer = new Lexer("123", 0, 0);
        lexer.Lex();
        Token token = lexer.tokens.get(0);
        assertEquals("[NUMBER(123)]", lexer.tokens);
        assertEquals(0, token.getLine());
        assertEquals(0, token.getPos());
    }

    @Test
    public void test3() {
        lexer = new Lexer("'\n'", 0, 0);
        lexer.Lex();
        Token token = lexer.tokens.get(0);
        assertEquals("[SEPARATOR]", lexer.tokens);
        assertEquals(0, token.getLine());
        assertEquals(0, token.getPos());
    }

    @Test
    public void test4() {
    }
    /*
     * @Test
     * public void test4() {
     * 
     * }
     * 
     * @Test
     * public void test5() {
     * 
     * }
     * 
     * @Test
     * public void test6() {
     * 
     * }
     * lexer.tokens.forEach(token.toString() -> {System.out.println(element);});
     */
}
