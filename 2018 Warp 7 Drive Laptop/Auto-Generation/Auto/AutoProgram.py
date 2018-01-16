import pygame
import os, sys, json
from Libs.element_group import Create_Element_Group

class Renderer:
	def __init__(self,display,size):
		self.display = display
		self.screen = self.display.set_mode(size, pygame.RESIZABLE)
		self.emList = {}
		self.resizeOffset = [float(size[0]),float(size[1])]
	
	def renderFrame(self,egList=[]):
		if len(egList) == 0:
			egList = self.emList.values()
		
		
		a = pygame.Surface(self.screen.get_size())
		for em in egList:
			for eg in em.nSearch.values():
				for objlst in eg.lSearch.values():	
					for obj in objlst:
						if obj.visible:
							print(obj.cords)
							a.blit(obj.pyimage,obj.cords)
						obj.size[0] = round(obj.size[0]*self.resizeOffset[0])
						obj.size[1] = round(obj.size[1]*self.resizeOffset[1])
						obj.cords[0] = round(obj.cords[0]*self.resizeOffset[0])
						obj.cords[1] = round(obj.cords[1]*self.resizeOffset[1])
						
		self.screen.blit(pygame.transform.scale(a,a.get_size()),(0,0))
		self.update()
	
	def em_regiser(self,em):
		self.emList[em.name] = em
	
	def find_obj_at_cords(self,cords,em=None, group=None,layer=None):
		def temp(em):
			item = em.find_obj_at_cords(cords,group=None, layer=layer)
			if item is not None:
				return item
			return None
			
		if em is None:
			for em in self.emList.values():
				item = temp(em)
				if item is not None:
					return item
		else:
			if em in self.emList.keys():
				return temp(self.emList[em])
			
		return None
		
	def setSize(self,size):
		self.resizeOffset[0] = size[0]/self.resizeOffset[0]
		self.resizeOffset[1] = size[1]/self.resizeOffset[1]
		self.screen = self.display.set_mode(size, pygame.RESIZABLE)
		
	def update(self):
		self.display.flip()

class elementMannger:
	emSearch = {}
	def __init__(self,name):
		self.gSearch = {} 
		self.nSearch = {}
		self.name = name
		elementMannger.emSearch[self.name] = self
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
	
	def find_obj_at_cords(self,cords,group=None,layer=None):
		def temp(group):
			for eg in group.values():
				item = eg.find_element_at_cords(eg,cords,layer=layer)
				if item is not None:
					return item
			return None
			
		if group is None:
			for i in range(len(self.gSearch)-1,-1,-1):
				group = self.gSearch[i]
				item = temp(group)
				if item is not None:
					return item
		else:
			if group in self.gSearch.keys():
				return temp(self.gSearch[group])
			
		return None
	
	def init_group(self,name,elements):
		a = self.nSearch['name']
		a.init_elements(elements)
		self.check_min_max_size(a.minSize,a.maxSize)
		
	def check_min_max_size(self,minSize,maxSize):
		if self.maxSize[0]  < maxSize[0]:
			self.maxSize[0] = maxSize[0]
		if self.maxSize[1]  < maxSize[1]:
			self.maxSize[1] = maxSize[1]
		if self.minSize[0] >= minSize[0]:
			self.minSize[0] = minSize[0]
		if self.minSize[1] >= minSize[1]:
			self.minSize[1] = minSize[1]
		
def getConfigfile(fName):
	with open(directoryConfig+fName, 'r') as f:
		a = json.load(f)
	return a

directoryConfig = 'Config/'
LMB = 1
MMB = 2
RMB = 3
SU  = 4
SD  = 5

def main():
	def left_mouse_down():
		nonlocal e
	
	def middle_mouse_down():
		nonlocal e
		obj = myRenderer.find_obj_at_cords(e.pos,em='bobr00s',group=0)
		print(obj)
		if obj.canStateChange:
			obj.stateChangeNext()
			obj.stateUpdate()
			print(obj.state)
		
	def right_mouse_down():
		nonlocal e
	
	elements = getConfigfile('config.json')
	a = elementMannger('bobr00s')
	a.add_group('wow',0,elements=elements)
	
	global myRenderer
	myRenderer = Renderer(pygame.display,a.maxSize)
	myRenderer.em_regiser(a)

	try:
		myRenderer.renderFrame()
		while True:
			e = pygame.event.wait()
			if e.type == pygame.QUIT:
				raise StopIteration
				
			if e.type == pygame.MOUSEBUTTONDOWN:
				myRenderer.renderFrame()
				if e.button == LMB:
					left_mouse_down()
				elif e.button == MMB:
					middle_mouse_down()
				elif e.button == RMB:
					right_mouse_down()
					
				myRenderer.update()
			elif e.type == pygame.VIDEORESIZE:
				#myRenderer.setSize((e.w, e.h))
				pass
				
	except StopIteration:
		pass

	pygame.quit()

if __name__ == '__main__':
	main()