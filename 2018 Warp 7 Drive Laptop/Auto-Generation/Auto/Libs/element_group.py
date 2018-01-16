from copy import deepcopy
from pygame.locals import*
import sys
import pygame

def Create_Element_Group(visible):
	a = deepcopy(elementGroup)
	a.visible = visible
	return a

class elementGroup:
	visible = True
	maxSize = [-1,-1]
	minSize = [sys.maxsize,sys.maxsize]
	lSearch = {}
	nSearch = {}
	def __init__(self,name,data):
		try:
			data['layer'] = int(data['layer'])
		except:
			raise ValueError('Layers cant be letters!')
		
		self.__dict__.update(data)
		
		self.layer -= 1
		self.name = name
		
		self.pyimage = pygame.image.load(self.src)
		elementGroup.lSearch[self.layer] = [self]
		elementGroup.nSearch[self.name] = self
		
		a = self.size
		if elementGroup.maxSize[0]  < a[0]:
			elementGroup.maxSize[0] = a[0]
		if elementGroup.maxSize[1]  < a[1]:
			elementGroup.maxSize[1] = a[1]
		if elementGroup.minSize[0] >= a[0]:
			elementGroup.minSize[0] = a[0]
		if elementGroup.minSize[1] >= a[1]:
			elementGroup.minSize[1] = a[1]
		
		self.canStateChange = len(self.state_colour) > 1
		self.visible = elementGroup.visible
	
	def contains_point(self, point):
		return (self.cords[0] <= point[0] <= self.cords[0] + self.size[0] and
				self.cords[1] <= point[1] <= self.cords[1] + self.size[1])
	
	def find_element_at_cords(self, point, layer=None):
		def temp(layer):
			for el in layer:
				if el.contains_point(point):
					return el
			return None
		
		if layer is None:
			for i in range(len(self.lSearch)-1,-1,-1):
				la = self.lSearch[i]
				item = temp(la)
				if item is not None:
					return item
		else:
			if layer in self.lSearch.keys():
				return temp(self.lSearch[layer])
			
		return None
	
	def stateUpdate(self):
		pass	
	
	def stateChangeNext(self):
		self.state += 1
		if len(self.state_colour) <= self.state:
			self.state = 0
			
	def stateChangeTo(self,val):
		self.state = val
		a = len(self.state_colour)
		if a <= self.state:
			self.state = 0
		elif self.state < 0:
			self.state = a-1
	
	def toogleVisible():
		elementGroup.visible = not elementGroup.visible
		for obj in elementGroup.nSearch.values():
			obj.visible = elementGroup.visible
	
	def setVisible(visible):
		elementGroup.visible = visible
		for obj in elementGroup.nSearch.values():
			obj.visible = elementGroup.visible
	
	def init_elements(elements):
		for key, item in elements.items():
			elementGroup(key,item)
	