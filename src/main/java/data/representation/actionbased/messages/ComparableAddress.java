package data.representation.actionbased.messages;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.NewsAddress;

public class ComparableAddress implements Comparable<ComparableAddress>{

	private Address address;
	
	public ComparableAddress(Address address) {
		this.address = address;
	}
	
	public Address getAddress() {
		return address;
	}

	@Override
	public int compareTo(ComparableAddress arg0) {
		return this.getComparableVal().compareTo(arg0.getComparableVal());
	}
	
	@Override
	public String toString() {
		return address.toString();
	}
	
	private String getComparableVal(Address address) {
		if (address == null) {
			return null;
		}
		if (address instanceof InternetAddress) {
			return ((InternetAddress) address).getAddress().toLowerCase();
		}
		if (address instanceof NewsAddress) {
			return ((NewsAddress) address).getNewsgroup().toLowerCase();
		}
		return address.toString().toLowerCase();
	}
	
	public String getComparableVal() {
		return getComparableVal(this.address);
	}
	
	public boolean equals(Address address) {
		if (this.address == null && address == null)   {
			return true;
		}
		if (this.address == null || address == null) {
			return false;
		}
		return getComparableVal().equals(getComparableVal(address));
		
	}
	
	public boolean equals(ComparableAddress address) {
		if (address == null) {
			return false;
		}
		return equals(address.address);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Address) {
			return equals((Address) o);
		} else if (o instanceof ComparableAddress) {
			return equals((ComparableAddress) o);
		} else {
			return false;
		}
	}
}
