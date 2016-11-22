class Node:
	children = []
	state = []
	def __init__(self,value = None):
		if value is not None:
			self.state = value
	def addChild(self,newNode):
		self.children.append(newNode)
	def __str__(self):
		return str(self.state)
	__repr__ = __str__
	
	def __cmp__(self,other):
		if self.state != other.state:
			return -1
		else:
			return 0
	def __eq__(self,other):
		for i in self.state:
			if i not in other.state:
				return False
		for i in other.state:
			if i not in self.state:
				return False
		return True