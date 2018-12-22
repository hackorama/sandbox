import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * In memory key value database with nested transactions and roll backs.
 * Supports count by value. This is not thread safe and do not use any locks, so
 * no transaction isolation.
 */
public class MemDB implements DataBase {

	private static final MemDB instance = new MemDB();

	public static MemDB getInstance() {
		return instance;
	}
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	// The database is a key valye map of strings
	private final Map<String, String> store = new HashMap<>();
	
	// The value count is tracked in a separate map and updated on each modifiying
	// operation SET/DELETE
	private final Map<String, Integer> valueCountMap = new HashMap<>();

	// Transactions start on BEGIN and end on COMMIT/ROLLBACK
	// nested transactions are supported by a stack of transactions
	// each transaction is tracked as a stack of data modifying operations
	// SET/DELETE
	private Stack<Stack<Operation>> transactionStack = new Stack<>();

	private MemDB() {
		// no public construction
	}

	@Override
	public void begin() {
		debug("BEGIN");
		transactionStack.push(new Stack<Operation>());
	}

	public void clear() {
		store.clear();
		transactionStack.clear();
		valueCountMap.clear();
	}

	@Override
	public void commit() {
		transactionStack.pop(); // remove innermost nested committed transaction
		debug("COMMIT");
	}

	@Override
	public int countValue(String value) {
		Integer count = valueCountMap.get(value);
		int result = (count == null) ? 0 : count;
		debug("COUNT " + value + " = " + result);
		return result;
	}

	private void debug(String msg) {
		String level = ""; // nested transaction aware logging
		for (int i = 0; i < transactionStack.size(); i++) {
			level = level + "  ";
		}
		level = level + "DEBUG";
		log(level, msg);
	}

	private void decremenetCount(String value) {
		Integer count = valueCountMap.get(value);
		if (count != null) {
			if (count == 1) { // this was the last instance
				valueCountMap.remove(value);
			} else {
				valueCountMap.put(value, --count);
			}
		}
	}

	@Override
	public void delete(String key) {
		String currentValue = store.get(key); // get the value before removing
		trackTx(key, currentValue);
		debug("DELETE " + key + " [" + currentValue + "]");
		store.remove(key);
		this.decremenetCount(currentValue);
	}

	@Override
	public String get(String key) {
		debug("GET " + key + " = " + store.get(key));
		return store.get(key);
	}

	private void incrementCount(String value) {
		Integer count = valueCountMap.get(value);
		if (count == null) { // this is the first instance
			valueCountMap.put(value, 1);
		} else {
			valueCountMap.put(value, ++count);
		}
	}

	private void log(String type, String msg) {
		System.out.format("%s %s : %s%n", dateFormat.format(new Date()), type, msg);
	}

	@Override
	public void rollback() {
		Stack<Operation> operationStack = transactionStack.pop(); // get the innermost nested transaction
		while (!operationStack.isEmpty()) {
			Operation op = operationStack.pop();
			if (op.value == null) {
				debug("ROLLBACK DELETE " + op.key);
				store.remove(op.key);
			} else {
				debug("ROLLBACK SET " + op.key + " = " + op.value);
				store.put(op.key, op.value);
			}
		}
	}

	@Override
	public void set(String key, String value) {
		String currentValue = store.get(key);
		trackTx(key, currentValue);
		debug("SET " + key + " = " + value);
		store.put(key, value);
		if (value != null && !value.equals(currentValue)) { // only for new values
			incrementCount(value);
		}
	}

	private void trackTx(String key, String value) {
		if (!transactionStack.isEmpty()) { // if part of an open transaction
			transactionStack.peek().add(new Operation(key, value)); // update the innermost nested transaction's operation stack
		}
	}

}
