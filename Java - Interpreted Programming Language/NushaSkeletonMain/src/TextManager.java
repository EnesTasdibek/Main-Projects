public class TextManager {

    //add private variable
    private String text;
    private int position;

    public TextManager(String input) {
        this.text = input;
    this.position = 0;

    }

    public boolean isAtEnd() {

    if(position >= text.length()) {
    return true;
    }
    return false;
    }

    public char PeekCharacter() {


            return text.charAt(position);

    }

    public char PeekCharacter(int dist) {


        return text.charAt(position + dist);

    }
    //this returns the character at “position” and increments position.
    //use charAt()
    public char GetCharacter() {

        return text.charAt(position++);
    }
}
