package com.example.words.util;

import android.annotation.SuppressLint;
import java.util.ArrayList;
import java.util.Random;
/*
 * Dictionary manages our word database
 */
public class WordDictionary {
	private WordNode root;
	private static final char firstLetter = 'a';
	private static final int minWordLength = 2;
	private static final int arrayLowerBound = 0;
	private static final int arrayUpperBound = 26; //26 letters + space char
	
	public WordDictionary(){
		root = new WordNode();
	}
	
	/*
	 * Add new word to the dictionary
	 */
	public void Add(String newWord){
		Find(newWord, true, false);
	}
	
	/*
	 * Returns true if words is contained in the dictionary
	 */
	public boolean Contains(String word){
		return Find(word, false, false);
	}
	
	/*
	 * Returns true if words is contained in the dictionary and marks it as used
	 */
	public boolean Get(String word){
		return Find(word, false, true);
	}
	
	/*
	 * Returns a randomly selected word
	 */
	public String GetRandomWord(){
		Random r = new Random();
		int randomIndex = r.nextInt(arrayUpperBound + 1);
		
		//There is a possibility of returning empty string if randomIndex points at the letter that is not in the root level.
		return FindAnswer(ItoA(randomIndex));
	}
	
	/*
	 * Returns an answer to a given letter, mark the words as used
	 */
	public String FindAnswer(char startChar){
		String answer = "";
		ArrayList<String> words = GetWordsStartingWith(startChar, true);
		if(words != null && words.size() > 0){
			//array words now contains all words that start with a given letter and have not been played yet.
			//this is where we want to put the 'smart' logic for picking a next word that AI will say
			//for now, just picking it randomly
			Random r = new Random();
			int randomIndex = r.nextInt(words.size());
			answer = words.get(randomIndex);
			
			//mark answer as used
			Get(answer);
		}
		return answer;
	}
	
	/*
	 * Returns a list of all words that start with a given letter
	 */
	public ArrayList<String> GetWordsStartingWith(char start, boolean excludeUsedWords){
		int asciiIndex = AtoI(start);
		if(asciiIndex < arrayLowerBound || asciiIndex > arrayUpperBound){
			throw new IllegalArgumentException("WordDictionary::GetWordsStartingWith::Invalid letter: '" + start + "' ascii index: '" + asciiIndex + "'");
		}
		ArrayList<String> results = new ArrayList<String>();
		StringBuilder curWord = new StringBuilder();
		curWord.append(start);
		WordNode parentNode = root.children[asciiIndex];
		if(parentNode != null){
			FindAllWords(parentNode, curWord, results, excludeUsedWords);
		}
		return results;
	}
	
	private void FindAllWords(WordNode parentNode, StringBuilder curWord, ArrayList<String> results, boolean excludeUsedWords){
		WordNode curNode;
		for(int i = arrayLowerBound; i <= arrayUpperBound; i++){
			curNode = parentNode.children[i];
			if(curNode != null){
				curWord.append(ItoA(i)); //add current letter to word
				if(curNode.isWord && (!excludeUsedWords || !curNode.beenUsed)){
					results.add(curWord.toString());
				}
				//recursively go down the list
				FindAllWords(curNode, curWord, results, excludeUsedWords);
				//remove last letter
				curWord.deleteCharAt(curWord.length() - 1); 
			}
		}
	}
	
	@SuppressLint("DefaultLocale") 
	private boolean Find(String word, boolean shouldAdd, boolean markAsUsed){
		Validate(word); //validate input
		word = word.toLowerCase(); //treat all letters as lower case
		WordNode curNode = root; //pointer to the first array of nodes
		
		for(int i = 0; i < word.length(); i++){
			char curLetter = word.charAt(i);
			int asciiIndex = AtoI(curLetter);
			
			if(asciiIndex < arrayLowerBound || asciiIndex > arrayUpperBound){
				throw new IllegalArgumentException("WordDictionary::Invalid letter: '" + word + "' in argument word: '" + curLetter + "' ascii index: '" + asciiIndex + "'");
			}
			WordNode[] curNodesArray = curNode.children; //move to the next child
			curNode = curNodesArray[asciiIndex];
			if(curNode == null){
				if(shouldAdd){
					curNode = new WordNode();
					curNodesArray[asciiIndex] = curNode;
				}else{
					return false;
				}
			}
			
			if(i == word.length() - 1){ //last letter in the word
				if(shouldAdd){
					curNode.isWord = true;
				}else{
					if(curNode.isWord && markAsUsed){
						curNode.beenUsed = true;
					}
					return curNode.isWord;
				}
			}
		}
		return true;
	}
	
	private void Validate(String input){
		if(input == null || input.length() < minWordLength){
			throw new IllegalArgumentException("Invalid input: [" + input +"]");
		}
	}
	
	private int AtoI(char c){
		if(c == ' '){
			return arrayUpperBound;
		}
		return c - firstLetter;
	}
	
	private char ItoA(int i){
		if(i == arrayUpperBound){
			return ' ';
		}
		return (char) (i + firstLetter);
	}
}

class WordNode{
	public boolean isWord; //indicates last char of a valid word
	public boolean beenUsed; //indicates that a word has been played
	public WordNode[] children;
	
	public WordNode(){
		this.children = new WordNode[27];
		this.isWord = false;
		this.beenUsed = false;
	}
}