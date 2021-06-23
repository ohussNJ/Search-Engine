package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		Scanner s=new Scanner(new File(docFile));
		String keyword="";
		HashMap<String, Occurrence> idk=new HashMap<String,Occurrence>();
		while (s.hasNext()) {
			String word=s.next();
				keyword=getKeyword(word);
				if (keyword!=null) {
					if (idk.containsKey(keyword)) {
						Occurrence name=idk.get(keyword);
						name.frequency++;
					} else {
						Occurrence name=new Occurrence(docFile, 1);
						idk.put(keyword, name);
					}
				}
			
		}
		s.close();
		return idk;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		for (String w : kws.keySet()) {
			if (keywordsIndex.containsKey(w)) {
				ArrayList<Occurrence> occ = keywordsIndex.get(w);
				occ.add(kws.get(w));
				insertLastOccurrence(occ);
				keywordsIndex.put(w, occ);
			} else {
				Occurrence kwsOccurence = kws.get(w);
				ArrayList<Occurrence> occurences = new ArrayList<>();
				occurences.add(kwsOccurence);
				keywordsIndex.put(w, occurences);
		}	}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		if (word == null) {
			return null;
		}
		if (noiseWords.contains(word)) {
			return null;
		}
		if (word.equals("")) {
			return null;
		}
		if (word.charAt(0) == '.' || word.charAt(0) == '?' || word.charAt(0) == ':' || word.charAt(0) == ';' || word.charAt(0) == '!' || word.charAt(0) == ',') {
			return null;
		}
		word = word.toLowerCase();
		ArrayList<Character> p = new ArrayList<>();
		p.add('.');
		p.add(',');
		p.add('?');
		p.add(':');
		p.add(';');
		p.add('!');
		while (p.contains(word.charAt(word.length()-1))) {
			word=word.substring(0, word.length()-1);
		}
		if (word.equals("")) {
			return null;
		}
		if (noiseWords.contains(word)) {
			return null;
		}
		String letters="abcdefghijklmnopqrstuvwxyz";
		for (int i=0; i<word.length(); i++) {
			if (!letters.contains("" + word.charAt(i))) {
				return null;
			}
		}
		return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		if(occs.size() ==1) {
			return null;
		}
		int begin=0;
		int end=occs.size()-1;
		Occurrence val = occs.remove(occs.size() - 1);
		ArrayList<Integer> midpoints = new ArrayList<>();
		while (begin<=end) {
			int mid=(begin + end) / 2;
			if (val.frequency==occs.get(mid).frequency) {
				midpoints.add(mid);
				occs.add(mid, val);
				return midpoints;
			}
			if (val.frequency > occs.get(mid).frequency) {
				end=mid-1;
				midpoints.add(mid);
			} else {
				begin=mid+1;
				midpoints.add(mid);
			}
		}
		occs.add(begin, val);
		return midpoints;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		ArrayList<String> docs=new ArrayList<>();
		ArrayList<Occurrence> kw1Docs=keywordsIndex.get(kw1);
		ArrayList<Occurrence> kw2Docs=keywordsIndex.get(kw2);
		if (kw2Docs==null && kw1Docs==null) {
			return null;
		}
		if(kw2Docs==null) {
		int index=0;
			while(docs.size()<5 && (index<kw1Docs.size())) {
				String document=kw1Docs.get(index).document;
					if(!docs.contains(document)) {
						docs.add(document);
					}
					index++;
			} 
			if(docs.size()==0){
				return null;
			}
			return docs;
		}
		else if(kw1Docs==null) {
			int index=0;
				while(docs.size()<5 && (index<kw2Docs.size())) {
					String document=kw2Docs.get(index).document;
						if(!docs.contains(document)) {
								docs.add(document);
						}
					index++;
				}
				if(docs.size()==0){
					return null;
				}
			return docs;	
		}
		String document="";
		int kw1index=0;
		int kw2index=0;
		while (docs.size()<5 && (kw1index<kw1Docs.size()  || kw2index<kw2Docs.size())) {
			if (kw1index>kw1Docs.size()-1) {
				document=kw2Docs.get(kw2index).document;
				if (!docs.contains(document)) {
					docs.add(document);
				}
				kw2index++;
				continue;
			}
			if (kw2index>kw2Docs.size()-1) {
				document=kw1Docs.get(kw1index).document;
				if (!docs.contains(document)) {
					docs.add(document);
				}
				kw1index++;
				continue;
			}
			if (kw2Docs.get(kw2index).frequency==kw1Docs.get(kw1index).frequency) {
				document=kw1Docs.get(kw1index).document;
				if (!docs.contains(document)) {
					docs.add(document);
				}
				kw1index++;
			} else {
				if (kw2Docs.get(kw2index).frequency > kw1Docs.get(kw1index).frequency) {
					document=kw2Docs.get(kw2index).document;
					if (!docs.contains(document)) {
						docs.add(document);
					}
					kw2index++;
				} else {
					if(kw1Docs.get(kw1index).frequency > kw2Docs.get(kw2index).frequency) {
					 document=kw1Docs.get(kw1index).document;
					  if (!docs.contains(document)) {
						  docs.add(document);
					}
					kw1index++;
				}
				}
			}
		}
		if (docs.size()==0) {
			return null;
		}
		return docs;
	}
}
