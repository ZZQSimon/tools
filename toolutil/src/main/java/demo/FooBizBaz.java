package demo;

public class FooBizBaz {
	
	static final int H = 50;
	
	public static void main(String[] args) {
	    for ( int number = 1; number <= H; number++ ) {
	      System.out.print(number);
	      if ( (number % 3) == 0 ) {
		    System.out.print(" foo");
	      }
	      if ( (number % 5) == 0 ) {
		    System.out.print(" bar");
	      }
	      if ( (number % 7) == 0 ) {
		    System.out.print(" baz");
	      }
	      System.out.println();
	    }
	  }
}
