public class StringHandler {
    // Holds text file as string
    private String document;

    // index shows the position that has been parsed so far through the document.
    private int index = 0;

    // Constructor for StringHandler class
    public StringHandler(String s) {
        document = s;
    }

    /*
     * char Peek(i) -looks “i” characters ahead and returns that character; doesn’t
     * move the index
     */
    public char Peek(int i) {
        return document.charAt(index + i);
    }

    /*
     * String PeekString(i) – returns a string of the next “i” characters but
     * doesn’t move the index
     */
    public String PeekString(int i) {
        String f = "";
        for (int n = 0; n < i; n++) {
            char c = document.charAt(index + n);
            f = f + c;
        }
        return f;
    }

    /*
     * char GetChar() – returns the next character and moves the index
     */
    public char GetChar() {
        index++;
        return document.charAt(index - 1);
    }

    /*
     * void Swallow(i) – moves the index ahead “i” positions
     */
    public void Swallow(int i) {
        index = index + i;
    }

    /*
     * boolean IsDone() – returns true if we are at the end of the document
     */
    public boolean IsDone() {
        try {
            document.charAt(index);
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    /*
     * String Remainder() – returns the rest of the document as a string
     */
    public String Remainder() {
        return document.substring(index, document.length());
    }
}
