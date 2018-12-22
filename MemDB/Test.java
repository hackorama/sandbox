/**
 * 
 * Tests for MemDB operations  including nested transactions and roll backs
 *
 */
class Test {

	public void test() {
		testSet();
		testBoundaries();
		testGet();
		testDelete();
		testCount();
		testCountMultipleAdd();
		testCountDelete();
		testCountMultipleDelete();
		testTransaction();
		testRollback();
		testNestedTransaction();
		testNestedTransactionRollback();
	}

	private void testBoundaries() {
		System.out.println();
		System.out.println("Test boundary conditions\n");
		System.out.println();

		MemDB memDB = MemDB.getInstance();
		memDB.clear();
		memDB.set("", "");
		memDB.set(" ", " ");
		memDB.set(null, "");
		memDB.set(null, null);
		memDB.set("", null);
		memDB.set(null, " ");
		memDB.set(null, null);
		memDB.set(" ", null);
		memDB.get(null);
		memDB.get("");
		memDB.get(" ");
		memDB.clear();
		memDB.get(null);
		memDB.get("");
		memDB.delete("");
		memDB.delete(" ");
		memDB.delete(null);
		memDB.clear();
	}

	public void testCount() {
		System.out.println();
		System.out.println("Test VALUE COUNT\n");
		System.out.println();

		MemDB memDB = MemDB.getInstance();
		memDB.clear();
		memDB.set("A", "1");
		memDB.set("B", "2");
		memDB.set("C", "2");
		memDB.set("D", "3");
		memDB.set("E", "3");
		memDB.set("F", "3");
		assert (memDB.countValue("1") == 1);
		assert (memDB.countValue("2") == 2);
		assert (memDB.countValue("3") == 3);
		assert (memDB.countValue("INVALID") == 0);
		memDB.clear();
	}

	public void testCountDelete() {
		System.out.println();
		System.out.println("Test VALUE COUNT with DELETE\n");
		System.out.println();

		MemDB memDB = MemDB.getInstance();
		memDB.clear();
		memDB.set("A", "1");
		memDB.delete("A");
		memDB.set("B", "1");
		assert (memDB.countValue("1") == 1);
		memDB.clear();
	}

	public void testCountMultipleAdd() {
		System.out.println();
		System.out.println("Test VALUE COUNT with multiple SET\n");
		System.out.println();

		MemDB memDB = MemDB.getInstance();
		memDB.clear();
		memDB.set("A", "1");
		memDB.set("A", "1");
		memDB.set("A", "1");
		assert (memDB.countValue("1") == 1);
		memDB.clear();
	}

	public void testCountMultipleDelete() {
		System.out.println();
		System.out.println("Test VALUE COUNT with multiple DELETE\n");
		System.out.println();

		MemDB memDB = MemDB.getInstance();
		memDB.clear();
		memDB.set("A", "1");
		memDB.delete("A");
		memDB.delete("A");
		assert (memDB.countValue("1") == 0);
		memDB.set("B", "1");
		memDB.set("C", "1");
		memDB.set("D", "1");
		memDB.delete("B");
		memDB.delete("D");
		assert (memDB.countValue("1") == 1);
		memDB.clear();
	}

	public void testDelete() {
		System.out.println();
		System.out.println("Test DELETE");
		System.out.println();

		MemDB memDB = MemDB.getInstance();
		memDB.clear();
		memDB.set("ONE", "1");
		memDB.set("TWO", "2");
		memDB.set("THREE", "3");
		memDB.get("ONE").equals("1");
		assert (memDB.countValue("1") == 1);
		memDB.delete("ONE");
		assert (memDB.get("ONE") == null);
		assert (memDB.countValue("1") == 0);
		memDB.delete("INVALID"); // test no side effect
		memDB.clear();
	}

	public void testGet() {
		System.out.println();
		System.out.println("Test GET");
		System.out.println();

		MemDB memDB = MemDB.getInstance();
		memDB.clear();
		memDB.set("ONE", "1");
		memDB.get("ONE").equals("1");
		assert (memDB.get("INVALID") == null);
		memDB.clear();
	}

	public void testNestedTransaction() {
		System.out.println();
		System.out.println("Test nested COMMIT\n");
		System.out.println();

		MemDB memDB = MemDB.getInstance();
		memDB.clear();
		memDB.set("A", "0");
		assert (memDB.get("A").equals("0"));
		memDB.begin();
		assert (memDB.get("A").equals("0"));
		assert (memDB.get("B") == null);
		assert (memDB.get("C") == null);
		memDB.set("A", "1");
		assert (memDB.get("A").equals("1"));
		assert (memDB.get("B") == null);
		assert (memDB.get("C") == null);
		memDB.begin();
		assert (memDB.get("A").equals("1"));
		assert (memDB.get("B") == null);
		assert (memDB.get("C") == null);
		memDB.set("C", "3");
		assert (memDB.get("A").equals("1"));
		assert (memDB.get("B") == null);
		assert (memDB.get("C").equals("3"));
		memDB.commit();
		assert (memDB.get("A").equals("1"));
		assert (memDB.get("B") == null);
		assert (memDB.get("C").equals("3"));
		memDB.set("B", "2");
		assert (memDB.get("A").equals("1"));
		assert (memDB.get("B").equals("2"));
		assert (memDB.get("C").equals("3"));
		memDB.commit();
		assert (memDB.get("A").equals("1"));
		assert (memDB.get("B").equals("2"));
		assert (memDB.get("C").equals("3"));
		memDB.clear();
	}

	public void testNestedTransactionRollback() {
		System.out.println();
		System.out.println("Test nested ROLLBACK\n");
		System.out.println();

		MemDB memDB = MemDB.getInstance();
		memDB.clear();
		memDB.set("A", "0");
		assert (memDB.get("A").equals("0"));
		memDB.begin();
		memDB.set("A", "1");
		assert (memDB.get("A").equals("1"));
		memDB.set("C", "3");
		assert (memDB.get("C").equals("3"));
		memDB.begin();
		memDB.set("C", "33");
		assert (memDB.get("C").equals("33"));
		memDB.rollback();
		assert (memDB.get("C").equals("3"));
		memDB.set("B", "2");
		assert (memDB.get("B").equals("2"));
		memDB.commit();
		assert (memDB.get("A").equals("1"));
		assert (memDB.get("B").equals("2"));
		assert (memDB.get("C").equals("3"));
		memDB.clear();
	}

	public void testRollback() {
		System.out.println();
		System.out.println("Test ROLLBACK\n");
		System.out.println();

		MemDB memDB = MemDB.getInstance();
		memDB.clear();
		memDB.set("A", "0");
		memDB.begin();
		assert (memDB.get("A").equals("0"));
		assert (memDB.get("B") == null);
		memDB.set("A", "1");
		assert (memDB.get("A").equals("1"));
		memDB.set("B", "2");
		assert (memDB.get("B").equals("2"));
		memDB.rollback();
		;
		assert (memDB.get("A").equals("0"));
		assert (memDB.get("B") == null);
		memDB.clear();
	}

	public void testSet() {
		System.out.println();
		System.out.println("Test SET");
		System.out.println();

		MemDB memDB = MemDB.getInstance();
		memDB.clear();
		memDB.set("ONE", "1");
		memDB.set("TWO", "1");
		memDB.set("THREE", "1");
		memDB.clear();
	}

	public void testTransaction() {
		System.out.println();
		System.out.println("Test COMMIT\n");
		System.out.println();

		MemDB memDB = MemDB.getInstance();
		memDB.clear();
		memDB.set("A", "0");
		assert (memDB.get("A").equals("0"));
		memDB.begin();
		assert (memDB.get("A").equals("0"));
		memDB.set("A", "1");
		assert (memDB.get("A").equals("1"));
		assert (memDB.get("B") == null);
		memDB.set("B", "2");
		assert (memDB.get("B").equals("2"));
		memDB.commit();
		assert (memDB.get("A").equals("1"));
		assert (memDB.get("B").equals("2"));
		memDB.clear();
	}

}
