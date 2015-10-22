===============================
DOCUMENTATION
===============================

This document will cover instructions on how to construct a corpus based on internet files.

-----------------------------

1) First we have to define URL seeds for the crawler:

Example:

python makeLinks.py files/formas.totalbr.txt RrYB+QTc/TJUih/6o6rTDLLcRL74wBwN1A4FhI/xqKA files/linksCompleted.txt 10 files/stopwords.txt

2) Then we give the seeds to the crawler:

Example (the visitedLinks.txt argument is optional):
java -jar dist/craw_simple.jar linksCompleted.txt visitedLinks.txt

3) The files obtained pass through a duplicates identification stage:

