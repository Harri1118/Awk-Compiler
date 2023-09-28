package icsi311;

import icsi311.Token.TokenType;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTests {

    private Lexer lexer;

    @Test
    public void test0() {
        lexer = new Lexer("");
        lexer.Lex();
        assertEquals("[]", lexer.getTokens().toString());
    }

    @Test
    public void test1() {
        lexer = new Lexer("example");
        lexer.Lex();
        assertEquals("[WORD(example)]", lexer.getTokens().toString());
    }

    @Test
    public void test2() {
        lexer = new Lexer("123");
        lexer.Lex();
        assertEquals("[NUMBER(123)]", lexer.getTokens().toString());
    }

    @Test
    public void test3() {
        lexer = new Lexer("\n");
        lexer.Lex();
        assertEquals("[SEPARATOR]", lexer.getTokens().toString());
    }

    @Test
    public void test4() {
        lexer = new Lexer("test 5");
        lexer.Lex();
        assertEquals("[WORD(test), NUMBER(5)]", lexer.getTokens().toString());
    }

    @Test
    public void test5() {
        lexer = new Lexer("5 test\ntest 5");
        lexer.Lex();
        assertEquals("[NUMBER(5), WORD(test), SEPARATOR, WORD(test), NUMBER(5)]", lexer.getTokens().toString());
    }

    @Test
    public void test6() {
        lexer = new Lexer("$0 = tolower($0)");
        lexer.Lex();
        assertEquals(8, lexer.getTokens().size());
        assertEquals(Token.TokenType.DOLLAR, lexer.getTokens().get(0).getType());
        assertEquals(Token.TokenType.NUMBER, lexer.getTokens().get(1).getType());
        assertEquals(Token.TokenType.ASSIGN, lexer.getTokens().get(2).getType());
        assertEquals(Token.TokenType.WORD, lexer.getTokens().get(3).getType());
        assertEquals("tolower", lexer.getTokens().get(3).getValue());
        assertEquals(Token.TokenType.OPENPAREN, lexer.getTokens().get(4).getType());
        assertEquals(Token.TokenType.DOLLAR, lexer.getTokens().get(5).getType());
        assertEquals(Token.TokenType.NUMBER, lexer.getTokens().get(6).getType());
        assertEquals(Token.TokenType.CLOSEPAREN, lexer.getTokens().get(7).getType());
    }

    @Test
    public void test7() {
        lexer = new Lexer(
                "while if do for break continue else return BEGIN END print printf next in delete getline exit nextfile function");
        lexer.Lex();
        assertEquals(19, lexer.getTokens().size());
        assertEquals(Token.TokenType.WHILE, lexer.getTokens().get(0).getType());
        assertEquals(Token.TokenType.IF, lexer.getTokens().get(1).getType());
        assertEquals(Token.TokenType.DO, lexer.getTokens().get(2).getType());
        assertEquals(Token.TokenType.FOR, lexer.getTokens().get(3).getType());
        assertEquals(Token.TokenType.BREAK, lexer.getTokens().get(4).getType());
        assertEquals(Token.TokenType.CONTINUE, lexer.getTokens().get(5).getType());
        assertEquals(Token.TokenType.ELSE, lexer.getTokens().get(6).getType());
        assertEquals(Token.TokenType.RETURN, lexer.getTokens().get(7).getType());
        assertEquals(Token.TokenType.BEGIN, lexer.getTokens().get(8).getType());
        assertEquals(Token.TokenType.END, lexer.getTokens().get(9).getType());
        assertEquals(Token.TokenType.PRINT, lexer.getTokens().get(10).getType());
        assertEquals(Token.TokenType.PRINTF, lexer.getTokens().get(11).getType());
        assertEquals(Token.TokenType.NEXT, lexer.getTokens().get(12).getType());
        assertEquals(Token.TokenType.IN, lexer.getTokens().get(13).getType());
        assertEquals(Token.TokenType.DELETE, lexer.getTokens().get(14).getType());
        assertEquals(Token.TokenType.GETLINE, lexer.getTokens().get(15).getType());
        assertEquals(Token.TokenType.EXIT, lexer.getTokens().get(16).getType());
        assertEquals(Token.TokenType.NEXTFILE, lexer.getTokens().get(17).getType());
        assertEquals(Token.TokenType.FUNCTION, lexer.getTokens().get(18).getType());
    }

    @Test
    public void test8() {
        lexer = new Lexer(">=  ++  --  <=  ==  !=  ^=  %=  *=  /=  +=  -=  !~   &&   >>   ||");
        lexer.Lex();
        assertEquals(16, lexer.getTokens().size());
        assertEquals(TokenType.GREQ, lexer.getTokens().get(0).getType());
        assertEquals(TokenType.ADD, lexer.getTokens().get(1).getType());
        assertEquals(TokenType.SUBT, lexer.getTokens().get(2).getType());
        assertEquals(TokenType.LEEQ, lexer.getTokens().get(3).getType());
        assertEquals(TokenType.EQUALS, lexer.getTokens().get(4).getType());
        assertEquals(TokenType.NEQ, lexer.getTokens().get(5).getType());
        assertEquals(TokenType.CAREQ, lexer.getTokens().get(6).getType());
        assertEquals(TokenType.PEREQ, lexer.getTokens().get(7).getType());
        assertEquals(TokenType.TIEQ, lexer.getTokens().get(8).getType());
        assertEquals(TokenType.DIVEQ, lexer.getTokens().get(9).getType());
        assertEquals(TokenType.PLEQ, lexer.getTokens().get(10).getType());
        assertEquals(TokenType.MINEQ, lexer.getTokens().get(11).getType());
        assertEquals(TokenType.REGEXP, lexer.getTokens().get(12).getType());
        assertEquals(TokenType.AND, lexer.getTokens().get(13).getType());
        assertEquals(TokenType.LEADS, lexer.getTokens().get(14).getType());
        assertEquals(TokenType.OR, lexer.getTokens().get(15).getType());
    }

    @Test
    public void test9() {
        lexer = new Lexer("{ } [ ] ( ) $ ~ = < > ! + ^ - ? : * / % ; \n | ,");
        lexer.Lex();
        assertEquals(TokenType.OPBRAC, lexer.getTokens().get(0).getType());
        assertEquals(TokenType.CLBRAC, lexer.getTokens().get(1).getType());
        assertEquals(TokenType.SQOPBRAC, lexer.getTokens().get(2).getType());
        assertEquals(TokenType.SQCLBRAC, lexer.getTokens().get(3).getType());
        assertEquals(TokenType.OPENPAREN, lexer.getTokens().get(4).getType());
        assertEquals(TokenType.CLOSEPAREN, lexer.getTokens().get(5).getType());
        assertEquals(TokenType.DOLLAR, lexer.getTokens().get(6).getType());
        assertEquals(TokenType.TILDE, lexer.getTokens().get(7).getType());
        assertEquals(TokenType.ASSIGN, lexer.getTokens().get(8).getType());
        assertEquals(TokenType.LETHAN, lexer.getTokens().get(9).getType());
        assertEquals(TokenType.GRTHAN, lexer.getTokens().get(10).getType());
        assertEquals(TokenType.EXLPT, lexer.getTokens().get(11).getType());
        assertEquals(TokenType.PLUS, lexer.getTokens().get(12).getType());
        assertEquals(TokenType.CARROT, lexer.getTokens().get(13).getType());
        assertEquals(TokenType.MINUS, lexer.getTokens().get(14).getType());
        assertEquals(TokenType.QMARK, lexer.getTokens().get(15).getType());
        assertEquals(TokenType.COLON, lexer.getTokens().get(16).getType());
        assertEquals(TokenType.STAR, lexer.getTokens().get(17).getType());
        assertEquals(TokenType.SLASH, lexer.getTokens().get(18).getType());
        assertEquals(TokenType.PERCNT, lexer.getTokens().get(19).getType());
        assertEquals(TokenType.SEPARATOR, lexer.getTokens().get(20).getType());
        assertEquals(TokenType.SEPARATOR, lexer.getTokens().get(21).getType());
        assertEquals(TokenType.VERTBAR, lexer.getTokens().get(22).getType());
        assertEquals(TokenType.COMMA, lexer.getTokens().get(23).getType());
    }

    @Test
    public void test10() {
        lexer = new Lexer("\"TEST\"\n\"\\\"TEST\\\"\"");
        lexer.Lex();
        assertEquals("STRINGLITERAL(TEST)", lexer.getTokens().get(0).toString());
        assertEquals("SEPARATOR", lexer.getTokens().get(1).toString());
        assertEquals("STRINGLITERAL(\"TEST\")", lexer.getTokens().get(2).toString());
    }

    @Test
    public void test11() {
        lexer = new Lexer("\"\"");
        lexer.Lex();
        assertEquals("STRINGLITERAL()", lexer.getTokens().get(0).toString());
    }

    @Test
    public void test12() {
        lexer = new Lexer("#test\ntest\n#test");
        lexer.Lex();
        assertEquals("SEPARATOR", lexer.getTokens().get(0).toString());
        assertEquals("WORD(test)", lexer.getTokens().get(1).toString());
        assertEquals("SEPARATOR", lexer.getTokens().get(2).toString());
    }

    @Test
    public void test13() {
        lexer = new Lexer("test\n#test");
        lexer.Lex();
        assertEquals("WORD(test)", lexer.getTokens().get(0).toString());
        assertEquals("SEPARATOR", lexer.getTokens().get(1).toString());
    }

    @Test
    public void test14() {
        lexer = new Lexer("_");
        try {
            lexer.Lex();
        } catch (Exception e) {
            assertEquals("Error at line 1\nNot a recognized character!", e.getMessage());
        }
    }

    @Test
    public void test15() {
        lexer = new Lexer("\n2.22.");
        try {
            lexer.Lex();
        } catch (Exception e) {
            assertEquals("Error at line 2\nNot a valid number!", e.getMessage());
        }
    }

    @Test
    public void test16() {
        lexer = new Lexer("&");
        try {
            lexer.Lex();
        } catch (Exception e) {
            assertEquals("Error at line 1\nCannot have single character '&'!", e.getMessage());
        }
    }

    @Test
    public void test17() {
        lexer = new Lexer("\"\"\"");
        try {
            lexer.Lex();
        } catch (Exception e) {
            assertEquals("Error at line 1\nMust have an even number of quotes!", e.getMessage());
        }
    }

    @Test
    public void test18() {
        lexer = new Lexer("\"\\\"test\"");
        try {
            lexer.Lex();
        } catch (Exception e) {
            assertEquals("Error at line 1\nMust have an even number of quotes in a string literal!", e.getMessage());
        }
    }

    @Test
    public void test19() {
        lexer = new Lexer("$0");
        lexer.Lex();
        LinkedList<Token> testList = new LinkedList<Token>();
        testList.add(new Token(Token.TokenType.DOLLAR, "$", 0, 1));
        testList.add(new Token(Token.TokenType.NUMBER, "0", 0, 1));
        TokenManager tokenManager = new TokenManager(lexer.getTokens());
        assertEquals(Token.TokenType.DOLLAR, tokenManager.Peek(0).get().getType());
        assertEquals(Token.TokenType.DOLLAR, tokenManager.MatchAndRemove(Token.TokenType.DOLLAR).get().getType());
        testList.remove(0);
        assertEquals(testList.get(0).toString(), tokenManager.Peek(0).get().toString());
        assertEquals(true, tokenManager.MoreTokens());
        tokenManager.MatchAndRemove(Token.TokenType.NUMBER);
        assertEquals(false, tokenManager.MoreTokens());
    }

    @Test
    public void test20() {
        LinkedList<Token> statement = new LinkedList<Token>();
        statement.add(new Token("SEPARATOR",0,1));
        statement.add(new Token("Hello", 1, 2));
        statement.add(new Token("SEPARATOR",0,2));
        Parser p = new Parser(statement);
    }

    @Test
    public void test21() {
        try {
            Lexer l = new Lexer("function test(a,b)\nBEGIN\nEND");
            l.Lex();
            Parser p = new Parser(l.getTokens());
            ProgramNode pr = p.Parse();
            assertEquals("BEGIN{[Condition: null, Statements: null]}\nOTHER{[]}\nEND{[Condition: null, Statements: null]}\nFunctions:\n[WORD(test)(WORD(a),WORD(b)) Statements: null]", pr.toString());
        }
        catch(Exception e){
            System.out.println("Function is wrong.");
        }
    }

    @Test
    public void test22() throws Exception {
        Lexer l = new Lexer("\nfunction test()\n\n\nBEGIN\nEND\n");
        l.Lex();
        Parser p = new Parser(l.getTokens());
        ProgramNode pr = p.Parse();
        assertEquals("BEGIN{[Condition: null, Statements: null]}\nOTHER{[]}\nEND{[Condition: null, Statements: null]}\nFunctions:\n[WORD(test)() Statements: null]", pr.toString());
    }

    @Test
    public void test23() throws Exception {
        Lexer l = new Lexer("BEGIN\nEND");
        l.Lex();
        Parser p = new Parser(l.getTokens());
        ProgramNode pr = p.Parse();
        assertEquals("BEGIN{[Condition: null, Statements: null]}\nOTHER{[]}\nEND{[Condition: null, Statements: null]}\nFunctions:\n[]", pr.toString());
    }

    @Test
    public void test24(){
        try{
            Lexer l = new Lexer("function");
            l.Lex();
            Parser p = new Parser(l.getTokens());
            p.Parse();
        }
        catch(Exception e){
            assertEquals("Reached end of line before proper method return!",e.getMessage());
        }
    }

    @Test
    public void test25(){
        try{
            Lexer l = new Lexer("function()\n\n");
            l.Lex();
            Parser p = new Parser(l.getTokens());
            p.Parse();
        }
        catch(Exception e){
            assertEquals("Must have a valid method name!", e.getMessage());
        }
    }

    @Test
    public void test26(){
        try{
            Lexer l = new Lexer("function test\n\n");
            l.Lex();
            Parser p = new Parser(l.getTokens());
            p.Parse();
        }
        catch(Exception e){
            assertEquals("Function must have parenthesis!", e.getMessage());
        }
        try{
            Lexer l = new Lexer("function test(\n\n");
            l.Lex();
            Parser p = new Parser(l.getTokens());
            p.Parse();
        }
        catch(Exception e){
            assertEquals("Function must have parenthesis!", e.getMessage());
        }
        try{
            Lexer l = new Lexer("function test)\n\n");
            l.Lex();
            Parser p = new Parser(l.getTokens());
            p.Parse();
        }
        catch(Exception e){
            assertEquals("Function must have parenthesis!", e.getMessage());
        }
    }
    @Test
    public void test27(){
        try{
            Lexer l = new Lexer("BEGIN\n{\nEND");
            l.Lex();
            Parser p = new Parser(l.getTokens());
            p.Parse();
        }
        catch(Exception e){
            assertEquals("Action not recognized!", e.getMessage());
        }
    }
    @Test
    public void test28(){
        try{
            Lexer l = new Lexer("function t(t,)");
            l.Lex();
            Parser p = new Parser(l.getTokens());
            p.Parse();
        }
        catch(Exception e){
            assertEquals("Cannot have comma at the end of a parameter list!", e.getMessage());
        }
    }
    @Test
    public void test29(){
        try{
            Lexer l = new Lexer("function t(t");
            l.Lex();
            Parser p = new Parser(l.getTokens());
            p.Parse();
        }
        catch(Exception e){
            assertEquals("Must have parenthesis for proper parameter declaration!", e.getMessage());
        }
    }
}
