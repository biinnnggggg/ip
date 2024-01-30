package mike;/*
TODO:
    1. Write comments to document the code.
 */
import mike.command.*;

import java.util.List;

class CommandParser {
    private final List<Token> tokens;
    private int current;

    CommandParser(List<Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
    }

    /**
     * Parses the token list to generate a Command object which is returned.
     * @return Command that can be executed.
     * @throws MikeException If there is no command input or the command is not recognized.
     */
    public Command parse() throws MikeException {
        Token commandToken = advance();
        switch (commandToken.getType()) {
        case EXIT:
            return parseExit();
        case LIST:
            return parseList();
        case MARK:
            return parseMark();
        case UNMARK:
            return parseUnmark();
        case TODO:
            return parseTodo();
        case DEADLINE:
            return parseDeadline();
        case EVENT:
            return parseEvent();
        case DELETE:
            return parseDelete();
        case EOC:
            throw new MikeException("Say something.");
        default:
            String errorMessage = "'" + commandToken.getText() + "' is not recognized as a command.\n"
                    + "That is the weirdest thing you've ever said.";
            throw new MikeException(errorMessage);
        }
    }

    private Command parseExit() throws MikeException {
        consume(TokenType.EOC, "Usage: bye");
        return new ExitCommand();
    }

    private Command parseList() throws MikeException {
        consume(TokenType.EOC, "Usage: list");
        return new ListCommand();
    }

    private Command parseMark() throws MikeException {
        String usage = "Usage: mark [number]";

        consume(TokenType.LITERAL, usage);
        String argument = previousToken().getText();

        consume(TokenType.EOC, usage);

        try {
            int taskNumber = Integer.parseInt(argument);
            return new MarkCommand(taskNumber);
        } catch(NumberFormatException e) {
            String errorMessage = "One, two, three, four, get the kid back through the door!\n" +
                    "'" + argument + "' is not an integer Sulley...";
            throw new MikeException(errorMessage);
        }
    }

    private Command parseUnmark() throws MikeException {
        String usage ="Usage: unmark [number]";

        consume(TokenType.LITERAL, usage);
        String argument = previousToken().getText();

        consume(TokenType.EOC, usage);

        try {
            int taskNumber = Integer.parseInt(argument);
            return new UnmarkCommand(taskNumber);
        } catch(NumberFormatException e) {
            String errorMessage = "One, two, three, four, get the kid back through the door!\n" +
                    "'" + argument + "' is not an integer Sulley...";
            throw new MikeException(errorMessage);
        }
    }

    private Command parseTodo() throws MikeException {
        String usage = "Usage: todo [description]";

        consume(TokenType.LITERAL, "Description missing.\n" + usage);
        String description = previousToken().getText().strip();

        consume(TokenType.EOC, usage);

        return new AddTodoCommand(description);
    }

    private Command parseDeadline() throws MikeException {
        String usage = "Usage: deadline [description] /by [date]";

        consume(TokenType.LITERAL, "Description missing.\n" + usage);
        String description = previousToken().getText().strip();

        consume(TokenType.FORWARD_DASH, usage);
        consume(TokenType.PARAM, usage);
        Token paramToken = previousToken();

        if (!paramToken.getText().equals("by")) {
            throw error(usage);
        }

        consume(TokenType.LITERAL, usage);
        String deadline = previousToken().getText().strip();

        consume(TokenType.EOC, usage);

        return new AddDeadlineCommand(description, deadline);
    }

    private Command parseEvent() throws MikeException {
        String usage = "Usage: event [description] /from [date] /to [date]";

        consume(TokenType.LITERAL, "Description missing.\n" + usage);
        String description = previousToken().getText().strip();

        consume(TokenType.FORWARD_DASH, usage);
        consume(TokenType.PARAM, usage);
        Token paramToken = previousToken();

        if (!paramToken.getText().equals("from")) {
            throw error(usage);
        }

        consume(TokenType.LITERAL, "Start date missing.\n" + usage);
        String startDate = previousToken().getText().strip();

        consume(TokenType.FORWARD_DASH, usage);
        consume(TokenType.PARAM, usage);
        paramToken = previousToken();

        if (!paramToken.getText().equals("to")) {
            throw error(usage);
        }

        consume(TokenType.LITERAL, "End date missing.\n" + usage);
        String endDate = previousToken().getText().strip();

        consume(TokenType.EOC, usage);

        return new AddEventCommand(description, startDate, endDate);
    }

    private Command parseDelete() throws MikeException {
        String usage = "Usage: delete [number]";

        consume(TokenType.LITERAL, usage);
        String argument = previousToken().getText();

        consume(TokenType.EOC, usage);

        try {
            int taskNumber = Integer.parseInt(argument);
            return new DeleteCommand(taskNumber);
        } catch(NumberFormatException e) {
            String errorMessage = "One, two, three, four, get the kid back through the door!\n" +
                    "'" + argument + "' is not an integer Sulley...";
            throw new MikeException(errorMessage);
        }
    }

    private MikeException error(String message) {
        return new MikeException(message);
    }

    private Token consume(TokenType type, String message) throws MikeException {
        if (check(type)) return advance();
        throw error(message);
    }

    private boolean check(TokenType type) {
        return peekToken().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previousToken();
    }

    private Token previousToken() {
        return tokens.get(current - 1);
    }

    private boolean isAtEnd() {
        return peekToken().getType() == TokenType.EOC;
    }

    private Token peekToken() {
        return tokens.get(current);
    }
}
