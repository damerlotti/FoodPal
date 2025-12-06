| Key          | Value                                  |
| ------------ | -------------------------------------- |
| Date :       | 05/dec/2025                            |
| Time :       | 15:45                                  |
| Location :   | Drebbelweg 2                           |
| Chair        | Daan                                   |
| Minute Taker | Vlad                                   |
| Attendees:   | Daan, Davide, Esad, Roni, Serban, Vlad |
Agenda Items :
- Opening by chair (1 min)
  - Roni and Daan (the current chair) were late, so this was delayed by about 15 minutes.
- Check-in: How is everyone doing? (1 min)
  - Quick question, everyone is doing well.
- Approval of the agenda - Does anyone have any additions? (3 min)
  - No additions were made to the agenda, although it was not uploaded on Gitlab on time.
- Announcements by the TA (0 / 5 minutes)
  - Gitlab will be down starting from next Wednesday, so we should make sure we are up to date with the main branch by then.
  - Next meeting (the one on **18 Dec**) will be online.
- Presentation of the current app to TA (5 minutes)
  - Vlad, Serban and Esad each presented their latest version of the project, given that Gitlab was slow and some MRs couldn't be merged into main.
  - **Vlad** showed most of the functionality regarding the Basic Requirements (adding a recipe, an ingredient, add/edit/delete operations).
  - **Serban** presented functionality regarding the searching of a recipe among the existing ones and downloading a recipe.
  - **Esad** showed how details of ingredients (nutritonal values, amount of recipes using it) are shown on the right side of the ingredients menu. 
- Questions for the TA
  - Can the first weeks of subjective grading be corrected? (3 minutes)
    - The feedback is summative, so it doesn't matter towards the grade, but we get the change to actually repair if something was done poorly.
    - So for example if we start having frequent Issues/Milestones just from week 4 onwards, it's still fine.
  - What falls under meaningful additions? (3 minutes)
    - If we have any ideas about what to add we should argue in the **README** file about why we did that and how did it help (e.g calories per serving).
  - backlog UI? + is UI considered usefull additions? (3 minutes)
     - We don't have to follow the backlog UI exactly.
     - UI improvements would go into accessibility.
- Talking Points: (Inform / brainstorm / decision-making / discuss)
  - Own meeting before next official one? (1 minute)
    - Maybe we'll have another meeting on **Monday, 15 Dec** given that the next meeting with the TA will be in 2 weeks.
  - Issue per backlog requirement or workable loads? (2 minutes)
    - Clarified that each backlog requirement should be split into multiple Gitlab issues.
  - Ingredients saved generally or separately (steak with subclasses like ribeye or steak and ribeye apart from each other) (5 minutes)
    - Discussed about issues arising when a user has a different version of some general, already existing ingredient.
    - Talked about storing a general category (e.g. meat) with subclasses for different types of meat, but it would be too complicated for the purpose of the project.
    - A nice solution would've been to store special versions in the user's configurations, and the general version to be used in recipes for other users.
      - But problems arise when deleting that ingredient (i.e should that recipe be updated for the other users as well?).
    - **Group decision**:
      - Simple solution to store just different named ingredients, the same for all users.
  - Possible sitdown to go over the code? (1 minute)
    - Esad asked about someone going over the code with him, did not happen during the meeting.
  - Someone else try the tests or help me? (1 minute)
     - Maybe Serban helps Daan with that
- Summarize action points: Who, what, when? (10 min)
  - We did not have time to assign tasks.
  - Established that we should implement the add, edit, delete operations for ingredients from the recipe overview, in order to finish the basic requirements.
  - Decided that we will further distribute tasks along the way, on Whatsapp.
- Feedback round: What went well and what can be improved next time? (3 min)
  - What went well:
    - People were involved in the conversation, an open environment.
  - What can be improved:
    - Some topics took too much time and we did not have enough time to assign tasks, maybe direct conversations to make sure the topic is relevant.
- Planned meeting duration != actual duration? Where / why did you misestimate? (3 min)
  - Meeting did not run within the allocated 45 minutes, due to starting late.
  - The questions for the TA and the talking points took more time than what was estimated.
- Question round: Does anyone have anything to add before the meeting closes? (4 min)
  - No further additions.
- Closure (1 min)
  - Chair ended the meeting.