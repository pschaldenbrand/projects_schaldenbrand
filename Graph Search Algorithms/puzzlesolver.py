import Queue
from Graph import Graph
from Node import Node
from operator import itemgetter, attrgetter, methodcaller
import time
import sys
import ast


capacity = []

cityCosts = {}
cityToLocation = {}
locationToCity = {}
cities = []

problem = 1

timeEff = 1
maxFront = -1
maxExpl = -1

def main():
	global problem
	if len(sys.argv) < 3:
		print "not enough arguments"
		return
	filename = sys.argv[1]
	alg = sys.argv[2]
	heur = ''
	if len(sys.argv) == 4:
		heur = sys.argv[3]
	
	f = open(filename,'r')
	lines = f.readlines()
	probName = lines[0].replace(' ','').replace('\n','')
	
	startAndGoal = []
	
	print 'Problem is      \t'+probName.replace('\n','')
	
	if probName == 'jugs':
		problem = 1
		startAndGoal = initJugs(lines)
	if probName == 'cities':
		problem = 3
		startAndGoal = initCities(lines)
	if probName == 'pancakes':
		problem = 4
		startAndGoal = initPancakes(lines)
	
	start = Graph(Node(startAndGoal[0]))
	goal = (startAndGoal[1])
	
	solPath = []
	print 'Search algorithm\t'+alg.replace('\n','')
	if alg == 'bfs':
		solPath =  (BFS(start, goal))
	if alg == 'dfs':
		solPath =  (DFS(start, goal))
	if alg == 'iddfs':
		solPath =  (IDDFS(start, goal))
	if alg == 'unicost':
		solPath =  (unicost(start, goal))
	if alg == 'greedy':
		solPath =  (greedy(start, goal, heur))
	if alg == 'astar':
		solPath =  (aStar(start, goal, heur))
	if alg == 'idastar':
		solPath =  (IDAStar(start,goal, heur))
	
	if problem == 3:
		temp = solPath[:]
		for i in range(len(temp)):
			solPath[i] = locationToCity[str(temp[i]).replace(' ','')]
	print '\nSolution Path:'
	for i in range(len(solPath)):
		print '\t'+str(solPath[i])
	
	print
	print 'Nodes Visited:  '+str(timeEff)
	if maxFront > 0:
		print 'Maximum nodes in Frontier:  '+str(maxFront)
	if maxExpl > 0:
		print 'Maximum nodes in Explored:  '+str(maxExpl)
	
##################################
#		INITIALIZE PROBLEMS
##################################
def initJugs(l):
	global problem
	global capacity
	capacity = list(ast.literal_eval(l[1]))
	if len(capacity) == 2:
		problem = 1
	else:
		problem = 2
	
	s = list(ast.literal_eval(l[2]))
	g = list(ast.literal_eval(l[3]))
	
	return [s,g]
	
def initPancakes(l):
	s = list(ast.literal_eval(l[1]))
	g = list(ast.literal_eval(l[2]))
	return [s,g]

def initCities(l):
	global cities
	global cityToLocation
	global cityCosts
	global locationToCity
	
	c = ast.literal_eval(l[1])
	#print cities
	for i in range(len(c)):
		coord = [c[i][1],c[i][2]]
		cities.append(coord)
		cityToLocation[c[i][0]] = coord
		locationToCity[str(coord).replace(' ','')] = c[i][0]
	#print l[2].replace('"','').replace('\n','')
	start = cityToLocation[l[2].replace('"','').replace('\n','')]
	goal = cityToLocation[l[3].replace('"','').replace('\n','')]
	
	for i in range(4,len(l)):
		if len(l[i]) < 5:
			continue
		c = ast.literal_eval(l[i])
		cityCosts[c[0]+c[1]] = c[2]
		cityCosts[c[1]+c[0]] = c[2]
	return [start,goal]
	
def main1():
	
	global problem
	problem = 1
	
	capacity = [3,4]
	g = Graph(Node([0,0]))
	goal = [0,2]
	
	print 'BFS'
	#print BFS(g,goal)
	
	
	print '\nDepthLimited'
	print DepthLimited(g,goal,9)
	
	print '\nIDDFS'
	print IDDFS(g,goal)
		
	print '\nUnicost'
	print unicost(g,goal)
	
	#print '\nGreedy'
	#print greedy(g,goal,2)
	
	print '\nA*'
	print aStar(g,goal,1)
	
	
	print '\nCities Problem\n'
	problem = 3
	g = Graph(Node([1,1]))
	goal = [1,5]
	
	print 'BFS'
	#print BFS(g,goal)
	
	print '\nDFS'
	print DFS(g,goal)
	
	print '\nDepthLimited'
	print DepthLimited(g,goal,9)
	
	print '\nIDDFS'
	print IDDFS(g,goal)
	
	print '\nUnicost'
	print unicost(g,goal)
	
	print '\nGreedy'
	print greedy(g,goal,1)
	
	print '\nA*'
	print aStar(g,goal,1)
	
	print '\nPancake Problem\n'
	
	problem = 4
	g = Graph(Node([3,-1,2,5,4]))
	goal = [1,2,3,4,5]
	
	start = time.time()
	print 'BFS'
	print BFS(g,goal)
	end = time.time()
	print 'Time elapsed: '+str(end-start)
	
	print '\nDFS'
	#print DFS(g,goal)
	
	start = time.time()
	print '\nDepthLimited'
	print DepthLimited(g,goal,9)
	end = time.time()
	print 'Time elapsed: '+str(end-start)
	
	start = time.time()
	print '\nIDDFS'
	print IDDFS(g,goal)
	end = time.time()
	print 'Time elapsed: '+str(end-start)
	
	start = time.time()
	print '\nUnicost'
	print unicost(g,goal)
	end = time.time()
	print 'Time elapsed: '+str(end-start)
	
	start = time.time()
	print '\nGreedy'
	print greedy(g,goal,1)
	end = time.time()
	print 'Time elapsed: '+str(end-start)
	
	start = time.time()
	print '\nA*'
	print aStar(g,goal,1)
	end = time.time()
	print 'Time elapsed: '+str(end-start)
	
	start = time.time()
	print 'kids'
	print pancakeChildren([1,2,3])

######################################
#		CHILDREN AND COST FUNCTIONS
######################################

def childrenFcn(node):
	if problem == 1:
		return twoJugChildren(node.state,capacity)
	if problem == 2:
		return threeJugChildren(node.state,capacity)
	if problem == 3:
		return cityChildren(node.state)
	if problem == 4:
		return pancakeChildren(node.state)

def cost(path):
	if problem == 1 or problem == 2:
		return len(path)
	if problem == 3:
		c = 0
		for i in range(len(path)-1):
			cities = locationToCity[str(path[i]).replace(' ','')] + locationToCity[str(path[i+1]).replace(' ','')]
			if cities not in cityCosts:
				c += 999999
				continue
			c += cityCosts[cities]
		return c
	if problem == 4:
		return len(path)
		
	
def twoJugChildren(state, capacities):
	children = []
	#fill jugs
	if(state[0] != capacities[0]):
		children.append(Node([capacities[0],state[1]]))
	if(state[1] != capacities[1]):
		children.append(Node([state[0],capacities[1]]))
	if((state[0] != capacities[0]) and (state[1] != capacities[1])):
		children.append(Node([capacities[0],capacities[1]]))
	
	#Pour one jug into the other
	for i in range(2):
		for j in range(2):
			if i == j:
				continue
			if state[j] == capacities[j]:
				continue
			if state[i] == 0:
				continue
			#if (s[i] + s[j]) >= c[j]:
			temp = state[:]
			temp[i] = max(0,state[i]-(capacities[j]-state[j]))
			temp[j] = min(capacities[j],state[i]+state[j])
			children.append(Node(temp))
	
	#empty the jugs
	if(state[0] != 0):
		children.append(Node([0,state[1]]))
	if(state[1] != 0):
		children.append(Node([state[0],0]))
	if(state[0] != 0 and state[1] != 0):
		children.append(Node([0,0]))
	
	#return unique values
	uniqueChildren = []
	for i in children:
		if i not in uniqueChildren:
			uniqueChildren.append(i)
	return uniqueChildren

def threeJugChildren(s, c):
	#s is state and c is capacity
	children = []
	
	#Fill Jugs
	for i in range(3):
		if(s[i] != c[i]):
			temp = s[:]
			temp[i] = c[i]
			children.append(Node(temp))
	for i in range(3):
		for j in range(3):
			if i == j:
				continue
			if s[i] != c[i] and s[j] != c[j]:
				temp = s[:]
				temp[i] = c[i]
				temp[j] = c[j]
				children.append(Node(temp))
	if s[0] != c[0] and s[1] != c[1] and s[2] != c[2]:
		children.append(Node(c[:]))
	
	#Pour one jug into the other
	for i in range(3):
		for j in range(3):
			if i == j:
				continue
			if s[j] == c[j]:
				continue
			if s[i] == 0:
				continue
			#if (s[i] + s[j]) >= c[j]:
			temp = s[:]
			temp[i] = max(0,s[i]-(c[j]-s[j]))
			temp[j] = min(c[j],s[i]+s[j])
			children.append(Node(temp))
			
	
	#Empty jugs
	for i in range(3):
		if(s[i] != 0):
			temp = s[:]
			temp[i] = 0
			children.append(Node(temp))
	for i in range(3):
		for j in range(3):
			if i == j:
				continue
			if s[i] != 0 and s[j] != 0:
				temp = s[:]
				temp[i] = 0
				temp[j] = 0
				children.append(Node(temp))
	if s[0] != 0 and s[1] != 0 and s[2] != 0:
		children.append(Node([0,0,0]))
		
	#return unique values
	uniqueChildren = []
	for i in children:
		if i not in uniqueChildren:
			uniqueChildren.append(i)
	return uniqueChildren

def cityChildren(s):
	temp = cities[:]
	temp.remove(s)
	ret = []
	for i in temp:
		ret.append(Node(i))
	return ret

	
def pancakeChildren(s):
	children = []
	for i in range(len(s)+1):
		child = []
		child = s[:i]
		child.reverse()
		for j in range(len(child)):
			child[j] = child[j] * -1
		child = child + s[i:]
		children.append(Node(child))
	if Node(s) in children:
		children.remove(Node(s))
	return children

	
	
#######################################
#    SEARCH ALGORITHMS
#######################################
limit = -1
maximum = 200

def DepthLimited( graph, value, M ):
	global limit
	limit = M
	return DFS(graph,value)


def IDDFS(graph,value):
	i = 1
	while(True):
		#print i
		ret = DepthLimited(graph,value,i)
		if ret != False:
			return ret
		i += 1
		
		'''print 'maxFront'+str(maxFront)
		print 'maxExpl '+str(maxExpl)
		print 'eff '+ str(timeEff)
		print 
		print i'''
		
		
		
def BFS( graph, value ):
	global maxFront
	global maxExpl
	global timeEff
	
	explored = []
	frontier = []
	frontier.append([graph.root])
	
	while(True):
		if( len(frontier) == 0 ):
			return False
		
		path = frontier[0]
		timeEff += 1
		if( path[-1].state == value ):
			return path
		explored.append(path)
		frontier.remove(path)
		
		children = childrenFcn(path[-1])
		
		for child in children:
			temp = path[:] 
			temp.append(child)
			if (temp in frontier) or (temp in explored):
				continue
			frontier.append(temp)
			
		if len(frontier) > maxFront:
			maxFront = len(frontier)
		if len(explored) > maxExpl:
			maxExpl = len(explored)


def unicost( graph, value ):
	global maxFront
	global maxExpl
	global timeEff
	
	explored = []
	frontier = []
	frontier.append([0,[graph.root]])
	
	while(True):
		if( len(frontier) == 0 ):
			return False
		
		frontier.sort(key=(itemgetter(0)),reverse=False)
		path = frontier[0][1]
		
		timeEff += 1
		if( path[-1].state == value ):
			return path
			#print path		#take out the return and leave this to print all possible paths
		explored.append(path)
		frontier.remove(frontier[0])
		
		children = childrenFcn(path[-1])
		
		for child in children:
			temp = path[:] 
			temp.append(child)
			if ([cost(temp),temp] in frontier) or (temp in explored):
				continue
			frontier.append([cost(temp),temp])
		
		if len(frontier) > maxFront:
			maxFront = len(frontier)
		if len(explored) > maxExpl:
			maxExpl = len(explored)

			
def removeDuplicates(list):
	newList = []
	for i in range(len(list)):
		for j in range(len(list)):
			if i == j:
				continue
			p1 = list[i]
			p2 = list[j]
			if( p1[1][0] == p2[1][0] and p1[1][-1] == p2[1][-1]):
				if p1[0] > p2[0]:
					if p2 not in newList:
						newList.append(p2)
				else:
					if p1 not in newList:
						newList.append(p1)
			#else:
			#	print 'hey'
		if list[i] not in newList:
			newList.append(list[i])
	return newList

	
def DFS( graph, value ):
	root = graph.root
	return DFSrec([root],value)#,[])
def DFSrec( path, value):#,expl ):
	global maxFront
	global maxExpl
	global timeEff
	
	if len(path) > maxFront:
		maxFront = len(path)
	'''if len(expl) > maxExpl:
		maxExpl = len(expl)'''
	
	if len(path) > maximum:
		return False
	if (limit >= 0) and (len(path) > limit):
		return False
		
	if path[-1].state == value:
		return path
	timeEff += 1
	
	#expl.append(path)
	children = childrenFcn(path[-1])

	for i in children:
		tempPath = path[:]
		tempPath.append(i)
		'''if tempPath in expl:
			print 'path in explored'
			continue'''
		ret = DFSrec( tempPath, value )
	
		if ret != False:
			return ret
	return False

	
def aStar( graph, value, heuristic ):
	global maxFront
	global maxExpl
	global timeEff
	
	explored = []
	frontier = []
	frontier.append([getHeuristic(graph.root.state,value,heuristic),[graph.root]])
	
	while(True):
		if( len(frontier) == 0 ):
			return False
		#frontier = removeDuplicates(frontier[:])
		frontier.sort(key=(itemgetter(0)),reverse=False)
		path = frontier[0][1]
		
		timeEff += 1
		if( path[-1].state == value ):
			return path
			#print path		#take out the return and leave this to print all possible paths
		explored.append(path)
		frontier.remove(frontier[0])

		children = childrenFcn(path[-1])
		
		for child in children:
			temp = path[:] 
			temp.append(child)
			if ([cost(temp),temp] in frontier) or (temp in explored):
				continue
			frontier.append([cost(temp)+getHeuristic(temp[-1].state,value,heuristic),temp])
		if len(frontier) > maxFront:
			maxFront = len(frontier)
		if len(explored) > maxExpl:
			maxExpl = len(explored)
			
			
def greedy( graph, value, heuristic ):
	global maxFront
	global maxExpl
	global timeEff
	
	explored = []
	frontier = []
	frontier.append([getHeuristic(graph.root.state,value,heuristic),[graph.root]])
	
	while(True):
		if( len(frontier) == 0 ):
			return False
		#frontier = removeDuplicates(frontier[:])
		frontier.sort(key=(itemgetter(0)),reverse=False)
		path = frontier[0][1]
		
		timeEff += 1
		if( path[-1].state == value ):
			return path
			#print path		#take out the return and leave this to print all possible paths
		explored.append(path)
		frontier.remove(frontier[0])
		
		children = childrenFcn(path[-1])
		
		for child in children:
			temp = path[:] 
			temp.append(child)
			if ([cost(temp),temp] in frontier) or (temp in explored):
				continue
			#print getHeuristic(temp[-1].state,value,heuristic)
			frontier.append([getHeuristic(temp[-1].state,value,heuristic),temp])
		if len(frontier) > maxFront:
			maxFront = len(frontier)
		if len(explored) > maxExpl:
			maxExpl = len(explored)


def costLimitedDFS(root, goal, cutOff, h):
	global maxFront
	global maxExpl
	global timeEff

	frontier = [[getHeuristic(root.state,goal,h),[root]]]
	nextMin = 99999999

	while(True):
		if len(frontier) == 0:
			return [nextMin,False]
		path = frontier[0][1]

		frontier.remove(frontier[0])
		if cost(path) <= cutOff:
			if path[-1].state == goal:
				return [nextMin,path]
			timeEff += 1
			
			children = childrenFcn(path[-1])
			
			for child in children:
				temp = path[:]
				temp.append(child)
				if( [cost(temp),temp] in frontier) :
					continue
				frontier.append([cost(temp)+getHeuristic(temp[-1].state,goal,h),temp])
			if len(frontier) > maxFront:
				maxFront = len(frontier)
		else:
			if cost(path) < nextMin:
				nextMin = cost(path)
			
			
def IDAStar(graph, goal, h):
	root = graph.root
	cutOff = cost([root])
	
	while(True):
		returned = costLimitedDFS(root, goal, cutOff, h)
		cutOff = returned[0]
		solution = returned[1]
		if solution != False:
			return solution
		if cutOff > 9999999:
			return False
			
			
			
####################################
# 			HEURISTICS
####################################
def getHeuristic( s, v, h ):
	h = int(h)
	if (problem == 1 or problem == 2) and h == 1:
		return jugHeuristic1(s,v)
	if (problem == 1 or problem == 2) and h == 2:
		return jugHeuristic2(s,v)
	if problem == 3:
		return cityHeuristic( s, v )
	if problem == 4 and h == 1:
		return pancakeHeuristic1( s, v )
	if problem == 4 and h == 2:
		return pancakeHeuristic2( s, v )

def pancakeHeuristic1(s,v):
	val = 0
	for i in range(len(s)):
		if s[i] != v[i]:
			val = val + 1
	return val

def pancakeHeuristic2(s,v):
	val = 0
	for i in s:
		if i < 0:
			val += 1
	for i in range(len(s)):
		t = s[i]
		if t < 0:
			t = t * -1
		t = t-1
		t = t-i
		if t < 0:
			t = t*-1
		val += t
	#val = val/len(s)
	return val

def jugHeuristic1( state, value ):
	#return the amount of jugs in state not equal to the goal
	ret = 0
	for i in range(len(state)):
		if state[i] != value[i]:
			ret = ret + 1;
	return ret
	
def jugHeuristic2( s, v ):
	#return the difference in jug sizes
	ret = 0
	for i in range(len(s)):
		t = s[i] - v[i]
		if t < 0:
			t = t*-1
		ret = ret + t
	return t

	
def cityHeuristic( s, v ):
	return ((s[0]-v[0])**2 + (s[1]-v[1])**2)**(0.5)
	
if __name__ == "__main__": main()