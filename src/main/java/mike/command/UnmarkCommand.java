package mike.command;

import mike.MikeException;
import mike.TaskList;
import mike.Ui;
import mike.command.Command;
import mike.task.Task;

/**
 * Command to unmark a task.
 * @author ningc
 */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Constructor.
     * @param taskNumber The number indexing the task.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public String execute(TaskList taskList) throws MikeException {
        int taskIndex = taskNumber - 1;

        if (taskList.isEmpty()) {
            throw new MikeException("There are no tasks to mark. Please add a task first.");
        } else if (taskIndex >= taskList.size() || taskIndex < 0) {
            throw new MikeException("That's suspicious. Please enter a number in the range 1-" + taskList.size() + ".");
        }

        Task task = taskList.get(taskIndex);
        return task.markAsNotDone();
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
