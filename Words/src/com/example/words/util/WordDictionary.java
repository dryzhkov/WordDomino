package com.example.words.util;

import android.annotation.SuppressLint;
import java.util.Random;
/*
 * Dictionary manages our word database
 */
public class WordDictionary {
	private WordArrayNode root;
	private static final char firstLetter = 'a';
	private static final int minWordLength = 2;
	private static final int arrayLowerBound = 0;
	private static final int arrayUpperBound = 26; //26 letters + space char
	
	public WordDictionary(){
		root = new WordArrayNode();
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
	public boolean Contains(String word, boolean markAsPlayed){
		return Find(word, false, true);
	}
	
	/*
	 * Returns a randomly selected word, if not found in 10 seconds returns empty string
	 */
	public String GetRandomWord(){
		StringBuilder rsl = new StringBuilder();
		Random r = new Random();
		boolean foundWord = false;
		WordArrayNode cur = root;
		WordNode curNode = null;
		long maxRunInMiliseconds = 10000;
		long startTime = System.currentTimeMillis();
		long currentTime = startTime;
		
		while(maxRunInMiliseconds > currentTime - startTime){
			//pick a random node
			int randomIndex = r.nextInt(arrayUpperBound + 1);
			curNode = cur.nodes[randomIndex];

			if(curNode != null){
				rsl.append(ItoA(randomIndex)); //add current letter to an existing collection
				
				if(curNode.isWord){ //are we done?
					foundWord = true;
					System.out.println("Found a word hurray!!!");
					break;
				}else{
					//move current one level down
					cur = curNode.children;
				}
			}
			//update current time
			currentTime = System.currentTimeMillis();
		}
		
		return (foundWord ? rsl.toString() : "");
	}
	
	@SuppressLint("DefaultLocale") 
	private boolean Find(String word, boolean shouldAdd, boolean markAsPlayed){
		Validate(word); //validate input
		word = word.toLowerCase(); //treat all letters as lower case
		WordArrayNode cur = root; //pointer to the first array of nodes
		
		for(int i = 0; i < word.length(); i++){
			char curLetter = word.charAt(i);
			int asciiIndex = AtoI(curLetter);
			if(asciiIndex < arrayLowerBound || asciiIndex > arrayUpperBound){
				throw new IllegalArgumentException("WordDictionary::Invalid letter: '" + word + "' in argument word: '" + curLetter + "' ascii index: '" + asciiIndex + "'");
			}
			WordNode[] curNodesArray = cur.nodes;
			WordNode curNode = curNodesArray[asciiIndex];
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
					if(curNode.isWord && markAsPlayed){
						curNode.isPlayed = true;
					}
					return curNode.isWord;
				}
			}else{
				cur = curNode.children; //move to the next child
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

class WordArrayNode{
	public WordNode[] nodes;
	
	public WordArrayNode(){
		nodes = new WordNode[27]; //keep in sync with arrayUpperBound + 1 in WordDictionary
	}
}

class WordNode{
	public boolean isWord; //indicates last char of a valid word
	public boolean isPlayed; //indicates that a word has been played
	public WordArrayNode children;
	
	public WordNode(){
		this.children = new WordArrayNode();
		this.isWord = false;
		this.isPlayed = false;
	}
}
