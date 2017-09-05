1) Running time analysis of code for generating similar words
  n = words in file; r = letters in word
  findSwapped:  O(n,r) ~ r(r+n) ~ n (for verste trestruktur, n->log2n for beste)
  findWrong:    O(n,r) ~ 26r(r+n) ~ n (for verste trestruktur, n->log2n for beste)
  findMissing:  O(n,r) ~ 26r(r+n) ~ n (for verste trestruktur, n->log2n for beste)
  findExtra:    O(n,r) ~ r(1.5r+n) ~ n (for verste trestruktur, n->log2n for beste)
  Altogether:   O(n,r) ~ 54.5r^2 + 54n ~ n (for verste trestruktur, n->log2n for beste)
2) How to compile your program (ie. javac *.java)
  javac Oblig1.java
3) Which file includes the main-method
  Oblig1.class
4) Any assumptions you have made when implementing the assignment
  I assumed we were not supposed to run each of the similar word searches within each of the others
  as that would have dramatically increased the complexity of the processes
5) Any peculiarities about your implementation
  It is possible to write multiple words and get results for all.
  If the input line begins with d, as in "d achieve", the words after d will be deleted
  It is possible to call it with searchwords, f.eks: java Oblig1 achieve ball darndests
6) The status of your delivery (what works and what does not)
  I haven't found anything that doesn't work
7) What you are most interested in receiving feedback about, and if you
  want feedback in Norwegian or English (the default is Norwegian)
  Language doesn't matter.
  Is the commenting sufficient?
  Is there anywhere I could/should have made the code more efficient?
  Is there a way I could have made it simpler for myself?
  How much time did you expect us to use on this, how would this assignment compare to an exam
  in time spent?
