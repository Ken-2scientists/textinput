A short bit of Reagent sample code to help me better understand
when re-rendering is triggered with components receive new props
versus deref'ing atoms and cursors.

The use case here is a simple text entry field that toggles between
display and edit modes when clicked.

References
----------

 * https://github.com/Day8/re-frame/wiki/When-do-components-update%3F
 * https://github.com/reagent-project/reagent-cookbook/tree/master/basics/component-level-state
 * https://reagent-project.github.io/news/news050.html  (description of cursors)


This project was created via `lein new reagent textinput`
