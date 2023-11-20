package icsi311;

import icsi311.Token.TokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UnitTests {

    private Lexer lexer;
    private Interpreter interpreter;
    @Test
    public void testBasicLexer() throws Exception{
        lexer = new Lexer("");
        assertEquals("[]", lexer.getTokens().toString());
        lexer = new Lexer("example");
        assertEquals("[example]", lexer.getTokens().toString());
        lexer = new Lexer("123");
        assertEquals("[123]", lexer.getTokens().toString());
        lexer = new Lexer("\n");
        assertEquals("[SEPARATOR]", lexer.getTokens().toString());
        lexer = new Lexer("test 5");
        assertEquals("[test, 5]", lexer.getTokens().toString());
        lexer = new Lexer("5 test\ntest 5");
        assertEquals("[5, test, SEPARATOR, test, 5]", lexer.getTokens().toString());

    }

    @Test
    public void ValidLexerCommand() throws Exception{
        lexer = new Lexer("$0 = tolower($0)");
        assertEquals(8, lexer.getTokens().size());
        assertEquals(Token.TokenType.DOLLAR, lexer.getTokens().get(0).getType());
        assertEquals(Token.TokenType.NUMBER, lexer.getTokens().get(1).getType());
        assertEquals(Token.TokenType.ASSIGN, lexer.getTokens().get(2).getType());
        assertEquals(Token.TokenType.WORD, lexer.getTokens().get(3).getType());
        assertEquals("tolower", lexer.getTokens().get(3).getValue());
        assertEquals(Token.TokenType.OPAREN, lexer.getTokens().get(4).getType());
        assertEquals(Token.TokenType.DOLLAR, lexer.getTokens().get(5).getType());
        assertEquals(Token.TokenType.NUMBER, lexer.getTokens().get(6).getType());
        assertEquals(Token.TokenType.CPAREN, lexer.getTokens().get(7).getType());
    }

    @Test
    public void AWKWords() throws Exception{
        lexer = new Lexer("while if do for break continue else return BEGIN END print printf next in delete getline exit nextfile function");
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
    public void AWKBooleans() throws Exception{
        lexer = new Lexer(">=  ++  --  <=  ==  !=  ^=  %=  *=  /=  +=  -=  !~   &&   >>   ||");
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
    public void AWKCharacters() throws Exception{
        lexer = new Lexer("{ } [ ] ( ) $ ~ = < > ! + ^ - ? : * / % ; \n | ,");
        assertEquals(TokenType.OPBRAC, lexer.getTokens().get(0).getType());
        assertEquals(TokenType.CLBRAC, lexer.getTokens().get(1).getType());
        assertEquals(TokenType.OPBRACE, lexer.getTokens().get(2).getType());
        assertEquals(TokenType.CLBRACE, lexer.getTokens().get(3).getType());
        assertEquals(TokenType.OPAREN, lexer.getTokens().get(4).getType());
        assertEquals(TokenType.CPAREN, lexer.getTokens().get(5).getType());
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
    public void LexerCommentsStringLiteralsAndLexer2Errors() throws Exception{
        lexer = new Lexer("\"TEST\"\n\"\\\"TEST\\\"\"");
        assertEquals("\"TEST\"", lexer.getTokens().get(0).toString());
        assertEquals("SEPARATOR", lexer.getTokens().get(1).toString());
        assertEquals("\"\"TEST\"\"", lexer.getTokens().get(2).toString());
        lexer = new Lexer("\"\"");
        assertEquals("\"\"", lexer.getTokens().get(0).toString());
        lexer = new Lexer("#test\ntest\n#test");
        assertEquals("SEPARATOR", lexer.getTokens().get(0).toString());
        assertEquals("test", lexer.getTokens().get(1).toString());
        assertEquals("SEPARATOR", lexer.getTokens().get(2).toString());
        lexer = new Lexer("test\n#test");
        assertEquals("test", lexer.getTokens().get(0).toString());
        assertEquals("SEPARATOR", lexer.getTokens().get(1).toString());
        Exception thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("_");
            lexer.getTokens();
        });
        Assertions.assertEquals("Error at line 1\nNot a recognized character!", thrown.getMessage());
         thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("\n2.22.");
            lexer.getTokens();
        });
        Assertions.assertEquals("Error at line 2\nNot a valid number!", thrown.getMessage());
         thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("&");
            lexer.getTokens();
        });
        assertEquals("Error at line 1\nCannot have single character '&'!", thrown.getMessage());
        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("&");
            lexer.getTokens();
        });
        assertEquals("Error at line 1\nCannot have single character '&'!", thrown.getMessage());
        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("\"\"\"");
            lexer.getTokens();
        });
        Assertions.assertEquals("Error at line 1\nMust have an even number of quotes!", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("\"\"\n\"\"\"");
            lexer.getTokens();
        });
        Assertions.assertEquals("Error at line 2\nMust have an even number of quotes!", thrown.getMessage());

    }

    @Test
    public void TokenManagerTest() throws Exception{
        lexer = new Lexer("$0");
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
    public void TestActionsExceptions() throws Exception {
        lexer = new Lexer("BEGIN{}\n(a==2){}\nEND{}");
        Parser p0 = new Parser(lexer.getTokens());
        ProgramNode ref = new ProgramNode();
        ref.addBegin(new BlockNode(new LinkedList<StatementNode>()));
        OperationNode Condition = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.EQ, new ConstantNode(2));
        ref.addOther(new BlockNode(Condition, new LinkedList<StatementNode>()));
        ref.addEnd(new BlockNode(new LinkedList<StatementNode>()));
        assertEquals(ref.toString(), p0.Parse().toString());
            Exception thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("BEGIN\nEND");
            Parser parser = new Parser(lexer.getTokens());
            parser.Parse();
        });
        Assertions.assertEquals("Invalid program structure! Must have a valid block structure! ex: BEGIN{Operation}", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("BEGIN\n{\nEND");
            Parser p = new Parser(lexer.getTokens());
            p.Parse();
        });
        Assertions.assertEquals("Invalid program structure! Must have a valid block structure! ex: BEGIN{Operation}", thrown.getMessage());
        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("function test(a,b){}\nBEGIN{}\nEND{}");
            Parser parser = new Parser(lexer.getTokens());
            parser.Parse();
        });
        Assertions.assertEquals("Not a valid program structure! Must define a function after a BEGIN Block!", thrown.getMessage());
    }

    @Test
    public void functionsExceptions() throws Exception{
        lexer = new Lexer("function a (b,c){}");
        Parser p0 = new Parser(lexer.getTokens());
        ProgramNode ref = new ProgramNode();
        String[] params = {"b","c"};
        FunctionDefinitionNode f = new FunctionDefinitionNode("a", params, new LinkedList<StatementNode>());
        ref.addFunction(f);
        assertEquals(ref.toString(), p0.Parse().toString());
        Exception thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("function");
            Parser p = new Parser(lexer.getTokens());
            p.Parse();
        });
        Assertions.assertEquals("File ended before function parsed!", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("function()");
            Parser p = new Parser(lexer.getTokens());
            p.Parse();
        });
        Assertions.assertEquals("Must have a valid method name!", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("function test\n");
            Parser p = new Parser(lexer.getTokens());
            p.Parse();
        });
        Assertions.assertEquals("Function must have parenthesis for proper parameter declaration! Ex: function myFunction(a){}", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("function test)");
            Parser p = new Parser(lexer.getTokens());
            p.Parse();
        });
        Assertions.assertEquals("Function must have parenthesis for proper parameter declaration! Ex: function myFunction(a){}", thrown.getMessage());
        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("function t(t,)");
            Parser p = new Parser(lexer.getTokens());
            p.Parse();
        });
        Assertions.assertEquals("Cannot have comma at the end of a parameter list without properly declaring parameters! Ex: function myFunc(a,b,c)", thrown.getMessage());

    }

    @Test
    public void BottomLevelAndLValue() throws Exception{
        Lexer l = new Lexer("++$b");
        Parser p = new Parser(l.getTokens());
        Node ref = new AssignmentNode(new OperationNode(new VariableReferenceNode("b"), OperationNode.PossibleOperations.DOLLAR), new OperationNode(new OperationNode(new VariableReferenceNode("b"), OperationNode.PossibleOperations.DOLLAR), OperationNode.PossibleOperations.PREINC));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("++a");
        p = new Parser(l.getTokens());
        VariableReferenceNode v = new VariableReferenceNode("a");
        ref = new OperationNode(v, OperationNode.PossibleOperations.PREINC);
        Node op = p.ParseOperation().get();
        assertEquals(ref.toString(), op.toString());
        l = new Lexer("(++d)");
        p = new Parser(l.getTokens());
        ref = new OperationNode(new VariableReferenceNode("d"), OperationNode.PossibleOperations.PREINC);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("-5");
        p = new Parser(l.getTokens());
        ref = new OperationNode(new ConstantNode(5), OperationNode.PossibleOperations.UNARYNEG);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("`[abc]`");
        p = new Parser(l.getTokens());
        assertEquals("[abc]", p.ParseOperation().get().toString());
        l = new Lexer("e[++b]");
        p = new Parser(l.getTokens());
        Optional<Node> refPart = Optional.of(new OperationNode(new VariableReferenceNode("b"), OperationNode.PossibleOperations.PREINC));
        ref = new VariableReferenceNode("e", refPart);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("$7");
        p = new Parser(l.getTokens());
        ref = new OperationNode(new ConstantNode(7), OperationNode.PossibleOperations.DOLLAR);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
    }


    @Test
    public void ParseBottomLevelAndLValueErrors() throws Exception{
        Exception thrown = assertThrows(Exception.class, () -> {
            Lexer lexer = new Lexer("a[");
            Parser p = new Parser(lexer.getTokens());
            p.ParseOperation();
        });
        Assertions.assertEquals("Error! Complete a full operation with a closing brace for the array! ex: VARIABLE[OPERATION]", thrown.getMessage());
        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("(a");
            Parser p = new Parser(lexer.getTokens());
            p.ParseOperation();
        });
        Assertions.assertEquals("Error! Must have closing parenthesis for an operation to occur!", thrown.getMessage());

    }

    @Test
    public void PostIncAndPostDec() throws Exception {
        Lexer l = new Lexer("a++");
        Parser p = new Parser(l.getTokens());
        OperationNode ref = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.POSTINC);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("a--");
        p = new Parser(l.getTokens());
        ref = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.POSTDEC);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        Exception thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("2++");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Incorrect syntax! Must be in the form of \"Variable++ or Variable--\"", thrown.getMessage());

    }

    @Test
    public void Exponents() throws Exception{
        Lexer l = new Lexer("a^b");
        Parser p = new Parser(l.getTokens());
        OperationNode ref = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.EXPONENT, new VariableReferenceNode("b"));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("a^2");
        p = new Parser(l.getTokens());
        ref = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.EXPONENT, new ConstantNode(2));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("2^a");
        p = new Parser(l.getTokens());
        ref = new OperationNode(new ConstantNode(2), OperationNode.PossibleOperations.EXPONENT, new VariableReferenceNode("a"));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("2^2");
        p = new Parser(l.getTokens());
        ref = new OperationNode(new ConstantNode(2), OperationNode.PossibleOperations.EXPONENT, new ConstantNode(2));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("(a^2)^2");
        p = new Parser(l.getTokens());
        OperationNode leftValue = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.EXPONENT, new ConstantNode(2));
        ref = new OperationNode(leftValue, OperationNode.PossibleOperations.EXPONENT, new ConstantNode(2));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("a^(a^2)");
        p = new Parser(l.getTokens());
        OperationNode rightValue = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.EXPONENT, new ConstantNode(2));
        ref = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.EXPONENT, rightValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("(a^2)^(a^2)");
        p = new Parser(l.getTokens());
        ref = new OperationNode(leftValue, OperationNode.PossibleOperations.EXPONENT, rightValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("a^a^a");
        p = new Parser(l.getTokens());
        rightValue = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.EXPONENT, new VariableReferenceNode("a"));
        ref = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.EXPONENT, rightValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        Exception thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("^a");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Must have a complete exponential expression! ex: EXPRESSION^EXPRESSION", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("a^");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Must have a complete exponential expression! ex: EXPRESSION^EXPRESSION", thrown.getMessage());

    }

    @Test
    public void ParseExpression() throws Exception{
        Lexer l = new Lexer("(a+b)");
        Parser p = new Parser(l.getTokens());
        OperationNode ref = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.ADD, new VariableReferenceNode("b"));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("(a+2)-c");
        p = new Parser(l.getTokens());
        OperationNode leftValue = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.ADD, new ConstantNode(2));
        ref = new OperationNode(leftValue, OperationNode.PossibleOperations.SUBTRACT, new VariableReferenceNode("c"));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("(a+b)-(c+5)");
        p = new Parser(l.getTokens());
        leftValue = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.ADD, new VariableReferenceNode("b"));
        OperationNode rightValue = new OperationNode(new VariableReferenceNode("c"), OperationNode.PossibleOperations.ADD, new ConstantNode(5));
        ref = new OperationNode(leftValue, OperationNode.PossibleOperations.SUBTRACT, rightValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        Exception thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("(a+t) + ()");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Invalid Term! Must be in the form of \"TERM +|- TERM\"", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("(a+t) +");
            Parser p3 = new Parser(lexer.getTokens());
            p3.ParseOperation();
        });
        Assertions.assertEquals("Invalid Term! Must be in the form of \"TERM +|- TERM\"", thrown.getMessage());
        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("(a");
            Parser p3 = new Parser(lexer.getTokens());
            p3.ParseOperation();
        });
        Assertions.assertEquals("Error! Must have closing parenthesis for an operation to occur!", thrown.getMessage());

    }

    @Test
    public void ParseTerm() throws Exception{
        Lexer l = new Lexer("a * b");
        Parser p = new Parser(l.getTokens());
        OperationNode ref = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.MULTIPLY, new VariableReferenceNode("b"));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("a/b");
        p = new Parser(l.getTokens());
        ref = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.DIVIDE, new VariableReferenceNode("b"));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("a * 2");
        p = new Parser(l.getTokens());
        ref = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.MULTIPLY, new ConstantNode(2));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("2 / a");
        p = new Parser(l.getTokens());
        ref = new OperationNode(new ConstantNode(2), OperationNode.PossibleOperations.DIVIDE, new VariableReferenceNode("a"));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("(a*2)*a");
        p = new Parser(l.getTokens());
        OperationNode leftValue = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.MULTIPLY, new ConstantNode(2));
        ref = new OperationNode(leftValue, OperationNode.PossibleOperations.MULTIPLY, new VariableReferenceNode("a"));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("a*(a*2)");
        p = new Parser(l.getTokens());
        OperationNode rightValue = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.MULTIPLY, new ConstantNode(2));
        ref = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.MULTIPLY, rightValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("(a*2)/(2/a)");
        p = new Parser(l.getTokens());
        leftValue = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.MULTIPLY, new ConstantNode(2));
        rightValue = new OperationNode(new ConstantNode(2), OperationNode.PossibleOperations.DIVIDE, new VariableReferenceNode("a"));
        ref = new OperationNode(leftValue, OperationNode.PossibleOperations.DIVIDE, rightValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());

        Exception thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("x/");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Invalid Term! Must be in the form of \"FACTOR *|/ FACTOR\"", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("*e");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Invalid Term! Must be in the form of \"FACTOR *|/ FACTOR\"", thrown.getMessage());

    }

    @Test
    public void ParseConcatination() throws Exception{
        // Tilde
        Lexer l = new Lexer("\"test\" \"test\"");
        Parser p = new Parser(l.getTokens());
        OperationNode ref = new OperationNode(new ConstantNode("test"), OperationNode.PossibleOperations.CONCATENATION, new ConstantNode("test"));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        // REGEXP
        l = new Lexer("\"test\" \"test\" \"test\"");
        p = new Parser(l.getTokens());
        OperationNode rightValue = new OperationNode(new ConstantNode("test"), OperationNode.PossibleOperations.CONCATENATION, new ConstantNode("test"));
        ref = new OperationNode(new ConstantNode("test"), OperationNode.PossibleOperations.CONCATENATION, rightValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
    }

    @Test
    public void ParseBooleanCompares() throws Exception{
        //a < 2
        Lexer l = new Lexer("(a+2) < 2");
        Parser p = new Parser(l.getTokens());
        OperationNode lValue = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.ADD, new ConstantNode(2));
        OperationNode ref = new OperationNode(lValue, OperationNode.PossibleOperations.LT, new ConstantNode(2));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        // 2 <= a
        l = new Lexer("2 <= (a/2)");
        p = new Parser(l.getTokens());
        OperationNode rValue = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.DIVIDE, new ConstantNode(2));
        ref = new OperationNode(new ConstantNode(2), OperationNode.PossibleOperations.LE, rValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        // 2 != a
        l = new Lexer("2 != a + 2");
        p = new Parser(l.getTokens());
        rValue = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.ADD, new ConstantNode(2));
        ref = new OperationNode(new ConstantNode(2), OperationNode.PossibleOperations.NE, rValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        // a == 2
        l = new Lexer("a == 2");
        p = new Parser(l.getTokens());
        ref = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.EQ, new ConstantNode(2));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        // 2 > a
        l = new Lexer("a > 2");
        p = new Parser(l.getTokens());
        ref = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.GT, new ConstantNode(2));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        // a <= 2
        l = new Lexer("a <= 2");
        p = new Parser(l.getTokens());
        ref = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.LE, new ConstantNode(2));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        // Check for AND
        l = new Lexer("(n == 2) && (a == 1)");
        p = new Parser(l.getTokens());
        lValue = new OperationNode(new VariableReferenceNode("n"), OperationNode.PossibleOperations.EQ, new ConstantNode(2));
        rValue = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.EQ, new ConstantNode(1));
        ref = new OperationNode(lValue, OperationNode.PossibleOperations.AND, rValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        // Check for OR
        l = new Lexer("(n == 2) || (a == 1)");
        p = new Parser(l.getTokens());
        ref = new OperationNode(lValue, OperationNode.PossibleOperations.OR, rValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        Exception thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("a < ");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Must have another value in order to make a comparison! Format must be (EXRESSION) (COMPARISON EXPRESSION) (EXPRESSION) ex: (x < 3)", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer(" < 3");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Must have another value in order to make a comparison! Format must be (EXRESSION) (COMPARISON EXPRESSION) (EXPRESSION) ex: (x < 3)", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("~ b");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Must have a valid satatement to match expressions! ex: EXPRESSION !~ | ~ EXPRESSION", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("a !~");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Must have a valid satatement to match expressions! ex: EXPRESSION !~ | ~ EXPRESSION", thrown.getMessage());
        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("&& a");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Must have a valid AND statement! ex: EXP && EXP", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("a && ");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Must have a valid AND statement! ex: EXP && EXP", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("|| a");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Invalid OR statement! ex: EXPRESSION || EXPRESSION", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("a || ");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Invalid OR statement! ex: EXPRESSION || EXPRESSION", thrown.getMessage());
    }

    @Test
    public void ParseMatch() throws Exception{
        Lexer l = new Lexer("a~b");
        Parser p = new Parser(l.getTokens());
        OperationNode ref = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.MATCH, new VariableReferenceNode("b"));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("a!~b");
        p = new Parser(l.getTokens());
        ref = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.NOTMATCH, new VariableReferenceNode("b"));
        assertEquals(ref.toString(), p.ParseOperation().get().toString());

    }

    @Test
    public void INOperations() throws Exception{
        // n in a[i++]
        Lexer l = new Lexer("n in a[i++]");
        Parser p = new Parser(l.getTokens());
        VariableReferenceNode rValue = new VariableReferenceNode("a", Optional.of(new OperationNode(new VariableReferenceNode("i"), OperationNode.PossibleOperations.POSTINC)));
        OperationNode ref = new OperationNode(new VariableReferenceNode("n"), OperationNode.PossibleOperations.IN, rValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        // n in a[2]
        l = new Lexer("n in a[2]");
        p = new Parser(l.getTokens());
        rValue = new VariableReferenceNode("a", Optional.of(new ConstantNode(2)));
        ref = new OperationNode(new VariableReferenceNode("n"), OperationNode.PossibleOperations.IN, rValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("2 in a[2]");
        p = new Parser(l.getTokens());
        ref = new OperationNode(new ConstantNode(2), OperationNode.PossibleOperations.IN, rValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("2 in a[2][n]");
        p = new Parser(l.getTokens());
        rValue = new VariableReferenceNode("a", Optional.of(new ConstantNode(2)), Optional.of(new VariableReferenceNode("n")));
        ref = new OperationNode(new ConstantNode(2), OperationNode.PossibleOperations.IN, rValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("n in a[2][2]");
        p = new Parser(l.getTokens());
        rValue = new VariableReferenceNode("a", Optional.of(new ConstantNode(2)), Optional.of(new ConstantNode(2)));
        ref = new OperationNode(new VariableReferenceNode("n"), OperationNode.PossibleOperations.IN, rValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        Exception thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer(" in a[2]");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();

        });
        Assertions.assertEquals("Incomplete IN condition! Must be in the form of EXPRESSION IN ARRAY", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("2 in ");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Incomplete IN condition! Must be in the form of EXPRESSION IN ARRAY", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("2 in 2");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Not an array! Must be a valid array! Must be in the form of EXPRESSION IN ARRAY", thrown.getMessage());

    }


    @Test
    public void ParseTernary() throws Exception{
        Lexer l = new Lexer("n==2? (1+1): m");
        Parser p = new Parser(l.getTokens());
        Node Condition = new OperationNode(new VariableReferenceNode("n"), OperationNode.PossibleOperations.EQ, new ConstantNode(2));
        Node Consequent = new OperationNode(new ConstantNode(1), OperationNode.PossibleOperations.ADD, new ConstantNode(1));
        Node Alternate = new VariableReferenceNode("m");
        TernaryNode ref = new TernaryNode(Condition, Consequent, Alternate);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        l = new Lexer("2 != e? n: 2+m");
        p = new Parser(l.getTokens());
        Condition = new OperationNode(new ConstantNode(2), OperationNode.PossibleOperations.NE, new VariableReferenceNode("e"));
        Consequent = new VariableReferenceNode("n");
        Alternate = new OperationNode(new ConstantNode(2), OperationNode.PossibleOperations.ADD, new VariableReferenceNode("m"));
        ref = new TernaryNode(Condition, Consequent, Alternate);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        Exception thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("? (2):(2)");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Invalid TernaryStatement! Must be in the form of EXP? EXP:EXP", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("33? :(2)");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Invalid TernaryStatement! Must be in the form of EXP? EXP:EXP", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("33? :(2) :");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Invalid TernaryStatement! Must be in the form of EXP? EXP:EXP", thrown.getMessage());

    }

    @Test
    public void ParseAssignment() throws Exception {
        Lexer l = new Lexer("c += 2");
        Parser p = new Parser(l.getTokens());
        VariableReferenceNode lValue = new VariableReferenceNode("c");
        OperationNode rValue = new OperationNode(lValue, OperationNode.PossibleOperations.ADD, new ConstantNode(2));
        AssignmentNode ref = new AssignmentNode(lValue, rValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        // Modulo
        l = new Lexer("c %= 6");
        p = new Parser(l.getTokens());
        rValue = new OperationNode(lValue, OperationNode.PossibleOperations.MODULO, new ConstantNode(6));
        ref = new AssignmentNode(lValue, rValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        // Times
        l = new Lexer("c *= g");
        p = new Parser(l.getTokens());
        rValue = new OperationNode(lValue, OperationNode.PossibleOperations.MULTIPLY, new VariableReferenceNode("g"));
        ref = new AssignmentNode(lValue, rValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        // Divide
        l = new Lexer("c /= f");
        p = new Parser(l.getTokens());
        rValue = new OperationNode(lValue, OperationNode.PossibleOperations.DIVIDE, new VariableReferenceNode("f"));
        ref = new AssignmentNode(lValue, rValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        // Plus
        l = new Lexer("c += (200^f)");
        p = new Parser(l.getTokens());
        OperationNode innerRValue = new OperationNode(new ConstantNode(200), OperationNode.PossibleOperations.EXPONENT, new VariableReferenceNode("f"));
        rValue = new OperationNode(lValue, OperationNode.PossibleOperations.ADD, innerRValue);
        ref = new AssignmentNode(lValue, rValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        // Minus
        l = new Lexer("c -= h");
        p = new Parser(l.getTokens());
        rValue = new OperationNode(lValue, OperationNode.PossibleOperations.SUBTRACT, new VariableReferenceNode("h"));
        ref = new AssignmentNode(lValue, rValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        // Assign
        l = new Lexer("c = 2");
        p = new Parser(l.getTokens());
        rValue = new OperationNode(lValue, OperationNode.PossibleOperations.ASSIGN, new ConstantNode(2));
        ref = new AssignmentNode(lValue, rValue);
        assertEquals(ref.toString(), p.ParseOperation().get().toString());
        Exception thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer(" *= n");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Assignment invalid! Must be in the form of VARIABLE OPERATION '=' EXPRESSION", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("s *= ");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Assignment invalid! Must be in the form of VARIABLE OPERATION '=' EXPRESSION", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("2 *= ");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseOperation();
        });
        Assertions.assertEquals("Assignment invalid! Must be in the form of VARIABLE OPERATION '=' EXPRESSION", thrown.getMessage());

    }

    // Test for incomplete array declarations
    @Test
    public void ParseArrayErrors(){
        Exception thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("a[");
            Parser p = new Parser(lexer.getTokens());
            p.ParseOperation();
        });
        Assertions.assertEquals("Error! Complete a full operation with a closing brace for the array! ex: VARIABLE[OPERATION]", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("a[2][");
            Parser p = new Parser(lexer.getTokens());
            p.ParseOperation();
        });
        Assertions.assertEquals("Must complete 2d array with a closing brace! ex: VARIABLE[OPERATION][OPERATION]", thrown.getMessage());
    }

    @Test public void ParseIf() throws Exception{
        lexer = new Lexer("if(a == 2)\nmyFunction()");
        Parser p = new Parser(lexer.getTokens());
        OperationNode Condition = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.EQ, new ConstantNode(2));
        FunctionCallNode funcCall = new FunctionCallNode("myFunction");
        BlockNode content = new BlockNode(funcCall);
        IfNode reference = new IfNode(Condition, content);
        assertEquals(reference.toString(), p.ParseStatement().get().toString());
        lexer  = new Lexer("if(a==2)\n{\nmyFunction()}\nelse if(a == 1)\nsuperFunction()");
        p = new Parser(lexer.getTokens());
        OperationNode Condition2 = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.EQ, new ConstantNode(1));
        FunctionCallNode newFuncCall = new FunctionCallNode("superFunction");
        BlockNode newContent = new BlockNode(newFuncCall);
        IfNode elseIfReference1 = new IfNode(Condition2, newContent);
        reference = new IfNode(Condition, content, Optional.of(elseIfReference1));
        assertEquals(reference.toString(), p.ParseStatement().get().toString());
        lexer = new Lexer("if(a==2)\n{myFunction()}else if(a == 1)\n{superFunction()}else if(a==1)\n{myFunction()}else\nsuperFunction()");
        p = new Parser(lexer.getTokens());
        IfNode elseReference = new IfNode(newContent);
        IfNode elseIfReference2 = new IfNode(Condition2, content,Optional.of(elseReference));
        elseIfReference1 = new IfNode(Condition2, newContent, Optional.of(elseIfReference2));
        reference = new IfNode(Condition, content, Optional.of(elseIfReference1));
        assertEquals(reference.toString(), p.ParseStatement().get().toString());
        Exception thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("if()");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseStatement();
        });
        Assertions.assertEquals("If must have a Condition! Ex: If(EXPR){STATEMENT}", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {
            lexer = new Lexer("if(a == 1)");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseStatement();
        });
        Assertions.assertEquals("Error! File ended before program could parse block!", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {

            lexer = new Lexer("else\na = 1");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseStatement();
        });
        Assertions.assertEquals("Cannot call 'else' and 'else if' outside of any initial if statement!", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {

            lexer = new Lexer("else if\na = 1");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseStatement();
        });
        Assertions.assertEquals("Cannot call 'else' and 'else if' outside of any initial if statement!", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {

            lexer = new Lexer("if(i == 1)\ndoSomething()\nelse if(r == 3){a = 1}\nelse(f == 2){}");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseStatement();
        });
        Assertions.assertEquals("Else statements cannot have parameters!", thrown.getMessage());
    }

    @Test
    public void ParseFor() throws Exception{
        lexer = new Lexer("for(i = 0; i < 3; i++){i = 0}");
        Parser p = new Parser(lexer.getTokens());
        VariableReferenceNode i = new VariableReferenceNode("i");
        ConstantNode zero = new ConstantNode(0);
        ConstantNode three = new ConstantNode(3);
        OperationNode Statement = new OperationNode(i, OperationNode.PossibleOperations.ASSIGN, zero);
        AssignmentNode forPart1 = new AssignmentNode(i,Statement);
        OperationNode forPart2 = new OperationNode(i, OperationNode.PossibleOperations.LT, three);
        OperationNode forPart3 = new OperationNode(i, OperationNode.PossibleOperations.POSTINC);
        LinkedList<Node> Params = new LinkedList<Node>();
        Params.add(forPart1);
        Params.add(forPart2);
        Params.add(forPart3);
        ForNode ref = new ForNode(Params, new BlockNode(forPart1));
        assertEquals(ref.toString(), p.ParseStatement().get().toString());
        lexer = new Lexer("for(i in a[2]){i = 0}");
        p = new Parser(lexer.getTokens());
        VariableReferenceNode a = new VariableReferenceNode("a", Optional.of(new ConstantNode(2)));
        OperationNode inParams = new OperationNode(i, OperationNode.PossibleOperations.IN, a);
        ForEachNode refFE = new ForEachNode(inParams, new BlockNode(forPart1));
        assertEquals(refFE.toString(), p.ParseStatement().get().toString());
        Exception thrown = assertThrows(Exception.class, () -> {

            lexer = new Lexer("for(){}");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseStatement();
        });
        Assertions.assertEquals("For loop params cannot be empty!", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {

            lexer = new Lexer("for(i == 1; a++){}");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseStatement();
        });
        Assertions.assertEquals("Error! For loop params must have a valid condition! ex: for(expr;expr;expr){}", thrown.getMessage());
        thrown = assertThrows(Exception.class, () -> {

            lexer = new Lexer("for i == 1; a++){}");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseStatement();
        });
        Assertions.assertEquals("Improper for loop format! For loop must have a structure of for(EXP;EXP;EXP|EXP in ARR[])", thrown.getMessage());
    }

    @Test
    public void ParseWhileAndDoWhile() throws Exception{
        lexer = new Lexer("while(a == 2)\nmyFunction()");
        Parser p = new Parser(lexer.getTokens());
        OperationNode Condition = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.EQ, new ConstantNode(2));
        FunctionCallNode funcCall = new FunctionCallNode("myFunction");
        BlockNode content = new BlockNode(funcCall);
        WhileNode reference = new WhileNode(Condition, content);
        assertEquals(reference.toString(), p.ParseStatement().get().toString());
        lexer = new Lexer("do{myFunction()}\nwhile(a == 2)");
        p = new Parser(lexer.getTokens());
        DoWhileNode DoWhileReference = new DoWhileNode(Condition, content);
        assertEquals(DoWhileReference.toString(), p.ParseStatement().get().toString());
        Exception thrown = assertThrows(Exception.class, () -> {

            lexer = new Lexer("while(){}");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseStatement();
        });
        Assertions.assertEquals("Must be a valid WHILE loop! EX: While(EXP){}", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {

            lexer = new Lexer("do{} (a + 1)");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseStatement();
        });
        Assertions.assertEquals("Must have a while at the end of a do-while statement! ex: do{} while(expr)", thrown.getMessage());

        thrown = assertThrows(Exception.class, () -> {

            lexer = new Lexer("do{}while()");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseStatement();
        });
        Assertions.assertEquals("Must have a condition for a do-while loop! Ex: do{} while(EXPR)", thrown.getMessage());
    }

    @Test
    public void ParseContinueBreak() throws Exception{
        lexer = new Lexer("break");
        Parser p = new Parser(lexer.getTokens());
        assertEquals((new BreakNode()).toString(), (p.ParseStatement().get().toString()));
        lexer = new Lexer("continue");
        p = new Parser(lexer.getTokens());
        assertEquals((new ContinueNode()).toString(), (p.ParseStatement().get().toString()));
    }

    @Test
    public void ParseDeleteReturn() throws Exception{
        lexer = new Lexer("return a+1");
        Parser p = new Parser(lexer.getTokens());
        OperationNode refContent = new OperationNode(new VariableReferenceNode("a"), OperationNode.PossibleOperations.ADD, new ConstantNode(1));
        ReturnNode ref = new ReturnNode(refContent);
        assertEquals(ref.toString(), p.ParseStatement().get().toString());
        lexer = new Lexer("delete a[test]");
        p = new Parser(lexer.getTokens());
        DeleteNode refDelete = new DeleteNode(new VariableReferenceNode("a", Optional.of(new VariableReferenceNode("test"))));
        assertEquals(refDelete.toString(), p.ParseStatement().get().toString());
        Exception thrown = assertThrows(Exception.class, () -> {

            lexer = new Lexer("return \n");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseStatement();
        });
        Assertions.assertEquals("Must return a value for a function! Ex: return (expr)", thrown.getMessage());
        thrown = assertThrows(Exception.class, () -> {

            lexer = new Lexer("delete \n");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseStatement();
        });
        Assertions.assertEquals("Must have a statement accompanying the Delete statement! Ex: 'delete: EXPR'", thrown.getMessage());
        thrown = assertThrows(Exception.class, () -> {

            lexer = new Lexer("delete 2");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseStatement();
        });
        Assertions.assertEquals("Target must be an array element! Ex: delete a[\"test\"]", thrown.getMessage());
    }

    @Test
    public void ParseFunctionCalls() throws Exception{
        lexer = new Lexer("myFunction()");
        Parser p = new Parser(lexer.getTokens());
        FunctionCallNode ref = new FunctionCallNode("myFunction");
        assertEquals(ref.toString(), p.ParseStatement().get().toString());
        lexer = new Lexer("myFunction(1)");
        p = new Parser(lexer.getTokens());
        LinkedList<Node> params = new LinkedList<Node>();
        params.add(new ConstantNode(1));
        ref = new FunctionCallNode("myFunction", params);
        assertEquals(ref.toString(), p.ParseStatement().get().toString());
        params.clear();
        lexer = new Lexer("myFunction(1,n,3,5)");
        p = new Parser(lexer.getTokens());
        params.add(new ConstantNode(1));
        params.add(new VariableReferenceNode("n"));
        params.add(new ConstantNode(3));
        params.add(new ConstantNode(5));
        ref = new FunctionCallNode("myFunction", params);
        assertEquals(ref.toString(), p.ParseStatement().get().toString());
        params.clear();
        lexer = new Lexer("myFunction(myNewFunction())");
        p = new Parser(lexer.getTokens());
        FunctionCallNode innerFunc = new FunctionCallNode("myNewFunction");
        params.add(innerFunc);
        ref = new FunctionCallNode("myFunction", params);
        assertEquals(ref.toString(), p.ParseStatement().get().toString());
        Exception thrown = assertThrows(Exception.class, () -> {

            lexer = new Lexer("myFunction(a,)");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseStatement();
        });
        Assertions.assertEquals("Incomplete Function Call!", thrown.getMessage());

    }

    @Test
    public void ParseBuiltInMethods() throws Exception {
        lexer = new Lexer("next");
        Parser parser = new Parser(lexer.getTokens());
        FunctionCallNode ref = new FunctionCallNode("next");
        assertEquals(ref.toString(), parser.ParseStatement().get().toString());
        lexer = new Lexer("nextfile");
        parser = new Parser(lexer.getTokens());
        ref = new FunctionCallNode("nextfile");
        assertEquals(ref.toString(), parser.ParseStatement().get().toString());
        lexer = new Lexer("exit");
        parser = new Parser(lexer.getTokens());
        ref = new FunctionCallNode("exit", Optional.empty());
        assertEquals(ref.toString(), parser.ParseStatement().get().toString());
        lexer = new Lexer("exit 0");
        parser = new Parser(lexer.getTokens());
        ref = new FunctionCallNode("exit", Optional.of(new ConstantNode(0)));
        assertEquals(ref.toString(), parser.ParseStatement().get().toString());
        lexer = new Lexer("exit $0");
        parser = new Parser(lexer.getTokens());
        OperationNode refInside = new OperationNode(new ConstantNode(0), OperationNode.PossibleOperations.DOLLAR);
        ref = new FunctionCallNode("exit", Optional.of(refInside));
        assertEquals(ref.toString(), parser.ParseStatement().get().toString());
        // exit has wrong param type/has more than two params
        Exception thrown = assertThrows(Exception.class, () -> {

            lexer = new Lexer("exit variable");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseStatement();
        });
        Assertions.assertEquals("Error! 'exit' cannot accept any other condition besides an int! (Or none) Example: exit Optional[int]", thrown.getMessage());
    }

    @Test
    public void prints() throws Exception{
        lexer = new Lexer("print fOne, fTwo, fThree");
        Parser parser = new Parser(lexer.getTokens());
        LinkedList<Node> refParams = new LinkedList<Node>();
        refParams.add(new VariableReferenceNode("fOne"));
        refParams.add(new VariableReferenceNode("fTwo"));
        refParams.add(new VariableReferenceNode("fThree"));
        FunctionCallNode ref = new FunctionCallNode("print", refParams);
        assertEquals(ref.toString(), parser.ParseStatement().get().toString());
        lexer = new Lexer("printf \"%2f\", fOne, fTwo, fThree");
        parser = new Parser(lexer.getTokens());
        refParams.clear();
        refParams.add(new ConstantNode("%2f"));
        refParams.add(new VariableReferenceNode("fOne"));
        refParams.add(new VariableReferenceNode("fTwo"));
        refParams.add(new VariableReferenceNode("fThree"));
        ref = new FunctionCallNode("printf", refParams);
        assertEquals(ref.toString(), parser.ParseStatement().get().toString());
        //print and printline is empty
        Exception thrown = assertThrows(Exception.class, () -> {

            lexer = new Lexer("print");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseStatement();
        });
        Assertions.assertEquals("Error! Cannot use print/printf without statements!", thrown.getMessage());
        thrown = assertThrows(Exception.class, () -> {

            lexer = new Lexer("printf");
            Parser p2 = new Parser(lexer.getTokens());
            p2.ParseStatement();
        });
        Assertions.assertEquals("Error! Cannot use print/printf without statements!", thrown.getMessage());
    }

    private Path filePath;
    private List<String> temp;
    public void fileMaker(String[] s) throws IOException{
        LinkedList<String> strings = new LinkedList<String>();
        if(s.length == 0) {
            strings.add("Hello this is line 1");
            strings.add("Hello this is line 2");
            strings.add("Hello this is line 3");
            strings.add("Hello this is line 4");
            strings.add("Hello this is line 5");
        }
        else{
            for(var i : s)
                strings.add(i);
        }
        filePath = Paths.get("input.txt");
        temp = Files.readAllLines(filePath);
        FileWriter fileWriter = new FileWriter("input.txt");
        fileWriter.write("");
        for (String line : strings) 
            fileWriter.write(line + "\n"); // Write each line to the file with a newline character
        fileWriter.close();
    }

    public void fileRestore() throws IOException {
        FileWriter fileWriter = new FileWriter("input.txt");
        fileWriter.write("");
        for (String line : temp)
            fileWriter.write(line + "\n"); // Write each line to the file with a newline character
        fileWriter.close();
    }
    @Test
    public void LineManager() throws Exception, IOException {
       fileMaker(new String[0]);
       interpreter =  new Interpreter(new ProgramNode(), Optional.of(filePath));
       interpreter.Manager.SplitAndAssign();
        assertEquals( "5", interpreter.GlobalVariables.get("NF").toString());
        assertEquals( "Hello", interpreter.GlobalVariables.get("$1").toString());
        assertEquals( "1", interpreter.GlobalVariables.get("$5").toString());
       for(int i = 1; i < 5; i++){
           assertEquals( String.valueOf(i), interpreter.GlobalVariables.get("NR").toString());
           assertEquals( "Hello this is line " + String.valueOf(i), interpreter.GlobalVariables.get("$0").toString());
           assertEquals(interpreter.GlobalVariables.get("$5").toString(), String.valueOf(i));
           interpreter.Manager.SplitAndAssign();
       }
        assertEquals(false, interpreter.Manager.SplitAndAssign());
       fileRestore();
    }
    @Test
    public void SimpleBuiltInFunctionDefinitionNodes() throws Exception, IOException{
        String[] s = new String[]{"uppercase", "LOWERCASE", "substring"};
        fileMaker(s);
        interpreter =  new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.Manager.SplitAndAssign();
        HashMap<String, InterpreterDataType> input = new HashMap<String, InterpreterDataType>();
        input.put("content",interpreter.GlobalVariables.get("$0"));
        BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("toupper");
        assertEquals("UPPERCASE", node.Execute(input));
        interpreter.Manager.SplitAndAssign();
        input.put("content",interpreter.GlobalVariables.get("$0"));
        node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("tolower");
        assertEquals("lowercase", node.Execute(input));
        interpreter.Manager.SplitAndAssign();
        input.put("content",interpreter.GlobalVariables.get("$0"));
        input.put("start", new InterpreterDataType("3"));
        input.put("end", new InterpreterDataType("9"));
        node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("substr");
        node.Execute(input);
        assertEquals("string", node.Execute(input));
        fileRestore();
    }

    @Test
    public void SimpleBuiltInFunctionDefinitionNodeErrors() throws Exception, IOException{
        fileMaker(new String[0]);
        interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.Manager.SplitAndAssign();
        HashMap<String, InterpreterDataType> input = new HashMap<String, InterpreterDataType>();
        Exception thrown = assertThrows(Exception.class, () -> {
            BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("toupper");
            node.Execute(input);
        });
        Assertions.assertEquals("Error! toupper must have 1 parameter!", thrown.getMessage());
        thrown = assertThrows(Exception.class, () -> {
            BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("tolower");
            node.Execute(input);
        });
        Assertions.assertEquals("Error! tolower must have 1 parameter!", thrown.getMessage());
        thrown = assertThrows(Exception.class, () -> {
            input.put("string", new InterpreterDataType("test1"));
            input.put("string2", new InterpreterDataType("test2"));
            BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("toupper");
            node.Execute(input);
        });
        Assertions.assertEquals("Error! toupper must have 1 parameter!", thrown.getMessage());
        thrown = assertThrows(Exception.class, () -> {
            input.put("string", new InterpreterDataType("test1"));
            input.put("string2", new InterpreterDataType("test2"));
            BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("tolower");
            node.Execute(input);
        });
        Assertions.assertEquals("Error! tolower must have 1 parameter!", thrown.getMessage());
        input.clear();
        thrown = assertThrows(Exception.class, () ->{
            input.put("string", new InterpreterDataType("test"));
            input.put("start", new InterpreterDataType("-1"));
            input.put("end", new InterpreterDataType("1"));
            BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("substr");
            node.Execute(input);
        });
        Assertions.assertEquals("Warning! Substring is configured incorrectly!", thrown.getMessage());
        thrown = assertThrows(Exception.class, () ->{
            input.put("string", new InterpreterDataType("test"));
            input.put("start", new InterpreterDataType("1"));
            input.put("end", new InterpreterDataType("100"));
            BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("substr");
            node.Execute(input);
        });
        Assertions.assertEquals("Warning! Substring is configured incorrectly!", thrown.getMessage());
        thrown = assertThrows(Exception.class, () ->{
            input.put("string", new InterpreterDataType("test"));
            input.put("start", new InterpreterDataType("1"));
            input.put("end", new InterpreterDataType("2"));
            input.put("test", new InterpreterDataType("test"));
            BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("substr");
            node.Execute(input);
        });
        Assertions.assertEquals("Warning! The substr method must have 3 and strictly 3 parameters! Ex: substr(str, 1,2)", thrown.getMessage());
        fileRestore();
    }

    @Test
    public void testNextAndGetline() throws Exception, IOException {
        fileMaker(new String[0]);
        interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.Manager.SplitAndAssign();
        BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("next");
        HashMap<String, InterpreterDataType> input = new HashMap<String, InterpreterDataType>();
        node.Execute(input);
        fileRestore();
        assertEquals("2", interpreter.GlobalVariables.get("NR").toString());
        node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("getline");
        node.Execute(input);
        assertEquals("3", interpreter.GlobalVariables.get("NR").toString());
        input.put("variable", new InterpreterDataType("$1"));
        assertEquals("1", node.Execute(input));
        assertEquals("Hello this is line 4", interpreter.GlobalVariables.get("$1").toString());
        input.clear();
        input.put("variable", new InterpreterDataType("var"));
        assertEquals("0", node.Execute(input));
        assertEquals("Hello this is line 4", interpreter.GlobalVariables.get("$1").toString());
        fileRestore();
    }

    @Test
    public void testGsub() throws IOException, Exception {
        String[] list = new String[]{"hello there", "hellohowareyou", "yeshhowareyou"};
        fileMaker(list);
        interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.Manager.SplitAndAssign();
        BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("gsub");
        HashMap<String, InterpreterDataType> input = new HashMap<String, InterpreterDataType>();
        input.put("regexp", new InterpreterDataType("hello"));
        input.put("replacement", new InterpreterDataType("TEST"));
        assertEquals("1",node.Execute(input).toString());
        assertEquals("TEST there", interpreter.GlobalVariables.get("$0").toString());
        assertEquals("TEST", interpreter.GlobalVariables.get("$1").toString());
        interpreter.Manager.SplitAndAssign();
        input.put("targetToChange", new InterpreterDataType("hello"));
        assertEquals("1",node.Execute(input).toString());
        input.put("targetToChange", new InterpreterDataType("nothinghere"));
        assertEquals("0",node.Execute(input).toString());
        interpreter.Manager.SplitAndAssign();
        assertEquals("0", node.Execute(input));
        input.put("targetToChange", new InterpreterDataType("yeshelloyeshello"));
        node.Execute(input);
        assertEquals("yesTESTyesTEST", input.get("targetToChange").toString());
        input.put("targetToChange", new InterpreterDataType("test"));
        assertEquals("0", node.Execute(input));
        fileRestore();
    }

    @Test
    public void gsubErrors() throws IOException, Exception{
        fileMaker(new String[0]);
        interpreter = new Interpreter(new ProgramNode(), Optional.ofNullable(filePath));
        HashMap<String, InterpreterDataType> input = new HashMap<String, InterpreterDataType>();
        Exception thrown = assertThrows(Exception.class, () ->{
            BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("gsub");
            node.Execute(input);
        });
        Assertions.assertEquals("Invalid method call for gsub! must be in the form: gsub(regex, replacement, Optional[array])!", thrown.getMessage());
        thrown = assertThrows(Exception.class, () ->{
            input.put("regexp", new InterpreterDataType("test"));
            input.put("replacement", new InterpreterDataType("test2"));
            input.put("target", new InterpreterDataType("test3"));
            input.put("test", new InterpreterDataType("test4"));
            BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("gsub");
            node.Execute(input);
        });
        Assertions.assertEquals("Invalid method call for gsub! must be in the form: gsub(regex, replacement, Optional[array])!", thrown.getMessage());
        fileRestore();
    }
    @Test
    public void testSub() throws IOException, Exception{
        fileMaker(new String[]{"hello","hellohello","thislineshouldreturn0"});
        interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.Manager.SplitAndAssign();
        BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("sub");
        HashMap<String, InterpreterDataType> input = new HashMap<String, InterpreterDataType>();
        input.put("pattern", new InterpreterDataType("hello"));
        input.put("replacement", new InterpreterDataType("TEST"));
        assertEquals("1",node.Execute(input));
        assertEquals("TEST", interpreter.GlobalVariables.get("$0").toString());
        assertEquals("TEST", interpreter.GlobalVariables.get("$1").toString());
        interpreter.Manager.SplitAndAssign();
        assertEquals("1", node.Execute(input));
        assertEquals("TESThello", interpreter.GlobalVariables.get("$0").toString());
        interpreter.Manager.SplitAndAssign();
        assertEquals("0", node.Execute(input));
        input.put("targetToChange", new InterpreterDataType("yeshelloyeshello"));
        node.Execute(input);
        assertEquals("yesTESTyeshello", input.get("targetToChange").toString());
        input.put("targetToChange", new InterpreterDataType("test"));
        assertEquals("0", node.Execute(input));
        fileRestore();
    }

    @Test
    public void testSubErrors() throws Exception{
        fileMaker(new String[0]);
        interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.Manager.SplitAndAssign();
        HashMap<String, InterpreterDataType> input = new HashMap<String, InterpreterDataType>();
        Exception thrown = assertThrows(Exception.class, () ->{
            BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("sub");
            node.Execute(input);
        });
        Assertions.assertEquals("Invalid method call for sub! must be in the form: sub(regex, replacement, Optional[array])!", thrown.getMessage());
        thrown = assertThrows(Exception.class, () ->{
            BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("sub");
            input.put("target", new InterpreterDataType("test1"));
            input.put("regexp", new InterpreterDataType("test2"));
            input.put("replacement", new InterpreterDataType("test3"));
            input.put("test", new InterpreterDataType("test4"));
            node.Execute(input);
        });
        Assertions.assertEquals("Invalid method call for sub! must be in the form: sub(regex, replacement, Optional[array])!", thrown.getMessage());
        fileRestore();
    }

    @Test
    public void testMatch() throws Exception{
        fileMaker(new String[0]);
        interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        HashMap<String, InterpreterDataType> input = new HashMap<String, InterpreterDataType>();
        input.put("content", new InterpreterDataType("thiswillreturn0"));
        input.put("pattern", new InterpreterDataType("wontwork"));
        BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("match");
        assertEquals("0",node.Execute(input));
        input.clear();
        input.put("content", new InterpreterDataType("thiswillreturnsomethingelse"));
        input.put("pattern", new InterpreterDataType("return"));
        assertEquals("9", node.Execute(input));
        input.clear();
        input.put("content", new InterpreterDataType("thiswillretur"));
        input.put("pattern", new InterpreterDataType("return"));
        assertEquals("0", node.Execute(input));
        input.put("content", new InterpreterDataType("thiswillreturn"));
        input.put("pattern", new InterpreterDataType("return"));
        assertEquals("9", node.Execute(input));
        input.put("content", new InterpreterDataType("thiswillreturn"));
        input.put("pattern", new InterpreterDataType("this"));
        assertEquals("1", node.Execute(input));
        fileRestore();
    }

    @Test
    public void testMatchErrors() throws Exception{
        fileMaker(new String[0]);
        interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        HashMap<String, InterpreterDataType> input = new HashMap<String, InterpreterDataType>();
        Exception thrown = assertThrows(Exception.class, () ->{
            BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("match");
            node.Execute(input);
        });
        Assertions.assertEquals("Illegal match declaration! The match method must be in the form: match(string, pattern)!", thrown.getMessage());
        thrown = assertThrows(Exception.class, () ->{
            input.put("string", new InterpreterDataType("testest"));
            input.put("pattern", new InterpreterDataType("hello"));
            input.put("test", new InterpreterDataType("test"));
            BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("match");
            node.Execute(input);
        });
        Assertions.assertEquals("Illegal match declaration! The match method must be in the form: match(string, pattern)!", thrown.getMessage());
        fileRestore();
    }

    @Test
    public void testSplit() throws Exception{
        fileMaker(new String[0]);
        interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        HashMap<String, InterpreterDataType> input = new HashMap<String, InterpreterDataType>();
        BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("split");
        input.put("target", new InterpreterDataType("This/is/a/sentence/to/separate"));
        input.put("separator", new InterpreterDataType("/"));
        node.Execute(input);
        assertEquals("(This,is,a,sentence,to,separate)", input.get("arrayToPost").toString());
        input.clear();
        input.put("target", new InterpreterDataType("This/is/a/sentence/to/separate"));
        input.put("separator", new InterpreterDataType("-"));
        node.Execute(input);
        assertEquals("(This/is/a/sentence/to/separate)", input.get("arrayToPost").toString());
        fileRestore();
    }

    @Test
    public void testSplitErrors() throws Exception{
        fileMaker(new String[0]);
        interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        HashMap<String, InterpreterDataType> input = new HashMap<String, InterpreterDataType>();
        Exception thrown = assertThrows(Exception.class, () ->{
            BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("split");
            node.Execute(input);
        });
        Assertions.assertEquals("Incorrect method call for split! It must be in the form of: split(target, array, separator)!", thrown.getMessage());
        thrown = assertThrows(Exception.class, () ->{
            input.put("target", new InterpreterDataType("This/is/a/sentence/to/separate"));
            input.put("separator", new InterpreterDataType("/"));
            input.put("array", new InterpreterDataType());
            input.put("test", new InterpreterDataType("test"));
            BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("split");
            node.Execute(input);
        });
        Assertions.assertEquals("Incorrect method call for split! It must be in the form of: split(target, array, separator)!", thrown.getMessage());
        fileRestore();

    }

    @Test
    public void testIndex() throws Exception{
        fileMaker(new String[0]);
        interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.Manager.SplitAndAssign();
        HashMap<String, InterpreterDataType> input = new HashMap<String, InterpreterDataType>();
        BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("index");
        input.put("string", new InterpreterDataType("thiswillhaveasubstring"));
        input.put("substring", new InterpreterDataType("will"));
        assertEquals("5",node.Execute(input));
        input.clear();
        input.put("string", new InterpreterDataType("thiswillnothaveasubstring"));
        input.put("substring", new InterpreterDataType("wont"));
        assertEquals("0",node.Execute(input));
        input.clear();
        input.put("string", new InterpreterDataType("thiswillhaveasubstringattheveryend"));
        input.put("substring", new InterpreterDataType("end"));
        assertEquals("32",node.Execute(input));
        input.put("string", new InterpreterDataType("thiswillendinzero"));
        input.put("substring", new InterpreterDataType("zeroes"));
        assertEquals("0",node.Execute(input));
        fileRestore();
    }

    @Test
    public void testIndexErrors() throws Exception{
        fileMaker(new String[0]);
        interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.Manager.SplitAndAssign();
        HashMap<String, InterpreterDataType> input = new HashMap<String, InterpreterDataType>();
        Exception thrown = assertThrows(Exception.class, () ->{
            BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("index");
            node.Execute(input);
        });
        Assertions.assertEquals("Illegal function call of index! It must only be called like index(string, substring)!", thrown.getMessage());
        thrown = assertThrows(Exception.class, () ->{
            input.put("content", new InterpreterDataType("test"));
            input.put("substring", new InterpreterDataType("test"));
            input.put("test", new InterpreterDataType("test"));
            BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("index");
            node.Execute(input);
        });
        Assertions.assertEquals("Illegal function call of index! It must only be called like index(string, substring)!", thrown.getMessage());
        fileRestore();
    }
    @Test
    public void testLength() throws Exception{
        fileMaker(new String[0]);
        interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.Manager.SplitAndAssign();
        BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("length");
        HashMap<String, InterpreterDataType> input = new HashMap<String, InterpreterDataType>();
        input.put("string", new InterpreterDataType("testString"));
        assertEquals("10", node.Execute(input));
        input.clear();
        input.put("string", new InterpreterDataType(""));
        assertEquals("0", node.Execute(input));
        Exception thrown = assertThrows(Exception.class, () ->{
            input.clear();
            node.Execute(input);
        });
        Assertions.assertEquals("Error! Length must be explicitly called with a param! Ex: length(string)", thrown.getMessage());
        thrown = assertThrows(Exception.class, () ->{
            input.put("string", new InterpreterDataType("test"));
            input.put("test", new InterpreterDataType("test"));
            node.Execute(input);
        });
        Assertions.assertEquals("Error! Length must be explicitly called with a param! Ex: length(string)", thrown.getMessage());

        fileRestore();
    }
    @Test
    public void testSprintf() throws Exception{
        fileMaker(new String[]{"Bob,80", "Joe,90", "Sherry,85", "Jack,70"});
        lexer = new Lexer("BEGIN{FS=\",\"}{i = i sprintf(\"Name: %-10s Grade: %02d\", $1, $2)}");
        Parser p = new Parser(lexer.getTokens());
        Interpreter interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("Name: Bob        Grade: 80Name: Joe        Grade: 90Name: Sherry     Grade: 85Name: Jack       Grade: 70", interpreter.GlobalVariables.get("i").toString());
        fileMaker(new String[]{"Bob,3.64", "Joe,4.0", "Sherry,2.4", "Jack,3.0"});
        lexer = new Lexer("BEGIN{FS=\",\"}{i = i sprintf(\"Name: %-10s GPA: %02.2f\", $1, $2)}");
        p = new Parser(lexer.getTokens());
        interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("Name: Bob        GPA: 3.64Name: Joe        GPA: 4.00Name: Sherry     GPA: 2.40Name: Jack       GPA: 3.00", interpreter.GlobalVariables.get("i").toString());
        fileRestore();
    }

    @Test
    public void testGETIDTAssignmentNodeASSIGN() throws Exception {
        fileMaker(new String[0]);
        lexer = new Lexer("a = 2");
        Parser p = new Parser(lexer.getTokens());
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.GetIDT(p.ParseOperation().get(), null);
        assertEquals( "2", interpreter.GlobalVariables.get("a").toString());
        HashMap<String, InterpreterDataType> locals = new HashMap<String, InterpreterDataType>();
        lexer = new Lexer("a = 3");
        p = new Parser(lexer.getTokens());
        interpreter.GetIDT(p.ParseOperation().get(), locals);
        assertEquals( "3", locals.get("a").toString());
        fileRestore();
    }

    @Test
    public void testGETIDTOperationNodeMathOperations() throws Exception{
        fileMaker(new String[0]);
        lexer = new Lexer("2 + 2");
        Parser p = new Parser(lexer.getTokens());
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        assertEquals("4", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("2 - 2");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("2 * 2");
        p = new Parser(lexer.getTokens());
        assertEquals("4", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("2 / 2");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("(2+2)/(2+2)");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        interpreter.GlobalVariables.put("f", new InterpreterDataType("2"));
        lexer = new Lexer("f % 2");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        fileRestore();
    }
    @Test
    public void testGETIDTAssignmentNodeEQ() throws Exception {
        fileMaker(new String[0]);
        lexer = new Lexer("a = 2");
        Parser p = new Parser(lexer.getTokens());
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.GetIDT(p.ParseOperation().get(), null);
        assertEquals( "2", interpreter.GlobalVariables.get("a").toString());
        HashMap<String, InterpreterDataType> locals = new HashMap<String, InterpreterDataType>();
        lexer = new Lexer("a += 3");
        p = new Parser(lexer.getTokens());
        interpreter.GetIDT(p.ParseOperation().get(), locals);
        assertEquals( "3", locals.get("a").toString());
        lexer = new Lexer("a ^= 2");
        p = new Parser(lexer.getTokens());
        interpreter.GetIDT(p.ParseOperation().get(), locals);
        assertEquals("9", locals.get("a").toString());
        lexer = new Lexer("a -= 2");
        p = new Parser(lexer.getTokens());
        interpreter.GetIDT(p.ParseOperation().get(), locals);
        assertEquals("7",locals.get("a").toString());
        lexer = new Lexer("a *= 3");
        p = new Parser(lexer.getTokens());
        interpreter.GetIDT(p.ParseOperation().get(), locals);
        assertEquals("21", locals.get("a").toString());
        interpreter.GlobalVariables.put("f", new InterpreterDataType("2"));
        lexer = new Lexer("a %= 2");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), locals).toString());
        assertEquals("1", locals.get("a").toString());
        fileRestore();
    }

    @Test
    public void testGETIDTPRE() throws Exception{
        fileMaker(new String[0]);
        lexer = new Lexer("a = 1");
        Parser p = new Parser(lexer.getTokens());
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.GetIDT(p.ParseOperation().get(), null);
        lexer = new Lexer("++a");
        p = new Parser(lexer.getTokens());
        assertEquals("2", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        assertEquals("2", interpreter.GlobalVariables.get("a").toString());
        lexer = new Lexer("--a");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        assertEquals("1", interpreter.GlobalVariables.get("a").toString());
        interpreter.GlobalVariables.put("b", new InterpreterDataType());
        lexer = new Lexer("++b");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("--c");
        p = new Parser(lexer.getTokens());
        assertEquals("-1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        interpreter.GlobalVariables.put("str", new InterpreterDataType("string"));
        lexer = new Lexer("--str");
        p = new Parser(lexer.getTokens());
        assertEquals("-1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        assertEquals("-1", interpreter.GlobalVariables.get("str").toString());
        lexer = new Lexer("++str");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        assertEquals("0", interpreter.GlobalVariables.get("str").toString());
        fileRestore();
    }

    @Test
    public void testGETIDTPREERROR() throws Exception{
        fileMaker(new String[0]);
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        Exception thrown = assertThrows(Exception.class, () ->{
            lexer = new Lexer("++3");
            Parser p2 = new Parser(lexer.getTokens());
            interpreter.GetIDT(p2.ParseOperation().get(), null);
        });
        Assertions.assertEquals("Error! Cannot pre-increment to any other value except a VariableReferenceNode! Ex: ++var, --var", thrown.getMessage());
        fileRestore();
    }
    @Test
    public void testGETIDTPOST() throws Exception{
        fileMaker(new String[0]);
        lexer = new Lexer("a = 1");
        Parser p = new Parser(lexer.getTokens());
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.GetIDT(p.ParseOperation().get(), null);
        lexer = new Lexer("a++");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        assertEquals("2", interpreter.GlobalVariables.get("a").toString());
        lexer = new Lexer("a--");
        p = new Parser(lexer.getTokens());
        assertEquals("2", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        assertEquals("1", interpreter.GlobalVariables.get("a").toString());
        lexer = new Lexer("b++");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        assertEquals("1", interpreter.GlobalVariables.get("b").toString());
        lexer = new Lexer("c--");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        assertEquals("-1", interpreter.GlobalVariables.get("c").toString());
        interpreter.GlobalVariables.put("str", new InterpreterDataType("string"));
        lexer = new Lexer("str--");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        assertEquals("-1", interpreter.GlobalVariables.get("str").toString());
        lexer = new Lexer("str++");
        p = new Parser(lexer.getTokens());
        assertEquals("-1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        assertEquals("0", interpreter.GlobalVariables.get("str").toString());
        fileRestore();
    }

    @Test
    public void testGETIDTEqualsComparisonsNUMS() throws Exception{
        fileMaker(new String[0]);
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.GlobalVariables.put("a", new InterpreterDataType("1"));
        interpreter.GlobalVariables.put("b", new InterpreterDataType("2"));
        lexer = new Lexer("a == b");
        Parser p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a != b");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        interpreter.GlobalVariables.put("a", new InterpreterDataType("2"));
        lexer = new Lexer("a != b");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        fileRestore();
    }

    @Test
    public void testGETIDTEqualsComparisonsWORDS() throws Exception{
        fileMaker(new String[0]);
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.GlobalVariables.put("a", new InterpreterDataType("test"));
        interpreter.GlobalVariables.put("b", new InterpreterDataType("test1"));
        lexer = new Lexer("a == b");
        Parser p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a != b");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        interpreter.GlobalVariables.put("b", new InterpreterDataType("test"));
        lexer = new Lexer("a != b");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a == b");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        fileRestore();
    }
    @Test
    public void testGETIDTGTLEComparisonsNUMS() throws Exception{
        fileMaker(new String[0]);
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.GlobalVariables.put("a", new InterpreterDataType("1"));
        interpreter.GlobalVariables.put("b", new InterpreterDataType("2"));
        lexer = new Lexer("a < b");
        Parser p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a > b");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a <= b");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a >= b");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a >= b");
        p = new Parser(lexer.getTokens());
        interpreter.GlobalVariables.put("a", new InterpreterDataType("2"));
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a <= b");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        fileRestore();
    }

    @Test
    public void testGETIDTGTLEComparisonsWORDS() throws Exception{
        fileMaker(new String[0]);
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.GlobalVariables.put("a", new InterpreterDataType("hello"));
        interpreter.GlobalVariables.put("b", new InterpreterDataType("goodbye"));
        lexer = new Lexer("a < b");
        Parser p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a > b");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a <= b");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a >= b");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a >= b");
        p = new Parser(lexer.getTokens());
        interpreter.GlobalVariables.put("a", new InterpreterDataType("2"));
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a <= b");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        fileRestore();
    }

    @Test
    public void GetIDTBoolOperatorBASIC() throws Exception{
        fileMaker(new String[0]);
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.GlobalVariables.put("a", new InterpreterDataType("hello"));
        interpreter.GlobalVariables.put("b", new InterpreterDataType("goodbye"));
        lexer = new Lexer("1 && 1");
        Parser p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("1 && 0");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a && b");
        p = new Parser(lexer.getTokens());
        //interpreter.GetIDT(p.ParseOperation().get(), null);
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a && 0");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a || b");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a || 0");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("b || b");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("c || f");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        fileRestore();
    }

    @Test
    public void testGetIDTNOT() throws Exception{
        fileMaker(new String[0]);
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.GlobalVariables.put("a", new InterpreterDataType("hello"));
        interpreter.GlobalVariables.put("b", new InterpreterDataType("goodbye"));
        lexer = new Lexer("!(0)");
        Parser p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("!(1)");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("!(a && b)");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("!(2 == 3)");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("!(a == b)");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        interpreter.GlobalVariables.put("b", new InterpreterDataType("hello"));
        lexer = new Lexer("!(a == b)");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        fileRestore();
    }
    @Test
    public void GetIDTBoolOperatorADVANCED() throws Exception{
        fileMaker(new String[0]);
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.GlobalVariables.put("a", new InterpreterDataType("2"));
        interpreter.GlobalVariables.put("b", new InterpreterDataType("2"));
        interpreter.GlobalVariables.put("c", new InterpreterDataType("3"));
        interpreter.GlobalVariables.put("d", new InterpreterDataType("5"));
        lexer = new Lexer("(a == b) && (c < d) && !(c < 2)");
        Parser p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("(a == 5) || (b > \"test\") && d < 5");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("!(a == 4) || !(b == 2) || a < 5");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("(!(a) && !(b) || a == \"hello\" || b == \"hello\" && a++ == 5)");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        fileRestore();
    }

    @Test
    public void testConcatination() throws Exception{
        fileMaker(new String[0]);
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        lexer = new Lexer("\"test\" \"test\"");
        Parser p = new Parser(lexer.getTokens());
        assertEquals("testtest", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("2 \"test\"");
        p = new Parser(lexer.getTokens());
        assertEquals("2test", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        interpreter.GlobalVariables.put("a",new InterpreterDataType("2"));
        lexer = new Lexer("2 a");
        p = new Parser(lexer.getTokens());
        assertEquals("22", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        fileRestore();
    }

    @Test
    public void testIn() throws Exception{
        fileMaker(new String[0]);
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        lexer = new Lexer("a[1] = \"test\"");
        Parser p = new Parser(lexer.getTokens());
        interpreter.GetIDT(p.ParseOperation().get(), null);
        lexer = new Lexer("a[\"two\"] = \"test2\"");
        p = new Parser(lexer.getTokens());
        interpreter.GetIDT(p.ParseOperation().get(), null);
        lexer = new Lexer("1 in a");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("\"two\" in b");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a[1] in a");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a[two] in a");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a[2] in a");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a[three] in a");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        fileRestore();
    }

    @Test
    public void InterpreterGetIDTVariableNode() throws Exception{
        fileMaker(new String[0]);
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        lexer = new Lexer("a[1] = \"test\"");
        Parser p = new Parser(lexer.getTokens());
        assertEquals("test",interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("b = 2");
        p = new Parser(lexer.getTokens());
        assertEquals("2",interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("c");
        p = new Parser(lexer.getTokens());
        assertEquals("", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a[1]");
        p = new Parser(lexer.getTokens());
        assertEquals("test", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        fileRestore();
    }

    @Test
    public void InterpreterGetIDTTernaryNode() throws Exception{
        fileMaker(new String[0]);
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.GlobalVariables.put("b", new InterpreterDataType("IAmB"));
        interpreter.GlobalVariables.put("c", new InterpreterDataType("IAmC"));
        lexer = new Lexer("a = (2 > 1) ? b : c");
        Parser p = new Parser(lexer.getTokens());
        assertEquals("IAmB",interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a = (1 == 2) ? b : c");
        p = new Parser(lexer.getTokens());
        assertEquals("IAmC",interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a = (1 > 2) ? \"b\" : (2 < 1) ? \"c\" : (5 > 1)? \"hello\" : \"goodbye\"");
        p = new Parser(lexer.getTokens());
        assertEquals("hello", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        fileRestore();
    }

    @Test
    public void InterpreterGetIDTAddFields() throws Exception{
        fileMaker(new String[]{"Line1Word1 Line1Word2 Line1Word3"});
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        lexer = new Lexer("$(1+2)");
        Parser p = new Parser(lexer.getTokens());
        interpreter.Manager.SplitAndAssign();
        assertEquals("Line1Word3",interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("$(1+\"test\")");
        p = new Parser(lexer.getTokens());
        assertEquals("Line1Word1",interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("$1 + test");
        p = new Parser(lexer.getTokens());
        assertEquals("0",interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("$(1 + test)");
        p = new Parser(lexer.getTokens());
        assertEquals("Line1Word1",interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        fileRestore();
    }

    @Test
    public void InterpretGetIDTMatch() throws Exception{
        fileMaker(new String[]{"Line1Word1 Line1Word2 Line1Word3"});
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.Manager.SplitAndAssign();
        lexer = new Lexer("$0 ~ `Word`");
        Parser p = new Parser(lexer.getTokens());
        assertEquals("1",interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("$1 ~ `xv3ed`");
        p = new Parser(lexer.getTokens());
        assertEquals("0",interpreter.GetIDT(p.ParseOperation().get(), null).toString() );
        lexer = new Lexer("$2 !~ `rfgrgg`");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("$3 !~ `3`");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        fileRestore();
    }

    @Test
    public void InterpretGetIDTMatchERRORS() throws Exception{
        fileMaker(new String[]{"Line1Word1 Line1Word2 Line1Word3"});
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.Manager.SplitAndAssign();
        Exception thrown = assertThrows(Exception.class, () ->{
            lexer = new Lexer("$0 ~ \"word\"");
            Parser p2 = new Parser(lexer.getTokens());
            interpreter.GetIDT(p2.ParseOperation().get(), null);
        });
        Assertions.assertEquals("Error! Cannot match with a non-pattern type! Ex: expr ~|!~ PATTERN", thrown.getMessage());
        thrown = assertThrows(Exception.class, () ->{
            lexer = new Lexer("$0 !~ \"word\"");
            Parser p2 = new Parser(lexer.getTokens());
            interpreter.GetIDT(p2.ParseOperation().get(), null);
        });
        Assertions.assertEquals("Error! Cannot match with a non-pattern type! Ex: expr ~|!~ PATTERN", thrown.getMessage());

        fileRestore();
    }

    @Test
    public void InterpretGetIDTUnary() throws Exception{
        fileMaker(new String[0]);
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        lexer = new Lexer("a = +\"2\"");
        Parser p = new Parser(lexer.getTokens());
        assertEquals("2",interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("b = -a");
        p = new Parser(lexer.getTokens());
        assertEquals("-2",interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("-\"3\"");
        p = new Parser(lexer.getTokens());
        assertEquals("-3", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("+\"test\"");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("-\"test\"");
        p = new Parser(lexer.getTokens());
        assertEquals("0", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        fileRestore();
    }

    @Test
    public void InterpretGetIDTVariableReferenceNode() throws Exception{
        fileMaker(new String[0]);
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        lexer = new Lexer("a[\"test1\"] = 1");
        Parser p = new Parser(lexer.getTokens());
        assertEquals("1",interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        assertEquals("1", interpreter.GlobalVariables.get("a[\"test1\"]").toString());
        lexer = new Lexer("a[\"test1\"]");
        p = new Parser(lexer.getTokens());
        assertEquals("1", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        lexer = new Lexer("a[\"test2\"]");
        p = new Parser(lexer.getTokens());
        assertEquals("", interpreter.GetIDT(p.ParseOperation().get(), null).toString());
        fileRestore();
    }

    @Test
    public void InterpretGetIDTPATTERNERROR() throws Exception{
        fileMaker(new String[0]);
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        Exception thrown = assertThrows(Exception.class, () ->{
            lexer = new Lexer("`test`");
            Parser p2 = new Parser(lexer.getTokens());
            interpreter.GetIDT(p2.ParseOperation().get(), null);
        });
        Assertions.assertEquals("Error! Cannot call a patternNode outside of a statement!", thrown.getMessage());
        fileRestore();
    }

    @Test
    public void testPrint() throws Exception{
        fileMaker(new String[0]);
        interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        interpreter.Manager.SplitAndAssign();
        HashMap<String, InterpreterDataType> input = new HashMap<String, InterpreterDataType>();
        BuiltInFunctionDefinitionNode node = (BuiltInFunctionDefinitionNode) interpreter.Functions.get("print");
        InterpreterArrayDataType content = new InterpreterArrayDataType();
        content.put("Original string: ");
        content.put(interpreter.GlobalVariables.get("$0").toString());
        content.put(", First word: ");
        content.put(interpreter.GlobalVariables.get("$1").toString());
        content.put(", Second word: ");
        content.put(interpreter.GlobalVariables.get("$2").toString());
        input.put("content", content);
        node.Execute(input);
        assertEquals("Original string: Hello this is line 1, First word: Hello, Second word: this",interpreter.printContent);
        fileRestore();
    }

    @Test
    public void testPrintf() throws Exception{
        fileMaker(new String[0]);
        lexer = new Lexer("{for(i=NF; i > 0; i--){printf \"%s\", $i \" \"\na = (a sprintf(\"%s\", $i \" \"))}print \"\"}");
        Parser p = new Parser(lexer.getTokens());
        Interpreter interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("1line is this Hello 2line is this Hello 3line is this Hello 4line is this Hello 5line is this Hello ", interpreter.GlobalVariables.get("a").toString());
        fileMaker(new String[]{"Hello frfrfr world"});
        lexer = new Lexer("{printf \"First: %-10s Third: %s\", $1,$3\n a = sprintf (\"First: %-10s Third: %s\", $1,$3)}");
        p = new Parser(lexer.getTokens());
        interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("First: Hello      Third: world", interpreter.GlobalVariables.get("a").toString());
        fileRestore();
    }


    @Test
    public void InterpreterGetIDTFunctionCallNode() throws Exception{
        fileMaker(new String[0]);
        Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(filePath));
        Exception thrown = assertThrows(Exception.class, () ->{
            lexer = new Lexer("myFunction()");
            Parser p2 = new Parser(lexer.getTokens());
            interpreter.GetIDT(p2.ParseOperation().get(), null);
        });
        Assertions.assertEquals("Error! Function doesn't exist!", thrown.getMessage());
        FunctionCallNode func = new FunctionCallNode("myFunction");
        interpreter.Functions.put(func.getName(), new FunctionDefinitionNode("myFunction", new String[0]));
        lexer = new Lexer("{a = myFunction()}function myFunction(){return 2}");
        Parser p = new Parser(lexer.getTokens());
        Interpreter interpreter2 = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter2.InterpretProgram();
        assertEquals("2", interpreter2.GlobalVariables.get("a").toString());
        lexer = new Lexer("{a = myFunction(1,2)}function myFunction(a,b){return a + b}");
        p = new Parser(lexer.getTokens());
        interpreter2 = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter2.InterpretProgram();
        assertEquals("3", interpreter2.GlobalVariables.get("a").toString());
        lexer = new Lexer("{a = myFunction(2,3,3,2)} function myFunction(a,b,c,d){return (a^b)+(c^d)}");
        p = new Parser(lexer.getTokens());
        interpreter2 = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter2.InterpretProgram();
        assertEquals("17", interpreter2.GlobalVariables.get("a").toString());
        fileRestore();
    }

    @Test
    public void testAssignment0() throws Exception{
        fileMaker(new String[]{"1,2,3,4","5,6,7,8","9,10,11,12"});
        lexer = new Lexer("BEGIN{FS=\",\"}{sum = 0for(i = 1; i <=NF; i++){sum+=$i}f = f + sum}END{print\"The sum is: \" f}");
        Parser p = new Parser(lexer.getTokens());
        Interpreter interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("78", interpreter.GlobalVariables.get("f").toString());
        fileMaker(new String[]{"There are 7 words in this line","Now there are 8 words in this line","And now there are 9 words in this line"});
        lexer = new Lexer("{f = f + NF}END{print f \" words\"}");
        p = new Parser(lexer.getTokens());
        interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("24", interpreter.GlobalVariables.get("f").toString());
        fileRestore();
    }

    @Test
    public void testBlockConditions() throws Exception{
        fileMaker(new String[]{"hello this is line 1", "hello this is line 2"});
        lexer = new Lexer("BEGIN{a = 0}(NR > 3){a = 1 print \"This block has ran!\"}");
        Parser p = new Parser(lexer.getTokens());
        Interpreter interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("0", interpreter.GlobalVariables.get("a").toString());
        fileMaker(new String[]{"hello this is line 1", "hello this is line 2", "hello this is line 3", "hello this is line 4"});
        lexer = new Lexer("BEGIN{a = 0}(NR > 3){a = 1 print \"This block has ran!\"}");
        p = new Parser(lexer.getTokens());
        interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("1", interpreter.GlobalVariables.get("a").toString());
        fileRestore();
    }

    @Test
    public void testForLoop() throws Exception{
        fileMaker(new String[0]);
        lexer = new Lexer("{for(i = 1; i <= 5; i++){print \"Iteration \" i \": \" $0; if(i == 1){n = n $0}}}");
        Parser p = new Parser(lexer.getTokens());
        Interpreter interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("Hello this is line 1Hello this is line 2Hello this is line 3Hello this is line 4Hello this is line 5", interpreter.GlobalVariables.get("n").toString());
        fileMaker(new String[]{"line1"});
        lexer = new Lexer("{a[\"test1\"] = \"hello\";a[\"test2\"] = \"world\"; str = \"loop: \"; for(i in a){str = str a[i]}}");
        p = new Parser(lexer.getTokens());
        interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("loop: helloworld", interpreter.GlobalVariables.get("str").toString());
        fileRestore();
    }

    @Test
    public void testBreakAndContinue() throws Exception{
        fileMaker(new String[0]);
        lexer = new Lexer("{i = 0;while(i<10){if(i==5){break}i++}}");
        Parser p = new Parser(lexer.getTokens());
        Interpreter interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("5", interpreter.GlobalVariables.get("i").toString());
        lexer = new Lexer("{i = 0;n = 0;while(i<10){i++;if(i==5){continue};n++}}");
        p = new Parser(lexer.getTokens());
        interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("9", interpreter.GlobalVariables.get("n").toString());
        fileRestore();
    }

    @Test
    public void testIfStatements() throws Exception{
        fileMaker(new String[]{"1", "1 2", "1 2 3", "1 2 3 4", "1 2 3 4 5", "1 2 3 4 5 6"});
        lexer = new Lexer("{a=0;b=0;c=0;d=0;e=0;f = 0;if(NF == 1){a=1}else if(NF == 2){b=1}else if(NF==3){c=1}else if(NF == 4){d=1}else if(NF==5){e = 1}else{f = 1}}");
        Parser p = new Parser(lexer.getTokens());
        Interpreter interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.Manager.SplitAndAssign();
        interpreter.InterpretBlock(interpreter.getProgram().getOther().get(0));
        assertEquals("1", interpreter.GlobalVariables.get("a").toString());
        interpreter.Manager.SplitAndAssign();
        interpreter.InterpretBlock(interpreter.getProgram().getOther().get(0));
        assertEquals("1", interpreter.GlobalVariables.get("b").toString());
        interpreter.Manager.SplitAndAssign();
        interpreter.InterpretBlock(interpreter.getProgram().getOther().get(0));
        assertEquals("1", interpreter.GlobalVariables.get("c").toString());
        interpreter.Manager.SplitAndAssign();
        interpreter.InterpretBlock(interpreter.getProgram().getOther().get(0));
        assertEquals("1", interpreter.GlobalVariables.get("d").toString());
        interpreter.Manager.SplitAndAssign();
        interpreter.InterpretBlock(interpreter.getProgram().getOther().get(0));
        assertEquals("1", interpreter.GlobalVariables.get("e").toString());
        interpreter.Manager.SplitAndAssign();
        interpreter.InterpretBlock(interpreter.getProgram().getOther().get(0));
        assertEquals("1", interpreter.GlobalVariables.get("f").toString());
        fileRestore();
    }

    @Test
    public void TestDoWhileLoop() throws Exception{
        fileMaker(new String[0]);
        lexer = new Lexer("{i = 0;do{a = a \"test\";i++}while(i < 5)}");
        Parser p = new Parser(lexer.getTokens());
        Interpreter interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.Manager.SplitAndAssign();
        interpreter.InterpretBlock(interpreter.getProgram().getOther().get(0));
        assertEquals("testtesttesttesttest", interpreter.GlobalVariables.get("a").toString());
        lexer = new Lexer("{while(i < 5){a = a \"test\";i++}}");
        p = new Parser(lexer.getTokens());
        interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("testtesttesttesttest", interpreter.GlobalVariables.get("a").toString());
        fileRestore();
    }
@Test
    public void TestBuiltInsPart1() throws Exception{
        fileMaker(new String[0]);
        lexer = new Lexer("BEGIN{i=0}{if(getline > 0){i += 1} }");
        Parser p = new Parser(lexer.getTokens());
        Interpreter interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("2", interpreter.GlobalVariables.get("i").toString());
        lexer = new Lexer("{i = getline 1}END{print i}");
        p = new Parser(lexer.getTokens());
        interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("1", interpreter.GlobalVariables.get("i").toString());
        lexer = new Lexer("match($0, `this`) { test++ } END{print test}");
        p = new Parser(lexer.getTokens());
        interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("5", interpreter.GlobalVariables.get("test").toString());
        fileRestore();
}

@Test
    public void TestBuiltInsPart2() throws Exception{
    fileMaker(new String[0]);
    lexer = new Lexer("{sub(`this`,\"newpattern\"); i = $0}");
    Parser p = new Parser(lexer.getTokens());
    Interpreter interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("Hello newpattern is line 5", interpreter.GlobalVariables.get("i").toString());
    fileMaker(new String[]{"test"});
    lexer = new Lexer("BEGIN{test = \"this is a variable\"}{sub(\"this is\", \"New pattern\", test);fin = fin test}END{print fin}");
    p = new Parser(lexer.getTokens());
    interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("New pattern a variable", interpreter.GlobalVariables.get("fin").toString());
    lexer = new Lexer("BEGIN{test = \"this hello is hello a hello variable hello\"}{gsub(\" hello\",\"\",test)}");
    p = new Parser(lexer.getTokens());
    interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("this is a variable", interpreter.GlobalVariables.get("test").toString());
    fileMaker(new String[0]);
    lexer=  new Lexer("{gsub(\"is\", \"test\"); fin = fin $0}END{print fin}");
    p = new Parser(lexer.getTokens());
    interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("Hello thtest test line 1Hello thtest test line 2Hello thtest test line 3Hello thtest test line 4Hello thtest test line 5", interpreter.GlobalVariables.get("fin").toString());
    p = new Parser(lexer.getTokens());
    interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    lexer = new Lexer("{position += index($0, \"is\")}");
    p = new Parser(lexer.getTokens());
    interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("45", interpreter.GlobalVariables.get("position").toString());
    lexer = new Lexer("{len += length($0)}END{print len}");
    p = new Parser(lexer.getTokens());
    interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("100", interpreter.GlobalVariables.get("len").toString());
    fileRestore();
}

@Test
public void testBuiltInsPart3() throws Exception{
    fileMaker(new String[0]);
    lexer = new Lexer("{split($0, parts)}END{for(n in parts){a = a parts[n]}}");
    Parser p = new Parser(lexer.getTokens());
    Interpreter interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("Hellothisisline5", interpreter.GlobalVariables.get("a").toString());
    fileMaker(new String[]{"column1:column2:column3:column4:column5"});
    lexer = new Lexer("{split($0, parts, \":\")}END{for(n in parts){a = a parts[n]}}");
    p = new Parser(lexer.getTokens());
    interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("column1column2column3column4column5", interpreter.GlobalVariables.get("a").toString());
    lexer = new Lexer("{a = toupper($0)}END{print a}");
    p = new Parser(lexer.getTokens());
    interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("COLUMN1:COLUMN2:COLUMN3:COLUMN4:COLUMN5", interpreter.GlobalVariables.get("a").toString());
    fileMaker(new String[]{"COLUMN1:COLUMN2:COLUMN3:COLUMN4:COLUMN5"});
    lexer = new Lexer("{a = tolower($0)}END{print a}");
    p = new Parser(lexer.getTokens());
    interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("column1:column2:column3:column4:column5", interpreter.GlobalVariables.get("a").toString());
    fileMaker(new String[0]);
    lexer = new Lexer("{a = a substr($0, 0, 5)}{print a}");
    p = new Parser(lexer.getTokens());
    interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("HelloHelloHelloHelloHello", interpreter.GlobalVariables.get("a").toString());
    fileMaker(new String[]{"Hello there", "Helllo there", "Hellllo there", "hellloooooo there"});
    lexer = new Lexer("{if((length($1) == 5) || (length($1) == 7)){a = a \"line skipped! \"; next}}END{len = length(a)-1;a = substr(a,0,len);print a}");
    p = new Parser(lexer.getTokens());
    interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("line skipped! line skipped!", interpreter.GlobalVariables.get("a").toString());
    fileRestore();
}

@Test
public void testRandomAwkScriptsPart1() throws Exception{
    fileMaker(new String[0]);
    lexer = new Lexer("BEGIN{start=5;end=10;sum=0;for(i = start; i <= end; i++){sum+=i};print \"Sum of numbers from \", start, \" to \", end, \" is: \", sum}");
    Parser p = new Parser(lexer.getTokens());
    Interpreter interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("Sum of numbers from 5 to 10 is: 45", interpreter.printContent);
    String script = "{total += $5; count++}END{if(count > 0){average = total/count;print \"Average: \", average};else{print \"No numbers in this file\"}}";
    lexer = new Lexer(script);
    p = new Parser(lexer.getTokens());
    interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("3", interpreter.GlobalVariables.get("average").toString());
    lexer = new Lexer(script);
    p = new Parser(lexer.getTokens());
    interpreter = new Interpreter(p.Parse(), Optional.empty());
    interpreter.InterpretProgram();
    assertEquals("No numbers in this file", interpreter.printContent);
    fileRestore();
    lexer = new Lexer("{for(i = 1; i <= NF; i++){if($i % 2 != 0){a = a \"Odd number: \" $i}}}");
    p = new Parser(lexer.getTokens());
    interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("Odd number: 1Odd number: 3Odd number: 5", interpreter.GlobalVariables.get("a").toString());
    lexer = new Lexer("{for(i = NF; i >= 1; i--){a = a $i \" \"}}END{len = length(a)-1;a = substr(a,0,len);print a}");
    p = new Parser(lexer.getTokens());
    interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("1line is this Hello 2line is this Hello 3line is this Hello 4line is this Hello 5line is this Hello", interpreter.GlobalVariables.get("a").toString());
    lexer = new Lexer("BEGIN{celsius = 25;fahrenheit = celsiusToFahrenheit(celsius); print \"Celsius: \", celsius, \"Fahrenheit: \", fahrenheit}function celsiusToFahrenheit(celsius){return celsius * 9/5 + 32}");
    p = new Parser(lexer.getTokens());
    interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("Celsius: 25Fahrenheit: 77", interpreter.printContent);
    fileRestore();
    }

    @Test
    public void testRandomAwkScripts4() throws Exception{
    fileMaker(new String[]{"1", "2"});
    lexer  = new Lexer("{number = $1;result = (number %2 == 0 ? \"even\" : \"odd\"); a = a result}END{print a}");
    Parser p = new Parser(lexer.getTokens());
    Interpreter interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("oddeven", interpreter.GlobalVariables.get("a").toString());
    fileMaker(new String[]{"Hello this is line 1", "line 2 is here"});
    lexer = new Lexer("($0 ~ `this`){a = a \"Line with 'this':\" $0}; ($0 !~ `this`){a = a \"This line doesn't contain 'this'!\"}END{print a}");
    p = new Parser(lexer.getTokens());
    interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
    interpreter.InterpretProgram();
    assertEquals("Line with 'this':Hello this is line 1This line doesn't contain 'this'!", interpreter.GlobalVariables.get("a").toString());
    fileMaker(new String[0]);
    lexer = new Lexer("{negated += -$5}END{print negated}");
    p = new Parser(lexer.getTokens());
        interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("-15", interpreter.GlobalVariables.get("negated").toString());
        lexer = new Lexer("BEGIN{negated = -1}END{negated = +negated; print negated}");
        p = new Parser(lexer.getTokens());
        interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("1", interpreter.GlobalVariables.get("negated").toString());
        // ;delete fruits["banana"]; for(fruit in fruits){print fruit}
    lexer = new Lexer("BEGIN{fruits[\"apple\"] = 3;fruits[\"banana\"] = 5;fruits[\"orange\"] = 2;print \"Original array\";for(fruit in fruits){print fruit, \" : \", fruits[fruit]};delete fruits[\"banana\"]; for(fruit in fruits){print fruit, \" : \",fruits[fruit]}}");
        p = new Parser(lexer.getTokens());
        interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
    fileRestore();
    }

    @Test
    public void testDelete() throws Exception{
        fileMaker(new String[0]);
        lexer = new Lexer("BEGIN{fruits[\"apple\"] = 3;fruits[\"banana\"] = 5;fruits[\"orange\"] = 2;print \"Original array\";for(fruit in fruits){print fruit, \" : \", fruits[fruit];};delete fruits[\"banana\"]; for(fruit in fruits){print fruit, \" : \",fruits[fruit]; a = a fruits[fruit]}}");
        Parser p = new Parser(lexer.getTokens());
        Interpreter interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("32", interpreter.GlobalVariables.get("a").toString());
        lexer = new Lexer("BEGIN{fruits[\"apple\"] = 3;fruits[\"banana\"] = 5;fruits[\"orange\"] = 2; delete fruits;a = \"empty\";for(n in fruits){a = a fruits[n]}}");
        p = new Parser(lexer.getTokens());
        interpreter = new Interpreter(p.Parse(), Optional.of(filePath));
        interpreter.InterpretProgram();
        assertEquals("empty", interpreter.GlobalVariables.get("a").toString());
        fileRestore();
    }

    public void awkScriptMaker(String[] s) throws IOException{
        LinkedList<String> strings = new LinkedList<String>();
        if(s.length == 0) {
            strings.add("{f = f + NF}");
            strings.add("END{print f \" words\"}");
        }
        else{
            for(var i : s)
                strings.add(i);
        }
        filePath = Paths.get("test.awk");
        temp = Files.readAllLines(filePath);
        FileWriter fileWriter = new FileWriter("test.awk");
        fileWriter.write("");
        for (String line : strings)
            fileWriter.write(line + "\n"); // Write each line to the file with a newline character
        fileWriter.close();
    }
    @Test
    public void testInputPaths() throws Exception{
        fileMaker(new String[0]);
        awkScriptMaker(new String[0]);
        PrintStream out = System.out;
        System.setOut(out);
        String[] args = new String[]{"test.awk", "input.txt"};
        Main.main(args);
        Exception thrown = assertThrows(Exception.class, () -> {
            String[] args2 = new String[0];
            Main.main(args2);
        });
        Assertions.assertEquals("args have not been declared!", thrown.getMessage());
    }
}
