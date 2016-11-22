# CS1571 -- Assignment #3: El Juego de las Amazonas in Python 2.7
# For more information about the game itself, please refer to:
#      http://en.wikipedia.org/wiki/Game_of_the_Amazons
#
# This file provides some basic support for you to develop your automatic Amazons player.
# It gives everyone a common starting point, and it will make it easier for us to set your players
# to play against each other. Therefore, you should NOT make any changes to the provided code unless
# directed otherwise. If you find a bug, please email me.

# This implementation includes two class definitions, some utility functions,
# and a function for a human player ("human").
# The two classes are:
# - The Amazons class: the main game controller
# - The Board class: contains info about the current board configuration.
#   It is through the Board class that the game controller
#   passes information to your player function.
# More details about these two classes are provided in their class definitions

# Your part: Write an automatic player function for the Game of the Amazons.
# * your automatic player MUST have your email userID as its function name (e.g., reh23)
# * The main game controller will call your function at each turn with
#   a copy of the current board as the input argument.  
# * Your function's return value should be your next move.
#   It must be expressed as a tuple of three tuples: e.g., ((0, 3), (1,3), (8,3)) 
#    - the start location of the queen you want to move (in row, column)
#    - the queen's move-to location,
#    - the arrow's landing location.
#   If you have no valid moves left, the function should return False.

# As usual, we won't spend much time on the user interface. 
# Updates of the game board are drawn with simple ascii characters.
#
# - Below is a standard initial board configuration:
#   * The board is a 10x10 grid. (It is advisable to use a smaller board during development/debugging)
#   * Each side has 4 queens. The white queens are represented as Q's; the black queens are represented as q's
#
#      a b c d e f g h i j
#   9  . . . q . . q . . . 
#   8  . . . . . . . . . . 
#   7  . . . . . . . . . . 
#   6  q . . . . . . . . q 
#   5  . . . . . . . . . . 
#   4  . . . . . . . . . . 
#   3  Q . . . . . . . . Q 
#   2  . . . . . . . . . . 
#   1  . . . . . . . . . . 
#   0  . . . Q . . Q . . . 
#
# - During a player's turn, one of the player's queens must be moved, then an arrow must be shot from the moved queen.
# - the arrow is represented as 'x'
# - neither the queens nor their arrows can move past another queen or an arrow
#
# - The objective of the game is to minimze your opponent's queens' movement.
# - The game technically ends when one side's queens have no more legal moves,
#   but the game practically ends when the queens from the two sides have been
#   segregated. We will just count up the territories owned by each side and
#   the side with the larger territory will be declared the winner

############################################

import copy, random, re, time, sys

# The Amazons class controls the flow of the game.
# Its data include:
# * size -- size of board: assume it's <= 10
# * time_limit -- # of seconds a mchine is allowed to take (<30)
# * playerW -- name of the player function who'll play white
# * playerB -- name of the player function who'll play black
# * wqs -- initial positions of the white queens
# * bqs -- initial positions of the black queens
# * board -- current board configuration (see class def for Board)
# Its main functions are:
# * play: the main control loop of a game, which would:
#   - turn taking management: calls each auto player's minimax function (or "human")
#   - check for the validity of the player's move:
#     an auto player loses a turn if an invalid move is returned or if it didn't return a move in the alloted time  
#   - check for end game condition 
#   - declare the winner
# * update: this function tries out the move on a temporary board.
#   if the move is valid, the real board will be updated.
# * end_turn: just get the score from the board class

class Amazons:
    def __init__(self, fname):
        fin = open(fname, 'r')
        self.time_limit = int(fin.readline())
        self.size = int(fin.readline())
        self.playerW = fin.readline().strip()
        self.wqs = tuple(map(ld2rc,fin.readline().split()))
        self.playerB = fin.readline().strip()
        self.bqs  = tuple(map(ld2rc,fin.readline().split()))
        self.board = Board(self.size, self.wqs, self.bqs)

    def update(self, move):
        try:
            (src,dst,adst) = move
        except: return False

        # try out the move on a temp board        
        tmp_board = copy.deepcopy(self.board)
        if tmp_board.valid_path(src,dst):
            tmp_board.move_queen(src,dst)
            if tmp_board.valid_path(dst, adst):
                # the move is good. make the real board point to it
                tmp_board.shoot_arrow(adst)
                del self.board
                self.board = tmp_board
                return True
        # move failed. 
        del tmp_board
        return False

    def end_turn(self):
        return self.board.end_turn()

    def play(self):
        bPlay = True
        wscore = bscore = 0
        while (bPlay):
            for p in [self.playerW, self.playerB]:
                # send player a copy of the current board
                tmp_board = copy.deepcopy(self.board)
                tstart = time.clock()
                tmp_board.time_limit = tstart+self.time_limit
                move = eval("%s(tmp_board)"%p)
                tstop = time.clock()
                del tmp_board

                if move:
                    print p,": move:", [rc2ld(x) for x in move],"time:", tstop-tstart, "seconds"
                else: 
                    print p,"time:", tstop-tstart, "seconds"
                    # if move == False --> player resigned   
                    if self.board.bWhite:
                        (wscore, bscore) = (-1,0)
                    else: (wscore, bscore) = (0,-1)
                    bPlay = False
                    break

                # only keep clock for auto players
                if p != "human" and (tstop - tstart) > self.time_limit:
                    print p, ": took too long -- lost a turn"
                elif not self.update(move):
                    print p, ": invalid move", move, " lost a turn"

                # at the end of the turn, check whether the game ended
                # and update whether white is playing next
                (wscore, bscore) = self.end_turn()
                if wscore and bscore:
                    continue
                else:
                    bPlay = False
                    break
        # print final board
        self.board.print_board()
        if wscore == -1:
            print self.playerW,"(white) resigned.", self.playerB,"(black) wins"
        elif bscore == -1:
            print self.playerB,"(black) resigned.", self.playerW,"(white) wins"
        elif not wscore:
            print self.playerB,"(black) wins by a margin of",bscore
        else: print self.playerW, "(white) wins by a margin of",wscore
                
        
##############################################
# The Board class stores basic information about the game configuration.
# 
# NOTE: The amount of info stored in this class is kept to a minimal. This
# is on purpose. This is just set up as a way for the game controller to
# pass information to your automatic player. Although you cannot change
# the definition of the Board class, you are not constrained to use the
# Board class as your main state reprsentation. You can define your own
# State class and copy/transform from Board the info you need.

# The Board class contains the following data:
#  * config: the board configuration represented as a list of lists.
#    The assumed convention is (row, column) so config[0][1] = "b0"
#  * bWhite: binary indicator -- True if it's white's turn to play
#  * time_limit: deadline for when a move must be returned by
# The Board class supports the following methods:
#  * print_board: prints the current board configuration
#  * valid_path: takes two location tuples (in row, column format) and returns 
#    whether the end points describe a valid path (for either the queen or the arrow)
#  * move_queen: takes two location tuples (in row, column format)
#    and updates the board configuration to reflect the queen moving
#    from src to dst
#  * shoot_arrow: takes one location tuple (in row, column format)
#    and updates the board configuration to include the shot arrow
#  * end_turn: This function does some end of turn accounting: update whose
#    turn it is and determine whether the game ended
#  * count_areas: This is a helper function for end_turn. It figures out
#    whether we can end the game
class Board:
    def __init__(self, size, wqs, bqs):
        self.time_limit = None
        self.bWhite = True
        self.config = [['.' for c in range(size)] for r in range(size)]
        for (r,c) in wqs:
            self.config[r][c] = 'Q'
        for (r,c) in bqs:
            self.config[r][c] = 'q'
            
    def print_board(self):
        size = len(self.config)
        print ("     Black")
        tmp = "  "+" ".join(map(lambda x: chr(x+ord('a')),range(size)))
        print (tmp)
        for r in range(size-1, -1, -1):
            print r, " ".join(self.config[r]), r
        print (tmp)
        print ("     White\n")

    def valid_path(self, src, dst):
        (srcr, srcc) = src
        (dstr, dstc) = dst        

        srcstr = rc2ld(src)
        dststr = rc2ld(dst)

        symbol = self.config[srcr][srcc]
        if (self.bWhite and symbol != 'Q') or (not self.bWhite and symbol != 'q'):
            print "invalid move: cannot find queen at src:",srcstr
            return False

        h = dstr-srcr
        w = dstc-srcc
        if h and w and abs(h/float(w)) != 1: 
            print("invalid move: not a straight line")
            return False
        if not h and not w:
            print("invalid move: same start-end")
            return False

        if not h:
            op = (0, int(w/abs(w)))
        elif not w:
            op = (int(h/abs(h)),0)
        else:
            op = (int(h/abs(h)),int(w/abs(w)))

        (r,c) = (srcr,srcc)
        while (r,c) != (dstr, dstc):
            (r,c) = (r+op[0], c+op[1])
            if (self.config[r][c] != '.'):
                print "invalid move: the path is not cleared between",srcstr,dststr
                return False
        return True

    def move_queen(self, src, dst):
        self.config[dst[0]][dst[1]] = self.config[src[0]][src[1]]
        self.config[src[0]][src[1]] = '.'

    def shoot_arrow(self, dst):
        self.config[dst[0]][dst[1]] = 'x'

    def end_turn(self):
        # count up each side's territories
        (w,b) = self.count_areas()
        # if none of the queens of either side can move, the player who just
        # played wins, since that player claimed the last free space.
        if b == w and b == 0:
            if self.bWhite: w = 1
            else: b = 1
        # switch player
        self.bWhite = not self.bWhite
        return (w,b)

    # adapted from standard floodfill method to count each player's territories
    # - if a walled-off area with queens from one side belongs to that side
    # - a walled-off area with queens from both side is neutral
    # - a walled-off area w/ no queens is deadspace
    def count_areas(self):
        # replace all blanks with Q/q/n/-
        def fill_area(replace):
            count = 0
            for r in range(size):
                for c in range(size):
                    if status[r][c] == '.':
                        count+=1
                        status[r][c] = replace
            return count
        
        # find all blank cells connected to the seed blank at (seedr, seedc) 
        def proc_area(seedr,seedc):
            symbols = {} # keeps track of types of symbols encountered in this region
            connected = [(seedr,seedc)] # a stack for df traversal on the grid
            while connected:
                (r, c) = connected.pop()
                status[r][c] = '.'
                for ops in [(-1,0),(1,0),(0,-1),(0,1),(-1,-1),(-1,1),(1,-1),(1,1)]:
                    (nr, nc) = (r+ops[0], c+ops[1])
                    if nr < 0 or nr >= size or nc < 0 or nc >= size:
                        continue
                    # if it's a new blank, need to process it; also add to seen
                    if self.config[nr][nc] == '.' and status[nr][nc] == '?':
                        status[nr][nc] = '.'
                        connected.append((nr,nc))
                    # if it's a queen or an arrow; just mark as seen
                    elif self.config[nr][nc] != '.': 
                        status[nr][nc] = 'x'
                        symbols[self.config[nr][nc]] = 1

            if 'Q' in symbols and not 'q' in symbols: # area belongs to white
                return (fill_area('Q'), 0, 0)
            elif 'q' in symbols and not 'Q' in symbols: #area belongs to black
                return (0, fill_area('q'),0)
            elif 'q' in symbols and 'Q' in symbols: # area is neutral
                return (0, 0, fill_area('n'))
            else: # deadspace -- still have to fill but don't return its area value
                fill_area('-')
                return (0,0,0)

        size = len(self.config)
        # data structure for keeping track of seen locations
        status = [['?' for i in range(size)] for j in range(size)]
        wtot = btot = ntot = 0
        for r in range(size):
            for c in range(size):            
                # if it's an empty space and we haven't seen it before, process it
                if self.config[r][c] == '.' and status[r][c] == '?':
                    (w,b,n) = proc_area(r,c)
                    wtot += w
                    btot += b
                    ntot += n
                # if it's anything else, but we haven't seen it before, just mark it as seen and move on
                elif status[r][c] == '?':
                    status[r][c] = 'x'
                    
        if ntot == 0: # no neutral space left -- should end game
            if wtot > btot:
                return (wtot-btot, 0)
            else: return (0, btot-wtot)
        else: return (wtot+ntot, btot+ntot)

# utility functions:
# ld2rc -- takes a string of the form, letter-digit (e.g., "a3")
# and returns a tuple in (row, column): (3,0)
# rc2ld -- takes a tuple of the form (row, column) -- e.g., (3,0)
# and returns a string of the form, letter-digit (e.g., "a3")

def ld2rc(raw_loc):
    return (int(raw_loc[1]), ord(raw_loc[0])-ord('a'))
def rc2ld(tup_loc):
    return chr(tup_loc[1]+ord('a'))+str(tup_loc[0])

# get next move from a human player
# The possible return values are the same as an automatic player:
# Usually, the next move should be returned. It must be specified in the following format:
# [(queen-start-row, queen-start-col), (queen-end-row,queen-end-col), (arrow-end-row, arrow-end-col)]
# To resign from the game, return False

def human(board):

    board.print_board()

    if board.bWhite:
        print("You're playing White (Q)")
    else:
        print("You're playing Black (q)")

    print("Options:")
    print('* To move, type "<loc-from> <loc-to>" (e.g., "a3-d3")')
    print('* To resign, type "<return>"')
    while True: # loop to get valid queen move from human
        while True: # loop to check for valid input syntax first
            raw_move = raw_input("Input please: ").split()
            if not raw_move: # human resigned
                return False
            # if they typed "a3-d3"
            elif re.match("^[a-j][0-9]\-[a-j][0-9]$",raw_move[0]):
                break
            else: print str(raw_move),"is not a valid input format"
        (src, dst) = map(ld2rc, raw_move[0].split('-'))
        if board.valid_path(src, dst):
            board.move_queen(src, dst)
            break 

    board.print_board()
    print("Options:")
    print('* To shoot, type "<loc-to>" (e.g., "h3")')
    print('* To resign, type "<return>"')
    while True: # loop to get valid move from human
        while True: # loop to check for valid syntax first
            raw_move = raw_input("Input please: ")
            if not raw_move:
                return False
            if re.match("^[a-j][0-9]$",raw_move):
                break
            else: print raw_move,"is not a valid input"
        adst = ld2rc(raw_move)
        if board.valid_path(dst,adst):
            return (src,dst,adst)

###################### Your code between these two comment lines ####################################
def pls21(board):
	global bestMove
	global startTime
	global endTime
	
	endTime = startTime+board.time_limit - 1
	
	piece = 'q'
	if board.bWhite:
		piece = 'Q'
	
	for i in range(2,10):
		#print 'depth = '+str(i)
		tree = makeTree(copy.deepcopy(board),piece,i)
		if (tree == 'outoftime'):
			if len(bestMove) == 0:
				endTime = endTime + 1000
				return minimaxAB( makeTree(copy.deepcopy(board),piece,i) )[0]
			return bestMove
		if (type(tree) is not list):
			return bestMove
		max = minimaxAB(tree)
		bestMove = max[0]
	return bestMove
	
bestMove = ()
startTime = time.time()
endTime = 0
	
def minimaxAB(tree):
	return maxValueAB(tree, -99999999, 9999999999)
	
def maxValueAB(tree, alpha, beta):
	if len(tree) <=1:
		print tree
	if type(tree[1]) is int:
		return [tree[0][1],tree[1]]
	
	maxVal = -9999999999
	maxDir = ''
	for i in range(1, len(tree)):
		child = tree[i]
		v = minValueAB(child, alpha, beta)
		if v[1] > maxVal:
			maxVal = v[1]
			maxDir = child[0][1]
		
		if v[1] >= beta:
			return [maxDir, maxVal]
		if alpha < v[1]:
			alpha = v[1]
	return [maxDir, maxVal]
	
def minValueAB(tree, alpha, beta):
	if type(tree[1]) is int:
		return [tree[0][1],tree[1]]
	minVal = 9999999999
	minDir = ''
	for i in range(1, len(tree)):
		child = tree[i]
		v = maxValueAB(child, alpha, beta)
		if v[1] < minVal:
			minVal = v[1]
			minDir = child[0][1]
		
		if v[1] <= alpha:
			return [minDir, minVal]
		if beta < v[1]:
			beta = v[1]
	return [minDir, minVal]
	
	
def makeTree(b,p,d):
	depth = d
	ret = makeTreeRec([copy.deepcopy(b.config),()],p,1,depth,-9999999,99999999)
	if type(ret) is str:
		return 'outoftime'
	else:
		return ret[1]

def makeTreeRec(b,p,level,maxDepth,alpha,beta):
	if time.time() > endTime:
		return 'outoftime'
	
	max = False
	if (level % 2 == 1):
		max = True
		
	children = successors(listToTuple(b[0]),p)
		
	if len(children) == 0:
		s = rankBoard2(b[0],p)
		#return [s,s]
		if ((level % 2) == 1):
			#return [100,100]
			return [0,1]
		else:
			#return [-100,-100]
			return [0,-1]
			
	if level == maxDepth:
		rank = rankBoard2(b[0],p)
		return [rank,rank]
		
	tree = [b]
	extreme = 99999999
	if max:
		extreme = -99999999
		
	for c in children:
		val = makeTreeRec(c,p,level+1,maxDepth,alpha,beta)
		
		if val == 'outoftime':
			return val
			
		tree.append( [c,val[1]] )
		
		if max:
			if val[0] > extreme:
				extreme = val[0]
		else:
			if val[0] < extreme:
				extreme = val[0]
		
		if max:
			if extreme >= beta:
				#print 'pruned in max'
				return [extreme,tree]
		else:
			if extreme <= alpha:
				#print 'pruned in min'
				return [extreme,tree]
		
		if max:
			if extreme > alpha:
				alpha = extreme
		else:
			if extreme < beta:
				beta = extreme
		
	return [extreme,tree]


'''	Heuristic that counts how many spaces are around each queen '''
def rankBoard(b,p):
	s = len(b)
	score1 = 0
	for i in range(len(b)):
		for j in range(len(b)):
			if b[i][j] != p:
				continue
				
			for k in range(i-1,i+2):
				for l in range(j-1,j+2):
					if k>=s or l>=s or k<0 or l<0:
						continue
					elif b[k][l] in ['x','q','Q']:
						continue
					else:
						score1 += 1
	if p == 'Q':
		p = 'q'
	else:
		p = 'Q'
	score2 = 0
	for i in range(len(b)):
		for j in range(len(b)):
			if b[i][j] != p:
				continue
				
			for k in range(i-1,i+2):
				for l in range(j-1,j+2):
					if k>=s or l>=s or k<0 or l<0:
						continue
					elif b[k][l] in ['x','q','Q']:
						continue
					else:
						score2 += 1
	return score1 - score2

'''	Heuristic. subtracts the # of possible moves by your queens by the
	# that your queens can make '''
def rankBoard2(b,p):
	otherPiece = 'Q'
	if p == 'Q':
		otherPiece = 'q'
	
	myscore = numMoves(b,p)
	theirscore = numMoves(b,otherPiece)
	if theirscore == 0:
		return myscore*10
	return myscore/theirscore
	
def numMoves(b,p):
	num = 0
	
	for i in range(len(b)):
		for j in range(len(b)):
			if b[i][j] != p:
				continue
			c = 0
			left = True
			right = True
			up = True
			down = True
			upleft = True
			upright = True
			downright = True
			downleft = True
			
			while( left or right or up or down or upleft or upright or downleft or downright):
				c += 1
				
				if left:
					x = i-c
					y = j
					if (isMove(b,p,x,y,i,j)):
						num += 1
					else:
						left = False
				if right:
					x = i+c 
					y = j 
					if (isMove(b,p,x,y,i,j)):
						num += 1
					else:
						right = False
				if up:
					x = i
					y = j + c
					if (isMove(b,p,x,y,i,j)):
						num += 1
					else:
						up = False
				if down:
					x = i
					y = j -c
					if (isMove(b,p,x,y,i,j)):
						num += 1
					else:
						down = False
				if upleft:
					x = i - c
					y = j + c
					if (isMove(b,p,x,y,i,j)):
						num += 1
					else:
						upleft = False
				if upright:
					x = i + c
					y = j + c
					if (isMove(b,p,x,y,i,j)):
						num += 1
					else:
						upright = False
				if downleft:
					x = i - c
					y = j - c
					if (isMove(b,p,x,y,i,j)):
						num += 1
					else:
						downleft = False
				if downright:
					x = i + c
					y = j - c
					if (isMove(b,p,x,y,i,j)):
						num += 1
					else:
						downright = False
	return num
	
def isMove(b,p,x,y,i,j):
	if ( x>=len(b) or x<0 or y >=len(b) or y<0 ):
		return False
	elif( b[x][y] in ['x','q','Q'] ):
		return False
	return True
	
def successors(board,piece):
	if type(board) is list:
		b = board
	elif type(board) is tuple:
		b = board
	else:
		b = listToTuple(board.config)
	p = piece
	
	ret = []
	
	for i in range(len(b)):
		for j in range(len(b)):
			if b[i][j] != p:
				continue
			c = 0
			left = True
			right = True
			up = True
			down = True
			upleft = True
			upright = True
			downright = True
			downleft = True
			
			while( left or right or up or down or upleft or upright or downleft or downright):
				c += 1
				
				if left:
					x = i-c
					y = j
					childs = successorHelper(b,p,x,y,i,j)
					if( len(childs) <= 0 ):
						left = False
					ret.extend(childs)
				if right:
					x = i+c 
					y = j 
					childs = successorHelper(b,p,x,y,i,j)
					if (len(childs) <= 0 ):
						right = False
					ret.extend(childs)
				if up:
					x = i
					y = j + c
					childs = successorHelper(b,p,x,y,i,j)
					if (len(childs) <= 0 ):
						up = False
					ret.extend(childs)
				if down:
					x = i
					y = j -c
					childs = successorHelper(b,p,x,y,i,j)
					if (len(childs) <= 0 ):
						down = False
					ret.extend(childs)
				if upleft:
					x = i - c
					y = j + c
					childs = successorHelper(b,p,x,y,i,j)
					if (len(childs) <= 0 ):
						upleft = False
					ret.extend(childs)
				if upright:
					x = i + c
					y = j + c
					childs = successorHelper(b,p,x,y,i,j)
					if (len(childs) <= 0 ):
						upright = False
					ret.extend(childs)
				if downleft:
					x = i - c
					y = j - c
					childs = successorHelper(b,p,x,y,i,j)
					if (len(childs) <= 0 ):
						downleft = False
					ret.extend(childs)
				if downright:
					x = i + c
					y = j - c
					childs = successorHelper(b,p,x,y,i,j)
					if (len(childs) <= 0 ):
						downright = False
					ret.extend(childs)
	return ret
	
def successorHelper(b,p,x,y,i,j):
	left = True
	
	if ( x>=len(b) or x<0 or y >=len(b) or y<0 ):
		left = False
	elif( b[x][y] in ['x','q','Q'] ):
		left = False
	else:
		t = tupleToList(b)#copy.deepcopy(b)
		t[i][j] = ''
		t[x][y] = p
		return( placeArrows(listToTuple(t),p,x,y,i,j) )
	return []
	
def placeArrows(b,p,i,j,originI,originJ):
	global Time
	
	left = True
	right = True
	up = True
	down = True
	upleft = True
	upright = True
	downright = True
	downleft = True
	
	ret = []
	c = 0
	while( left or right or up or down or upleft or upright or downleft or downright):
		c += 1
		
		if left:
			x = i - c
			y = j
			if ( x>=len(b) or x<0 or y >=len(b) or y<0 ):
				left = False
			elif( b[x][y] in ['x','q','Q'] ):
				left = False
			else:
				t = ((originI,originJ),(i,j),(x,y))
				newB = tupleToList(b)
				newB[x][y] = 'x'
				ret.append([newB,t])
				
		if right:
			x = i + c
			y = j
			if ( x>=len(b) or x<0 or y >=len(b) or y<0 ):
				right = False
			elif( b[x][y] in ['x','q','Q'] ):
				right = False
			else:
				t = ((originI,originJ),(i,j),(x,y))
				newB = tupleToList(b)
				newB[x][y] = 'x'
				ret.append([newB,t])
		if up:
			x = i 
			y = j + c
			if ( x>=len(b) or x<0 or y >=len(b) or y<0 ):
				up = False
			elif( b[x][y] in ['x','q','Q'] ):
				up = False
			else:
				t = ((originI,originJ),(i,j),(x,y))
				newB = tupleToList(b)
				newB[x][y] = 'x'
				ret.append([newB,t])
		if down:
			x = i 
			y = j - c
			if ( x>=len(b) or x<0 or y >=len(b) or y<0 ):
				down = False
			elif( b[x][y] in ['x','q','Q'] ):
				down = False
			else:
				t = ((originI,originJ),(i,j),(x,y))
				newB = tupleToList(b)
				newB[x][y] = 'x'
				ret.append([newB,t])
		if upleft:
			x = i - c
			y = j + c
			if ( x>=len(b) or x<0 or y >=len(b) or y<0 ):
				upleft = False
			elif( b[x][y] in ['x','q','Q'] ):
				upleft = False
			else:
				t = ((originI,originJ),(i,j),(x,y))
				newB = tupleToList(b)
				newB[x][y] = 'x'
				ret.append([newB,t])
		if upright:
			x = i + c
			y = j + c
			if ( x>=len(b) or x<0 or y >=len(b) or y<0 ):
				upright = False
			elif( b[x][y] in ['x','q','Q'] ):
				upright = False
			else:
				t = ((originI,originJ),(i,j),(x,y))
				newB = tupleToList(b)
				newB[x][y] = 'x'
				ret.append([newB,t])
		if downleft:
			x = i - c
			y = j - c
			if ( x>=len(b) or x<0 or y >=len(b) or y<0 ):
				downleft = False
			elif( b[x][y] in ['x','q','Q'] ):
				downleft = False
			else:
				t = ((originI,originJ),(i,j),(x,y))
				newB = tupleToList(b)
				newB[x][y] = 'x'
				ret.append([newB,t])
		if downright:
			x = i + c
			y = j - c
			if ( x>=len(b) or x<0 or y >=len(b) or y<0 ):
				downright = False
			elif( b[x][y] in ['x','q','Q'] ):
				downright = False
			else:
				t = ((originI,originJ),(i,j),(x,y))
				newB = tupleToList(b)
				newB[x][y] = 'x'
				ret.append([newB,t])
	return ret
	
def tupleToList(x):
	z = []
	for j in x:
		z.append(list(j))
	return z
	
def listToTuple(x):
	z = ()
	for j in x:
		z += (tuple(j),)
	return z
###################### Your code between these two comment lines ####################################
        
def main():
    if len(sys.argv) == 2:
        fname = sys.argv[1]
    else:
        fname = raw_input("setup file name?")
    game = Amazons(fname)
    game.play()

if __name__ == "__main__":
    main()
    

