# commitStats

A program written in poor style to help give practice with the VS Code debugger and refactoring.

## Setup and Exploration
1. Have 1 partner fork the repository and add the other partner as a collaborator.
1. Have both partners clone that fork, NOT the base repository.
2. Open the repository in VS Code. Make sure you open the repository root, not the directory above. It should say COMMITSTATS directly under EXPLORER when the left file explorer is open.
3. Open src/Main.java.
4. Run the file by clicking the grey run button over `main` (this sometimes takes a while to pop up when you first open the file and VS Code extensions start up).
5. When prompted for a data file, use `data/commit_data.csv`.
6. Try using the program and discuss with your partner what information it gives the user. Do not worry about reading the code yet.
7. Open `data/commit_data.csv` in VS Code. Look through it and discuss with your partner what each of the 3 fields represents.

## Wave 1
1. Take an initial scan over the code. Discuss with your partner broadly what it looks like each part is doing. Add comments to general blocks of code giving broad descriptions. For example, `// This block of code reads data from the CSV`.
2. Look at the `mp1` variable. Strategically choose a position to set a breakpoint that will allow you to look in `mp1` and see the structure of what it contains in an iteration of a loop after a few key/value pairs have been put into it. Click the Debug button above `main` and use the debugger to look inside the variable. Discuss with your partner what you find.
3. Decide with your partner what a better variable name for `mp1` would be. Have one partner right click on the variable and choose "Rename symbol" to refactor the code. Save the file, and re-run it to make sure it still works. Fix any bugs if any are introduced.
4. Do the same for `dta`.
5. Add, commit, and push your code.

## Wave 2
1. Extract the file parsing logic into a helper method that has the following signature:

