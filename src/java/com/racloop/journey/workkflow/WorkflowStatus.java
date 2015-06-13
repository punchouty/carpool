package com.racloop.journey.workkflow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;



public enum WorkflowStatus {
	
	NONE("None"),
	REQUESTED("Requested"),
	REQUEST_RECIEVED("Request Recieved"),
	ACCEPTED("Accepted"),
	REJECTED("Rejected"),
	REJECTED_BY_ME("Rejected by Me"),
	CANCELLED("Cancelled"),
	CANCELLED_BY_REQUESTER("Cancelled by Requester"),
	INHERITED("Inherited"),
	DELEGATED("Delegated"),
	CANCELLED_BY_OTHER("Cancelled by Other"),
	CANCELLED_BY_ME("Cancelled by Me"),
	FORCED_CANCELLED("Forced Cancelled");

	private final String status;
	private static final String [] NONE_ACTIONS = {};
	private static final String [] REQUESTED_ACTIONS = {WorkflowAction.CANCEL.getAction()};
	private static final String [] REQUEST_RECIEVED_ACTIONS = {WorkflowAction.ACCEPT.getAction(), WorkflowAction.REJECT.getAction()};
	private static final String [] ACCEPTED_ACTIONS = {WorkflowAction.CANCEL.getAction()};
	private static final String [] REJECTED_ACTIONS = {};
	private static final String [] REJECTED_BY_ME_ACTIONS = {};
	private static final String [] CANCELLED_ACTIONS = {};
	private static final String [] CANCELLED_BY_REQUESTER_ACTION = {};
	private static final String [] CANCELLED_BY_ME_ACTION = {};
	private static final String [] CANCELLED_BY_OTHER_ACTION = {};
	private static final String [] INHERITED_ACTIONS = {};
	private static final String [] DELEGATED_ACTIONS = {};
	private static final String [] FORCED_CANCELLED_ACTIONS = {};
	private static final HashMap<WorkflowStatus, String []> statusToActionMapping = new HashMap<WorkflowStatus, String []>();
	private static final List<WorkflowStatus> ignoreableStatus = Arrays.asList(CANCELLED, CANCELLED_BY_ME, REJECTED, REJECTED_BY_ME, CANCELLED_BY_OTHER, FORCED_CANCELLED);
	private static final List<WorkflowStatus> indirectStatus = Arrays.asList(DELEGATED, INHERITED);
	
	static {
		statusToActionMapping.put(WorkflowStatus.NONE, NONE_ACTIONS);
		statusToActionMapping.put(WorkflowStatus.REQUESTED, REQUESTED_ACTIONS);
		statusToActionMapping.put(WorkflowStatus.REQUEST_RECIEVED, REQUEST_RECIEVED_ACTIONS);
		statusToActionMapping.put(WorkflowStatus.ACCEPTED, ACCEPTED_ACTIONS);
		statusToActionMapping.put(WorkflowStatus.REJECTED, REJECTED_ACTIONS);
		statusToActionMapping.put(WorkflowStatus.REJECTED_BY_ME, REJECTED_BY_ME_ACTIONS);
		statusToActionMapping.put(WorkflowStatus.CANCELLED, CANCELLED_ACTIONS);
		statusToActionMapping.put(WorkflowStatus.INHERITED, INHERITED_ACTIONS);
		statusToActionMapping.put(WorkflowStatus.DELEGATED, DELEGATED_ACTIONS);
		statusToActionMapping.put(WorkflowStatus.CANCELLED_BY_REQUESTER, CANCELLED_BY_REQUESTER_ACTION);
		statusToActionMapping.put(WorkflowStatus.CANCELLED_BY_ME, CANCELLED_BY_ME_ACTION);
		statusToActionMapping.put(WorkflowStatus.CANCELLED_BY_OTHER, CANCELLED_BY_OTHER_ACTION);
		statusToActionMapping.put(WorkflowStatus.FORCED_CANCELLED, FORCED_CANCELLED_ACTIONS);
		
	}

	WorkflowStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
	
	public String[] getActions() {
		return statusToActionMapping.get(this);
	}

	public static WorkflowStatus fromString(String status) {
		if (status != null) {
			for (WorkflowStatus b : WorkflowStatus.values()) {
				if (status.equalsIgnoreCase(b.status)) {
					return b;
				}
			}
		}
		return null;
	}

	public static boolean canBeIgnored(String status){
		WorkflowStatus workflowstate = fromString(status);
		return ignoreableStatus.contains(workflowstate);
	}
	
	public static boolean isIndirectStatus(String status){
		WorkflowStatus workflowstate = fromString(status);
		return indirectStatus.contains(workflowstate);
	}
}
