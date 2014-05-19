package data.representation.actionbased.messages.newsgroup;

import data.representation.actionbased.messages.email.EmailThread;

public class NewsgroupThread<RecipientType, MessageType extends NewsgroupPost<RecipientType>>
		extends EmailThread<RecipientType, MessageType> {

}
