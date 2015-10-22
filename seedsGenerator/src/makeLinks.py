import codecs,sys,random
from WebSearch import *

class WordList:
	def __init__(self, stopwordsFile = None):
		self.stopwordsList = []
		self.words = []
		if stopwordsFile != None:
			stopsOpener = codecs.open(stopwordsFile,'r','utf-8')
			stopsString = stopsOpener.read()
			for word in stopsString.splitlines():
				self.stopwordsList.append(word)
		
	def getWordsFromFile(self, inputFile):
		inputOpener = codecs.open(inputFile,'r',encoding='utf-8')
		inputText = inputOpener.read()
		for line in inputText.splitlines():
			word = self.getWordFromLine(line) # this function will depend on the file format
			verifiedWord = self.verifyWord(word)
			if verifiedWord != None:
				self.words.append(verifiedWord)
		return self.words

	def getWordFromLine(self, line):
		isFirstWord = True
		for word in line.split('\t'): # first word of a line is the number of occurrences, and the second is the word itself
			if isFirstWord:
				isFirstWord = False
			else:
				return word

	def verifyWord(self, word): # the function will return the string that should be printed for the word passed as argument
		if (not word.isupper()) and (not word.isnumeric()) and (len(word) > 3):
			chars = set('/|\<>:;.)(_+!?$,\'')
			if any((c in chars) for c in word):
				return None
			elif not self.isStopword(word):
				return word.lower()
		else:
			return None

	def isStopword(self,word):
		if word in self.stopwordsList:
			return True
		else:
			return False

def makePairs(List):
	pairList = []
	for i in range(0,len(List),2):
		pairList.append(List[i]+" "+List[i+1])
	return pairList

def printListToFile(List,outputFile):
	outputOpener = codecs.open(outputFile,'w','utf-8')
	outputOpener.writelines(["%s\n" % item  for item in List])

# argv -> list of arguments containing the initial words File, your key to the BING Search API, the outputFile that will contain
# the URLS generated, the number of pairs wanted to generate and (optional) the path to the stopwords file
# 
def main(argv):
	if len(argv) < 4:
		print "The program will not execute with these parameters passed. Make sure this order is followed: path_of_words_file, key_bing_searchApi, path_of_output_file, number_of_pairs_to_generate, stopwords_file_path (optional)"
	else:
		pairsNumber = int(argv[3])
		# First Step: generate list with non stopwords, more than 3 characters, and no special characters
		if len(argv) == 4:
			firstWordList = WordList()
		elif len(argv) == 5:
			firstWordList = WordList(argv[4])
		firstWordList.getWordsFromFile(argv[0])
		randomWords = random.sample(firstWordList.words,pairsNumber*2)
		randomPairs = makePairs(randomWords)
		webSearchInstance = WebSearch(argv[1])
		urls = webSearchInstance.perform(randomPairs)
		printListToFile(urls,argv[2])

if __name__ == "__main__":
   main(sys.argv[1:])