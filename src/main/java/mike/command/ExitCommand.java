package mike.command;

import mike.TaskList;
import mike.Ui;
import mike.command.Command;

public class ExitCommand extends Command {
    public ExitCommand() {
        super("");
    }

    @Override
    public void execute(TaskList taskList) {
        Ui.farewell();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}

