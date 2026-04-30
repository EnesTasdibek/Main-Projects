import AST.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Lexer {

    private final TextManager textManager;
    private int lineNumber;
    private int characterPosition;
    private int previousLineIndent = 0;
    int currentIndent = 0;
    LinkedList<Token> ListOfTokens = new LinkedList<>();
    private final HashMap<String, Token.TokenTypes> keywords;

    public Lexer(String input) {

        this.textManager = new TextManager(input);
        this.lineNumber = 0;
        this.characterPosition = 0;

        this.keywords = new HashMap<>();
        keywords.put("identifier", Token.TokenTypes.IDENTIFIER);
        keywords.put("unique", Token.TokenTypes.UNIQUE);
        keywords.put("var", Token.TokenTypes.VAR);
        keywords.put("=", Token.TokenTypes.EQUAL);
        keywords.put("!=", Token.TokenTypes.NOTEQUAL);
        keywords.put(":", Token.TokenTypes.COLON);
        keywords.put("[", Token.TokenTypes.LEFTBRACE);
        keywords.put("]", Token.TokenTypes.RIGHTBRACE);
        keywords.put("{", Token.TokenTypes.LEFTCURLY);
        keywords.put("}", Token.TokenTypes.RIGHTCURLY);
        keywords.put(".", Token.TokenTypes.DOT);
        keywords.put(",", Token.TokenTypes.COMMA);

    }

    public LinkedList<Token> Lex() throws SyntaxErrorException {
        //  Use 4 spaces as one indentation level.
        //  A tab is counted as 4 spaces.
        //  Indentation that is not a multiple of 4 is a syntax error
        //  CurrentWord = empty

        while (!textManager.isAtEnd()) {

            checkIndentation();

            char C = textManager.PeekCharacter();

            if (Character.isLetter(C)) {
                ListOfTokens.add(readWord());

            } else if (Character.isDigit(C)) {
                ListOfTokens.add(readNumber());
            }
            //allow newline and output NEWLINE token
            else if (C == '\n') {
                textManager.GetCharacter();
                characterPosition++;
                ListOfTokens.add(new Token(Token.TokenTypes.NEWLINE, this.lineNumber, this.characterPosition));
                lineNumber++;
                characterPosition = 0;
            } else if (C == '.') {
                // get character after the “.”
                char next = textManager.PeekCharacter(1);
                if (Character.isDigit(next)) {

                    ListOfTokens.add(readNumber());
                } else {
                    ListOfTokens.add(readPunctuation());
                }
            }
            else if (C == ' ')  {
                //eating spaces after the indent(multiple of 4)
                //or any space that is not indent or dedent
                textManager.GetCharacter();
                characterPosition++;
            }else {
                ListOfTokens.add(readPunctuation());
            }
        }

        //checking indentation on end to put dedent
        checkIndentation();

        //throw newline
        ListOfTokens.add(new Token(Token.TokenTypes.NEWLINE, this.lineNumber, this.characterPosition));
        lineNumber++;
        characterPosition = 0;


        return ListOfTokens;
    }

        private Token readPunctuation() throws SyntaxErrorException {

        char C  =  textManager.PeekCharacter();
         if(C == '!' && textManager.PeekCharacter(1) == '=') {
            textManager.GetCharacter();
            textManager.GetCharacter();
            characterPosition +=2;

             return new Token(Token.TokenTypes.NOTEQUAL, lineNumber, characterPosition);

         }
         else if(C == '=' && textManager.PeekCharacter(1) == '>') {

             textManager.GetCharacter();
             textManager.GetCharacter();
             characterPosition +=2;
             return new Token(Token.TokenTypes.YIELDS, lineNumber, characterPosition);
         }

         else{

             C =  textManager.GetCharacter();
             characterPosition++;
             switch(C) {

                 case '=':
                     return new Token(Token.TokenTypes.EQUAL, lineNumber, characterPosition);
                 case ':':
                     return new Token(Token.TokenTypes.COLON, lineNumber, characterPosition);
                 case '[':
                     return new Token(Token.TokenTypes.LEFTBRACE, lineNumber, characterPosition);
                 case ']':
                     return new Token(Token.TokenTypes.RIGHTBRACE, lineNumber, characterPosition);
                 case '{':
                     return new Token(Token.TokenTypes.LEFTCURLY, lineNumber, characterPosition);
                 case '}':
                     return new Token(Token.TokenTypes.RIGHTCURLY, lineNumber, characterPosition);
                 case '.':
                     return new Token(Token.TokenTypes.DOT, lineNumber, characterPosition);
                 case ',':
                     return new Token(Token.TokenTypes.COMMA, lineNumber, characterPosition);
                 case ' ':
                    return new Token(Token.TokenTypes.IDENTIFIER, lineNumber, characterPosition);
                 default:
                     //return new Token(Token.TokenTypes.IDENTIFIER, lineNumber, characterPosition);
                     throw new SyntaxErrorException("Error!", lineNumber, characterPosition);
             }
         }

        }

        private void checkIndentation() throws SyntaxErrorException {
            //starting from beginning of the line
            if (characterPosition == 0) {

                int spaceCounter = 0;

                while (!textManager.isAtEnd() && textManager.PeekCharacter() == ' ') {

                    textManager.GetCharacter();
                    characterPosition++;
                    spaceCounter++;
                }

                if (spaceCounter % 4 != 0) {
                    throw new SyntaxErrorException("Error!", lineNumber, characterPosition);
                }
                //currentIndent will be spaces multiple of 4
                currentIndent = spaceCounter;

                //indent is multiple of 4 consisting of spaces,
                //4,8,12,16
                //space is 1

                //checking current indent to previous line indent
                //if current indent is bigger, INDENT
                //else DEDENT
                if (currentIndent > previousLineIndent) {

                    ListOfTokens.add(new Token(Token.TokenTypes.INDENT, lineNumber, currentIndent));
                } else if (currentIndent < previousLineIndent) {

                    ListOfTokens.add(new Token(Token.TokenTypes.DEDENT, lineNumber, currentIndent));
                }
            }
                //previous line indent = new line indent
                //previous indent will be basically saved as
                //last current indent
                previousLineIndent = currentIndent;
            }

        private Token readNumber () {

            String buffer2 = "";
            while (!textManager.isAtEnd() && Character.isDigit(textManager.PeekCharacter())) {


                buffer2 += (textManager.GetCharacter());

                characterPosition++;
                if (textManager.isAtEnd()) {
                    break;
                }
            }
            return new Token(Token.TokenTypes.NUMBER, this.lineNumber, this.characterPosition, buffer2);
        }

        private Token readWord () {
            //initialize buffer
            //need to accept words
            String buffer = "";

            //filling buffer to check keywords
            while (!textManager.isAtEnd() && Character.isLetter(textManager.PeekCharacter()) || Character.isDigit(textManager.PeekCharacter())) {
                buffer += (textManager.GetCharacter());
                characterPosition++;
                if (textManager.isAtEnd()) {
                    break;
                }
            }
            if (keywords.containsKey(buffer)) {
                return new Token(keywords.get(buffer), this.lineNumber, this.characterPosition);
            }

            else {

                return new Token(Token.TokenTypes.IDENTIFIER, this.lineNumber, this.characterPosition, buffer);

            }
        }
    }





