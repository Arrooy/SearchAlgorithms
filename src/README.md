# Search Algorithms
SBC project
## Requirements:
- JDK: openjdk-16
- Configure language level to 16 in Intellij project confiuguration.
- Check Meaven installed gson lib.
# Execution
Open the project with Intellij and execute main inside the Main class.
Important Main variables to play with:
- TESTING -> When true, disables the Menu.
- EXECUTE_EVALUATION -> When EXECUTE_EVALUATION is true and TESTING is also true then the test suite is executed. 

## Test suite:
Main line 70 defines the list of tests to evaluate. The user can add tests as needed.
Each test is evaluated and benchmarked. The benchmark result is stored in a json file (src/main/Output/EvaluationResults.json)

This benchmark json can be visualized with a Python script (src/pythonScripts/graphs.py). 
Make sure to check its contents before executing.

Team members: Adrià Arroyo, Miquel Saula, Roger Galván