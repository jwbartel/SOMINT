package util.tools.io;

import javax.mail.Address;
import javax.mail.internet.NewsAddress;

import data.representation.actionbased.messages.ComparableAddress;

public class ComparableAddressValueParser implements ValueParser<ComparableAddress>{

	@Override
	public ComparableAddress parse(String str) {
		Address address = new NewsAddress(str);
		return new ComparableAddress(address);
	}

}
