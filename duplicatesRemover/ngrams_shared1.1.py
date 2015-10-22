import codecs
import re
from sets import Set
import random
import os
import shutil
NUM_NGRAMS = 25
lista_conjuntos=[]
path="brwac0/" #files directory (corpus)
duplicated_path="duplicated" #where removed files are saved


def retira_stopwords(texto): #remove stopwords from files
	arq_stops = codecs.open("stopwords.txt",'r',encoding='utf-8')
	S=arq_stops.read()
	REMOVE_LIST = []
	for linha in S.splitlines():
		REMOVE_LIST.append(linha)
	for palavra_remove in REMOVE_LIST:
		texto = filter(lambda substitui:substitui != palavra_remove, texto)
 	return texto	

def read_words(texto):
	return re.findall(r'\w+', texto)

def abre_arquivo(filename): #open file and get n-grams
	filename=path+filename
	arq_open = codecs.open(filename,'r',encoding='utf-8')
	texto_completo=arq_open.read()
	arq_open.close()
	lista=read_words(texto_completo)
	lista=retira_stopwords(lista)
	conjunto = Set()
	for numero in range(6,len(lista)-6):
		conjunto.add(numero)
	ngrams = Set()
	ngrams.clear()
	for numero in range(1,NUM_NGRAMS):
		if len(conjunto) > 5:
			escolha_list=random.sample(conjunto,1)
			escolha=escolha_list[0]
			for n in range(-5, 5):
				conjunto.discard(escolha+n)
			string_palavras=""
			for indice in range(0,5):
				string_palavras+=lista[escolha+indice]+" "
				ngrams.add(string_palavras)
	lista_conjuntos.append(ngrams)	

def remove_arq(filename):
	filename=path+filename
	try:
		shutil.copy2(filename, duplicated_path)
		os.remove(filename)
		print("File "+filename+" removed!")		
	except IOError:
		print("File "+filename+" already removed!")
   		pass

#main
arqs=[]
dirs=os.listdir(path)
for files in dirs:
	abre_arquivo(files)
	arqs.append(files)
	print(files+" opened!")

for conj in range(0,len(lista_conjuntos)):
	for conj_compara in range(conj,len(lista_conjuntos)):
		if conj != conj_compara:
			if len(lista_conjuntos[conj_compara] & lista_conjuntos[conj]) > 4:
				print(arqs[conj_compara]+" is probably equal to "+arqs[conj])
				remove_arq(arqs[conj_compara])
				
