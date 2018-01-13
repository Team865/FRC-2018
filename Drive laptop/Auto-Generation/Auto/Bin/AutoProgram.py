import pygame, random
from pygame.locals import*
import os, sys, json
from libs.element_group import Create_Element_Group

directoryConfig = 'Config/'

class Renderer:
	def __init__(self,display,size):
		self.display = display
		self.screen = self.display.set_mode(size)
	
	def renderFrame(self,elmList):
		self.screen.fill((0,0,0))
		for eg in elmList:
			for layer,objlst in eg.lSearch.items():	
				for obj in objlst:
					if obj.visible:
						self.screen.blit(obj.pyimage,obj.cords)
		
		self.update()
	
	def setSize(self,size):
		self.screen = self.display.set_mode(size)
	
	def update(self):
		self.display.flip()

class elementMannger:
	emSearch = {}
	def __init__(self,name):
		self.gSearch = {} 
		self.nSearch = {}
		elementMannger.emSearch[name] = self
		self.maxSize = [-1,-1]
		self.minSize = [sys.maxsize,sys.maxsize]
		
	def add_group(self,name,group,visible=True,elements=None):
		try:
			group = int(group)
		except:
			raise ValueError('Groups cant be letters!')
		a = Create_Element_Group(visible)
		self.gSearch[group] = {name:a}
		self.nSearch[name] = a
		if elements != None:
			a.init_elements(elements)
			self.check_min_max_size(a.minSize,a.maxSize)
		
	def init_group(self,name,elements):
		a = self.nSearch['name']
		a.init_elements(elements)
		self.check_min_max_size(a.minSize,a.maxSize)
		
	def check_min_max_size(self,minSize,maxSize):
		if self.maxSize[0] < maxSize[0]:
			self.maxSize[0] = maxSize[0]
		if self.maxSize[1] < maxSize[1]:
			self.maxSize[1] = maxSize[1]
		if self.minSize[0] >= minSize[0]:
			self.minSize[0] = minSize[0]
		if self.minSize[1] >= minSize[1]:
			self.minSize[1] = minSize[1]
		
def getConfigfile(fName):
	with open(directoryConfig+fName, 'r') as f:
		a = json.load(f)
	return a
	
elements = getConfigfile('config.json')

a = elementMannger('bobr00s')
b = elementMannger.emSearch['bobr00s']
b.add_group('wow',0,elements=elements,visible=False)

myRenderer = Renderer(pygame.display,b.maxSize)
draw_on=False
radius=200
try:
	while True:
		e = pygame.event.wait()
		myRenderer.renderFrame(b.nSearch.values())
		if e.type == pygame.QUIT:
			raise StopIteration
		if e.type == pygame.MOUSEBUTTONDOWN:
			color = (random.randrange(256), random.randrange(256), random.randrange(256))
			pygame.draw.circle(myRenderer.screen, color, e.pos, radius)
			draw_on = True
		if e.type == pygame.MOUSEBUTTONUP:
			draw_on = False
		if e.type == pygame.MOUSEMOTION:
			if draw_on:
				pygame.draw.circle(myRenderer.screen, color, e.pos, radius)

except StopIteration:
    pass

pygame.quit()