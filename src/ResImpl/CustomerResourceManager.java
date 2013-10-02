package ResImpl;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;

/**
 * A subset of a resource manager. Handles customers only. Add/remove/query
 * customers, and set reservations in the customer objects.
 * 
 * @author Jake Shamash
 * 
 */
public class CustomerResourceManager {

	protected RMHashtable m_itemHT = new RMHashtable();

	// Reads a data item
	private RMItem readData(int id, String key) {
		return (RMItem) m_itemHT.get(key);
	}

	// Writes a data item
	private void writeData(int id, String key, RMItem value) {
		m_itemHT.put(key, value);
	}

	// Remove the item out of storage
	protected RMItem removeData(int id, String key) {
		return (RMItem) m_itemHT.remove(key);
	}

	public int newCustomer(int id) throws RemoteException {
		Trace.info("INFO: RM::newCustomer(" + id + ") called");
		// Generate a globally unique ID for the new customer
		int cid = Integer.parseInt(String.valueOf(id)
				+ String.valueOf(Calendar.getInstance().get(
						Calendar.MILLISECOND))
				+ String.valueOf(Math.round(Math.random() * 100 + 1)));
		Customer cust = new Customer(cid);
		synchronized (m_itemHT) {
			writeData(id, cust.getKey(), cust);
		}
		Trace.info("RM::newCustomer(" + cid + ") returns ID=" + cid);
		return cid;
	}

	public boolean newCustomer(int id, int customerID) {
		Trace.info("INFO: RM::newCustomer(" + id + ", " + customerID
				+ ") called");
		// So that no one creates a customer at the same time.
		synchronized (m_itemHT) {
			Customer cust = (Customer) readData(id, Customer.getKey(customerID));
			if (cust == null) {
				cust = new Customer(customerID);
				writeData(id, cust.getKey(), cust);
				Trace.info("INFO: RM::newCustomer(" + id + ", " + customerID
						+ ") created a new customer");
				return true;
			} else {
				Trace.info("INFO: RM::newCustomer(" + id + ", " + customerID
						+ ") failed--customer already exists");
				return false;
			}
		}
	}

	public Customer getCustomer(int id, int customerID) {
		return (Customer) readData(id, Customer.getKey(customerID));
	}

	public boolean deleteCustomer(int id, int customerID) {
		Trace.info("RM::deleteCustomer(" + id + ", " + customerID + ") called");
		// Wouldn't want to delete while someone is, say, making a reservation
		// with it.
		synchronized (m_itemHT) {
			Customer cust = (Customer) readData(id, Customer.getKey(customerID));
			if (cust == null) {
				Trace.warn("RM::deleteCustomer(" + id + ", " + customerID
						+ ") failed--customer doesn't exist");
				return false;
			} else {
				removeData(id, cust.getKey());
				Trace.info("RM::deleteCustomer(" + id + ", " + customerID
						+ ") succeeded");
				return true;
			}
		}
	}

	public String queryCustomerInfo(int id, int customerID) {
		Trace.info("RM::queryCustomerInfo(" + id + ", " + customerID
				+ ") called");
		Customer cust = (Customer) readData(id, Customer.getKey(customerID));
		if (cust == null) {
			Trace.warn("RM::queryCustomerInfo(" + id + ", " + customerID
					+ ") failed--customer doesn't exist");
			return ""; // NOTE: don't change this--WC counts on this value
						// indicating a customer does not exist...
		} else {
			String s = cust.printBill();
			Trace.info("RM::queryCustomerInfo(" + id + ", " + customerID
					+ "), bill follows...");
			System.out.println(s);
			return s;
		}
	}

	public boolean reserveCustomer(int id, int customerID, String key,
			String location, int price) {
		// Read customer object if it exists (and read lock it)
		synchronized (m_itemHT) {
			Customer cust = (Customer) readData(id, Customer.getKey(customerID));
			if (cust == null) {
				Trace.warn("CRM::reserveCustomer( " + id + ", " + customerID
						+ ", " + location + ")  failed--customer doesn't exist");
				return false;
			}

			cust.reserve(key, location, price);
			writeData(id, cust.getKey(), cust);
		}
		return true;
	}
}
