package dadjoke;

import data.SubsequentWord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DadJokeGenerator {
	private static HttpURLConnection con;
	private static Random r = new Random();
	private static ArrayList<String> startingWords = new ArrayList<String>();
	private static ArrayList<String> endingWords = new ArrayList<String>();
	private static HashMap<String, ArrayList<SubsequentWord>> markovChain = new HashMap<String, ArrayList<SubsequentWord>>();
	private static HashMap<String, Integer> totalNumWordsDirectlyAfter = new HashMap<String, Integer>();
	
	private static JSONObject getHttpResponse(String url) {
		try {
			URL myurl = new URL(url);
			con = (HttpURLConnection) myurl.openConnection();
			con.setRequestMethod("GET");
			con.addRequestProperty("User-Agent", "My Library (michellenguyen.sf@gmail.com)");
			con.addRequestProperty("Accept", "application/json");
			
			// Get response
			InputStream is = con.getErrorStream();
			if (is == null) is = con.getInputStream(); 
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			
			// Parse JSON with JSON Parser Object
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObj = (JSONObject) jsonParser.parse(in);
			return jsonObj;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			con.disconnect();
		}
		return null;
	}
	
	public static String generateJoke(int numWords) {
		StringBuilder jokeStringBuilder = new StringBuilder();
		
		// Add a starting word 
		jokeStringBuilder.append(startingWords.get(r.nextInt(startingWords.size()))).append(" ");
		
		// Add a random word from markov chain keys
		int len = markovChain.size(); 
		String[] words = markovChain.keySet().toArray(new String[len]);
		int randIndex = r.nextInt(len);
		String nextWord = words[randIndex];
		jokeStringBuilder.append(nextWord).append(" ");
		
		// Choose next word based on probability of possible subsequent words
		for (int i = 0; i < numWords - 3; i++) {
			nextWord = getNextMarkovChainWord(nextWord);
			jokeStringBuilder.append(nextWord).append(" ");
		}
		
		// Add an ending word
		jokeStringBuilder.append(endingWords.get(r.nextInt(endingWords.size())));
		return jokeStringBuilder.toString();
	}
	
	public static String getNextMarkovChainWord(String currWord) {
		ArrayList<SubsequentWord> subsequentWords = markovChain.get(currWord);
		String nextStr = "";
		int randIndex;
		// Check that current word has subsequent words to choose from
		if (subsequentWords != null) {
			int totalNumSubseqWords = totalNumWordsDirectlyAfter.get(currWord);
			randIndex = r.nextInt(totalNumSubseqWords);
			
			// Get next subsequent word based on relative frequency.
			// Since arraylist is condensed (no duplicate words), we find the next
			// word by subtracting each word's frequency from randIndex
			// until we arrive at the corresponding random word.
			for (int i = 0; i < subsequentWords.size(); i++) {
				String currSubseqWord = subsequentWords.get(i).getSubseqWord();
				int currSubseqWordFrequency = subsequentWords.get(i).getNumOccurrences();
				randIndex -= currSubseqWordFrequency;
				if (randIndex < 0) { 
					return currSubseqWord; 
				}
			}
		} else {	// Pick a random word if current word has no subsequent words
			int len = markovChain.size();
			randIndex = r.nextInt(len);
			String[] words = markovChain.keySet().toArray(new String[len]);
			nextStr = words[randIndex]; 
		}
		return nextStr;
	}
	
	public static void trainMarkovChain(String baseUrl) {
		// Test get request on first page and find total number of pages
		JSONObject firstPage = getHttpResponse(baseUrl);
		if (firstPage == null) {
			System.err.println("Error: No HTTP Response");
			return;
		}
		int totalPages = Integer.parseInt(firstPage.get("total_pages").toString());
		JSONArray jokes = (JSONArray) firstPage.get("results");
		trainMarkovChain(jokes);
		
		// Train markov chain on every page of jokes
		for (int p = 2; p <= totalPages; p++) {			
			JSONObject page = getHttpResponse(baseUrl + "?page=" + p);
			jokes = (JSONArray) page.get("results");
			trainMarkovChain(jokes);
		}
	}
	
	public static void trainMarkovChain(JSONArray jokes) {
		Iterator i = jokes.iterator();
		
		// Store word frequencies
		while (i.hasNext()) {
			JSONObject jokeObj = (JSONObject) i.next();
			String joke = (String) jokeObj.get("joke");
			String[] words = joke.trim().split(" ");
			ArrayList<SubsequentWord> subsequentWords;
			
			// Add word or update word frequency
			for (int w = 0; w < words.length - 1; w++) {
				String startingWord = words[w];
				SubsequentWord subseqentWord = new SubsequentWord(words[w+1]);
				
				if (w == 0) {
					startingWords.add(startingWord);
					continue;
				}
				
				if (w == words.length - 2) {
					endingWords.add(subseqentWord.getSubseqWord());
					break;
				}
				
				// Add word or update frequency
				if (markovChain.containsKey(startingWord)) {	
					subsequentWords = markovChain.get(startingWord);
					// Update subsequent word count
					if (subsequentWords.contains(subseqentWord)) {
						int subseqWordIndex = subsequentWords.indexOf(subseqentWord);
						subseqentWord = subsequentWords.get(subseqWordIndex);
						subseqentWord.incrementNumOccurrences();
					} else { 	// Add new subsequent word
						subsequentWords.add(subseqentWord);
					}
					// Update total number of subsequent words for computing probability 
					int numWordsDirectlyAfter = totalNumWordsDirectlyAfter.get(startingWord);
					totalNumWordsDirectlyAfter.put(startingWord, numWordsDirectlyAfter + 1);
				
				} else { 	// Add new starting word
					subsequentWords = new ArrayList<SubsequentWord>();
					subsequentWords.add(subseqentWord);
					markovChain.put(startingWord, subsequentWords);
					totalNumWordsDirectlyAfter.put(startingWord, 1);
				}
			}
		}
	}
	
	public static String dadJoke(int numWords) throws MalformedURLException, ParseException {
		String url = "https://icanhazdadjoke.com/search";
		trainMarkovChain(url);
		return generateJoke(numWords);
	}
}