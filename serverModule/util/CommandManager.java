package serverModule.util;

import serverModule.commands.Command;
import common.utility.User;

import java.util.ArrayList;
import java.util.List;


public class CommandManager {
    private final int COMMAND_HISTORY_SIZE = 14;
    private String[] commandHistory = new String[COMMAND_HISTORY_SIZE];

    private final List<Command> commands = new ArrayList<>();
    private final Command helpCommand;
    private final Command infoCommand;
    private final Command showCommand;
    private final Command addCommand;
    private final Command updateCommand;
    private final Command removeByIdCommand;
    private final Command clearCommand;
    private final Command executeScriptCommand;
    private final Command exitCommand;
    private final Command removeGreaterCommand;
    private final Command removeLowerCommand;
    private final Command historyCommand;
    private final Command removeAnyByFormOfEducationCommand;
    private final Command printUniqueGroupAdminCommand;
    private final Command printFieldDescendingShouldBeExpelledCommand;
    private final Command signUpCommand;
    private final Command signInCommand;
    private final Command logOutCommand;

    public CommandManager(Command helpCommand, Command infoCommand, Command showCommand, Command addCommand,
                          Command updateCommand, Command removeByIdCommand, Command clearCommand,
                          Command executeScriptCommand, Command exitCommand, Command removeGreaterCommand,
                          Command removeLowerCommand, Command historyCommand, Command removeAnyByFormOfEducationCommand,
                          Command printUniqueGroupAdminCommand, Command printFieldDescendingShouldBeExpelledCommand,
                          Command signUpCommand, Command signInCommand, Command logOutCommand) {
        this.helpCommand = helpCommand;
        this.infoCommand = infoCommand;
        this.showCommand = showCommand;
        this.addCommand = addCommand;
        this.updateCommand = updateCommand;
        this.removeByIdCommand = removeByIdCommand;
        this.clearCommand = clearCommand;
        this.executeScriptCommand = executeScriptCommand;
        this.exitCommand = exitCommand;
        this.removeGreaterCommand = removeGreaterCommand;
        this.removeLowerCommand = removeLowerCommand;
        this.historyCommand = historyCommand;
        this.removeAnyByFormOfEducationCommand = removeAnyByFormOfEducationCommand;
        this.printUniqueGroupAdminCommand = printUniqueGroupAdminCommand;
        this.printFieldDescendingShouldBeExpelledCommand = printFieldDescendingShouldBeExpelledCommand;
        this.signUpCommand = signUpCommand;
        this.signInCommand = signInCommand;
        this.logOutCommand = logOutCommand;

        commands.add(helpCommand);
        commands.add(infoCommand);
        commands.add(showCommand);
        commands.add(addCommand);
        commands.add(updateCommand);
        commands.add(removeByIdCommand);
        commands.add(clearCommand);
        commands.add(exitCommand);
        commands.add(executeScriptCommand);
        commands.add(historyCommand);
        commands.add(removeGreaterCommand);
        commands.add(removeLowerCommand);
        commands.add(removeAnyByFormOfEducationCommand);
        commands.add(printUniqueGroupAdminCommand);
        commands.add(printFieldDescendingShouldBeExpelledCommand);
        commands.add(logOutCommand);
        commands.add(new Command("log_in", "авторизоваться") {
            @Override
            public boolean execute(String argument, Object objectArgument, User user) {
                return false;
            }
        });
    }

    /**
     * @return List of manager's com.serverModule.commands.
     */
    public List<Command> getCommands() {
        return commands;
    }


    /**
     * Prints info about the all com.serverModule.commands.
     * @param argument Its argument.
     * @return Command exit status.
     */
    public boolean help(String argument, Object objectArgument, User user) {
        if (helpCommand.execute(argument, objectArgument, user)) {
            for (Command command : commands) {
                ResponseOutputer.appendTable(command.getName(), command.getDescription());
            }
            return true;
        } else return false;
    }

    /**
     * Executes needed command.
     * @param argument Its argument.
     * @return Command exit status.
     */
    public boolean info(String argument, Object objectArgument, User user) {
        return infoCommand.execute(argument, objectArgument, user);
    }

    /**
     * Executes needed command.
     * @param argument Its argument.
     * @return Command exit status.
     */
    public boolean show(String argument, Object objectArgument, User user) {
        return showCommand.execute(argument, objectArgument, user);
    }

    /**
     * Executes needed command.
     * @param argument Its argument.
     * @return Command exit status.
     */
    public boolean exit(String argument, Object objectArgument, User user) {
        return exitCommand.execute(argument, objectArgument, user);
    }

    public boolean sign_up(String argument, Object objectArgument, User user) {
        return signUpCommand.execute(argument, objectArgument, user);
    }

    public boolean sign_in(String argument, Object objectArgument, User user) {
        return signInCommand.execute(argument, objectArgument, user);
    }

    public boolean log_out(String argument, Object objectArgument, User user) {
        return logOutCommand.execute(argument, objectArgument, user);
    }

    /**
     * Executes needed command.
     * @param argument Its argument.
     * @return Command exit status.
     */
    public boolean add(String argument, Object objectArgument, User user) {
        return addCommand.execute(argument, objectArgument, user);
    }
    /**
     * Executes needed command.
     * @param argument Its argument.
     * @return Command exit status.
     */
    public boolean update(String argument, Object objectArgument, User user) {
        return updateCommand.execute(argument, objectArgument, user);
    }

    /**
     * Executes needed command.
     * @param argument Its argument.
     * @return Command exit status.
     */
    public boolean removeById(String argument, Object objectArgument, User user) {
        return removeByIdCommand.execute(argument, objectArgument, user);
    }

    /**
     * Executes needed command.
     * @param argument Its argument.
     * @return Command exit status.
     */
    public boolean clear(String argument, Object objectArgument, User user) {
        return clearCommand.execute(argument, objectArgument, user);
    }

    /**
     * Executes needed command.
     * @param argument Its argument.
     * @return Command exit status.
     */
    public boolean executeScript(String argument, Object objectArgument, User user) {
        return executeScriptCommand.execute(argument, objectArgument, user);
    }

    /**
     * Executes needed command.
     * @param argument Its argument.
     * @return Command exit status.
     */
    public boolean removeGreater(String argument, Object objectArgument, User user) {
        return removeGreaterCommand.execute(argument, objectArgument, user);
    }

    /**
     * Executes needed command.
     * @param argument Its argument.
     * @return Command exit status.
     */
    public boolean removeLower(String argument, Object objectArgument, User user) {
        return removeLowerCommand.execute(argument, objectArgument, user);
    }

    /**
     * Executes needed command.
     * @param argument Its argument.
     * @return Command exit status.
     */
    public boolean removeAny(String argument, Object objectArgument, User user) {
        return removeAnyByFormOfEducationCommand.execute(argument, objectArgument, user);
    }

    /**
     * Executes needed command.
     * @param argument Its argument.
     * @return Command exit status.
     */
    public boolean printUniqueGroupAdmin(String argument, Object objectArgument, User user) {
        return printUniqueGroupAdminCommand.execute(argument, objectArgument, user);
    }
    /**
     * Executes needed command.
     * @param argument Its argument.
     * @return Command exit status.
     */
    public boolean printFieldDescendingShouldBeExpelled(String argument, Object objectArgument, User user) {
        return printFieldDescendingShouldBeExpelledCommand.execute(argument, objectArgument, user);
    }


    /**
     * Adds command to command history.
     * @param commandToAdd command To Add
     * @param user user
     */
    public void addToHistory(String commandToAdd, User user) {
        for (Command command : commands) {
            if (command.getName().split(" ")[0].equals(commandToAdd)) {
                for (int i = COMMAND_HISTORY_SIZE - 1; i > 0; i--) {
                    commandHistory[i] = commandHistory[i - 1];
                }
                commandHistory[0] = commandToAdd;
            }
        }
    }

    /**
     * Prints the history of used serverModule.commands.
     * @param argument Its argument.
     * @return Command exit status.
     */
    public boolean history(String argument, Object objectArgument, User user) {
        if (historyCommand.execute(argument, objectArgument, user)) {

            if (commandHistory.length == 0) {
                ResponseOutputer.append("Ни одной команды еще не было использовано!\n");
                return false;
            }
            ResponseOutputer.append("Последние использованные команды:\n");
            for (int i = 0; i < commandHistory.length; i++) {
                if (commandHistory[i] != null) ResponseOutputer.append(" " + commandHistory[i] + "\n");
            }
            return true;
        }
        return false;
    }
}
