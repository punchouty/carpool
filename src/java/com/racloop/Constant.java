package com.racloop;

public interface Constant {
	
	public static final String HISTORY_QUEUE = "msg.history.queue";
	public static final String NOTIFICATION_WORKFLOW_STATE_CHANGE_QUEUE = "msg.notification.workflow.state.change.queue";
	public static final String MOBILE_VERIFICATION_QUEUE = "msg.notification.mobile.verification.queue";
	public static final String MOBILE_SOS_QUEUE = "msg.notification.mobile.sos.queue";
	public static final String NOTIFICATION_EMAIL_QUEUE = "msg.email.notification.queue";
	public static final String NOTIFICATION_SMS_QUEUE = "msg.sms.notification.queue";
	public static final String NOTIFICATION_MOBILE_PUSH_QUEUE = "msg.push.notification.queue";
	public static final String NOTIFICATION_MOBILE_FORGOT_PASSWORD_QUEUE = "msg.sms.mobile.forgot.password";
	public static final String NOTIFICATION_PROMOTION_MESSAGE_QUEUE = "msg.notification.promotion.queue";
	public static final String ANALYTICS_QUEUE = "msg.notification.analytic.queue";
	public static final String NOTIFICATION_AUTOMATCH_MESSAGE_QUEUE = "msg.notification.autoMatch.queue";
	
	public static final String MOBILE_KEY = "mobile";
	public static final String EMAIL_KEY = "email";
	public static final String SOS_NAME_KEY = "sosName";
	public static final String JOURNEY_IDS_KEY = "journeyIds";
	public static final String EMERGENCY_CONTACT_ONE_KEY = "emergencyContactOne";
	public static final String EMERGENCY_CONTACT_TWO_KEY = "emergencyContactTwo";

	public static final String SOS_KEY = "sos";
	public static final String SOS_USER_KEY = "sosUser";
	public static final String SOS_ADMIN_KEY = "sosAdmin";
	
	public static final String AUTO_MATCH_KEY = "autoMatch";
	
	public static final String SOS_USER_LATITUDE = "sosUserLatitude";
	public static final String SOS_USER_LONGITUDE = "sosUserLongitude";
	public static final String VERIFICATION_CODE_KEY = "verificationCode";
	public static final String NEW_PASSWORD_KEY = "newPassword";
	
	public static final String WORKFLOW_ID_KEY = "workflowIdKey";
	public static final String WORKFLOW_STATE_KEY = "workflowStateKey";
	public static final String JOURNEY_WORKFLOW_SOURCE_ID = "journeyWorkflowSourceId";
	public static final String JOURNEY_WORKFLOW_TARGET_ID = "journeyWorkflowTargetId";

	
	public static final String GRAVATAR_URL = "http://www.gravatar.com/avatar/";
	public static final String GRAVATAR_URL_SUFFIX = "?s=200&d=mm";
	public static final String FACEBOOK_URL = "http://graph.facebook.com/";
	public static final String PICTURE = "/picture";
	
	public static final String DATE_FORMAT_DYNAMODB = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";//"yyyy-MM-dd HH:mm:ss.u Z";
	public static final String DATE_FORMAT_UI = "yyyy-MM-dd'T'HH:mm:ssXXX";
	public static final String DATE_FORMAT_ES = "yyyyMMdd'T'HHmmssZ";
	
	public static final String DUMMY_USER_MOBILE = "9800000000";
	public static final String DUMMY_USER_MAIL = "dummy@racloop.com";
	
	public static final String LOGIN_ON_FLY = "LoginOnFly";
	
	public static final String DEFAULT_PASSWORD = "P@ssw0rd";
}
