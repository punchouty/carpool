package com.racloop.notification.model

class EmailMessage implements Serializable{

	
	String toAddress
	String fromAddress
	String mailSubject
	String mailContent
	
	@Override
	public String toString() {
		return "EmailMessage [toAddress=" + toAddress + ", fromAddress="
				+ fromAddress + ", mailSubject=" + mailSubject + "]";
	}
}
