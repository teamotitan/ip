package Timo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


class Task {
    private String tasktype = "T";
    private boolean mark;
    private final String description;
    public Task(boolean mark, String description) {
        this.mark = mark;
        this.description = description;
    }

    public void markDone() {
        this.mark = true;
        return;
    }

    public void markUndone() {
        this.mark = false;
        return;
    }

    public String getTask() {
        return this.tasktype;
    }

    public String getStatusIcon() {
        return (this.mark ? "X" : " ");
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + this.description;
    }
}

class Todo extends Task {
    private final String tasktype = "T";


    public Todo(boolean mark, String val) {
        super(mark, val);
    }


    @Override
    public String toString() {
        return "[" + this.tasktype + "]" + super.toString();
    }
}

class Deadline extends Task {
    private final LocalDateTime date;
    private final String tasktype = "D";

    public Deadline(boolean mark, String val, LocalDateTime date) {
        super(mark, val);
        this.date = date;
    }

    @Override
    public String toString() {
        return "[" + this.tasktype + "]" + super.toString()
                + " (by: " + this.date.format(DateTimeFormatter.ofPattern("MMM dd yyyy HHmm"))
                + ")";
    }
}

class Event extends Task {
    private final String from;
    private final String to;
    private final String tasktype = "E";

    public Event(boolean mark, String val, String from, String to) {
        super(mark, val);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[" + this.tasktype + "]" + super.toString() + " (from: " + this.from + " to: " + this.to + ")";
    }
}

class TimoException extends Exception {

    public TimoException(String errorMessage) {
        super(errorMessage);
    }

}

//Timo.Storage: beginning I read from the file, end I update the file
class Storage {
    private final String filepath;

    public Storage(String filepath) {
        this.filepath = filepath;
    }

    /**
     * Loads tasks from a file specified by the `filepath` and returns them as a list of `Task` objects.
     * <p>
     * The tasks in the file are expected to be in a specific format, indicating the type of task
     * (Todo, Deadline, or Event) and its completion status.
     * </p>
     *
     * @return A `List` of `Task` objects representing the tasks stored in the file.
     *
     * @throws FileNotFoundException if the file at the specified `filepath` does not exist.
     *
     * @see Task
     * @see Todo
     * @see Deadline
     * @see Event
     */
    public List<Task> load() throws FileNotFoundException {
        File f = new File(this.filepath);

        //initialise array to store the values
        List<Task> arr = new ArrayList<Task>();

        //check if the file exists
        if (f.exists()) {
            Scanner s = new Scanner(f);
            while (s.hasNext()) {
                String tmp = s.nextLine();
                if (tmp.startsWith("[T]")) {
                    String[] a = tmp.split("] ", 2);
                    if (Character.compare(tmp.charAt(4), 'X') == 0) {
                        arr.add(new Todo(true, a[1]));
                    } else {
                        arr.add(new Todo(false, a[1]));
                    }
                } else if (tmp.startsWith("[D]")) {
                    //remove the [D][?] from the line
                    String a = tmp.split("] ")[1];

                    //get the important values to create the Timo.Deadline
                    String[] b = a.split(" \\(by: |\\)");

                    LocalDateTime datetime = LocalDateTime.parse(b[1], DateTimeFormatter.ofPattern("MMM dd yyyy HHmm"));


                    //see if the task has been done or not
                    if (Character.compare(tmp.charAt(4), 'X') == 0) {
                        arr.add(new Deadline(true, b[0], datetime));
                    } else {
                        arr.add(new Deadline(false, b[0], datetime));
                    }
                } else {
                    //removing the [E][?] from the line
                    String details = tmp.split("] ", 2)[1];
                    //getting important values to create the Timo.Event
                    String[] split_up = details.split(" \\(from: | to: |\\)");

                    //see if the task has been done or not
                    if (Character.compare(tmp.charAt(4), 'X') == 0) {
                        arr.add(new Event(true, split_up[0], split_up[1], split_up[2]));
                    } else {
                        arr.add(new Event(false, split_up[0], split_up[1], split_up[2]));
                    }
                }
            }
            return arr;
        } else {
            throw new FileNotFoundException("file not found!");
        }

    }

    /**
     * Stores a list of tasks to a file specified by the `filepath`.
     * <p>
     * If the file does not exist, it will be created. The method overwrites any existing content
     * in the file and writes each task in the provided list to the file, with each task on a new line.
     * </p>
     *
     * @param arr A `List` of `Task` objects to be stored in the file.
     *
     * @see Task
     */
    public void store(List<Task> arr) {
        //create new file if file does not exist
        File file = new File(this.filepath);


        try {
            boolean filecreated = file.createNewFile();
            //delete all contents in the file
            FileWriter fil = new FileWriter(this.filepath);
            fil.write("");
            fil.close();


            //create FileWriter to append to file
            FileWriter fw = new FileWriter(this.filepath, true);
            for (Task i: arr) {
                fw.write(i + "\n");
            }
            fw.close();

        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }
}

//Timo.TaskList: operations to add and delete tasks in the list
//has operations to return list
class TaskList {
    private final List<Task> arr;

    public TaskList(List<Task> arr) {
        this.arr = arr;
    }

    public TaskList() {

        this.arr = new ArrayList<Task>();
    }

    /**
     * adds a Task to the array
     * @param task a task
     */
    public void add(Task task) {

        this.arr.add(task);
    }

    /**
     * removes the Task given the Task number and returns the Task that is removed
     * @param number the task number
     * @return Task
     */
    public Task delete(int number) {
        return this.arr.remove(number);
    }

    /**
     * displays the Task in the array
     * @return list of tasks
     */
    public List<Task> showList() {
        return this.arr;
    }

    /**
     * Marks the Task given the Task number, and returns the Task
     * @param num the task number
     * @return Task
     */
    public Task mark(int num) {
        Task chosen = this.arr.get(num - 1);
        chosen.markDone();
        return chosen;
    }

    /**
     * Unmark the Task given the Task number, and returns the Task
     * @param num the task number
     * @return Task
     */
    public Task unmark(int num) {
        Task chosen = this.arr.get(num - 1);
        chosen.markUndone();;
        return chosen;
    }
}


class UI {

    /**
     * greets the user
     */
    public void greet() {
        System.out.println("----------------------------");
        System.out.println("Hello! I'm Timo.\nWhat can I do for you?");
        System.out.println("----------------------------");
    }

    /**
     * says goodbye to the user
     */
    public void bye() {
        System.out.println("----------------------------");
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("----------------------------");
    }

    /**
     * given a TaskList, prints our the list items
     * @param lst the tasklist
     *
     * @see TaskList
     */
    public void printList(TaskList lst) {
        System.out.println("----------------------------");
        System.out.println("Here are the tasks in your list:");
        for (int i = 1; i <= lst.showList().size(); i++) {
            Task chosen = lst.showList().get(i - 1);
            System.out.println(i + ". " + chosen);
        }
        System.out.println("----------------------------");
    }

    /**
     * reads user input, and returns it
     * @return String
     */
    public String readCommand() {
        Scanner echo = new Scanner(System.in);
        return echo.nextLine();
    }

    /**
     * given Task, prints out that the Task is marked
     * @param chosen the task that is marked
     */
    public void printMark(Task chosen) {
        System.out.println("----------------------------");
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(chosen);
        System.out.println("----------------------------");
    }

    /**
     * given Task, prints out that the Task is unmarked
     * @param chosen the task that is unmarked
     */
    public void printUnmark(Task chosen) {
        System.out.println("----------------------------");
        System.out.println("Nice! I've marked this task as not done yet:");
        System.out.println(chosen);
        System.out.println("----------------------------");
    }

    /**
     * given Todo and size, prints out that Todo task has been added, and prints out the total size of array
     * @param todo the todo task
     * @param size the size of the task list
     */
    public void printTodo(Task todo, int size) {
        System.out.println("----------------------------");
        System.out.println("Got it. I've added this task:");
        System.out.println(todo);
        System.out.println("Now you have " + size + " tasks in the list.");
        System.out.println("----------------------------");
    }

    /**
     * given Deadline and size, prints out that Deadline task has been added, and prints out the total size of array
     * @param deadline the deadline task
     * @param size the size of the task list
     */
    public void printDeadline(Deadline deadline, int size) {
        System.out.println("----------------------------");
        System.out.println("Got it. I've added this task:");
        System.out.println(deadline);
        System.out.println("Now you have " + size + " tasks in the list.");
        System.out.println("----------------------------");
    }

    /**
     * prints our Deadline error
     */
    public void printDeadlineError() {
        System.out.println("----------------------------");
        System.out.println("deadline usage: deadline <task> /by yyyy-mm-dd <time/24hr format>");
        System.out.println("----------------------------");
    }

    /**
     * given Event task and size, prints out that Event task has been added, and prints out the total size of the array
     * @param event the event task
     * @param size the size of the task list
     */
    public void printEvent(Event event, int size) {
        System.out.println("----------------------------");
        System.out.println("Got it. I've added this task:");
        System.out.println(event.toString());
        System.out.println("Now you have " + size + " tasks in the list.");
        System.out.println("----------------------------");
    }

    /**
     * given the task and the size, prints out the task that was deleted and the size of the array
     * @param task the task
     * @param size the size of task list
     */
    public void printDelete(Task task, int size) {
        System.out.println("----------------------------");
        System.out.println("Got it. I've removed this task:");
        System.out.println(task.toString());
        System.out.println("Now you have " + size + " tasks in the list.");
        System.out.println("----------------------------");
    }

    /**
     * prints out the error
     * @param e the unknown command error
     */
    public void printUnknownCommandError(TimoException e) {
        System.out.println("----------------------------");
        System.out.println(e);
        System.out.println("----------------------------");
    }

}


//Timo.Parser: deals with making sense of the commands
//parser deal, then send commands to ui
class Parser {
    private final UI ui;
    private final Storage storage;
    private final TaskList taskList;
    public Parser(UI ui, Storage storage, TaskList taskList) {
        this.ui = ui;
        this.storage = storage;
        this.taskList = taskList;
    }

    /**
     * Parses and executes a given command string.
     * <p>
     * The method interprets the command and performs the corresponding action, such as marking tasks,
     * adding tasks (Todo, Deadline, Event), deleting tasks, printing the task list, or exiting the application.
     * If the command is not recognized or lacks necessary arguments, a `TimoException` is thrown.
     * </p>
     *
     * @param command The command string input by the user.
     *
     * @throws TimoException If the command is invalid or does not include the necessary arguments.
     *
     * @see TaskList
     * @see Task
     * @see Todo
     * @see Deadline
     * @see Event
     * @see TimoException
     */
    public void parse(String command) throws TimoException {
        switch (command) {
        case "bye":
            this.ui.bye();
            this.storage.store(this.taskList.showList());
            break;

        case "list":
            this.ui.printList(this.taskList);
            break;

        case "mark":
            String taskNumber = String.valueOf(command.charAt(command.length() - 1));

            //get the Timo.Task number to mark
            int markTarget = Integer.parseInt(taskNumber);

            //find the task to mark
            Task markedTask = this.taskList.mark(markTarget);
            this.ui.printMark(markedTask);
            break;

        case "unmark":

            //get the Timo.Task number to unmark
            int unmarkTarget = Integer.parseInt(String.valueOf(command.charAt(command.length() - 1)));

            //find the task to unmark
            Task unmarkedTask = this.taskList.unmark(unmarkTarget);
            this.ui.printUnmark(unmarkedTask);
            break;

        case "todo":
            String[] todoCommands = command.split(" ", 2);
            if (todoCommands.length != 2) {
                throw new TimoException("Usage todo: todo <task> (need argument)");
            }
            Todo task = new Todo(false, todoCommands[1]);
            this.taskList.add(task);
            this.ui.printTodo(task, this.taskList.showList().size());
            break;

        case "deadline":
            String[] deadlineCommands = command.split("deadline |/by ");
            String todo = deadlineCommands[1];
            String datetime = deadlineCommands[2].trim();
            DateTimeFormatter a = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

            try {
                LocalDateTime date = LocalDateTime.parse(datetime, a);
                System.out.println(date);
                Deadline deadline = new Deadline(false, todo, date);
                this.taskList.add(deadline);
                this.ui.printDeadline(deadline, this.taskList.showList().size());
                break;
            } catch (DateTimeException e) {
                this.ui.printDeadlineError();
                break;
            }
        case "event":
            String[] eventCommands = command.split("event |/from |/to ");
            Event event = new Event(false, eventCommands[1], eventCommands[2], eventCommands[3]);
            this.taskList.add(event);
            this.ui.printEvent(event, this.taskList.showList().size());
            break;
        case "delete":
            //get the Timo.Task number to delete
            int deleteTarget = Integer.parseInt(String.valueOf(command.charAt(command.length() - 1)));

            // Timo.Task that is deleted
            Task deleteTask = this.taskList.delete(deleteTarget);
            this.ui.printDelete(deleteTask, this.taskList.showList().size());
            break;

        case "find":
            String phrase = command.split(" ", 2)[1];

            // TaskList to print out
            TaskList temporaryLst = new TaskList();

            for (Task currentTask: this.taskList.showList()) {
                if (currentTask.toString().contains(phrase)) {
                    temporaryLst.add(currentTask);
                }
            }
            this.ui.printList(temporaryLst);
            break;
        default:
            throw new TimoException("I'm sorry, I do not know what that means");
        }
    }
}

/**
 * main class Timo
 */
public class Timo {

    private final Storage storage;
    private TaskList tasks;
    private final UI ui;

    private final Parser parser;

    /**
     * initialises Timo with ui, storage and parser
     * @param filepath the path to store the data
     */
    public Timo(String filepath) {
        ui = new UI();
        storage = new Storage(filepath);
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException e) {
            tasks = new TaskList();
        }
        parser = new Parser(ui, storage, tasks);
    }

    /**
     * run method which runs the entire program
     */
    public void run() {
        //welcome
        ui.greet();

        //print list initially
        ui.printList(this.tasks);

        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                if (fullCommand.equals("bye")) {
                    isExit = true;
                }
                parser.parse(fullCommand);
            } catch (TimoException e) {
                this.ui.printUnknownCommandError(e);
            }
        }
    }

    public static void main(String[] args) {
        new Timo("list.txt").run();
    }
}
