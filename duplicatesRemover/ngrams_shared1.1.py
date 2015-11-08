import codecs, re, random, os, shutil, itertools, sys
from sets import Set

class NGRAMS:
	def __init__(self, numberOfNGrams, sizeNGrams, dirPath, stopwordsFile = None):
		self.stopwordsList = []
		self.dirPath = dirPath
		self.sizeNGrams = sizeNGrams
		self.numberOfNGrams = numberOfNGrams
		if stopwordsFile != None:
			stopsOpener = codecs.open(stopwordsFile,'r','utf-8')
			stopsString = stopsOpener.read()
			for word in stopsString.splitlines():
				self.stopwordsList.append(word)

	def getNGramsFromDir(self, verbose = False):
		ret = []
		directory = os.listdir(self.dirPath)
		for f in directory:
			ret.append([f,self.getNGrams(self.dirPath+f)])
			if verbose:
				print(f+" opened!")
		return ret

	def removeStopwords(self,listOfWords): #remove stopwords from files
		return [x for x in listOfWords if x not in self.stopwordsList]

	def stringToListOfWords(self,text):
		return text.split()

	def getNGrams(self,f):
		fOpen = codecs.open(f,'r',encoding='utf-8')
		fileText = fOpen.read()
		fOpen.close()
		wordsList=self.stringToListOfWords(fileText)
		wordsList=self.removeStopwords(wordsList)
		excludedNumbers = Set()
		ngrams = Set()
		ngrams.clear()

		for curNGram in range(0,self.numberOfNGrams):
			r = None
			if len(excludedNumbers) < len(wordsList)-(2*(self.sizeNGrams+1)):
				while r in excludedNumbers or r is None:
				     r = random.randrange(self.sizeNGrams+1,len(wordsList)-self.sizeNGrams-1)
				for i in range(-self.sizeNGrams, self.sizeNGrams):
					excludedNumbers.add(r+i)
					strNGram = ""
				for i in range(0,self.sizeNGrams):
					strNGram+=wordsList[r+i]+" "
				ngrams.add(strNGram)
		return ngrams

	def compareAndRemove(self,wordsList,permanentPath, threshold, verbose = False):
		toMove = []
		for l1, l2 in itertools.combinations(wordsList, 2):
			if len(l1[1] & l2[1]) > threshold:
				if verbose:
					print(l1[0]+" is probably equal to "+l2[0])
				toMove.append(l2[0])
		for elem in toMove:
			moveFile(elem,permanentPath,verbose)

def moveFile(name,path,verbose = False):
	try:
		shutil.move("out/"+name, path+name)
		if verbose:
			print("File "+completeName+" removed!")		
	except IOError:
		if verbose:
			print("File "+completeName+" already removed!")
   		pass

def printListToFile(List,outputFile):
	outputOpener = codecs.open(outputFile,'w','utf-8')
	outputOpener.writelines(["%s\n" % item  for item in List])

def main(argv):
	verbose = True
	NGrams = NGRAMS(25,5,"out/","stopwords.txt")
	completeList = NGrams.getNGramsFromDir(verbose)
	#printListToFile(completeList,"listOfNGrams.txt")
	NGrams.compareAndRemove(completeList,"permanentFiles",2,verbose)

if __name__ == "__main__":
   main(sys.argv[1:])