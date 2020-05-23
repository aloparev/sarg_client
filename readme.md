# Six-gons are really great

## intro

This client implementation contains minimax (https://en.wikipedia.org/wiki/Minimax#Minimax_algorithm_with_alternate_moves) and alpha-beta (https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning) search algorithms for the next move. The switch is in the logic getBestMoveForOwner() function.

## run
### server
```
java -Djava.library.path=repo/htw/ai/lenz/sarg-server/3.0/lib/native -jar repo/htw/ai/lenz/sarg-server/3.0/sarg-server-3.0.jar 400 900 700 showcoords
```
- window mode with coords on the screen
- 400 sec move time for analyzing the board
```
java -Djava.library.path=lib/native -jar repo/htw/ai/lenz/sarg-server/3.0/sarg-server-3.0.jar 4 headless
```
- windowless mode with four sec for move

### client
```
mvn clean package
java -jar target/SargClient.jar <SERVER_IP> <TEAM_NAME> <EVALUATION_FUNCTION>
eg: java -jar target/SargClient.jar 127.0.0.1 abeta 3
```
Available evaluation functions and their win probability:
1. getPointsFieldFraction 34%
2. getPointsFieldFractionAndAvgDistanceAll 17%
3. getPointsFieldFractionAndAvgDistanceMy 49%

### evaluation script
The goal of evaluation script if to play the evaluation functions against each other to determine who's the best. The number of games can be adjusted in the for loop. To run, type from project root: 
```
./run
```

## improvement ideas
1. keep key set of move maps as int array only
    - getBestMoveForOwner() keys/moves mess
2. merge left/right move finder functions in one, using loop with two variables if possible
3. evaluation f.
    - my stones
    - enemy stones
    - distance to the end
    - points
    - weights
4. since the base board is addressed everywhere in the logic, why not inject root board instance directly   
5. search alg and evaluation f switchers as parameters
6. parametrize run script 