from Node import Node

class Graph:
	#root = Node();
	
	def __init__(self,rootNode=None):
		if rootNode is not None:
			self.root = rootNode
		else:
			self.root = Node()
		
		

	