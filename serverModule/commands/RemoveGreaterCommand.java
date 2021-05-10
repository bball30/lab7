package serverModule.commands;

import common.data.StudyGroup;
import common.exceptions.*;
import common.utility.StudyGroupLite;
import common.utility.User;
import serverModule.util.CollectionManager;
import serverModule.util.DatabaseCollectionManager;
import serverModule.util.ResponseOutputer;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 'remove_greater' command. Removes elements greater than user entered.
 */
public class RemoveGreaterCommand extends Command {
    private CollectionManager collectionManager;
    private DatabaseCollectionManager databaseCollectionManager;


    public RemoveGreaterCommand(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("remove_greater {element}", "удалить из коллекции все элементы, превышающие заданный");
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
            if (!argument.isEmpty() || objectArgument != null) throw new WrongAmountOfArgumentsException();
            if (collectionManager.collectionSize() == 0) throw new CollectionIsEmptyException();
            StudyGroupLite groupLite = (StudyGroupLite) objectArgument;
            StudyGroup studyGroupToCompare = new StudyGroup(
                    collectionManager.generateNextId(),
                    groupLite.getName(),
                    groupLite.getCoordinates(),
                    LocalDateTime.now(),
                    groupLite.getStudentsCount(),
                    groupLite.getShouldBeExpelled(),
                    groupLite.getAverageMark(),
                    groupLite.getFormOfEducation(),
                    groupLite.getGroupAdmin(),
                    user
            );
            List<StudyGroup> groups = collectionManager.getGreater(studyGroupToCompare);
            int k = 0;
            for (StudyGroup group : groups) {
                if (!group.getOwner().equals(user)) continue;
                if (!databaseCollectionManager.checkStudyGroupByIdAndUserId(group.getId(), user)) throw new IllegalDatabaseEditException();
                databaseCollectionManager.deleteGroupById(group.getId());
                collectionManager.removeFromCollection(group);
                k ++ ;
            }
            ResponseOutputer.append("Удалено групп: " + k);
            return true;
        } catch (WrongAmountOfArgumentsException exception) {
            ResponseOutputer.append("Использование: '" + getName() + "'\n");
        } catch (CollectionIsEmptyException exception) {
            ResponseOutputer.append("Коллекция пуста!\n");
        } catch (DatabaseManagerException e) {
            ResponseOutputer.append("Произошла ошибка при обращении к базе данных!\n");
        } catch (IllegalDatabaseEditException exception) {
            ResponseOutputer.append("Произошло нелегальное изменение объекта в базе данных!\n");
            ResponseOutputer.append("Перезапустите клиент для избежания ошибок!\n");
        } catch (NonAuthorizedUserException e) {
            ResponseOutputer.append("Необходимо авторизоваться!\n");
        }
        return false;
    }
}
