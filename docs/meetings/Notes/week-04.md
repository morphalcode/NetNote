# Minutes for week 4 meeting

##### Things that need further consideration:
- We need to talk more about tags and collections
- We need to talk about linking tables in another meeting
- We need to start thinking about accessibility
- We should ask about tags on mattermost (when should a tag be saved)

##### Events:
- The backlog was updated
- A file called "Error log" was added to docs/TeamDocuments and everyone should use it to report any errors, bugs or issues that they encounter in the app so that we can keep track of them
- Tudor Butoeru added a filter method in collection if anyone needs it in the future

##### Deadlines:
- BuddyCheck until Friday the 6th -> use the AID model!

##### Formative evaluations (We do not need to submit/upload anything):
- Tasks and planning (6th)
- Code contribution and code reviews (13th)

##### Agreements:
- When someone deletes a collection all notes should be moved to the default collection
- We will have a table for the relation between tag and note and a repository for this relation

##### What everyone did/is working on:
- Tudor Bordea will push and merge the methods he created in ServerUtils and the menu to change between collections after he solves the problems he is encountering
- Tudor Butoeru finished his task (the class and repository for the collections). He is able to pick up a new task if needed
- Ori fixed the issues with the saving feature and the errors that happen when the application is started without any notes. He will make it so that when you delete a note it automatically refreshes the contrent and the title. He was also assigned with the task of implementing the "add collection" feature during our meeting on Sunday
- Ianis is coding the basic things related to tags until we know how tags should work concretely/we get an answer on mattermost. He will also add checkstyle in his next MR (we should change some of the rules)
- Stefan added extensions to the markdown rendering. He no longer needs to make it so that the application shows the errors during rendering since this requirement was removed from the backlog. He needs to meet the contribution criteria for the next weeks. His new task is to work on tests for the collections API and maybe references to other notes
- Beau finished the API for collections. He is able to pick up a new task

