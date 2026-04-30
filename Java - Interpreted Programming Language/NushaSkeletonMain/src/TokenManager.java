import AST.Token;
import java.util.LinkedList;
import java.util.Optional;

public class TokenManager {

//TextManager manager = new TextManager;
    private final LinkedList<Token> tokens;

    //line number
    //column number
    //The columnNumber and lineNumber
    // members on the Token class keep
    // track of where this Token
    // was found in the text.
    //Create the TokenTypes enum and the Token class.
    public TokenManager(LinkedList<Token> tokens) {

        this.tokens = tokens;

    }
    public int getLine() {


        return 0;
    }

    public int getColumn() {
       return 0;

    }

    public boolean Done() {
    return false;
    }

    public Optional<Token> MatchAndRemove(Token.TokenTypes t) {

        if (tokens.isEmpty()) {

            return Optional.empty();
        }

        else if (t == tokens.get(0).Type) {
            //this.tokens.removeLast();
            return Optional.of(tokens.remove(0));
        }

          else {  //return Optional.of(tokens.removeFirst());
            return Optional.empty();
        }

    }

    public Optional<Token> Peek (int i) {

        if (this.tokens.get(i) != null) {
            Token b = this.tokens.get(i);

            return Optional.of(b);

        }

        else {
        return Optional.empty();}
    }
}
