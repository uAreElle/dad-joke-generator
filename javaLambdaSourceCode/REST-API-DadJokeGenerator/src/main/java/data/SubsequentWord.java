package data;

public class SubsequentWord {
	private final String word;
	private int numOccurrences;
	
	public SubsequentWord(String word, int numOccurrences) {
		this.word = word;
		this.setNumOccurrences(numOccurrences);
	}
	
	public SubsequentWord(String word) {
		this.word = word;
		this.setNumOccurrences(1);
	}
	
	public String getSubseqWord() {
		return word;
	}
	
	public int getNumOccurrences() {
		return numOccurrences;
	}

	public void setNumOccurrences(int numOccurrences) {
		this.numOccurrences = numOccurrences;
	}
	
	public void incrementNumOccurrences() {
		this.numOccurrences++;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (o instanceof SubsequentWord) {
			SubsequentWord subsequentWord = (SubsequentWord) o;
			return this.word.equals(subsequentWord.getSubseqWord());
		}
		if (o instanceof String) {
			String subsequentWord = (String) o;
			return this.word.equals(subsequentWord);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.word.hashCode();
	}

}