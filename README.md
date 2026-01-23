# CSEP Template Project

To run the template project from the command line, you either need to have [Maven](https://maven.apache.org/install.html) installed on your local system (`mvn`) or you need to use the Maven wrapper (`mvnw`). You can then execute

	mvn -pl server -am spring-boot:run

to run the server and

	mvn -pl client -am javafx:run

to run the client. Please note that the server needs to be running, before you can start the client.

Get the template project running from the command line first to ensure you have the required tools on your sytem.

Once it is working, you can try importing the project into your favorite IDE. Especially the client is a bit more tricky to set up there due to the dependency on a JavaFX SDK.
To help you get started, you can find additional instructions in the corresponding README of the client project.

<h3>4.2 **Automated Change Synchronization**</h3>
As an addition to this epic, we have implemented synchronization for the ingredients page as well. When a user adds, edits, or deletes an ingredient, this gets reflected across all other clients as well. This was done by a subscription to a channel specific for ingredients.

<h3>4.5 **Shopping List**</h3>
We have extended this epic by implementing checkboxes, which allow users to mark items on their shopping list or in the overview of ingredients which are going to be added from a recipe. This could help, for example, in marking them as bought when a person is at the supermarket. 
Ingredients which are marked in the overview will still be marked in the shopping list. Furthermore, as an addition to the user stories, the shopping list gets persisted across restarts of the app. So if a user closes the app and then opens it again, it will still find those ingredients in the shopping list.
