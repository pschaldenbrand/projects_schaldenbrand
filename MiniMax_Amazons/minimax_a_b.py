import sys
import ast


def minimax(tree):
	return maxValue(tree)
	
def maxValue(tree):
	if type(tree) is tuple:
		print str(tree[0]) + " " + str(tree[1])
		return [0,tree[1]]
	maxVal = -999999999
	maxDir = ''
	for i in range(1, len(tree)):
		child = tree[i]
		v = minValue(child)
		if v[1] > maxVal:
			maxVal = v[1]
			maxDir = child[0]
	print str(tree[0]) + ' ' + str(maxVal)
	return [maxDir, maxVal]

def minValue(tree):
	if type(tree) is tuple:
		print str(tree[0]) + " " + str(tree[1])
		return [0,tree[1]]
	minVal = 999999999
	minDir = ''
	for i in range(1, len(tree)):
		child = tree[i]
		v = maxValue(child)
		if v[1] < minVal:
			minVal = v[1]
			minDir = child[0]
	print str(tree[0]) + ' ' + str(minVal)
	return [minDir, minVal]


def minimaxAB(tree):
	return maxValueAB(tree, -99999999, 9999999999)
	
def maxValueAB(tree, alpha, beta):
	if type(tree) is tuple:
		print str(tree[0]) + " " + str(tree[1])
		return [0,tree[1]]
	maxVal = -9999999999
	maxDir = ''
	for i in range(1, len(tree)):
		child = tree[i]
		v = minValueAB(child, alpha, beta)
		if v[1] > maxVal:
			maxVal = v[1]
			maxDir = child[0]
		
		if v[1] >= beta:
			return [maxDir, maxVal]
		if alpha < v[1]:
			alpha = v[1]
	print str(tree[0]) + ' ' + str(maxVal)
	return [maxDir, maxVal]
	
def minValueAB(tree, alpha, beta):
	if type(tree) is tuple:
		print str(tree[0]) + " " + str(tree[1])
		return [0,tree[1]]
	minVal = 9999999999
	minDir = ''
	for i in range(1, len(tree)):
		child = tree[i]
		v = maxValueAB(child, alpha, beta)
		if v[1] < minVal:
			minVal = v[1]
			minDir = child[0]
		
		if v[1] <= alpha:
			return [minDir, minVal]
		if beta < v[1]:
			beta = v[1]
	print str(tree[0]) + ' ' + str(minVal)
	return [minDir, minVal]
		
	
	
def main():
	treeFile = open(sys.argv[1], 'r')
	tree = ast.literal_eval(treeFile.readline())
	print
	print 'File: '+str(sys.argv[1])
	print
	
	print 'minimax'
	v = minimax(tree)
	print "Root Value: "+str(v[1])
	print
	print 'minimax with alpha beta pruning'
	v = minimaxAB(tree)
	print "root Value: "+str(v[1])
	
if __name__ == '__main__': main()