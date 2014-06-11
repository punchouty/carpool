package com.racloop.journey.workkflow


enum WorkflowState {
	INITIATED("New"),
	ACCEPTED("Accepted"),
	CANCELLED("Cancelled"),
	REJECTED("Rejected")

	WorkflowState(String state) {
		this.state = state
	}

	private final String state
	private static final Map<String,WorkflowState> lookup= new HashMap<String,WorkflowState>()
	
	static {
		for(WorkflowState s : EnumSet.allOf(WorkflowState.class))
			lookup.put(s.getState(), s);
	}
	
	public String getState() {
		return state
	}
	
	public static WorkflowState get(String state) {
		return lookup.get(state);
	}
	
	public static List getAction (String state) {
		def action=[]
		WorkflowState workflowstate = this.get(state)
		switch(workflowstate) {
			case INITIATED: action=["Accept", "Reject"]
				break
			case ACCEPTED: action=["Cancel"]
				break
			case CANCELLED: action =[]
				break
			case REJECTED: action=[]
				break
		}
		return action
	}
	
}
