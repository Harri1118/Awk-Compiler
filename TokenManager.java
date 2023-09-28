package icsi311;

import icsi311.Token.TokenType;

import java.util.LinkedList;
import java.util.Optional;
public class TokenManager {
    private LinkedList<Token> tokens;

    public TokenManager(LinkedList<Token> t){
        tokens = t;
    }
    // tokens ahead and return the token if we aren’t past the end of the token list
    public Optional<Token> Peek(int j) {
        return Optional.of(tokens.get(j));
    }

    // returns true if the token list is not empty
    public boolean MoreTokens() {
        return !(tokens.isEmpty());
    }

    // looks at the head of the list. If the token type of the head is the same as
    // what was passed in,
    // remove that token from the list and return it. In all other cases, returns
    // Optional.Empty(). You will use
    // this extensively.
   public Optional<Token> MatchAndRemove(TokenType t) {

        if(tokens.get(0).getType() == t){
            Token f = tokens.get(0);
            tokens.remove(0);
            return Optional.of(f);
        }
        else
            return Optional.empty();

    }

}
