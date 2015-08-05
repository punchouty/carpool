package com.racloop.promotion;


public enum PromotionEvent {

	USER_REFERAL_EVENT("User Referal Event"), 
	FEEDBACK_SUBMITTED("Feedback Submitted");

	private final String promotionEventName;

	PromotionEvent(String promotionEventName) {
		this.promotionEventName = promotionEventName;
	}

	public String getEventName() {
		return promotionEventName;
	}

	public static PromotionEvent fromString(String promotionEventName) {
		if (promotionEventName != null) {
			for (PromotionEvent event : PromotionEvent.values()) {
				if (promotionEventName
						.equalsIgnoreCase(event.promotionEventName)) {
					return event;
				}
			}
		}
		return null;
	}

}
