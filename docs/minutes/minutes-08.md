| Key | Value                                 |
| --- |---------------------------------------|
| Date: | 16.01.2026                            |
| Time: | 15:45                                 |
| Location: | Drebbelweg 2                          |
| Chair: | Esad Yurt                             |
| Minute Taker: | Serban Rosca                          |
| Attendees: | Daan, Davide, Esad, Roni, Serban, Vlad |

## Agenda Items

- Opening by chair (1 min)
    - Everyone arrived on time.
- Check-in: How is everyone doing? (5 min)
    - Everyone doing allright.
    - Exams and deadlines coming up though.
- Announcements by the TA (5 min)
    - The results for the Implemented feature parts were released; We did decently well (only anbout 26% of teams actually passed), we got a 7/10, and most of what we lost points on had been implemented the next day.
- Updates and review about last week: What have we done so far? (5-10 min)
    - Vlad:
        - Shopping list (https://gitlab.ewi.tudelft.nl/cse1105/2025-2026/teams/csep-team-40/-/merge_requests/47)
        - Recipe scaling (https://gitlab.ewi.tudelft.nl/cse1105/2025-2026/teams/csep-team-40/-/merge_requests/41)
    - Roni:
        - Favourite persistance (https://gitlab.ewi.tudelft.nl/cse1105/2025-2026/teams/csep-team-40/-/merge_requests/44)
    - Serban:
        - Search Features (https://gitlab.ewi.tudelft.nl/cse1105/2025-2026/teams/csep-team-40/-/merge_requests/46)
    - Davide:
        - Bugfix of adding nutritional value to ingredients (https://gitlab.ewi.tudelft.nl/cse1105/2025-2026/teams/csep-team-40/-/merge_requests/40)
    - Esad:
        - ingredients page display
- Presentation of the current app to TA (5-10 min)
    - Happened during the previous section, as we actually demo'd what we added in realtime.
    - Presentation was well received, and some feature were discussed a bit more in depth (see next points)
- Current Issues & Talking Points (20 min)
    - Discussed future plans + what other tasks we have this week, as not everyone has 100 LOC done.
        - Esad suggests ingredients page
        - Vlad suggests looking at feedback for nutritional value chapter (5-10 failed user stories)
            - Extra feature for nutritional value (adding another tracker, sugar, ...)
            - See ingredients sorted by name
        - Serban suggests:
            - Adding button to add recipe
            - Contextual right click menu
    - Summarised dsitribution of actions:
        - Vlad
            - Client Syncronization Extra Features (adding sync to ingredients)
        - Esad
            - Takes adding the extra nutrional feature (keeping track of another measurement aside from fat, protein, ...) 
        - Serban
            - Searching feature finishing + Nutritional extra feature (fixing some small search bugs, exit input quitting, and allowing to search based on calories / protein / other stuff, counting as an extra feature for nutrition as well)
        - Roni
            - Contextual right click menu for ingredients and recipes
        - Davide:
            - Adding a way to add and select a language when adding recipes
    - README.md discussions (final week coming up):
        - List custom build steps, if any.
        - Explicitly list what extra features we have
        - Maybe some project setup for initial data to allow for easier setup / verification from TAs
        - Shouldn't go overboard, short and succint is the way. (A TA has to check tens of projects)
- Questons to the TA:
    - Esad asks what happened, considering Daan resigned; Does that impact our grade in any way?
        - Not much, approximately 0.1 added to grade if anything
        - HOWEVER it does aid us by fixing some problems we had with the Tasks & Planning section, so we might get a better score there.
    - Next week is code freeze, do the requirements apply for next week?
        - Yes, they do apply, however if you never missed any week until now you can skip 1 without your grade being affected at all.
    - Roni asks about Buddycheck (Summative) deadline.
        - Brightspace says 30th of january
        - Follow up question, how often to people fail the Buddycheck, and, is there a repair?
            - We shouldn't have to worry about that, and there is also a repair.
- Went over deleting branches:
    - As gitlab wasn't working, we just listed them down:
        - deleteIngredientFromRecipe
        - recipeSearch
        - warningDeleteIngredient
        - 7-language-persistence-settings
        - estimateRecipeKcal
        - defineServings
        - 11-adding-languages-to-recipes
        - retroactive-testing
        - removeFavouriteGlobal
- Small panic as apparently the main branch had a runtime error, but was fixed shortly.
- Established next week's chair by a dice roll
    - It is Vlad.
- Summarize action points (3 min)
    - Already done and listed, see "Summarised dsitribution of actions" on line 42.
- Feedback & questions (2 min)
    - None left, all were addressed and discussed.
- Closure (1 min)
    - Chair thanked the team, the team thanked the chair. The meeting went really well.
