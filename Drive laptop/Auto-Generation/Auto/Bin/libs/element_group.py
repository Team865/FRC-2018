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
	def __init__(self,name,data,visible=True):
		try:
			data['layer'] = int(data['layer'])
		except:
			raise ValueError('Layers cant be letters!')
		
		self.__dict__.update(data)
		
		elementGroup.visible = visible
		self.name = name
		
		self.pyimage = pygame.image.load(self.src)
		elementGroup.lSearch[self.layer] = [self]
		elementGroup.nSearch[name] = self
		
		a = self.pyimage.get_rect().size
		if elementGroup.maxSize[0] < a[0]:
			elementGroup.maxSize[0] = a[0]
		if elementGroup.maxSize[1] < a[1]:
			elementGroup.maxSize[1] = a[1]
		if elementGroup.minSize[0] >= a[0]:
			elementGroup.minSize[0] = a[0]
		if elementGroup.minSize[1] >= a[1]:
			elementGroup.minSize[1] = a[1]
		
		self.cords[0] -= a[0]//2
		self.cords[1] -= a[1]//2
		
		self.canStateChange = len(self.state_colour) >= 1
		self.visible = visible
			
	def state_change(self):
		self.state += 1
		if len(self.state_colour) >= self.state:
			self.state = 0
		
	def init_elements(elements):
		for key, item in elements.items():
			elementGroup(key,item)
