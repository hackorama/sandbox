/**
 * Represents a modifying database operation SET/DELETE using key, value pair
 * DELETE is represented by a null value, a non null value means SET 
 */
public class Operation {
	public Operation(String key, String value) {
		this.key = key;
		this.value = value;
	}
	public String key; //TODO encapsulate 
	public String value; //TODO encapsulate
}
