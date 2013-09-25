package ResImpl;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;

/**
 * A subset of a resource manager. Handles customers only.
 * Add/remove/query customers, and set reservations in the customer objects.
 * 
 * @author Jake Shamash
 * 
 */
public class CustomerResourceManager {

	protected RMHashtable m_itemHT = new RMHashtable();

	// Reads a data item
	private RMItem readData(int id, String key) {
		synchronized (m_itemHT) {
			return (RMItem) m_itemHT.get(key);
		}
	}

	// Writes a data item
	private void writeData(int id, String key, RMItem value) {
		synchronized (m_itemHT) {
			m_itemHT.put(key, value);
		}
	}

	// Remove the item out of storage
	protected RMItem removeData(int id, String key) {
		synchronized (m_itemHT) {
			return (RMItem) m_itemHT.remove(key);
		}
	}

	// deletes the entire item
	protected boolean deleteItem(int id, String key) {
		Trace.info("RM::deleteItem(" + id + ", " + key + ") called");
		ReservableItem curObj = (ReservableItem) readData(id, key);
		// Check if there is such an item in the storage
		if (curObj == null) {
			Trace.warn("RM::deleteItem(" + id + ", " + key
					+ ") failed--item doesn't exist");
			return false;
		} else {
			if (curObj.getReserved() == 0) {
				removeData(id, curObj.getKey());
				Trace.info("RM::deleteItem(" + id + ", " + key
						+ ") item deleted");
				return true;
			} else {
				Trace.info("RM::deleteItem("
						+ id
						+ ", "
						+ key
						+ ") item can't be deleted because some customers reserved it");
				return false;
			}
		} // if
	}

	public int newCustomer(int id) throws RemoteException {
		Trace.info("INFO: RM::newCustomer(" + id + ") called");
		// Generate a globally unique ID for the new customer
		int cid = Integer.parseInt(String.valueOf(id)
				+ String.valueOf(Calendar.getInstance().get(
						Calendar.MILLISECOND))
				+ String.valueOf(Math.round(Math.random() * 100 + 1)));
		Customer cust = new Customer(cid);
		writeData(id, cust.getKey(), cust);
		Trace.info("RM::newCustomer(" + cid + ") returns ID=" + cid);
		return cid;
	}

	public boolean newCustomer(int id, int customerID) throws RemoteException {
		Trace.info("INFO: RM::newCustomer(" + id + ", " + customerID
				+ ") called");
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
		} // else
	}

	public boolean deleteCustomer(int id, int customerID)
			throws RemoteException {
		Trace.info("RM::deleteCustomer(" + id + ", " + customerID + ") called");
		Customer cust = (Customer) readData(id, Customer.getKey(customerID));
		if (cust == null) {
			Trace.warn("RM::deleteCustomer(" + id + ", " + customerID
					+ ") failed--customer doesn't exist");
			return false;
		} else {
			// Increase the reserved numbers of all reservable items which the
			// customer reserved.
			RMHashtable reservationHT = cust.getReservations();
			for (Enumeration e = reservationHT.keys(); e.hasMoreElements();) {
				String reservedkey = (String) (e.nextElement());
				ReservedItem reserveditem = cust.getReservedItem(reservedkey);
				Trace.info("RM::deleteCustomer(" + id + ", " + customerID
						+ ") has reserved " + reserveditem.getKey() + " "
						+ reserveditem.getCount() + " times");
				ReservableItem item = (ReservableItem) readData(id,
						reserveditem.getKey());
				Trace.info("RM::deleteCustomer(" + id + ", " + customerID
						+ ") has reserved " + reserveditem.getKey()
						+ "which is reserved" + item.getReserved()
						+ " times and is still available " + item.getCount()
						+ " times");
				item.setReserved(item.getReserved() - reserveditem.getCount());
				item.setCount(item.getCount() + reserveditem.getCount());
			}

			// remove the customer from the storage
			removeData(id, cust.getKey());

			Trace.info("RM::deleteCustomer(" + id + ", " + customerID
					+ ") succeeded");
			return true;
		} // if
	}

	public String queryCustomerInfo(int id, int customerID)
			throws RemoteException {
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
		Customer cust = (Customer) readData(id, Customer.getKey(customerID));
		if (cust == null) {
			Trace.warn("CRM::reserveCustomer( " + id + ", " + customerID + ", "
					+ location + ")  failed--customer doesn't exist");
			return false;
		}

		cust.reserve(key, location, price);
		writeData(id, cust.getKey(), cust);
		return true;
	}
}
