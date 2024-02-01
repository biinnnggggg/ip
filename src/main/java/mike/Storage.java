package mike;

import mike.task.Deadline;
import mike.task.Event;
import mike.task.Task;
import mike.task.Todo;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

class Storage {
    private final String fileDirectory;
    private final String filePath;
    private File file;

    Storage(String filePath) {
        this.fileDirectory = filePath.split("/")[1];
        this.filePath = filePath;
    }

    /**
     * Return a TaskList object constructed from file data.
     * @return TaskList
     */
    public TaskList load() {
        try {
            if (new File(fileDirectory).mkdirs()) {
                Ui.display("File location created at " + filePath);
            } else {
                Ui.display("File location already exists");
            }

            File file = new File(filePath);
            this.file = file;

            if (file.createNewFile()) {
                Ui.display("File created: " + file.getName());
            } else {
                Ui.display("File already exists");
            }

            Scanner fileScanner = new Scanner(file);
            String line;
            Task newTask;
            TaskList taskList = new TaskList();

            while (fileScanner.hasNext()) {
                line = fileScanner.nextLine();
                newTask = extractTask(line);
                taskList.add(newTask);
            }

            return taskList;
        } catch (IOException e) {
            Ui.displayError("404 File not found");
            return new TaskList();
        } catch (MikeException e) {
            Ui.displayError(e.getMessage());
            return new TaskList();
        }
    }

    public void writeToFile(TaskList taskList) {
        try (PrintWriter out = new PrintWriter(filePath)) {
            for (Task task : taskList) {
                out.println(task.getFileEncoding());
            }
        } catch (IOException e) {
            Ui.display(e.getMessage());
        }
    }

    private Task extractTask(String line) throws IndexOutOfBoundsException, MikeException {
        String[] taskInformation = line.split(",");
        String taskType = taskInformation[0];
        String taskDescription = taskInformation[1];
        boolean taskIsCompleted = taskInformation[2].equals("true");

        Task newTask;

        switch (taskType) {
        case "Todo":
            newTask = new Todo(taskDescription);
            break;
        case "Deadline": // format: taskType,taskDescription,taskDone,deadline
            String deadline = taskInformation[3];
            newTask = new Deadline(taskDescription, deadline);
            break;
        case "Event": // format: taskType,taskDescription,taskDone,startDate,endDate
            String startDate = taskInformation[3];
            String endDate = taskInformation[4];
            newTask = new Event(taskDescription, startDate, endDate);
            break;
        default:
            throw new MikeException("File corrupted. Unable to load saved data.");
        }

        if (taskIsCompleted) {
            newTask.markAsDone();
        }

        return newTask;
    }
}
