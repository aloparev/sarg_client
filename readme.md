# Six-gons are really great

## intro

This client implementation contains minimax (https://en.wikipedia.org/wiki/Minimax#Minimax_algorithm_with_alternate_moves) and alpha-beta (https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning) search algorithms for the next move. The switch is in the logic getBestMoveForOwner() function.

## run server
1. java -Djava.library.path=lib/native -jar sarg.jar 400 900 700 showcoords
- window mode with coords on the screen
- 400 sec move time
2. java -Djava.library.path=lib/native -jar sarg.jar 4 headless
- windowless mode with four sec for move

## things that can be improved
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