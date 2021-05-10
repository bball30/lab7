package serverModule.commands;

import common.data.Coordinates;
import common.data.FormOfEducation;
import common.data.Person;
import common.data.StudyGroup;
import common.exceptions.*;
import common.utility.StudyGroupLite;
import common.utility.User;
import serverModule.util.CollectionManager;
import serverModule.util.DatabaseCollectionManager;
import serverModule.util.ResponseOutputer;


import java.time.LocalDateTime;

/**
 * 'update' command. Updates the information about selected studyGroup.
 */
public class UpdateCommand extends Command {
    private final CollectionManager collectionManager;
    private final DatabaseCollectionManager databaseCollectionManager;

    public UpdateCommand(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("update <ID> {element}", "обновить значение элемента коллекции по ID");
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
            if (argument.isEmpty() || objectArgument == null) throw new WrongAmountOfArgumentsException();
            if (collectionManager.collectionSize() == 0) throw new CollectionIsEmptyException();

            int id = Integer.parseInt(argument);
            StudyGroup studyGroup = collectionManager.getById(id);
            if (studyGroup == null) throw new StudyGroupNotFoundException();
            if (!studyGroup.getOwner().equals(user)) throw new PermissionDeniedException();
            if (!databaseCollectionManager.checkStudyGroupByIdAndUserId(studyGroup.getId(), user)) throw new IllegalDatabaseEditException();
            StudyGroupLite groupLite = (StudyGroupLite) objectArgument;

            databaseCollectionManager.updateStudyGroupByID(id, groupLite);

            if (studyGroup == null) throw new StudyGroupNotFoundException();

            String name = groupLite.getName() == null ? studyGroup.getName() : groupLite.getName();
            Coordinates coordinates = groupLite.getCoordinates() == null ? studyGroup.getCoordinates() : groupLite.getCoordinates();
            LocalDateTime creationDate = studyGroup.getCreationDate();
            long studentsCount = groupLite.getStudentsCount() == -1 ? studyGroup.getStudentsCount() : groupLite.getStudentsCount();
            long shouldBeExpelled = groupLite.getShouldBeExpelled() == -1 ? studyGroup.getShouldBeExpelled() : groupLite.getShouldBeExpelled();
            int averageMark = groupLite.getAverageMark() == -1 ? studyGroup.getAverageMark() : groupLite.getAverageMark();
            FormOfEducation formOfEducation = groupLite.getFormOfEducation() == null ? studyGroup.getFormOfEducation() : groupLite.getFormOfEducation();
            Person groupAdmin = groupLite.getGroupAdmin() == null ? studyGroup.getGroupAdmin() : groupLite.getGroupAdmin();

            collectionManager.removeFromCollection(studyGroup);

            collectionManager.addToCollection(new StudyGroup(
                    id,
                    name,
                    coordinates,
                    creationDate,
                    studentsCount,
                    shouldBeExpelled,
                    averageMark,
                    formOfEducation,
                    groupAdmin,
                    user
            ));
            ResponseOutputer.append("Группа успешно изменена!\n");
            return true;
        } catch (WrongAmountOfArgumentsException exception) {
            ResponseOutputer.append("Использование: '" + getName() + "'\n");
        } catch (CollectionIsEmptyException exception) {
            ResponseOutputer.append("Коллекция пуста!\n");
        } catch (NumberFormatException exception) {
            ResponseOutputer.append("ID должен быть представлен числом!\n");
        } catch (StudyGroupNotFoundException exception) {
            ResponseOutputer.append("Группы с таким ID в коллекции нет!\n");
        } catch (PermissionDeniedException e) {
            ResponseOutputer.append("Принадлежащие другим пользователям объекты доступны только для чтения!\n");
        } catch (DatabaseManagerException e) {
            ResponseOutputer.append("Произошла ошибка при обращении к базе данных!\n");
        } catch (IllegalDatabaseEditException e) {
            ResponseOutputer.append("Произошло нелегальное изменение объекта в базе данных!\n");
            ResponseOutputer.append("Перезапустите клиент для избежания ошибок!\n");
        } catch (NonAuthorizedUserException e) {
            ResponseOutputer.append("Необходимо авторизоваться!\n");
        }
        return false;
    }
}
