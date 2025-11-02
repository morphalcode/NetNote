package client.services;

import client.Config;
import client.Exceptions.ConfigFileCorruptedException;
import com.google.inject.Inject;
import commons.Collection;
import commons.Note;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class InterfaceService {

    private final Config config;

    /**
     * Constructor using dependency injection
     * @param config instance of Config class
     */
    @Inject
    public InterfaceService(Config config) {
        this.config = config;
    }

    /**
     * Uses method initialize in config to initialize
     * @throws ConfigFileCorruptedException if config file is corrupted
     */
    public void initialize() throws ConfigFileCorruptedException {
        config.initialize();
    }

    /**
     * Uses method saveToFile in config
     */
    public void refresh() {
        try {
            config.saveToFile();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Adds all selected items in the menu as filters
     * @param items the list of items in the menu
     * @return the new list of filters
     */
    public List<String> getFilters(List<MenuItem> items) {
        List<String> filters = new ArrayList<>();
        for (MenuItem item : items) {
            if (((CheckMenuItem) item).isSelected()) {
                filters.add(item.getText());
            }
        }
        return filters;
    }

    /**
     * Generates items for the filter menu if they are not already there
     * @param allTags the list of all tags
     * @param existingItems the already existing items
     * @return the new list of items (might be the same)
     */
    public List<MenuItem> generateFilterMenuItems(List<String> allTags, List<MenuItem> existingItems) {
        if(allTags == null || allTags.isEmpty()) {
            return existingItems;
        }
        Iterator<String> tags = allTags.iterator();
        //for each tag
        while (tags.hasNext()) {
            boolean ok = true;
            String item = tags.next();
            CheckMenuItem tagMenuItem = new CheckMenuItem(item);
            for (MenuItem menuItem : existingItems) {
                if (tagMenuItem.getText().equals(menuItem.getText())) {
                    ok = false;
                }
            }
            if (ok) {
                existingItems.add(tagMenuItem);
            }
        }
        return (existingItems.stream().filter(x -> {
            for (String item : allTags) {
                if (item.equals(x.getText())) {
                    return true;
                }
            }
            return false;
        }).toList());
    }

    /**
     * a
     * @param note the specified note
     * @param collection the specified collection
     * @return true if , false otherwise
     */
    public boolean isNoteAndCurrentAll(Note note, Collection collection){
        return  (note.collection.id != collection.id &&
                collection.id != 0 &&
                !collection.name.equals("All Notes"));
    }

}
