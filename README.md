# Namesake
A Mass File Renaming program.  Cross-platform
Requires Java 1.8 or higher

Can rename multiple files at once, using very an intuitive interface made with JavaFX.

Quick tips:
When selecting a substring to replace, remove, or add after,
you can use \[] or \() to select everything inside brackets or parentheses respectively
The Extension Filter box allows comma separated values to select multiple extension types (png,jpg,mp4)


Any errors when trying to rename will be written in a file called errorlog.txt, located in the same folder the jar or executable is.  
Errors generally are that the name you're trying to rename a file to already exists, or you do not have permission to edit that file.

When changes are made, the file undolog.dat is created.  
This saves all changes you've made in your sessions, and the "Undo Last" button will undo the last set of changes you made.

A file watcher runs in the background, and refreshes the file list every 5 seconds.
