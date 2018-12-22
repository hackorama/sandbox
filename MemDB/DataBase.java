/**
 * Simple key value store database interface 
 *
 */
public interface DataBase {
	
	/**
	 * Get the value for a given key
	 * 
	 * @param key The key to get value for
	 * @return The value or null
	 */
	public String get(String key);
	
	/**
	 * Set the given value to the given key
	 * 
	 * @param key The key to set value for
	 * @param value The value to set
	 */
	public void set(String key, String value);
	
	/**
	 * Delete a given key from the store
	 * 
	 * @param key The key to delete 
	 */
	public void delete(String key);
	
	/**
	 * Count of a given value in the store
	 * 
	 * @param value The value to get the count for
	 * @return The count
	 */
	public int countValue(String value);
	
	/**
	 * Start transaction
	 */
	public void begin();

	/**
	 * Commit transaction
	 */
	public void commit();
	
	/**
	 * Rollback transaction
	 */
	public void rollback();
}
