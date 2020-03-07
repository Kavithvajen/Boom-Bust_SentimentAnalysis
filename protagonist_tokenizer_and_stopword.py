# -*- coding: utf-8 -*-
"""Protagonist_Tokenizer_And_StopWord.ipynb

Automatically generated by Colaboratory.

Original file is located at
    https://colab.research.google.com/drive/1l3C_JnHx63l9q9eHMAdsd7DdRg1pP7cW
"""

from nltk import word_tokenize
from textblob import TextBlob

import nltk
nltk.download('punkt')
nltk.download('averaged_perceptron_tagger')
nltk.download('brown')

def readTxt():
  text=open('/content/drive/My Drive/The secret of old  clock.txt')
  text= text.read()
  return text

def tokenizeTxt(text):
  token = word_tokenize(text)
  return token

def remove_stopwords(token):
  stop_words=["a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "could", "did", "do", "does", "doing", "down", "during", "each", "few", "for", "from", "further", "had", "has", "have", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "it", "it's", "its", "itself", "let's", "me", "more", "most", "my", "myself", "nor", "of","oh " ,"oh", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "she", "she'd", "she'll", "she's", "should", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "we", "we'd", "we'll", "we're", "we've", "were", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", "with", "would", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves"]
  for i in token:
    for s in stop_words:
      if(i == s):
        token.remove(s)
    return token

def tokenTostring(token):
  token_toString=""
  for i in token:
    token_toString+=i+" "
  return token_toString

def remove_puntuations(token_toString):
  clearingSymbols = '!"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~“’”'
  for c in clearingSymbols:
    token_toString = token_toString.replace(c,'')
  return token_toString

def blob(token_toString):
  blob=TextBlob(token_toString)
  return blob

def top10words(blob):
  d= blob.np_counts
  d=dict(d)
  top10= sorted(d,key=d.get,reverse=True)
  return top10[:10]

def word_tagging(blob):
  tagged_dict=blob.tags
  return tagged_dict

def possible_protagonist(top10,tagged_dict):
  pp=[]
  proper_noun=['NNP']
  verbs= ['VB','VBD','VBG','VBN','VBP','VBZ']
  for word in top10:
    count = 0
    for i in range(0,len(tagged_dict)):
      if(tagged_dict[i][0] == word or tagged_dict[i][0] == word.title()):  
        if(tagged_dict[i][1] in proper_noun):
          if(word not in pp):
            pp.append(word)
  return pp[:1]

a= readTxt()
b= tokenizeTxt(a)
c= remove_stopwords(b)
d= tokenTostring(c)
e= remove_puntuations(d)
f= blob(e)
g= top10words(f)
h= word_tagging(f)
i=possible_protagonist(g,h)

i