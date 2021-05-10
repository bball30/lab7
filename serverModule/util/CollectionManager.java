package serverModule.util;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import common.data.Person;
import common.data.StudyGroup;

/**
 * Operates the collection itself.
 */
public class CollectionManager {
    private CopyOnWriteArraySet<StudyGroup> myCollection = new CopyOnWriteArraySet<>();
    //private LinkedHashSet<StudyGroup> myCollection = new LinkedHashSet<>();
    private LocalDateTime lastInitTime;
    private LocalDateTime lastSaveTime;
    private DatabaseCollectionManager databaseCollectionManager;

    public CollectionManager(DatabaseCollectionManager databaseCollectionManager){
        this.lastInitTime = null;
        this.lastSaveTime = null;
        this.databaseCollectionManager = databaseCollectionManager;
        loadCollection();
    }

    /**
     * @return The collection itself.
     */
    public CopyOnWriteArraySet<StudyGroup> getCollection() {
        return myCollection;
    }

    /**
     * @return Last initialization time or null if there wasn't initialization.
     */
    public LocalDateTime getLastInitTime() {
        return lastInitTime;
    }

    /**
     * @return Last save time or null if there wasn't saving.
     */
    public LocalDateTime getLastSaveTime() {
        return lastSaveTime;
    }

    /**
     * @return Name of the collection's type.
     */
    public String collectionType() {
        return myCollection.getClass().getName();
    }

    /**
     * @return Size of the collection.
     */
    public int collectionSize() {
        return myCollection.size();
    }

    /**
     * @param collection myCollection
     * @param <StudyGroup> studyGroup
     * @return The last element of the collection or null if collection is empty
     */
    public <StudyGroup> StudyGroup getLast(Collection<StudyGroup> collection) {
        if (myCollection.isEmpty()) {return null;}
        else {StudyGroup last = null;
        for (StudyGroup s : collection) last = s;
        return last;
        }
    }

    /**
     * @param groupToCompare The marine used to compare with others.
     */
    public List<StudyGroup> getGreater(StudyGroup groupToCompare) {
        return myCollection.stream().filter(studyGroup -> studyGroup.compareTo(groupToCompare) > 0).collect(Collectors.toList());
    }

    /**
     * @param groupToCompare The marine used to compare with others.
     */
    public List<StudyGroup> getLower(StudyGroup groupToCompare) {
        return myCollection.stream().filter(studyGroup -> studyGroup.compareTo(groupToCompare) < 0).collect(Collectors.toList());
    }

    /**
     * @param id ID of the studyGroup.
     * @return studyGroup by his ID or null if studyGroup isn't found.
     */
    public StudyGroup getById(Integer id) {
        for (StudyGroup studyGroup : myCollection) {
            if (studyGroup.getId() == id) return studyGroup;
        }
        return null;
    }

    /**
     * @param studyGroupToFind A studyGroup who's value will be found.
     * @return A studyGroup by his value or null if studyGroup isn't found.
     */
    public StudyGroup getByValue(StudyGroup studyGroupToFind) {
        for (StudyGroup studyGroup : myCollection) {
            if (studyGroup.similar(studyGroupToFind)) return studyGroup;
        }
        return null;
    }

    public StudyGroup getByFormOfEducation(String form) {
        for (StudyGroup studyGroup : myCollection) {
            if (studyGroup.getFormOfEducation().name().equals(form)) return studyGroup;
        }
        return null;
    }

    /**
     * Adds a new studyGroup to collection.
     * @param studyGroup A studyGroup to add.
     */
    public void addToCollection(StudyGroup studyGroup) {
        myCollection.add(studyGroup);
    }

    /**
     * Removes a new studyGroup to collection.
     * @param studyGroup A studyGroup to remove.
     */
    public void removeFromCollection(StudyGroup studyGroup) {
        myCollection.remove(studyGroup);
    }

    /**
     * Clears the collection.
     */
    public void clearCollection() {
        myCollection.clear();
    }

    /**
     * Generates next ID. It will be (the bigger one + 1).
     * @return Next ID.
     */
    public Integer generateNextId() {
        if (myCollection.isEmpty()) return 1;
        return getLast(myCollection).getId() + 1;
    }

    /**
     * Loads the collection from DB.
     */
    public void loadCollection() {
        myCollection = databaseCollectionManager.getCollection();
        lastInitTime = LocalDateTime.now();
        System.out.println("Коллекция загружена.");
    }

    public ArrayList<Long> sortedByShouldBeExpelled(){
        ArrayList<Long> arrayList = new ArrayList<Long>();
        for(StudyGroup sg : myCollection){
            arrayList.add(sg.getShouldBeExpelled());
        }
        Collections.sort(arrayList);
        Collections.reverse(arrayList);
        return arrayList;
    }

    public Set<Person> uniqueGroupAdmin() {
        Set<Person> set = new HashSet<Person>();
        for(StudyGroup sg : myCollection){
            set.add(sg.getGroupAdmin());
        }
        return set;
    }

    public String showCollection() {
        if (myCollection.isEmpty()) return "Коллекция пуста!";
        return myCollection.stream().reduce("", (sum, m) -> sum += m + "\n\n", (sum1, sum2) -> sum1 + sum2).trim();
    }

    @Override
    public String toString() {
        if (myCollection.isEmpty()) return "Коллекция пуста!";

        StringBuilder info = new StringBuilder();
        for (StudyGroup studyGroup : myCollection) {
            info.append(studyGroup);
            info.append("\n\n");
        }
        return info.toString();
    }
}
