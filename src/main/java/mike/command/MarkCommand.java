package mike.command;

import mike.MikeException;
import mike.TaskList;
import mike.Ui;
import mike.task.Task;

/**
 * Marks task as done.
 * @author ningc
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Constructor.
     * @param taskNumber The number that refers to the task.
     */
    public MarkCommand(int taskNumber) {
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
        return task.markAsDone();
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
