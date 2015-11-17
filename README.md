===============================
DOCUMENTATION
===============================

This document will cover instructions on how to construct a corpus based on internet files.

-----------------------------

1) First we have to define URL seeds for the crawler:
	
	1.1) Description: This step consists of, given a list of medium frequency words, generate N random pairs with these words, and then perform a webSearch using Bing search API. The return is a file with the first M links for each pair.

	1.2) Input (in order):
		*file with list of words. Format: each line of the file contains a number, then \t, then the word itself.
		*Stopwords file (optional)
		*Number of pairs to generate (N - optional). Default = 10.
		*Number of links to get from each pair (M - optional). Default = 10.
		*Language / Country code to search (optional). Default = pt-BR.

	1.3) Output:
		*Text file containning N*M links returned from Bing Search API.

	1.4) Example of execution:
	
		python makeLinks.py files/formas.totalbr.txt RrYB+QTc/TJUih/6o6rTDLLcRL74wBwN1A4FhI/xqKA files/linksCompleted.txt 10 files/stopwords.txt

2) Then we give these seeds to the crawler:
	2.1) Description: This step consists of, given a list of URLs, perform a web craw in .br pages, getting human produced content from each link visited. Acknowledges to BoilerPipe library, which has helped to remove non human produced texts. To know how this step was performed in details (removal tactics, search, etc) read the brWac article (link to article).

	2.2) Input (in order):
		*Seeds file. Format: each line containing one URL.
		*Out Directory: path to directory where texts will be stored.
		*Number of threads. In our experiments, it was set to 100.

	2.3) Output:
		*Association File: file containing in each file, nome of the generate text file, then \t, then the related URL.
		*Visited Links: a list containing all the links already crawled, in case the crawled is stopped.

	2.4) Example of execution:

		java -jar dist/threadedCrawler.jar seedsFile.txt out/ associationFile.txt visitedLinks.txt numberOfThreads

3) The files obtained pass through a duplicates identification stage (fixes TO DO):

	3.1) Description: Given the directory containing all the files obtained from step 2, identifies the files that might have duplicated content, then moving the probably "unique" files to an assigned directory. The methodology consists of comparing N-Grams between each text, being M N-Grams. Comparing two texts, if from these M N-Grams, at least T are equal, then the script assumes the 2 files are equal.

	3.2) Input (in order):
		*N (integer) -> number of words in each N-Gram (default = 5).
		*M (integer) -> number of N-Grams got for each text (default = 25).
		*T (integer) -> threshold to check if two texts are probably equal (default = 2).
		*Stopwords File.
		*Permanent Files Directory. Path to where the "unique" files will be stored.

	3.3 Output:
		The probably unique files are moved to the permanent files directory.

	3.4) Example of execution:

		python ngramsSharedAlpha.py 5 25 2 stopwordsPT.txt permanentFiles/