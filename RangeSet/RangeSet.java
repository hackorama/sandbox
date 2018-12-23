import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A text range set implementation, that allows adding and removing of ranges
 * and provides within range set check for a given text string.
 * 
 * Implemented using two range sets one that tracks all allowed/included ranges
 * and one that tracks all excluded ranges. The tracked ranges are checked and
 * resized as needed when ever a new range is added or deleted.
 * 
 * For the within check first the given text string is checked in all
 * allowed/include ranges and only if found then a second check of exclude list
 * is done before confirming.
 * 
 * Implementation is thread safe.  TODO: Thread safe tests
 * 
 * NOTE: If the within range set checks vastly outnumber the add/delete ranges
 * to the set then please consider using CopyOnWriteArrayList for the
 * include/exclude range sets.
 * 
 */
public class RangeSet {

	private Collator collator = Collator.getInstance(); // locale-sensitive String comparison
	// all non overlapping ranges are tracked here
	private List<Range> includeSet = Collections.synchronizedList(new ArrayList<>());
	// all non overlapping exclude ranges are tracked here
	private List<Range> excludeSet = Collections.synchronizedList(new ArrayList<>());

	private void addRange(List<Range> rangeList, String begin, String end) {
		if (!isValid(begin, end)) {
			throw new IllegalArgumentException("A range requires two valid non empty strings in lexical order");
		}
		Range targetRange = new Range(begin, end);
		int i = 0; // to replace the resized range in place
		synchronized (rangeList) {
			for (Range range : rangeList) {
				if (isWithin(range, targetRange)) {
					log("INFO", targetRange + " contained in existing range " + range);
					return;
				} else if (isPartiallyWithinRange(range, targetRange)) {
					log("INFO", targetRange + " is partially contained in existing range " + range
							+ ", will resize this range to include the new range");
					rangeList.set(i, resizeRange(range, targetRange)); // replace in place
					return;
				}
				i++;
			}
		}
		log("INFO", targetRange + " is added as a new range");
		rangeList.add(targetRange);
	}

	/**
	 * Add a range to the set.
	 * 
	 * @param begin
	 *            the beginning of the range
	 * @param end
	 *            the end of the range
	 */
	public void addRange(String begin, String end) {
		log("INFO", "Adding a new range " + begin + ", " + end + " ...");
		addRange(includeSet, begin, end);
	}

	/**
	 * Remove a range from the set.
	 * 
	 * @param begin
	 *            the beginning of the range
	 * @param end
	 *            the end of the range
	 */
	public void deleteRange(String begin, String end) {
		log("INFO", "Deleting a new range " + begin + ", " + end + " ...");
		addRange(excludeSet, begin, end);
	}

	public void describe() {
		System.out.println("Range set includes " + this.includeSet);
		System.out.println("Range set exludes " + this.excludeSet);
	}

	private boolean isAfter(String a, String b) {
		return collator.compare(a, b) <= 0;
	}

	private boolean isBefore(String a, String b) {
		return collator.compare(a, b) >= 0;
	}

	private boolean isBetween(String begin, String end, String text) {
		return isAfter(begin, text) && isBefore(end, text);
	}

	private boolean isExcluded(String text) {
		synchronized (excludeSet) {
			for (Range range : excludeSet) {
				if (isWithin(range, text)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isPartiallyWithinRange(Range source, Range target) {
		return isBetween(source.getBegin(), source.getEnd(), target.getBegin())
				|| isBetween(source.getBegin(), source.getEnd(), target.getEnd());
	}

	private boolean isValid(String string) {
		return null != string && !string.trim().isEmpty();
	}

	private boolean isValid(String begin, String end) {
		return isValid(begin) && isValid(end) && isAfter(begin, end);
	}

	private boolean isWithin(Range source, Range target) {
		return isAfter(source.getBegin(), target.getBegin()) && isBefore(source.getBegin(), target.getBegin());
	}

	private boolean isWithin(Range range, String text) {
		return isBetween(range.getBegin(), range.getEnd(), text);
	}

	/**
	 * Check if this range set contains the specified text string.
	 * 
	 * @param text
	 *            the string to check for
	 * @return true if and only if this range set contains the specified text
	 *         string, false otherwise
	 */
	public boolean isWithin(String text) {
		if (!isValid(text)) {
			log("DEBUG", "Invalid text '" + text + "' will not be checked for within range test");
			return false;
		}
		synchronized (includeSet) {
			for (Range range : includeSet) {
				if (isWithin(range, text)) { // if found in one of the include range set
					if (!isExcluded(text)) { // and then check it is not in one of the exclude set
						return true;
					}
				}
			}
		}
		return false;
	}

	private void log(String prefix, String message) {
		System.out.format("%s : %s%n", prefix, message);
	}

	private Range resizeRange(Range source, Range target) {
		Range range = new Range(source.getBegin(), source.getEnd());
		if (isBefore(source.getBegin(), target.getBegin())) {
			range.setBegin(target.getBegin());
		}
		if (isAfter(source.getEnd(), target.getEnd())) {
			range.setEnd(target.getEnd());
		}
		log("INFO", "Updated range " + source + " -> " + range);
		return range;
	}

}
