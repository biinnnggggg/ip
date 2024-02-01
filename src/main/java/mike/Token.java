package mike;

class Token {
    public final TokenType type;
    public final String text;

    Token(TokenType type, String text) {
        this.type = type;
        this.text = text;
    }

    public TokenType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return type + " " + text;
    }
}
