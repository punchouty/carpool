package com.racloop.promotion

import com.racloop.Constant;
import com.racloop.domain.LoginDetail;
import com.racloop.notification.promotion.model.PromotionMessage;

import grails.plugin.jms.Queue
import grails.transaction.Transactional

class PromotionMessageProcessorService {
	static exposes = ['jms']
	
	@Queue(name = "msg.notification.promotion.queue")
    def processMessage(def promotionMessage) {
		log.info "Received message with promotionMessage ${promotionMessage}"
		PromotionMessage message = promotionMessage
		switch(message.promotionEvent) {
			case PromotionEvent.USER_REFERAL_EVENT :
				processUserReferalEvent(message)
			break 
			
			case PromotionEvent.FEEDBACK_SUBMITTED :
				processFeedbackSubmittedEvent(message)
			break
			
			default :
				log.error "Unknown message type recieved ${message}"
		}
    }
	
	private void processUserReferalEvent(PromotionMessage message) {
		log.info "User referal event. Message recieved is ${message}"
		PromotionMetaData promotionMetaData = PromotionMetaData.findByPromotionEvent(message.promotionEvent)
		if(promotionMetaData?.isPromotionValid(new Date())) {
			String userName = message.messageData.get('benificaryUserName')
			log.info "Referal code for user ${userName} is used"
		}
		
	}
	
	private void processFeedbackSubmittedEvent(PromotionMessage message) {
		log.info "User feedback submitted. Message recieved is ${message}"
	}
	

}
