package serverModule.util;

import common.data.*;
import common.exceptions.DatabaseManagerException;
import common.utility.StudyGroupLite;
import common.utility.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.CopyOnWriteArraySet;
//import java.util.LinkedHashSet;

public class DatabaseCollectionManager {
    private final String SELECT_ALL_STUDY_GROUPS = "SELECT * FROM " + DatabaseManager.GROUP_TABLE;
    private final String SELECT_STUDY_GROUP_BY_ID = SELECT_ALL_STUDY_GROUPS + " WHERE " +
            DatabaseManager.GROUP_TABLE_ID_COLUMN + " = ?";
    private final String SELECT_STUDY_GROUP_BY_ID_AND_USER_ID = SELECT_STUDY_GROUP_BY_ID + " AND " +
            DatabaseManager.GROUP_TABLE_USER_ID_COLUMN + " = ?";
    private final String INSERT_STUDY_GROUP = "INSERT INTO " +
            DatabaseManager.GROUP_TABLE + " (" +
            DatabaseManager.GROUP_TABLE_NAME_COLUMN + ", " +
            DatabaseManager.GROUP_TABLE_COORDINATES_ID_COLUMN + ", " +
            DatabaseManager.GROUP_TABLE_CREATION_DATE_COLUMN + ", " +
            DatabaseManager.GROUP_TABLE_STUDENTS_COUNT_COLUMN + ", " +
            DatabaseManager.GROUP_TABLE_SHOULD_BE_EXPELLED_COLUMN + ", " +
            DatabaseManager.GROUP_TABLE_AVERAGE_MARK_COLUMN + ", " +
            DatabaseManager.GROUP_TABLE_FORM_OF_EDUCATION_COLUMN + ", " +
            DatabaseManager.GROUP_TABLE_PERSON_ID_COLUMN + ", " +
            DatabaseManager.GROUP_TABLE_USER_ID_COLUMN + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String DELETE_GROUP_BY_ID = "DELETE FROM " + DatabaseManager.GROUP_TABLE +
            " WHERE " + DatabaseManager.GROUP_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_GROUP_NAME_BY_ID = "UPDATE " + DatabaseManager.GROUP_TABLE + " SET " +
            DatabaseManager.GROUP_TABLE_NAME_COLUMN + " = ?" + " WHERE " +
            DatabaseManager.GROUP_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_GROUP_STUDENTS_COUNT_BY_ID = "UPDATE " + DatabaseManager.GROUP_TABLE + " SET " +
            DatabaseManager.GROUP_TABLE_STUDENTS_COUNT_COLUMN + " = ?" + " WHERE " +
            DatabaseManager.GROUP_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_GROUP_SHOULD_BE_EXPELLED_BY_ID = "UPDATE " + DatabaseManager.GROUP_TABLE + " SET " +
            DatabaseManager.GROUP_TABLE_SHOULD_BE_EXPELLED_COLUMN + " = ?" + " WHERE " +
            DatabaseManager.GROUP_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_GROUP_AVERAGE_MARK_BY_ID = "UPDATE " + DatabaseManager.GROUP_TABLE + " SET " +
            DatabaseManager.GROUP_TABLE_AVERAGE_MARK_COLUMN + " = ?" + " WHERE " +
            DatabaseManager.GROUP_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_GROUP_FORM_OF_EDUCATION_BY_ID = "UPDATE " + DatabaseManager.GROUP_TABLE + " SET " +
            DatabaseManager.GROUP_TABLE_FORM_OF_EDUCATION_COLUMN + " = ?" + " WHERE " +
            DatabaseManager.GROUP_TABLE_ID_COLUMN + " = ?";

    private final String SELECT_ALL_COORDINATES = "SELECT * FROM " + DatabaseManager.COORDINATES_TABLE;
    private final String SELECT_COORDINATES_BY_ID = SELECT_ALL_COORDINATES + " WHERE " + DatabaseManager.COORDINATES_TABLE_ID_COLUMN + " = ?";
    private final String DELETE_COORDINATES_BY_ID = "DELETE FROM " + DatabaseManager.COORDINATES_TABLE +
            " WHERE " + DatabaseManager.COORDINATES_TABLE_ID_COLUMN + " = ?";
    private final String INSERT_COORDINATES = "INSERT INTO " +
            DatabaseManager.COORDINATES_TABLE + " (" +
            DatabaseManager.COORDINATES_TABLE_X_COLUMN + ", " +
            DatabaseManager.COORDINATES_TABLE_Y_COLUMN + ") VALUES (?, ?)";
    private final String UPDATE_COORDINATES_BY_ID = "UPDATE " + DatabaseManager.COORDINATES_TABLE + " SET " +
            DatabaseManager.COORDINATES_TABLE_X_COLUMN + " = ?, " +
            DatabaseManager.COORDINATES_TABLE_Y_COLUMN + " = ?" + " WHERE " +
            DatabaseManager.COORDINATES_TABLE_ID_COLUMN + " = ?";

    private final String SELECT_ALL_PERSONS = "SELECT * FROM " + DatabaseManager.PERSON_TABLE;
    private final String SELECT_PERSON_BY_ID = SELECT_ALL_PERSONS + " WHERE " + DatabaseManager.PERSON_TABLE_ID_COLUMN + " = ?";
    private final String DELETE_PERSON_BY_ID = "DELETE FROM " + DatabaseManager.PERSON_TABLE +
            " WHERE " + DatabaseManager.PERSON_TABLE_ID_COLUMN + " = ?";
    private final String INSERT_PERSON = "INSERT INTO " +
            DatabaseManager.PERSON_TABLE + " (" +
            DatabaseManager.PERSON_TABLE_NAME_COLUMN + ", " +
            DatabaseManager.PERSON_TABLE_PASSPORT_COLUMN + ", " +
            DatabaseManager.PERSON_TABLE_HAIR_COLOR_COLUMN + ", " +
            DatabaseManager.PERSON_TABLE_NATIONALITY_COLUMN + ", " +
            DatabaseManager.PERSON_TABLE_LOCATION_ID_COLUMN + ") VALUES (?, ?, ?, ?, ?)";
    private final String UPDATE_PERSON_BY_ID = "UPDATE " + DatabaseManager.PERSON_TABLE + " SET " +
            DatabaseManager.PERSON_TABLE_NAME_COLUMN + " = ?, " +
            DatabaseManager.PERSON_TABLE_PASSPORT_COLUMN + " = ?, " +
            DatabaseManager.PERSON_TABLE_HAIR_COLOR_COLUMN + " = ?, " +
            DatabaseManager.PERSON_TABLE_NATIONALITY_COLUMN + " = ?, " +
            DatabaseManager.PERSON_TABLE_LOCATION_ID_COLUMN + " = ?" + " WHERE " +
            DatabaseManager.PERSON_TABLE_ID_COLUMN + " = ?";

    private final String SELECT_ALL_LOCATIONS = "SELECT * FROM " + DatabaseManager.LOCATION_TABLE;
    private final String SELECT_LOCATION_BY_ID = SELECT_ALL_LOCATIONS + " WHERE " + DatabaseManager.LOCATION_TABLE_ID_COLUMN + " = ?";
    private final String DELETE_LOCATION_BY_ID = "DELETE FROM " + DatabaseManager.LOCATION_TABLE +
            " WHERE " + DatabaseManager.LOCATION_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_LOCATION_BY_ID = "UPDATE " + DatabaseManager.LOCATION_TABLE + " SET " +
            DatabaseManager.LOCATION_TABLE_X_COLUMN + " = ?, " +
            DatabaseManager.LOCATION_TABLE_Y_COLUMN + " = ?, " +
            DatabaseManager.LOCATION_TABLE_Z_COLUMN + " = ?, " +
            DatabaseManager.LOCATION_TABLE_NAME_COLUMN + " = ?" + " WHERE " +
            DatabaseManager.LOCATION_TABLE_ID_COLUMN + " = ?";

    private DatabaseManager databaseManager;
    private DatabaseUserManager databaseUserManager;

    public DatabaseCollectionManager(DatabaseManager databaseManager, DatabaseUserManager databaseUserManager) {
        this.databaseManager = databaseManager;
        this.databaseUserManager = databaseUserManager;
    }

    private StudyGroup returnStudyGroup(ResultSet resultSet, int id) throws SQLException{
        String name = resultSet.getString(DatabaseManager.GROUP_TABLE_NAME_COLUMN);
        Coordinates coordinates = getCoordinatesByID(resultSet.getInt(DatabaseManager.GROUP_TABLE_COORDINATES_ID_COLUMN));
        LocalDateTime creationDate = resultSet.getTimestamp(DatabaseManager.GROUP_TABLE_CREATION_DATE_COLUMN).toLocalDateTime();
        long studentsCount = resultSet.getLong(DatabaseManager.GROUP_TABLE_STUDENTS_COUNT_COLUMN);
        long shouldBeExpelled = resultSet.getLong(DatabaseManager.GROUP_TABLE_SHOULD_BE_EXPELLED_COLUMN);
        int averageMark = resultSet.getInt(DatabaseManager.GROUP_TABLE_AVERAGE_MARK_COLUMN);
        FormOfEducation formOfEducation  = FormOfEducation.valueOf(resultSet.getString(DatabaseManager.GROUP_TABLE_FORM_OF_EDUCATION_COLUMN));
        Person groupAdmin = getPersonByID(resultSet.getInt(DatabaseManager.GROUP_TABLE_PERSON_ID_COLUMN));
        User owner = databaseUserManager.getUserById(resultSet.getInt(DatabaseManager.GROUP_TABLE_USER_ID_COLUMN));
        return new StudyGroup(id, name, coordinates, creationDate, studentsCount, shouldBeExpelled, averageMark, formOfEducation, groupAdmin, owner);
    }

    public CopyOnWriteArraySet<StudyGroup> getCollection() {
        CopyOnWriteArraySet<StudyGroup> groups = new CopyOnWriteArraySet<>();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = databaseManager.doPreparedStatement(SELECT_ALL_STUDY_GROUPS, false);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(DatabaseManager.GROUP_TABLE_ID_COLUMN);
                groups.add(returnStudyGroup(resultSet, id));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            databaseManager.closePreparedStatement(preparedStatement);
        }
        return groups;
    }

    private Coordinates getCoordinatesByID(int id) throws SQLException {
        Coordinates coordinates;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = databaseManager.doPreparedStatement(SELECT_COORDINATES_BY_ID, false);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                coordinates = new Coordinates(
                        resultSet.getDouble(DatabaseManager.COORDINATES_TABLE_X_COLUMN),
                        resultSet.getDouble(DatabaseManager.COORDINATES_TABLE_Y_COLUMN)
                );
            } else throw new SQLException();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка при выполнении запроса SELECT_COORDINATES_BY_ID!");
            throw new SQLException(e);
        } finally {
            databaseManager.closePreparedStatement(preparedStatement);
        }
        return coordinates;
    }

    private Person getPersonByID(int id) throws SQLException {
        Person person;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = databaseManager.doPreparedStatement(SELECT_PERSON_BY_ID, false);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                person = new Person(
                        resultSet.getString(DatabaseManager.PERSON_TABLE_NAME_COLUMN),
                        resultSet.getString(DatabaseManager.PERSON_TABLE_PASSPORT_COLUMN),
                        Color.valueOf(resultSet.getString(DatabaseManager.PERSON_TABLE_HAIR_COLOR_COLUMN)),
                        Country.valueOf(resultSet.getString(DatabaseManager.PERSON_TABLE_NATIONALITY_COLUMN)),
                        getLocationByID(getLocationIdByPersonID(id))
                );
            } else throw new SQLException();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка при выполнении запроса SELECT_PERSON_BY_ID!");
            throw new SQLException(e);
        } finally {
            databaseManager.closePreparedStatement(preparedStatement);
        }
        return person;
    }

    private Location getLocationByID(int id) throws SQLException {
        Location location;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = databaseManager.doPreparedStatement(SELECT_LOCATION_BY_ID, false);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                location = new Location(
                        resultSet.getLong(DatabaseManager.LOCATION_TABLE_X_COLUMN),
                        resultSet.getFloat(DatabaseManager.LOCATION_TABLE_Y_COLUMN),
                        resultSet.getDouble(DatabaseManager.LOCATION_TABLE_Z_COLUMN),
                        resultSet.getString(DatabaseManager.LOCATION_TABLE_NAME_COLUMN)
                );
            } else throw new SQLException();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка при выполнении запроса SELECT_LOCATION_BY_ID!");
            e.printStackTrace();
            throw new SQLException(e);
        } finally {
            databaseManager.closePreparedStatement(preparedStatement);
        }
        return location;
    }

    private int getLocationIdByPersonID (int personID) throws SQLException {
        int locationID;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = databaseManager.doPreparedStatement(SELECT_PERSON_BY_ID, false);
            preparedStatement.setInt(1, personID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                locationID = resultSet.getInt(DatabaseManager.PERSON_TABLE_LOCATION_ID_COLUMN);
            } else throw new SQLException();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка при выполнении запроса SELECT_PERSON_BY_ID!");
            throw new SQLException(e);
        } finally {
            databaseManager.closePreparedStatement(preparedStatement);
        }
        return locationID;
    }

    private int getPersonIdByGroupID (int studyGroupID) throws SQLException {
        int personID;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = databaseManager.doPreparedStatement(SELECT_STUDY_GROUP_BY_ID, false);
            preparedStatement.setInt(1, studyGroupID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                personID = resultSet.getInt(DatabaseManager.GROUP_TABLE_PERSON_ID_COLUMN);
            } else throw new SQLException();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка при выполнении запроса SELECT_STUDY_GROUP_BY_ID!");
            throw new SQLException(e);
        } finally {
            databaseManager.closePreparedStatement(preparedStatement);
        }
        return personID;
    }

    private int getCoordinatesIdByGroupID(int studyGroupID) throws SQLException {
        int coordinatesID;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = databaseManager.doPreparedStatement(SELECT_STUDY_GROUP_BY_ID, false);
            preparedStatement.setInt(1, studyGroupID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                coordinatesID = resultSet.getInt(DatabaseManager.GROUP_TABLE_COORDINATES_ID_COLUMN);
            } else throw new SQLException();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка при выполнении запроса SELECT_STUDY_GROUP_BY_ID!");
            throw new SQLException(e);
        } finally {
            databaseManager.closePreparedStatement(preparedStatement);
        }
        return coordinatesID;
    }

    public StudyGroup insertStudyGroup(StudyGroupLite groupLite, User user) throws DatabaseManagerException {
        StudyGroup groupToInsert;
        PreparedStatement insertGroup = null;
        PreparedStatement insertCoordinates = null;
        PreparedStatement insertPerson = null;
        PreparedStatement insertLocation = null;
        try {
            databaseManager.setCommit();
            databaseManager.setSavepoint();

            LocalDateTime localDateTime = LocalDateTime.now();

            String INSERT_LOCATION = "INSERT INTO " +
                    DatabaseManager.LOCATION_TABLE + " (" +
                    DatabaseManager.LOCATION_TABLE_X_COLUMN + ", " +
                    DatabaseManager.LOCATION_TABLE_Y_COLUMN + ", " +
                    DatabaseManager.LOCATION_TABLE_Z_COLUMN + ", " +
                    DatabaseManager.LOCATION_TABLE_NAME_COLUMN + ") VALUES (?, ?, ?, ?)";
            insertLocation = databaseManager.doPreparedStatement(INSERT_LOCATION, true);
            insertLocation.setLong(1, groupLite.getGroupAdmin().getLocation().getX());
            insertLocation.setFloat(2, groupLite.getGroupAdmin().getLocation().getY());
            insertLocation.setDouble(3, groupLite.getGroupAdmin().getLocation().getZ());
            insertLocation.setString(4, groupLite.getGroupAdmin().getLocation().getName());
            if (insertLocation.executeUpdate() == 0) throw new SQLException();
            ResultSet resultSetLocation = insertLocation.getGeneratedKeys();
            int locationID;
            if (resultSetLocation.next()) locationID = resultSetLocation.getInt(1) ;
            else throw new SQLException();

            insertPerson = databaseManager.doPreparedStatement(INSERT_PERSON, true);
            insertPerson.setString(1, groupLite.getGroupAdmin().getName());
            insertPerson.setString(2, groupLite.getGroupAdmin().getPassportID());
            insertPerson.setString(3, groupLite.getGroupAdmin().getHairColor().toString());
            insertPerson.setString(4, groupLite.getGroupAdmin().getNationality().toString());
            insertPerson.setInt(5, locationID);
            if (insertPerson.executeUpdate() == 0) throw new SQLException();
            ResultSet resultSetPerson = insertPerson.getGeneratedKeys();
            int personID;
            if (resultSetPerson.next()) personID = resultSetPerson.getInt(1);
            else throw new SQLException();

            insertCoordinates = databaseManager.doPreparedStatement(INSERT_COORDINATES, true);
            insertCoordinates.setDouble(1, groupLite.getCoordinates().getX());
            insertCoordinates.setDouble(2, groupLite.getCoordinates().getY());
            if (insertCoordinates.executeUpdate() == 0) throw new SQLException();
            ResultSet resultSetCoordinates = insertCoordinates.getGeneratedKeys();
            int coordinatesID;
            if (resultSetCoordinates.next()) coordinatesID = resultSetCoordinates.getInt(1);
            else throw new SQLException();

            insertGroup = databaseManager.doPreparedStatement(INSERT_STUDY_GROUP, true);
            insertGroup.setString(1, groupLite.getName());
            insertGroup.setInt(2, coordinatesID);
            insertGroup.setTimestamp(3, Timestamp.valueOf(localDateTime));
            insertGroup.setLong(4, groupLite.getStudentsCount());
            insertGroup.setLong(5, groupLite.getShouldBeExpelled());
            insertGroup.setInt(6, groupLite.getAverageMark());
            insertGroup.setString(7, groupLite.getFormOfEducation().toString());
            insertGroup.setInt(8, personID);
            insertGroup.setInt(9, databaseUserManager.getUserIdByUsername(user));
            if (insertGroup.executeUpdate() == 0) throw new SQLException();
            ResultSet resultSetGroup = insertGroup.getGeneratedKeys();
            int studyGroupID;
            if (resultSetGroup.next()) studyGroupID = resultSetGroup.getInt(1);
            else throw new SQLException();
            groupToInsert = new StudyGroup(
                    studyGroupID,
                    groupLite.getName(),
                    groupLite.getCoordinates(),
                    localDateTime,
                    groupLite.getStudentsCount(),
                    groupLite.getShouldBeExpelled(),
                    groupLite.getAverageMark(),
                    groupLite.getFormOfEducation(),
                    groupLite.getGroupAdmin(),
                    user
            );
            databaseManager.commit();
            return groupToInsert;
        } catch (SQLException exception) {
            System.out.println("Произошла ошибка при добавлении нового объекта в БД!");
            exception.printStackTrace();
            databaseManager.rollback();
            throw new DatabaseManagerException();
        } finally {
            databaseManager.closePreparedStatement(insertPerson);
            databaseManager.closePreparedStatement(insertCoordinates);
            databaseManager.closePreparedStatement(insertGroup);
            databaseManager.closePreparedStatement(insertLocation);
            databaseManager.setAutoCommit();
        }
    }

    public void updateStudyGroupByID(int studyGroupID, StudyGroupLite groupLite) throws DatabaseManagerException {
        PreparedStatement updateStudyGroupName = null;
        PreparedStatement updateStudyGroupCoordinates = null;
        PreparedStatement updateStudyGroupStudentsCount = null;
        PreparedStatement updateStudyGroupStudentsShouldBeExpelled = null;
        PreparedStatement updateStudyGroupAverageMark = null;
        PreparedStatement updateStudyGroupFormOfEducation = null;
        PreparedStatement updateStudyGroupPerson = null;
        PreparedStatement updateStudyGroupPersonLocation = null;
        try {
            databaseManager.setCommit();
            databaseManager.setSavepoint();

            updateStudyGroupName = databaseManager.doPreparedStatement(UPDATE_GROUP_NAME_BY_ID, false);
            updateStudyGroupCoordinates = databaseManager.doPreparedStatement(UPDATE_COORDINATES_BY_ID, false);
            updateStudyGroupStudentsCount = databaseManager.doPreparedStatement(UPDATE_GROUP_STUDENTS_COUNT_BY_ID, false);
            updateStudyGroupStudentsShouldBeExpelled = databaseManager.doPreparedStatement(UPDATE_GROUP_SHOULD_BE_EXPELLED_BY_ID, false);
            updateStudyGroupAverageMark = databaseManager.doPreparedStatement(UPDATE_GROUP_AVERAGE_MARK_BY_ID, false);
            updateStudyGroupFormOfEducation = databaseManager.doPreparedStatement(UPDATE_GROUP_FORM_OF_EDUCATION_BY_ID, false);
            updateStudyGroupPerson = databaseManager.doPreparedStatement(UPDATE_PERSON_BY_ID, false);
            updateStudyGroupPersonLocation = databaseManager.doPreparedStatement(UPDATE_LOCATION_BY_ID, false);

            if (groupLite.getName() != null) {
                updateStudyGroupName.setString(1, groupLite.getName());
                updateStudyGroupName.setInt(2, studyGroupID);
                if (updateStudyGroupName.executeUpdate() == 0) throw new SQLException();
            }
            if (groupLite.getCoordinates() != null) {
                updateStudyGroupCoordinates.setDouble(1, groupLite.getCoordinates().getX());
                updateStudyGroupCoordinates.setDouble(2, groupLite.getCoordinates().getY());
                updateStudyGroupCoordinates.setInt(3, getCoordinatesIdByGroupID(studyGroupID));
                if (updateStudyGroupCoordinates.executeUpdate() == 0) throw new SQLException();
            }
            if (groupLite.getStudentsCount() != -1) {
                updateStudyGroupStudentsCount.setLong(1, groupLite.getStudentsCount());
                updateStudyGroupStudentsCount.setInt(2, studyGroupID);
                if (updateStudyGroupStudentsCount.executeUpdate() == 0) throw new SQLException();
            }
            if (groupLite.getShouldBeExpelled() != -1) {
                updateStudyGroupStudentsShouldBeExpelled.setLong(1, groupLite.getShouldBeExpelled());
                updateStudyGroupStudentsShouldBeExpelled.setInt(2, studyGroupID);
                if (updateStudyGroupStudentsShouldBeExpelled.executeUpdate() == 0) throw new SQLException();
            }
            if (groupLite.getAverageMark() != -1) {
                updateStudyGroupAverageMark.setInt(1, groupLite.getAverageMark());
                updateStudyGroupAverageMark.setInt(2, studyGroupID);
                if (updateStudyGroupAverageMark.executeUpdate() == 0) throw new SQLException();
            }
            if (groupLite.getFormOfEducation() != null) {
                updateStudyGroupFormOfEducation.setString(1, groupLite.getFormOfEducation().toString());
                updateStudyGroupFormOfEducation.setInt(2, studyGroupID);
                if (updateStudyGroupFormOfEducation.executeUpdate() == 0) throw new SQLException();
            }
            if (groupLite.getGroupAdmin().getLocation() != null) {
                updateStudyGroupPersonLocation.setLong(1, groupLite.getGroupAdmin().getLocation().getX());
                updateStudyGroupPersonLocation.setFloat(2, groupLite.getGroupAdmin().getLocation().getY());
                updateStudyGroupPersonLocation.setDouble(3, groupLite.getGroupAdmin().getLocation().getZ());
                updateStudyGroupPersonLocation.setString(4, groupLite.getGroupAdmin().getLocation().getName());
                updateStudyGroupPersonLocation.setInt(5, getLocationIdByPersonID(getPersonIdByGroupID(studyGroupID)));
            }
            if (groupLite.getGroupAdmin() != null) {
                updateStudyGroupPerson.setString(1, groupLite.getGroupAdmin().getName());
                updateStudyGroupPerson.setString(2, groupLite.getGroupAdmin().getPassportID());
                updateStudyGroupPerson.setString(3, groupLite.getGroupAdmin().getHairColor().toString());
                updateStudyGroupPerson.setString(4, groupLite.getGroupAdmin().getNationality().toString());
                updateStudyGroupPerson.setInt(5, getLocationIdByPersonID(getPersonIdByGroupID(studyGroupID)));
                updateStudyGroupPerson.setInt(6, getPersonIdByGroupID(studyGroupID));
                if (updateStudyGroupPerson.executeUpdate() == 0) throw new SQLException();
            }
            databaseManager.commit();
        } catch (SQLException exception) {
            System.out.println("Произошла ошибка при выполнении группы запросов на обновление объекта!");
            databaseManager.rollback();
            throw new DatabaseManagerException();
        } finally {
            databaseManager.closePreparedStatement(updateStudyGroupName);
            databaseManager.closePreparedStatement(updateStudyGroupCoordinates);
            databaseManager.closePreparedStatement(updateStudyGroupStudentsCount);
            databaseManager.closePreparedStatement(updateStudyGroupStudentsShouldBeExpelled);
            databaseManager.closePreparedStatement(updateStudyGroupAverageMark);
            databaseManager.closePreparedStatement(updateStudyGroupFormOfEducation);
            databaseManager.closePreparedStatement(updateStudyGroupPerson);
            databaseManager.closePreparedStatement(updateStudyGroupPersonLocation);
            databaseManager.setAutoCommit();
        }
    }

    public boolean checkStudyGroupByIdAndUserId(int studyGroupID, User user) throws DatabaseManagerException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = databaseManager.doPreparedStatement(SELECT_STUDY_GROUP_BY_ID_AND_USER_ID, false);
            preparedStatement.setInt(1, studyGroupID);
            preparedStatement.setInt(2, databaseUserManager.getUserIdByUsername(user));
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException exception) {
            System.out.println("Произошла ошибка при выполнении запроса SELECT_STUDY_GROUP_BY_ID_AND_USER_ID!");
            throw new DatabaseManagerException();
        } finally {
            databaseManager.closePreparedStatement(preparedStatement);
        }
    }

    public void deleteGroupById(int studyGroupID) throws DatabaseManagerException {
        PreparedStatement deleteStudyGroup = null;
        PreparedStatement deleteCoordinates = null;
        PreparedStatement deletePerson = null;
        PreparedStatement deleteLocation = null;
        try {
            int coordinatesID = getCoordinatesIdByGroupID(studyGroupID);
            int personID = getPersonIdByGroupID(studyGroupID);
            int locationID = getLocationIdByPersonID(personID);
            deleteStudyGroup = databaseManager.doPreparedStatement(DELETE_GROUP_BY_ID, false);
            deleteStudyGroup.setInt(1, studyGroupID);
            if (deleteStudyGroup.executeUpdate() == 0) throw new SQLException();
            deleteCoordinates = databaseManager.doPreparedStatement(DELETE_COORDINATES_BY_ID, false);
            deleteCoordinates.setInt(1, coordinatesID);
            if (deleteCoordinates.executeUpdate() == 0) throw new SQLException();
            deletePerson = databaseManager.doPreparedStatement(DELETE_PERSON_BY_ID, false);
            deletePerson.setInt(1, personID);
            if (deletePerson.executeUpdate() == 0) throw new SQLException();
            deleteLocation = databaseManager.doPreparedStatement(DELETE_LOCATION_BY_ID, false);
            deleteLocation.setInt(1, locationID);
            if (deleteLocation.executeUpdate() == 0) throw new SQLException();
        } catch (SQLException exception) {
            System.out.println("Произошла ошибка при выполнении запроса DELETE_GROUP_BY_ID!");
            exception.printStackTrace();
            throw new DatabaseManagerException();
        } finally {
            databaseManager.closePreparedStatement(deleteStudyGroup);
            databaseManager.closePreparedStatement(deleteCoordinates);
            databaseManager.closePreparedStatement(deletePerson);
            databaseManager.closePreparedStatement(deleteLocation);
        }
    }

    public void clearCollection() throws DatabaseManagerException{
        CopyOnWriteArraySet<StudyGroup> studyGroups = getCollection();
        for (StudyGroup studyGroup : studyGroups) {
            deleteGroupById(studyGroup.getId());
        }
    }
}
