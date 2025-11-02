package client;

import client.utils.LanguageUtils;

import java.util.Locale;
import java.util.ResourceBundle;

public enum ServerStatus {
    SERVER_NOT_REACHABLE("serverNotReachableEnum"),
    COLLECTION_NOT_VALID("collectionNotValidEnum"),
    COLLECTION_WILL_BE_SAVED("collectionWillBeSavedEnum"),
    COLLECTION_WILL_BE_ADDED("collectionWillBeAddedEnum"),
    COLLECTION_ALREADY_ON_CLIENT("collectionAlreadyOnClientEnum"),
    NO_CHANGES_MADE("noChangesMadeEnum"),
    ALREADY_DEFAULT("alreadyDefaultCollectionEnum"),
    COLLECTION_DOES_NOT_EXIST("serverReachableCollectionDoesNotExistEnum");


    private final String key;
    private Locale locale;
    private ResourceBundle bundle;
    private long collectionId;

    ServerStatus(String key) {
        locale = Locale.forLanguageTag(LanguageUtils.getSavedLanguage().replace('_', '-'));
        bundle = ResourceBundle.getBundle("client.properties.text", locale);
        this.key = key;
        this.collectionId = -1;
    }

    public long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(long collectionId) {
        this.collectionId = collectionId;
    }

    public String getMessage() {
        return bundle.getString(key);
    }

    @Override
    public String toString() {
        return bundle.getString(key);
    }
}