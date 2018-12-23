/**
 * A text range represented by strings for range beginning and end.
 * 
 */
public class Range {

	private String begin;

	private String end;

	/**
	 * Initializes a new Range object.
	 * 
	 * @param begin
	 *            the range begin string
	 * @param end
	 *            the range end string
	 */
	public Range(String begin, String end) {
		this.begin = begin;
		this.end = end;
	}

	public String getBegin() {
		return begin;
	}

	public String getEnd() {
		return end;
	}

	public void setBegin(String begin) {
		this.begin = begin;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	@Override
	public String toString() {
		return String.format("[%s, %s]", begin, end);
	}
}
