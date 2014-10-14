package com.example.words.util;

import android.R;
import android.annotation.SuppressLint;

import java.io.FileInputStream;
import java.io.InputStream;
/*
 * Dictionary manages our word database
 */
public class WordDictionary {
	private WordArrayNode root;
	private static final char firstLetter = 'a';
	private static final int minWordLength = 2;
	private static final int arrayLowerBound = 0;
	private static final int arrayUpperBound = 25;
	
	public WordDictionary(){
		root = new WordArrayNode();
	}
	
	/*
	 * Add new word to the dictionary
	 */
	public void Add(String newWord){
		Find(newWord, true);
	}
	
	/*
	 * Returns true if words is contained in the dictionary
	 */
	public boolean Contains(String word){
		return Find(word, false);
	}
	
	@SuppressLint("DefaultLocale") 
	private boolean Find(String word, boolean shouldAdd){
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
		return c - firstLetter;
	}
	
	@SuppressWarnings("unused") /*remove later*/
	private char ItoA(int i){
		return (char) (i + firstLetter);
	}
	
}

class WordArrayNode{
	public WordNode[] nodes;
	
	public WordArrayNode(){
		nodes = new WordNode[26];
	}
}

class WordNode{
	public boolean isWord;
	public WordArrayNode children;
	
	public WordNode(){
		this.children = new WordArrayNode();
	}
}
