import java.util.stream.IntStream;

/**
 * String Range Set Driver and Tests
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class Main {

	private static void boundaryTests() {

		// add and delete same range
		RangeSet rangeSet = new RangeSet();
		rangeSet.addRange("C", "K");
		rangeSet.deleteRange("C", "K");
		printRanges(rangeSet, 'A', 'M');

		// add same range again
		rangeSet = new RangeSet();
		rangeSet.addRange("C", "K");
		rangeSet.addRange("C", "K");
		printRanges(rangeSet, 'A', 'M');

		// add delete and add same range again
		rangeSet = new RangeSet();
		rangeSet.addRange("C", "K");
		rangeSet.addRange("C", "K");
		printRanges(rangeSet, 'A', 'M');

		// add and multiple delete
		rangeSet = new RangeSet();
		rangeSet.addRange("C", "K");
		rangeSet.deleteRange("C", "F");
		rangeSet.deleteRange("G", "K");
		printRanges(rangeSet, 'A', 'M');

		// delete without adding
		rangeSet = new RangeSet();
		rangeSet.deleteRange("C", "K");
		printRanges(rangeSet, 'A', 'M');

		// single member range
		rangeSet = new RangeSet();
		rangeSet.addRange("C", "C");
		printRanges(rangeSet, 'A', 'M');

		// delete single member range
		rangeSet = new RangeSet();
		rangeSet.addRange("C", "C");
		rangeSet.deleteRange("C", "C");
		printRanges(rangeSet, 'A', 'M');
	}

	private static void excludeRangeTest() {
		RangeSet rangeSet = new RangeSet();
		rangeSet.addRange("C", "K");
		rangeSet.deleteRange("F", "H");
		rangeSet.addRange("C", "K");
		printRanges(rangeSet, 'A', 'M');
	}

	private static void llegalArgsTests() {
		llegalArgsTests(false); // test add
		llegalArgsTests(true); // test delete
		RangeSet rangeSet = new RangeSet();
		rangeSet.isWithin(null);
		rangeSet.isWithin("");
		rangeSet.isWithin(" ");
		rangeSet.isWithin("   ");

	}

	private static void llegalArgsTests(boolean delete) {
		llegalArgsTests(null, "B", delete);
		llegalArgsTests("A", null, delete);
		llegalArgsTests(null, null, delete);
		llegalArgsTests("", "B", delete);
		llegalArgsTests("A", "", delete);
		llegalArgsTests("", "", delete);
		llegalArgsTests(" ", "B", delete);
		llegalArgsTests("A", " ", delete);
		llegalArgsTests(" ", " ", delete);
		llegalArgsTests("    ", "B", delete);
		llegalArgsTests("A", "   ", delete);
		llegalArgsTests("   ", "   ", delete);
		llegalArgsTests("B", "A", delete);
	}

	private static void llegalArgsTests(String begin, String end, boolean delete) {
		RangeSet rangeSet = new RangeSet();
		try {
			if (delete) {
				rangeSet.deleteRange(begin, end);
			} else {
				rangeSet.addRange(begin, end);
			}
		} catch (IllegalArgumentException e) {
			System.out.println((delete ? "Delete " : "Add ") + begin + ", " + end + " : " + e.getMessage());
		}
	}

	private static void multipleExcludeRangeTest() {
		RangeSet rangeSet = new RangeSet();
		rangeSet.addRange("C", "K");
		rangeSet.deleteRange("D", "F");
		rangeSet.deleteRange("H", "J");
		printRanges(rangeSet, 'A', 'M');
	}

	private static void nonOverlappingRangeTest() {
		RangeSet rangeSet = new RangeSet();
		rangeSet.addRange("C", "F");
		rangeSet.addRange("I", "K");
		printRanges(rangeSet, 'A', 'M');
	}

	private static void overlappingRangeTest() {
		RangeSet rangeSet = new RangeSet();
		rangeSet.addRange("C", "H");
		rangeSet.addRange("F", "K");
		printRanges(rangeSet, 'A', 'M');
	}

	/**
	 * Prints alphabetical single char limited set test range formatted for easy
	 * testing. The char set is printed on first line and the '*' in the next line
	 * indicates if the char is in range
	 * 
	 * @param rangeSet
	 *            the limited single char range set used for testing
	 * @param begin
	 *            the beginning of the limited single char test range
	 * @param end
	 *            the end of the limited single char test range
	 */
	private static void printRanges(RangeSet rangeSet, char begin, char end) {
		System.out.println("--");
		rangeSet.describe();
		System.out.print("RANGE: ");
		IntStream.rangeClosed(begin, end).mapToObj(var -> (char) var).forEach(var -> {
			System.out.print(var + " ");
		});
		System.out.println();
		System.out.print("FOUND: ");
		IntStream.rangeClosed(begin, end).mapToObj(var -> (char) var).forEach(var -> {
			System.out.print(rangeSet.isWithin(String.valueOf(var)) ? "* " : "  ");
		});
		System.out.println();
		System.out.println("--");
	}

	private static void simpleTestFromProblemDocument() {
		RangeSet rangeSet = new RangeSet();
		rangeSet.addRange("AaA", "BaB");
		System.out.println(rangeSet.isWithin("Aaab"));
		System.out.println(rangeSet.isWithin("Cblahblah"));
	}

	public static void test() {
		simpleTestFromProblemDocument();
		nonOverlappingRangeTest();
		overlappingRangeTest();
		excludeRangeTest();
		multipleExcludeRangeTest();
		boundaryTests();
		llegalArgsTests();
	}

	public static void main(String[] args) {
		Main.test();
	}

}
