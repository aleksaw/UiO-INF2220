import java.net.*;
import java.util.*;
import java.io.*;
class Oblig1 {
    public static void main(String[] args){
	Dictionary d = new Dictionary("http://www.uio.no/studier/emner/matnat/ifi/INF2220/h17/obligatoriske-innleveringer/oblig-1/dictionary.txt");
	System.out.println("Welcome to our dictionary!");
	System.out.println("Type in a word to find it in the dictionary, or 'q' to quit");
	if(args.length == 0){
	    // No arguments are passed, ask for input
	    try{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String in = br.readLine().trim();
		// As long as input isn't 'q' we keep asking
		while(!in.equals("q")){
		    String[] input = in.split(" ");
		    if(input[0].equals("d")){
			for(int i = 1; i < input.length; i++){
			    d.delete(input[i]);
			}
		    }
		    else{
			for(int i = 0; i < input.length; i++){
			    processInput(d, input[i]);
			}
		    }
		    // Input next line
		    System.out.println("Would you like to search for another word?\n");
		    in = br.readLine().trim();
		}
		br.close();
	    } catch (IOException e) {
		System.out.println("IOError: "+e);
		System.exit(0);
	    }
	}
	else{
	    // We have arguments passed, lets search them
	    for(int i = 0; i < args.length; i++){
		// Print the word to be searched
		System.out.println(args[i]);
		processInput(d, args[i]);
		System.out.println("Would you like to search for another word?\n");
	    }
	    System.out.println("q");
	}
	d.printStatistics();
    }
    private static void processInput(Dictionary d, String input){
	// Search for word
	if(d.search(input)){
	    // We found it!
	    System.out.println("We found your word "+input.toUpperCase());
	}
	else{
	    // We didn't find it, now we have to search for similar words
	    System.out.println("Sorry, we didn't find your word "+input.toUpperCase());
	    // Lets see if we can find similar matches
	    String similar = d.findSwappedChars(input);
	    similar += d.findWrongChar(input);
	    similar += d.findMissingChar(input);
	    similar += d.findExtraChar(input);
	    // If we found any, we print them
	    if(similar.length() > 0){
		// Split the string into words
		String[] words = similar.split(" ");
		similar = "";
		// Loop through the words to look for duplicates
		for(int i = 0; i < words.length; i++){
		    // Check that we haven't already removed it
		    if(!words[i].equals("")){
			// Loop through the rest of the words to see if any are equal
			for(int j = i+1; j < words.length; j++){
			    //If they are equal, remove them
			    if(words[i].equals(words[j])){
				words[j] = "";
			    }
			}
			// Reappend the words to the string
			similar += words[i] + " ";
		    }
		}
		System.out.println("We did, however, find the following words that resemble "+input.toUpperCase());
		System.out.println(similar);
	    }
	}
    }
}
class Dictionary{
    Word root;

    Dictionary(String infile){
	// Constructor, load infile, create root, and then insert words into root.
	// Try to open file
	try {
	    URL url = new URL(infile);
	    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
	    // Create root
	    root = new Word(br.readLine().toLowerCase().trim());
	    // Go through the rest of the lines in file
	    for(String line; (line = br.readLine()) != null; ) {
		// Insert word
		this.insert(line.toLowerCase().trim());
	    }
	    br.close();
	} catch (Exception e){
	    // If we couldn't, print error and exit program
	    System.out.println("Couldn't read dictionary file\n"+e);
	    System.exit(0);
	}
    }
    void insert(String word){
	root.insert(word);
    }
    boolean search(String word){
	if(root.search(word) != null){
	    return true;
	}
	else{
	    return false;
	}
    }
    boolean delete(String word){
	// Find the Word containing the string
	Word toBeRemoved = root.search(word);
	// check if it exists
	if(toBeRemoved == null){
	    // Return false if the word doesn't exist
	    System.out.println("Didn't find it");
	    return false;
	}
	// Start search for parent
	Word forelder = toBeRemoved.findParent(root);
	// Check if there is a replacement
	Word replacement;
	Word replacementParent;
	if(toBeRemoved.right != null){
	    // Find replacement, the first word in the right subtree, to take the deleted words place
	    replacement = toBeRemoved.right.findFirstUnder();
	    // Find the parent of replacement
	    replacementParent = replacement.findParent(toBeRemoved);
	    // Connect the right (and only) branch of replacement to the left of it's parent
	    // to take it's place
	    replacementParent.left = replacement.right;
	    // Connect the left of the node to be removed to the left of replacement to
	    // disconnect toBeRemoved from it's left and connect replacement it it's place
	    replacement.left = toBeRemoved.left;
	    if(toBeRemoved.right != replacement){
		// If the replacement isn't the right child of toBeRemoved then we have
		// to connect replacement to toBeRemoved's right branch to replace it
		// If replacement is the direct child we don't need to do anything to the
		// right branch
		replacement.right = toBeRemoved.right;
	    }
	    // Find if we're left or right of parent
	    if(forelder != null){
		if(forelder.right == toBeRemoved){
		    // toBeRemoved was right of parent, so we put replacement in it's place
		    forelder.right = replacement;
		}
		else if(forelder.left == toBeRemoved){
		    // toBeRemoved was left of parent, so we put replacement in it's place
		    forelder.left = replacement;
		}
		else{
		    System.out.println("Deletion failed! Neither left nor right of parent");
		    return false;
		}
	    }
	    else{
		// No parent => We're removing root
		root = replacement;
	    }
	    return true;
	}
	else{
	    // replacement = toBeRemoved.left;
	    if(forelder != null){
		if(forelder.right == toBeRemoved){
		    forelder.right = toBeRemoved.left;
		    return true;
		}
		else if(forelder.left == toBeRemoved){
		    forelder.left = toBeRemoved.left;
		    return true;
		}
		else{
		    return false;
		}
	    }
	    else{
		// No parent => We're removing root
		root = toBeRemoved.left;
		return true;
	    }
	}
    }
    String findSwappedChars(String word){
	// Find a word that is equal to argument if we swap to letters in argument and print result
	String returnString = "";
	// Create an array of characters in the string
	char[] w = word.toCharArray();
	// Initialize a buffer so we can work on a copy of w
	char[] wc;
	// Loop through all characters in the string except the first
	for(int i = 1; i < w.length; i++){
	    wc = w.clone();
	    // Swap the character in position i with the preceding character
	    char temp = wc[i-1];
	    wc[i-1] = wc[i];
	    wc[i] = temp;
	    // Reasseble into a string and see if we can find it now
	    Word match = root.search(new String(wc));
	    // If we did, we append it to returnstring
	    if(match != null){
		returnString += match.value+" ";
	    }
	}
	// return all matches found
	return returnString;
    }
    String findWrongChar(String word){
	// Find a word that is equal to argument if we change a letter in argument with another and print result
	String returnString = "";
	// Create an array of characters in the string
	char[] w = word.toCharArray();
	// Initialize a buffer so we can work on a copy of w
	char[] wc;
	// Create an array of the letters arranged according to frequency
	char[] letters = "etaoinshrdlcumwfgypbvkjxqz".toCharArray();
	// Loop through all characters in the string
	for(int i = 0; i < w.length; i++){
	    // Loop through all the letters in the alphabet
	    for(int j = 0; j < letters.length; j++){
		wc = w.clone();
		// change character i with another from the alphabet to see if we can find a match
		wc[i] = letters[j];
		Word match = root.search(new String(wc));
		// If there's a match, append it to returnstring
		if(match != null){
		    returnString += match.value+" ";
		}
	    }
	}
	return returnString;
    }
    String findMissingChar(String word){
	// Find a word that is equal to argument if we add a letter in argument and print result
	String returnString = "";
	// Create an array of characters in the string
	char[] w = ("x"+word).toCharArray();
	// Initialize a buffer so we can work on a copy of w
	char[] wc;
	// Create an array of the letters arranged according to frequency
	char[] letters = "etaoinshrdlcumwfgypbvkjxqz".toCharArray();
	// Loop through all characters in the string
	for(int i = 0; i < w.length; i++){
	    // Loop through all the letters in the alphabet
	    for(int j = 0; j < letters.length; j++){
		wc = w.clone();
		// change character i with another from the alphabet to see if we can find a match
		wc[i] = letters[j];
		Word match = root.search(new String(wc));
		// If there's a match, append it to returnstring
		if(match != null){
		    returnString += match.value+" ";
		}
	    }
	    // Prepare w for next loop if there is one
	    if(i < (w.length - 1)){
		// Swap our appended 'x' to the next spot in the string
		w[i] = w[i+1];
		w[i+1] = 'x';
	    }
	}
	return returnString;
    }
    String findExtraChar(String word){
	// Find a word that is equal to argument if we remove a letter from argument and print result
	String returnString = "";
	// Create an array of characters in the string
	char[] w = word.toCharArray();
	// Initialize a buffer so we can work on a copy of w
	char[] wc;
	// Loop through all characters in the string
	for(int i = 0; i < w.length; i++){
	    wc = w.clone();
	    // Loop through all letters after i+1
	    for(int j = i+1; j < w.length; j++){
		// Overwrite letter w[i] and move the rest one step forward
		wc[j-1] = w[j];
	    }
	    wc[w.length-1] = ' ';
	    Word match = root.search((new String(wc)).trim());
	    // If there's a match, append it to returnstring
	    if(match != null){
		returnString += match.value+" ";
	    }
	}
	return returnString;
    }
    void printStatistics(){
	// Print statistics about the dictionary
	int depth = root.findNodeHeight();
	// Print depth of the tree
	System.out.println("Depth: "+depth);
	// Initialize an array of integers to denote number of nodes at each level
	int[] nodesAtDepth = new int[depth];
	// Initialize an ArrayList that will hold all the nodes at each level
	ArrayList<Word> nodesAtLevel = new ArrayList<Word>();
	ArrayList<Word> nodesAtPrevLevel = new ArrayList<Word>();
	nodesAtLevel.add(root);
	// Go through tree to find all nodes
	for(int i = 0; i < depth; i++){
	    // Set numder of nodes at this level euqal to size of arraylist conaining all nodes at this level
	    nodesAtDepth[i] = nodesAtLevel.size();
	    // Clear arraylist of nodes of previous level to prepare for moving to next level
	    nodesAtPrevLevel.clear();
	    nodesAtPrevLevel.addAll(nodesAtLevel);
	    nodesAtLevel.clear();
	    // Go through all nodes on this level
	    for(int j = 0; j < nodesAtDepth[i]; j++){
		// Create intermediary variable to avoid calling get every time
		Word thisNode = nodesAtPrevLevel.get(j);
		// If left child exists, add it to next level
		if(thisNode.left != null){
		    nodesAtLevel.add(thisNode.left);
		}
		// If right child exists, add it to next level
		if(thisNode.right != null){
		    nodesAtLevel.add(thisNode.right);
		}
	    }
	}
	// Print number of nodes for each depth
	for(int i = 0; i < depth; i++){
	    // For each level, print number of nodes
	    System.out.println("Nodes at depth "+i+": "+nodesAtDepth[i]);
	}
	// Print average depth of all nodes
	int weightedSum = 0;
	int numNodes = 0;
	for(int i = 1; i < depth; i++){
	    // For each level, add to variables
	    weightedSum += i * nodesAtDepth[i];
	    numNodes += nodesAtDepth[i];
	}
	System.out.println("The average depth of all nodes is "+(float)Math.round(1000*weightedSum/numNodes)/1000);
	// Print first word
	System.out.println("The first word in the dictionary is "+root.findFirstUnder().value);
	// Print last word
	System.out.println("The last word in the dictionary is "+root.findLastUnder().value);
    }

    private class Word{
	String value;
	Word left;
	Word right;

	Word(String word) {
	    this.value = word;
	}
	void insert(String word){
	    // Find appropriate parent for word and create a new Word in the appropriate spot in parent.
	    int compareValue = this.value.compareTo(word);
	    if(compareValue < 0){
		// Go right
		if(this.right != null){
		    //Right node already exists, try to insert in that
		    this.right.insert(word);
		}
		else {
		    // Right node doesn't exist, create
		    this.right = new Word(word);
		}
	    }
	    else if(compareValue > 0){
		// Go left
		if(this.left != null){
		    // Left node already exists, try to insert in that
		    this.left.insert(word);
		}
		else{
		    //Left node doesnæt exist, create
		    this.left = new Word(word);
		}
	    }
	    /*else{
        // argument is equal to current word, we don't need duplicates, do nothing
	}*/
	}
	Word search(String word){
	    // Find this string and the Word that contains it and return the Word
	    int compareValue = this.value.compareTo(word);
	    if(compareValue > 0){
		// word is before this, go left
		if(this.left != null){
		    // There is a word to the left, search that
		    return this.left.search(word);
		}
		else{
		    // There are no words to the left, we can't find the word
		    return null;
		}

	    }
	    else if(compareValue < 0){
		// Word is after this, go right
		if(this.right != null){
		    // There is a word to the right, search that
		    return this.right.search(word);
		}
		else{
		    // There are no words to the right, we can't find the word
		    return null;
		}
	    }
	    else{
		// This is the word!
		return this;
	    }
	}
	Word findFirstUnder(){
	    // Find the first word under this, including this
	    // If there is a word to the left, it comes before, so we search under that word.
	    if(this.left != null){
		return this.left.findFirstUnder();
	    }
	    else{
		// If there are no words to the left, this is the first word in subtree, so we return it.
		return this;
	    }
	}
	Word findLastUnder(){
	    // Find the last word under this, including this
	    // If there is a word to the right, it comes after, so we search under that word.
	    if(this.right != null){
		return this.right.findLastUnder();
	    }
	    else{
		// If there are no words to the right, this is the last word in subtree, so we return it.
		return this;
	    }
	}
	int findNodeHeight(){
	    // Find height of this node
	    int height = 0;
	    if(this.right != null){
		// If right node exists, return it's height +1
		height = 1 + this.right.findNodeHeight();
	    }
	    if(this.left != null){
		// If left node exists, return whichever is larger of rights height +1 and lefts height +1.
		height = Math.max((1 + this.left.findNodeHeight()), height);
	    }
	    return height;
	}
	Word findParent(Word startPoint){
	    // Start search for parent
	    Word forelder = startPoint;
	    // Loop until we have found that the deleted word is a parents left or right
	    // But avoiding infinite loop by running it max 1000 times.
	    for(int i = 0; i < 1000; i++){
		// Compare parent to this
		int comp = forelder.value.compareTo(this.value);
		// See if the word to be deleted comes before word in parent, if so we go left
		if(comp > 0){
		    // Check if we've found our word
		    if(forelder.left == this){
			// If the left value of parent is our word we've found our parent
			return forelder;
		    }
		    else{
			// We didn't find it, traverse left
			forelder = forelder.left;
		    }
		}
		else if(comp < 0){
		    // The word to be deleted comes after the word in parent, so we go right
		    if(forelder.right == this){
			// We found our parent, and we're the right value
			return forelder;
		    }
		    else{
			// We didn't find it, traverse right
			forelder = forelder.right;
		    }
		}
		else{
		    // The word to be deleted is equal to parent, which only happens
		    // for the first word in the tree, so we chose wrong startpoint
		    return null;
		}
	    }
	    // We didn't find parent, return null
	    return null;
	}
    }
}
