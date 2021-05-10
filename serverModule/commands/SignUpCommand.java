package serverModule.commands;

import common.exceptions.DatabaseManagerException;
import common.exceptions.UserAlreadyExistException;
import common.exceptions.WrongAmountOfArgumentsException;
import common.utility.User;
import serverModule.util.DatabaseUserManager;
import serverModule.util.ResponseOutputer;

public class SignUpCommand extends Command {
    private DatabaseUserManager databaseUserManager;

    public SignUpCommand(DatabaseUserManager databaseUserManager) {
        super("sign_up", "регистрация нового пользователя");
        this.databaseUserManager = databaseUserManager;
    }

    @Override
    public boolean execute(String argument, Object objectArgument, User user) {
        try {
            if (!argument.isEmpty() || objectArgument != null) throw new WrongAmountOfArgumentsException();
            if (databaseUserManager.insertUser(user))
                ResponseOutputer.append("Регистрация прошла успешно!\n");
            else throw new UserAlreadyExistException();
            return true;
        } catch (WrongAmountOfArgumentsException e) {
            ResponseOutputer.append("У этой команды должен быть только один параметр: 'user'\n");
        } catch (DatabaseManagerException exception) {
            ResponseOutputer.append("Произошла ошибка при обращении к базе данных!\n");
        } catch (UserAlreadyExistException e) {
            ResponseOutputer.append("Этот пользователь уже существует!\n");
        } catch (ClassCastException e) {
            ResponseOutputer.append("Переданный клиентом объект неверен!\n");
        }
        return false;
    }
}
