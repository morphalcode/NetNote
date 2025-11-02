package commons;

public enum Action {
    CREATE,
    READ,  //Not really needed as a read does not require an update but just wanted to have CRUD
    UPDATE,
    DELETE,
    TEST;
}
