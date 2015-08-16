package com.racloop.staticdata

class StaticdataService {

    public String getSaticValueBasedOnKey(String staticDataKey) {
		StaticData data  = StaticData.find {staticDataKey == staticDataKey}
		return data?.pageData
		
	}
	
	public boolean canRunJob() {
		RacloopConfig config = RacloopConfig.find {configKey == RacloopConfig.SCHEDULER_RUNNER_KEY}
		String ipAddress = InetAddress.localHost.getHostAddress()
		return ipAddress == config.configValue
	}
}
