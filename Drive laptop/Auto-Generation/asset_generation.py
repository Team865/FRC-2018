import subprocess
import re
import os
from shutil import move
from PIL import Image, ImageChops
from colorthief import ColorThief
from copy import deepcopy
import collections
import json

def get_image_sum(imgpath,color_count=10, quality=10):
	color_thief = ColorThief(imgpath[0])
	palette = color_thief.get_palette(color_count, quality)
	return(palette)

def trim(im):
    bg = Image.new(im.mode, im.size, im.getpixel((0,0)))
    diff = ImageChops.difference(im, bg)
    diff = ImageChops.add(diff, diff, 2.0, -100)
    bbox = diff.getbbox()
    if bbox:
        return im.crop(bbox)
	
def crop_smallest(im,imp):
	im = trim(im)
	im.save(imp)
	
def find_object(imp):
	im = Image.open(imp)
	pix = im.load()
	picx , picy = im.size
	avgxy=[0,0]
	count=0
	for x in range(0, picx,2):
		for y in range(0, picy,2):
			val = pix[x,y]
			val += (0,)*(4-len(val))
			if val[3] > 10:
				avgxy[0]+=x
				avgxy[1]+=y
				count+=1
	
	if count == 0:
		return (-1,-1)
	
	
	crop_smallest(im,imp)
	return [avgxy[0]//count,avgxy[1]//count]
	
pdnfile = 'pdn/FieldGameLayers1px-1cm.pdn'

BUILD = 'BUILD/'
directoryGame = BUILD+'/Auto'+'/'
directoryElements = directoryGame+'/Elements/'
directoryConfig = directoryGame+'/Config/'

if not os.path.exists(directoryConfig):
    os.makedirs(directoryConfig)

if not os.path.exists(directoryElements):
    os.makedirs(directoryElements)

print('Extracting map...')
subprocess.call(['pdn2png.exe','/split', '/outfile=.\\'+directoryElements+'\\.png', pdnfile])

with open('objects.json', 'r') as f:#yes i know of json.loads() 
    types = eval(f.read())#json loads errors with->json.decoder.JSONDecodeError: Invalid \escape: line 2 column 14 (char 15)
	#errors because of RE in json
	
elementConfig=collections.OrderedDict()
for key in types.keys():
	elementConfig[re.compile(key)] = types[key]
types=None
elementConfig[re.compile('[^/]+$')] = {}
print(elementConfig)

objects ={}
for x in os.listdir(directoryElements):
	if  x.startswith('-L') and x.endswith(".png"):
		layer = int(re.split('-L|Normal',x)[1])
		x = '{}/{}'.format(directoryElements,x)
		b = re.sub('(-)(L)(\d+)(Normal)(\d+)(V)','',x)
		move(x,b)
		for element in elementConfig.keys():
			d = element.findall(b)
			c=len(d)-1
			if c>=0:
				value = deepcopy(elementConfig[element])#FeelsDeepMan
				print(b)
				value['cords'] = find_object(b)
				
				if 'moveable' not in value:
					value['moveable'] = False
				
				if 'state' not in value:
					value['state'] = 0
					
				p = get_image_sum((b,),color_count=2)[0]
				if 'state_colour' in value:
					value['state_colour'][:] = [p] + value['state_colour']
				else:
					value['state_colour'] = [p]
				
				value['src'] = '//'.join(b.split('//')[2:])
				value['layer'] = layer
				objects[''.join(d[c])] = value
				break
		
print(objects)
with open(directoryConfig+'/config.json', 'w') as f:
	json.dump(objects,f)
