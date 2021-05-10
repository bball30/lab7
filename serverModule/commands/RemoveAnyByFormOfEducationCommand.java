package serverModule.commands;

import common.data.StudyGroup;
import common.exceptions.*;
import common.utility.User;
import serverModule.util.CollectionManager;
import serverModule.util.DatabaseCollectionManager;
import serverModule.util.ResponseOutputer;

/**
 * 'remove_by_id' command. Removes the element by its ID.
 */
public class RemoveAnyByFormOfEducationCommand extends Command {
    private CollectionManager collectionManager;
    private DatabaseCollectionManager databaseCollectionManager;

    public RemoveAnyByFormOfEducationCommand(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("remove_any_by_form_of_education formOfEducation", " удалить из коллекции один элемент, значение поля formOfEducation которого эквивалентно заданному");
        this.collectionManager = collectionManager;
        this.databaseCollectionManager = databaseCollectionManager;
    }

    /**
     * Executes the command.
     * @return Command execute status.
     */
    @Override
    public boolean execute(String argument, Object objectArgument, User user) {
        try {
            if (user == null) throw new NonAuthorizedUserException();
            if (argument.isEmpty() || objectArgument != null) throw new WrongAmountOfArgumentsException();
            if (collectionManager.collectionSize() == 0) throw new CollectionIsEmptyException();
            StudyGroup studyGroup = collectionManager.getByFormOfEducation(argument);
            if (studyGroup == null) throw new StudyGroupNotFoundException();
            if (!studyGroup.getOwner().equals(user)) throw new PermissionDeniedException();
            if (!databaseCollectionManager.checkStudyGroupByIdAndUserId(studyGroup.getId(), user)) throw new IllegalDatabaseEditException();
            databaseCollectionManager.deleteGroupById(studyGroup.getId());
            collectionManager.removeFromCollection(studyGroup);
            ResponseOutputer.append("Группа успешно удалена!\n");
            return true;
        } catch (WrongAmountOfArgumentsException exception) {
            ResponseOutputer.append("Использование: '" + getName() + "'\n");
        } catch (CollectionIsEmptyException exception) {
            ResponseOutputer.append("Коллекция пуста!\n");
        } catch (DatabaseManagerException e) {
            ResponseOutputer.append("Произошла ошибка при обращении к базе данных!\n");
        } catch (StudyGroupNotFoundException exception) {
            ResponseOutputer.append("Группы с такой формой обучения в коллекции нет!\n");
        } catch (IllegalDatabaseEditException exception) {
            ResponseOutputer.append("Произошло нелегальное изменение объекта в базе данных!\n");
            ResponseOutputer.append("Перезапустите клиент для избежания ошибок!\n");
        } catch (NonAuthorizedUserException e) {
            ResponseOutputer.append("Необходимо авторизоваться!\n");
        } catch (PermissionDeniedException exception) {
            ResponseOutputer.append("Принадлежащие другим пользователям объекты доступны только для чтения!\n");
        }
        return false;
    }
}
