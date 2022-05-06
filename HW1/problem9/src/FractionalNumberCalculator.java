public class FractionalNumberCalculator {
	public static void printCalculationResult(String equation) {
		// DO NOT change the skeleton code.
		// You can add codes anywhere you want.

		String[] split = equation.split(" ");

		FractionalNumber num1 = toFrac(split[0]);
		FractionalNumber num2 = toFrac(split[2]);

		switch(split[1].charAt(0)){
			case '+': num1.add(num2).print(); break;
			case '-': num1.subtract(num2).print(); break;
			case '*': num1.multiply(num2).print(); break;
			case '/': num1.divide(num2).print(); break;
			default:;
		}
	}

	private static FractionalNumber toFrac(String str){
		String[] split = str.split("/");
		if(split.length == 1){
			return new FractionalNumber(split[0]);
		}
		else{
			return new FractionalNumber(split[0], split[1]);
		}
	}

}

class FractionalNumber {
	private int numerator; // can be negative
	private int denominator; // cannot be negative

	FractionalNumber(String a, String b){
		numerator = Integer.parseInt(a);
		denominator = Integer.parseInt(b);
	}
	FractionalNumber(int a, int b){
		numerator = a;
		denominator = b;
	}
	FractionalNumber(String a){
		numerator = Integer.parseInt(a);
		denominator = 1;
	}
	FractionalNumber(int a){
		numerator = a;
		denominator = 1;
	}

	public FractionalNumber add(FractionalNumber num){
		if(this.denominator == num.denominator){
			FractionalNumber result = new FractionalNumber(this.numerator + num.numerator, denominator);
			result.reduce();
			return result;
		}
		else{
			int lcm = getLCM(this.denominator, num.denominator);
			this.commonize(lcm);
			num.commonize(lcm);
			FractionalNumber result = this.add(num);
			return result;
		}
	}
	public FractionalNumber subtract(FractionalNumber num){
		if(this.denominator == num.denominator){
			FractionalNumber result = new FractionalNumber(this.numerator - num.numerator, denominator);
			result.reduce();
			return result;
		}
		else{
			int lcm = getLCM(this.denominator, num.denominator);
			this.commonize(lcm);
			num.commonize(lcm);
			FractionalNumber result = this.subtract(num);
			return result;
		}
	}
	public FractionalNumber multiply(FractionalNumber num){
		FractionalNumber result = new FractionalNumber(this.numerator * num.numerator, this.denominator * num.denominator);
		result.reduce();
		return result;
	}
	public FractionalNumber divide(FractionalNumber num){
		FractionalNumber result = new FractionalNumber(this.numerator * num.denominator, this.denominator * num.numerator);
		result.reduce();
		return result;
	}

	public void commonize(int newDenom){
		numerator *= newDenom / denominator;
		denominator = newDenom;
		}

	public void print(){
		if(isInteger()){
			System.out.println(numerator);
		}
		else{
			System.out.println(numerator + "/" + denominator);
		}
	}

	private boolean isInteger(){
		return denominator == 1;
	}

	private void reduce(){
		int gcd = getGCD(Math.abs(numerator), denominator);
		if(gcd != 1){
			numerator /= gcd;
			denominator /= gcd;
		}
	}
	private int getGCD(int a, int b){
		int gcd = 1;
		for(int i=2; i <= Math.min(a, b); i++){
			if(a % i == 0 && b % i ==0){
				gcd = i;
			}
		}
		return gcd;
	}

	private int getLCM(int a, int b){
		int gcd = getGCD(a, b);
		return a * b / gcd;
	}

}
