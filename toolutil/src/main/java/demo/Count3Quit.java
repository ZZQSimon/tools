package demo;

public class Count3Quit {
	static final int M = 500;

	public static void main(String[] args) {
		boolean[] arr = new boolean[M];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = true;
		}

		int leftCount = arr.length;
		int countNum = 0;
		int index = 0;

		while (leftCount > 1) {
			if (arr[index] == true) {
				countNum++;
				if (countNum == 3) {
					countNum = 0;
					arr[index] = false;
					leftCount--;
				}
			}

			index++;

			if (index == arr.length) {
				index = 0;
			}
		}

		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == true) {
				System.out.println(i);
			}
		}

	}
}
