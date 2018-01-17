import numpy as np

class Path:
	def __init__(self,colour):
		self.colour = colour
		self.points = [Points((0,0)),Points((0,0))]
		self.calculated = None
		
	def renderFrame(self):
		for z in range(len(points)):
			for point in points:
				x = point[0](z)
				y = point[1](z)
				
	
	def recalculatePath(self,points):
		for point in points:
			x1.append(point.x)
			y1.append(point.y)
		
		x = np.array(x)
		y = np.array(y)
		num = len(points)
		n = range(0,num)
		num *= 5
		x = np.poly1d(np.polyfit(n, x, num))
		y = np.poly1d(np.polyfit(n, y, num))
			
		return np.column_stack(x,y)
	
	def addPoint(self,point):
		self.points.append(Points(point))
		
class Points:
	def __init__(self,val):
		self.x = val[0]
		self.y = val[1]
		