Peter Schaldenbrand
pls21
3771162

python version: 2.7.11

To run the program type in the form of:

	python puzzlesolver.py (filename).config (search algorithm) [heuristic]

for example:
	python puzzlesolver.py test_jugs.config idastar 2
This would solve the problem in test_jugs.config using the ida* algorithm
using the second heuristic for the jug problem.

Both the pancake problem and jug problem have two heuristics ("1" and "2")
and cities has just one heuristic ("1")

So the keywords for heuristic are 1 or 2
	ex. python puzzlesolver.py test_cities.config greedy 1
	use these heuristics for greedy, astar, and ida*

The keywords for the algorithm names are:
	bfs
	dfs
	iddfs
	unicost
	greedy
	astar
	idastar
	
The test files that I have included are:
	test_jugs.config
	jugs.config
	test_cities.config
	cities.config
	test_pancakes1.config
	test_pancakes2.config
	test_pancakes3.config
	
I've consulted stack overflow for simple questions native to using python.
I have not copied significant amounts of code from this or any site.