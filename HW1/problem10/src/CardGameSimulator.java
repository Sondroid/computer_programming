public class CardGameSimulator {
	private static final Player[] players = new Player[2];
	private static Card cardOnTable;

	public static void simulateCardGame(String inputA, String inputB) {
		// DO NOT change the skeleton code.
		// You can add codes anywhere you want.

		players[0] = new Player("A", inputA);
		players[1] = new Player("B", inputB);

		cardOnTable = players[0].getFirstCard();
		players[0].playCard(cardOnTable);

		int turn = 1;
		while(players[turn].hasProperCard(cardOnTable)){
			cardOnTable = players[turn].getNextCard(cardOnTable);
			players[turn].playCard(cardOnTable);
			turn = 1 - turn;
		}
		printWinMessage(players[1-turn]);
	}

	private static void printWinMessage(Player player) {
		System.out.printf("Player %s wins the game!\n", player);
	}
}


class Player {
	private String name;
	private Card[] deck;

	Player(String name, String input){
		this.name = name;
		this.deck = parseInput(input);
	}

	private Card[] parseInput(String input){
		String[] strSplit = input.split(" ");
		Card[] deck = new Card[10];
		for(int i=0; i < 10; i++){
			deck[i] = new Card(strSplit[i].charAt(0) - '0', strSplit[i].charAt(1));
		}
		return deck;
	}

	public Card getFirstCard(){
		Card candidate = deck[0];
		for(int i=1; i<10; i++){
			if(deck[i].getNumber() > candidate.getNumber()){
				candidate = deck[i];
			}
			else if(deck[i].getNumber() == candidate.getNumber() && deck[i].getShape() == 'X'){
				candidate = deck[i];
			}
		}
		return candidate;
	}

	public boolean hasProperCard(Card cardOnTable){
		return hasSameNumber(cardOnTable) || hasSameShape(cardOnTable);
	}

	private boolean hasSameNumber(Card cardOnTable){
		for(int i=0; i<10; i++){
			if(deck[i] != null && deck[i].getNumber() == cardOnTable.getNumber()){
				return true;
			}
		}
		return false;
	}
	private boolean hasSameShape(Card cardOnTable){
		for(int i=0; i<10; i++){
			if(deck[i] != null && deck[i].getShape() == cardOnTable.getShape()){
				return true;
			}
		}
		return false;
	}

	private Card getSameNumber(Card cardOnTable){ //only executed when there exist same number
		for(int i=0; i<10; i++){
			if(deck[i] != null && deck[i].getNumber() == cardOnTable.getNumber()){
				return deck[i];
			}
		}
		return null;
	}

	private Card getLargestSameShape(Card cardOnTable){ // only executed when there exist same shape
		int candidateIndex = -1;
		for(int i=0; i<10; i++){
			if(deck[i] != null && deck[i].getShape() == cardOnTable.getShape()){
				candidateIndex = i;
				break;
			}
		}
		for(int j=candidateIndex+1; j<10; j++){
			if(deck[j] != null
					&& deck[j].getShape() == deck[candidateIndex].getShape()
					&& deck[j].getNumber() > deck[candidateIndex].getNumber()){
				candidateIndex = j;
			}
		}
		return deck[candidateIndex];
	}

	public Card getNextCard(Card cardOnTable){ //only executed when there exist proper next card

		if(hasSameNumber(cardOnTable)){
			return getSameNumber(cardOnTable);
		}
		else return getLargestSameShape(cardOnTable);
	}

	public void playCard(Card card) {
		removeCardFromDeck(card);
		System.out.printf("Player %s: %s\n", name, card);
	}

	private void removeCardFromDeck(Card card){
		for(int i=0; i < 10; i++){
			if(deck[i] != null && deck[i].equals(card)){
				deck[i] = null;
				break;
			}
		}
	}

	@Override
	public String toString() {
		return name;
	}
}


class Card {
	private int number;
	private char shape;

	Card(int number, char shape){
		this.number = number;
		this.shape = shape;
	}

	public char getShape() {
		return shape;
	}

	public int getNumber() {
		return number;
	}

	@Override
	public String toString() {
		return "" + number + shape;
	}

	public boolean equals(Card otherCard){
		return number == otherCard.number && shape == otherCard.shape;
	}

}