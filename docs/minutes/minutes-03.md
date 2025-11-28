| Key          | Value                                  |
|--------------|----------------------------------------|
| Date :       | 28/nov/2025                            |
| Time :       | 15:45                                  |
| Location :   | Drebbelweg 2                           |
| Chair        | Vlad                                   |
| Minute Taker | Roni                                   |
| Attendees:   | Daan, Davide, Esad, Roni, Serban, Vlad |

Agenda Items :
- Opening by chair (1 min) by vlad
- - Everyone was on time and present
- Check - in : How is everyone doing ? (1 min) 
- - Everyone is doing well
- Announcements by the team (1 min)
- - No announcement was made
- Updates about last week's work
    - Formalized and submitted Code of Conduct (Serban) (1 min) => Done, by serban
    - Defined the checkstyle rules (Esad) (1 min) => Done, by esad
    - Implemented rules in xml file (Vlad) (1 min) => Done, by vlad
    - Extra meeting on Wednesday to review progress (1 min) => went well
    - Created ER diagram (Davide) (1 min) => Done, by davide
    - Completed self studies (all) (1 min) => everyone has done them
    - Completed coding tasks assigned during the additional meeting (1 min) => done
- Announcements by the TA (2 min) 
-  - Complete the buddy check by next week and gitlab might be down
  Questions for the TA:
  - made one commit with the wrong name and email, what happens or how can it be repaired? 
  - - ->TA has to look at the repo and see, but should be fine.
  - more information related to how do the coding lines get counted not asked?
  - - ->Any java line code is considered a java line, even if its 1 line split into 2 lines
    - it does not matter how weather to use comment or review, but make sure to use the right one
    - the ta will send us useful links for Dependency Injection/Spring
- Presentation of the current app to TA (0 min) 
- - We did show it at the end, it's just base classes and ui. Base ui for the add recipe functionality
- Approval of the agenda - Does anyone have any additions? (1 min) 
- - No additions
- Approval of last minutes - Did everyone read the last minutes? Do you approve of these? (1 min) 
- - Yes
- Talking Points : (Inform / brainstorm / decision-making / discuss)
    - Agenda - base classes discussion (5 min) 
    - - recipe class by Davide, we should make a menu for all the recipes by esad. Daan pointed out that it might be easier to do that once Davide finishes his recipe class. Serban suggests using an alternative to gitlab like Trello to issue tasks and milestones. Issues and milestones are part of the grading according to the TA.
    - Agenda - ui (opening window, scenes, sidebar, placeholders) (5 min)
    - - we've established a general structure with option to switch between ingredients and recipes, on the left being the names and on the right information about them (without additional scenes for a specific recipe/ingredient)
    - Agenda - backend (api, recipe/item CRUD, combining ingredients with recipes, converting between measurement units) (3 min)
    - -  we need to have the actual recipes in order to begin implementing crud operations, we already have conversion between units
    - Agenda - adding ingredient to recipe (2 min)
    - - We need to finish this quickly, as it's a necessity for the rest
    - Agenda - cloning recipe (2 min)
    - - Vlad mentioned it, but this will be tackled later
- Summarize action points : Who, what, when ? (10 min)
- - Davide will make the additional recipe button and controller, by friday (today) or saturday
- - Esad will make the menu of the ingredients and recipe steps & for each measurement show the amount, by friday
- - Serban will make the ingredients menu, it should be the same window rather than a new window. We discussed how the database is empty in gitlab, even if there is something in the database on the server.
- - Roni will make a bean class that initializes the database with 10 recipes, so it's easier to test.
- - Vlad talked about adding and deleting recipes, which should be pretty similar.
- - We cant plan all the tasks for next week effectively, so we will communicate on whatsapp which who and when someone can do a task
- Feedback round: What went well and what can be improved next time ? (3 min) 
- - Roni pointed out that the discussion happened in the summarize action points, even though that should be done in the talking points
- Planned meeting duration != actual duration ? Where / why did you misestimate ? (3 min) 
- - summarizing was a lot longer than 10 minutes
- Serban suggest using Figma to design the UI for all of us to collaborate on that will help us in the long term
- Question round: Does anyone have anything to add before the meeting closes ? (1 min)
- - Vlad will be the next week note taker, and daan the chair
- Closure (1 min)
