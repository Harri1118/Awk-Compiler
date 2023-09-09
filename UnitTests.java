package project2;

import static org.junit.Assert.assertEquals;

import java.beans.Transient;

import org.junit.Before;
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
        lexer = new Lexer("example");
        lexer.Lex();
        assertEquals("[WORD(example)]", lexer.getTokens().toString());
    }

    // Tests for a number
    @Test
    public void test2() {

        lexer = new Lexer("123");
        lexer.Lex();
        assertEquals("[NUMBER(123)]", lexer.getTokens().toString());
    }

    // Tests for a newline
    @Test
    public void test3() {
        lexer = new Lexer("\n");
        lexer.Lex();
        assertEquals("[SEPARATOR]", lexer.getTokens().toString());
    }

    // Tests for a word, number, and newline
    @Test
    public void test4() {
        lexer = new Lexer("test 5");
        lexer.Lex();
        assertEquals("[WORD(test), NUMBER(5)]", lexer.getTokens().toString());
    }

    @Test
    // Tests for word and number, then number and word.
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
    public void test7(){
        lexer = new Lexer("while if do for break continue else return BEGIN END print printf next in delete getline exit nextfile function");
        assertEquals(19, lexer.getTokens().size());

    }

    @Test
    public void test8(){
        lexer = new Lexer("BEGIN {old_word=\"apple\"; new_word=\"orange\"} {gsub(old_word, new_word); print}");
    }

    @Test
    public void test9(){
        lexer = new Lexer("BEGIN {max_length=0; longest_line=\"\"} {if(length>max_length){max_length=length;longest_line=$0}} END {print \"Longest line:\", longest_line}");
    }

    @Test
    public void test10(){
        lexer = new Lexer("{for(i=1;i<=NF;i++){word=$i;word_count[word]++}} END {for(word in word_count){print \"Word:\",word,\"Count:\",word_count[word]}}");
    }

    @Transient
    public void test11(){
        lexer = new Lexer("BEGIN {FS=\",\"; total=0; count=0} {for(i=1;i<=NF;i++){total+=$i;count++}} END {if(count>0){average=total/count;print \"Average:\",average}else{print \"No numbers found.\"}}");
    }
}
