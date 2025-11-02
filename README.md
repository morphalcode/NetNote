# NetNote documentation
Team 88's implementation of the NetNote project for CSE1105
## How to use the app
- The app can be started by running main on the client. 

For conciseness, words such as "press", "click" and "button" will be used in this section. Refer to the "Keyboard Shortcuts and Keyboard Navigation" section later in this document for instruction on how use the app's accessibility features.
### Change Language
- You can change the language of the interface by clicking on the dropdown menu with the flag on it the the top of the window. 
- To select a new language click on the flag associated with the language you want to select.

### Manipulating Collections:
- After the app is started, collections can be added using the "Edit Collections" button at the top of the window.
- Pressing this button will open a new window called the "edit collections window" which allows to manipulate collections:

#### Create a New Collection
- First, insert the url of the server you want to access at the "server" text field (e.g http://localhost:8081).
- The server status will update as you type. 
- When it says server reachable;(...) you can press the green + button to create a new collection on the server.
- The collection's name will be assigned automatically. 
    - Refer to "Edit a collection" for instructions on how to change it to a more user-friendly name.
    The created collection will be added to the client it is created for automatically.
    - Refer to "Remove a collection" for instructions on how to remove a configured collection from the client.
- For convience, if you have a collection selected and you press the create collection button, it will create the collection in the same server as the selected collection.

#### Edit a Collection
- Select the collection you want to edit using the list on the left.

##### Change Name
- You can now edit the title text field.
- The title has to be not empty uniqe and in the server. This will be enforced by the app.
    - If the status says "no changes made" it means there is a collection with that name in the server it is in.
- After you insert a valid new title, you can save the changes using the save button at the bottom left corner.

##### Make Default
- You can make the selected collection the default collection by pressing the "make default" button left to the save button.
    - The first collection you add to the client will automatically be the default collection.
    - You cannot remove the default collection from your client. If you wish to remove the default collection, you must
    make another collection the default first.

#### Remove a Collection From Client
- Select the collection you want to remove from the client using the list on the left.
- Press on the red button with the remove icon at the bottom left part of the window.
- Confirm whether you want to remove the collection.
- The collection will be removed from your client (but not deleted from the server).
    - Refer to "Add an existing collection to client" for instruction on how to add the removed collection back. 

#### Add an Existing Collection to Client
- First, make sure you have no collection selected.
    - You can deselect a collection by clicking on it again.
- Insert the server url to the server text field 
- Insert the name of the collection to the title text field
- When valid details of an existing collection (that is not already on the client) are entred, the status will change to "Collection will be added", and the "Add" button on the bottom left corner will become enabled.
- To add the collection to the client, press on the "Add" button. 

As the same interface is used both for editing and adding, the "Action" text can help clarify what action you are currently doing.

#### Filter by collection
- Back on the main window, click on the drop-down left of the "edit collection" button
    - The drop-down shows the current collection shown.
- Click on the collection you want to filter by 
- Now you will only be able to view notes of that collection until the filter is changed.
- When you view a colltion, notes you create will be created in that collection 
    -Refer to "Create a note" for instructions on how to create notes.

### Manipulate Notes
- Notes are fully dependent on collecions. To start manipulating notes, ensure you have a collection configured on your client.

#### Create a New Note
- To create a new Note click on the green button with the + icon on the top left part of the main window
- The created collection will be named auotomatically. 
    - Refer to "Edit a note" for instructions on how to edit a note

#### Delete a Note
- Select the note you want to delete from the list on the left, or by searchig for it.
    - Refer to "Search for Notes" for instructions on how to search for a note.
- Press on the red button with the delete icon on the top left part of the main window.
- Confirm whether you want to the delete the note.

#### Edit a Note 
- Select the note you want to delete from the list on the left, or by searchig for it.
- The note will now be opened.
- Now, you can change the note's title and content.
- The changes will be saved auotomatically.
- A note's title has to be unique in its server and not empty. This will be enforced by the app.

#### Markdown
- To view a markdown rendering of the selected note, press on the markdown toggle button at the top left part of the main interface.

#### Custom CSS
- To include custom CSS in the markdown render, add content to /client/src/main/resources/CSS/UserStyle.css
- Bootstrap styling will be used unless this file exists and is not empty.

#### Search for Notes
- Use the search bar on the top-left side to look up notes in the app.
- As you type the list of matching notes will change according to your input
- You can click on one of the notes from the list to select it, or press enter to select the first note in the list.
- Notes can be found by searching for words from their title or from their content.

#### Add Files to a Note and Manipulate Them
- To embed files into a note use the button on the right side of the "Added files" section which is at the bottom of the interface.
- Click on the file's name to save it locally. 
- Click the button with a pen on it to edit the name of the file. 
- Click the button with a red "X" on it to delete a file from the note.
- Upon deletion of a note, the files that were embedded in it are also deleted from the app.
- In order to render images in the markdown webview, select the file's cell to copy its URL and paste it in markdown syntax.

#### Create a tag
- Write #name for a tag called name to be created
- Press the refresh button for the tag to be visible in the filter menu

#### Delete a tag
- Simply delete the #name from the note for the tag to be deleted
- Press the refresh button for the tag to disappear from the filter menu

#### Deselect all tags
- While having tags selected in the filter menu, press the clear all button

#### Switch from Complete Match to Partial Match and back
- While having the tags menu open, press the Complete Match button to turn on Partial Match
- Then press the refresh button for the notes on the left to be filtered using Partial Match
- To switch back to Complete Match press the Partial Match Button (which used to say Complete Match) and refresh

#### Filter by tags
- To filter by a single tag you can press on the markdown representation of that tag while having a note with that tag open
- To filter by one or many tags you should open the filter menu and tick all tags that you want to filter by

#### Create a reference
- Type \[\[note title\]\] to create a reference to a note with that title in the current collection.
- The markdown preview will indicate whether the reference is valid.

#### Delete a reference
- Simply delete the reference text.

#### Follow a reference
- If the reference is valid, you should be able to click on it in the markdown preview. This will take you to the note.

#### Update references by changing title
- If you change a title of a note that has been referenced elsewhere, the references will update to reflect the change.
- You will be notified of how many references were updated (meaningful addition to references). The notification is clear if you wait a few seconds after changing the title.


## Technology
### Dependency Injection
Both the client and the server use dependency injection, which is used automatically by their respective framework.

The backend uses springboot's DI in order to inject the services into the controllers.

Similarly, the client also uses DI to inject many classes, including: 
- 9 difference service classes
- The config class
- Server utils
- web sockets manager
- more

This DI is configured to be intergrated with Javafx, so when a Javafx component is created by the FXMLLoader in MyFXML (which is the one that loads the app) it will use the constructor with DI, instead of an empty constructor, and inject the required singeltons.
All the used singletons are in MyModule.java 

### Server Implementation
The server has two main parts: the API and the web sockets handler.

The API uses the standart HTTP methods to manipulate notes, collections and files.
Each of these entities has a controller class which handles the http requests and a service class which interacts with the repository in the database. There are several validation rules that work together with the validation rules in the client to ensure no invalid entities are saved to the database.

The web socket handler managed different web socket connections from different clients, and notifies subscribed clients of an update whenever an update occurs, except from the client who sent the update.
### Client Implementation
(add any information related to Client Implementation)
## Accessibility
### Keyboard Shortcuts and Keyboard Navigation
#### Main Interface Shortcuts
- ESC -> access the search bar

- CTRL + N -> add new note
- CTRL + D -> deletee note
- CTRL + R -> refresh
- CTRL + T -> move the focus to the note title bar
- CTRL + B -> move the focus to the note body
- CTRL + E -> open menu to edit collections

- ALT + L -> move the focus to the list of notes (list of titles)
- ALT + Z -> focus on the menu that allows you to switch the collection that you are viewing (upper one)
- ALT + X -> focus on the menu that allows you to change the collection of the note you are viewing (lower one)
#### Edit colledctions menu shortcuts
- CTRL + N -> add new collection
- CTRL + D -> delete collection
- CTRL + M -> make default collection
- CTRL + Q -> access title text field
- CTRL + W -> access server text field

- ALT + L -> move focus on the list of collections

- ESC -> close menu
### Multi-modal Visualization
Buttons with suggestive color and icon:
- Add note
- Delete note
- Refresh
- Add collection
- Delete collection

Buttons with suggestive color and text:
- Yes/No options in pop-up alerts

Buttons with text and icon:
- Tags
- Clear all
- Filter
- Markdown
- Edit Collections
- Add/Save (inside edit collections menu)

### Error messages and Informative feedback
List of all error messages and notifications the app sends (in english):

- New note has been created: "
- Server not reachable. Specify the server you want to add the collection to in the server field
- Cannot move notes to different servers
- New collection has been created on "
    - " and added to this client
- Cannot remove default collection. Set another collection to default before removing
- Successfully saved changes
- Could not save changes
- Already default
- " is now the default collection
- All elements have been refreshed
- Collection "
    - " has been removed from client
- Note "
    - " has been removed
- The note you are currently editing has been changed by another client
- The collection you are currently viewing has been changed by another client
- It seems like the note you are viewing is not loaded on this client anymore. You might want to refresh
- You have no collections configured
- references in this collection updated
- Note title must be unique in its server
- Could not add file
- Could not download file
- You have no collection selected
- Name must not be empty
- Config file corrupted. New config file created
- One of the servers you have configured is not online. Its collections won't be shown
### Confirmation for Key Actions
You need to confirm when:
- Deleting a note
- Removing a collection

## Implemented features
All features have been implemented.
### Basic Requirements
- Users can add notes to a server and access them from multiple client which are connected to the server
- Users are able to view all notes on the servers they are connected to
- Users are able to create new notes on the servers 
- Users are able to add and change the titl of their notes
- Users are able to delete notes 
- Updates are automatically sent to the server so users don't need to manually refresh
- Users are able to edit the content of their notes as free text
- Users are able to refresh their client's view 
- Users have a search feature for notes by both title and content
- Users are able to use markdown in their notes
- Users are able to render markdown version of their notes 
- The markdown render updates as the usres edit the note, so users are able to see a real time reflection 
- Users are prevented from having duplicate title in the same server 

- There is a dynamic configuration file which changes according to the collections users have configured, and persists upon restarts
- The client fully supports basic (and some advanced) markdown syntax 
- Markdown uses webview
- Users can add custom css to a local css file so the it will be used in the webview (/client/src/main/resources/CSS/UserStyle.css)

#### Meaningful Addition(s)

- To optimize performace, not every keystroke is sent to the server; when the content of the note changes, it starts a timer of two seconds, and changes in these two seconds will be sent as one request to the server. While the content of the note keeps changing, the timer will keep sending the request every two seconds. If in two seconds no more changes have been made, the timer will stop. 
- Markdown rendering supports more advanced markdown syntax such as strikethrough and tables

### Multi-Collection
- Users can create collections and move notes between collections, allowing them to distribute thier notes accross several collections
- Users can filter to view notes by collection 
- in the select collection menu there is an option called all notes to view all notes
- Users are able to use the "edit collections" button to configure their collections on their client, as well as creating new collections 
- users are able to edit their collections' names through the "edit collections window"
- Users can see the server status updates according to several crieria in the "edit collections window"
- Users are able to define a default collection and change it through the "edit collections window"
- Users are able to create notes in a collection by selecting it in the collection filter. (Notes are created in the collection the user is currently viewing, or in the default collection if the user is viewing all notes)
- Users can move note between collections (only on the same server)
- configured collections are saved in Config.json on the client, so they are presisted between restarts

#### Meaningful Addition(s):
- Supports connections to multiple servers (was removed from requirements)
   - Has much more feedback to user through more than the required server-status
   - If the config file is corrupted the app can recognize and replace it with a new one
   - If a configured collection is deleted from the server, it will be removed from the client config.json when it is recognized.

### Embedded Files
- The user can embed files into notes. 
- Files can be renamed directly from the app.
- Files can be deleted from the app.
- Upon deletion of a note all the files embedded in it are deleted.
- The user can refer to image files in the markdown to render them in the preview.
- The user can download files from the app.
- The client does not store any local data.
- files are accessible through a path in the api

- As a meaningful addition, the user can copy the URL of the file to their clipboard by clicking on the file's cell. This addition enables the user to render images in the markdown webview with ease by simply pasting the URL in the note.
 
### Interconnected Content
- In order to create a tag the user has to write #name, for a tag called name 

- By pressing the tags button, an entire tags interface is opened 

- The Filter menu button allows for filtering by certain tags, the results being a note with all of the selected tags 

- The tags under the Filter menu button require a refresh using the refresh button to update 

- The Clear All button deselects all currently selected tags 

- All tags in the current note are highlighted in the markdown preview 

- By clicking on a tag in the markdown preview you can filter by that one selected tag. Clicking on another tag filters by that other tag 

- The tags shown in the filter menu are the ones that are available in the remaining notes. To keep this up to date, press
the refresh button after filtering to see only the remaining tags

- To create a reference to a note titled 'note title' in the current collection, \[\[note title\]\] can be typed.

- The markdown preview will indicate whether the reference is valid

- If the reference is valid, it can be clicked on in the webview to take the user to the relevant note

- If a note's title is updated, all references to this note will reflect the change

#### Meaninful Addition(s):

- Complete Match/Partial Match allows the user to switch from one filtering mode to another 

- Complete Match is the regular searching mode where the filtered notes should have all of the selected tags 

- Partial Match is a new searching mode where the filtered notes should have at least one selected tag 

- Partial Match is useful when you are interested in notes from two or more disjoint topics (there are not two notes with both of these tags)

- When a note's title is updated, if any references were changed a notification will be shown that informs the user of how many were changed

- This is useful because it ensures the user knows that some of their notes were automatically changed, rather than this coming as a surprise at a later point. 

- For example, if a user changes titles from note1 -> note2 and note3 -> note1, they might be under the impression that original \[\[note1\]\] references point to the new note1, when they really will have changed to \[\[note2\]\]. The notification lets them know this is not the case.

- It is also useful if a user wants to identify how many references were made to a specific note, in case they are refactoring their notes (for eg trying to make sure all references to a note are deleted).

- The notification is most clear when waiting a few seconds for the new title to save (when clicking refresh, refresh notification takes priority)

### Automated Change Synchronization
- Many changes are auto-propagated across all clients, reducing the need in manual refresh:
- A change of a title is propagated
- addition/deletion of notes are propagated
- change in content of note are propogated
- a change in the content affects the preview
- web sockets are used to subscribe for changes
- so the client does not poll, as it uses web sockets
- the client filters irrelevant updates (updates for collections & notes which are configured and open on the client)

#### Meaningful Addition(s)
- Changes to collections are also propagated, including editig a collection name, and moving note between collections
- When a single note is updates (so it does not affect other things in the application) only that note is refetched, thus making updates faster and saving resources.
- The client is able to manage multiple web socket connections to multiple servers (as the client can have collections from multiple servers configured)
- The server keeps track on the connections, and does not send an update notification to the client who made the update.


### Live Language Switch
The app fully supports:
- English
- Dutch
- A 3rd language

As a meaningfull addition, instead of a made up language, the app also supports:
- Romanian
- Italian
- Spanish
- German
- Portuguese
- Freanch

As we are using the load method from the MyFXML class, instead of FXMLLoader, I have overloaded the load method so that you can also specify a resource bundle that will be used to translate the components.

The app gives a lot of notifications which are also translated.