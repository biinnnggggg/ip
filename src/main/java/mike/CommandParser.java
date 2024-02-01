package mike;

import mike.command.AddDeadlineCommand;
import mike.command.AddEventCommand;
import mike.command.AddTodoCommand;
import mike.command.Command;
import mike.command.DeleteCommand;
import mike.command.ExitCommand;
import mike.command.ListCommand;
import mike.command.MarkCommand;
import mike.command.UnmarkCommand;

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
            throw error("Say something.");
        default:
            String errorMessage = "'" + commandToken.getText() + "' is not recognized as a command.\n"
                    + "That is the weirdest thing you've ever said.";
            throw error(errorMessage);
        }
    }

    private Command parseExit() throws MikeException {
        consume(TokenType.EOC, "Usage: bye");
        return new ExitCommand();
    }

    private Command parseList() throws MikeException {
        String usage = "Usage: list";
        if (match(TokenType.FORWARD_DASH) && match(TokenType.PARAM)) {
            usage = usage + " /view [type] /attribute [attribute]";
            Token paramToken = previousToken();
            if (!paramToken.getText().equals("view")) {
                throw error(usage);
            }
            consume(TokenType.LITERAL, usage);
            String type = previousToken().getText().strip();

            ListViewType listViewType;

            switch (type) {
            case "date":
                listViewType = ListViewType.DATE;
                break;
            default:
                throw error("Invalid type");
            }

            consume(TokenType.FORWARD_DASH, usage);
            consume(TokenType.PARAM, usage);
            paramToken = previousToken();
            if (!paramToken.getText().equals("attribute")) {
                throw error(usage);
            }

            consume(TokenType.LITERAL, usage);
            String attribute = previousToken().getText();

            consume(TokenType.EOC, usage);

            ListView listView = new ListView(listViewType, attribute);
            return new ListCommand(listView);
        } else {
            consume(TokenType.EOC, "Usage: list");
            return new ListCommand(new ListView(ListViewType.NONE));
        }

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
            throw error(errorMessage);
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
            throw error(errorMessage);
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
            throw error(errorMessage);
        }
    }

    private boolean match(TokenType...types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private MikeException error(String message) {
        return new MikeException(message);
    }

    private Token consume(TokenType type, String message) throws MikeException {
        if (check(type)) {
            return advance();
        }
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
