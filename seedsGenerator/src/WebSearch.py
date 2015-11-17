import urllib
import urllib2
import json
import codecs
import sys

class WebSearch:
	def __init__(self, key, resultsNumber = 10, searchType = "Web", language = "pt-BR"):
		
		self.key = key
		self.resultsNumber = resultsNumber
		self.searchType = searchType
		self.language = language

	def perform(self, keywordsList):
		links = []
		for query in keywordsList:
			links.extend(self.makeQuery(query))
		return links


	def makeQuery(self, query):
		retList = []
		key = self.key
		query = urllib.quote(query.encode("utf-8"))
		user_agent = 'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; FDM; .NET CLR 2.0.50727; InfoPath.2; .NET CLR 1.1.4322)'
		credentials = (':%s' % key).encode('base64')[:-1]
		auth = 'Basic %s' % credentials
		url = 'https://api.datamarket.azure.com/Data.ashx/Bing/Search/'+self.searchType+'?Query=%27'+query+'%27&$top='+str(self.resultsNumber)+'&$format=json&Market=%27'+self.language+'%27'
		request = urllib2.Request(url)
		request.add_header('Authorization', auth)
		request.add_header('User-Agent', user_agent)
		request_opener = urllib2.build_opener()
		response = request_opener.open(request)
		response_data = response.read()
		json_result = json.loads(response_data)
		resultList = json_result['d']['results']

		for link in resultList:
			ext = link['Url'][-4:]
			if not(ext == ".pdf" or ext == ".doc" or ext == ".xml" or ext == ".rar" or ext == ".zip" or ext == ".mp3" or ext == ".png" or ext == ".jpg"):
				retList.append(link['Url'])
		return retList


