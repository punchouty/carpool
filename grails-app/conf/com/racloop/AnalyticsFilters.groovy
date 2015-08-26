package com.racloop

import org.apache.shiro.SecurityUtils;
import org.codehaus.groovy.grails.web.util.WebUtils;

class AnalyticsFilters {
	
	def jmsService

    def filters = {
        all(controller:'*', action:'*', uriExclude : '*health*') {
            before = {
				def user = SecurityUtils.getSubject()?.getPrincipal()
				String resolvedIp = getClientIpAddress(request)
				def messageMap = [
					user: user?.toString(),
					ip : resolvedIp,
					userAgent : request.getHeader("User-Agent"),
					context : request.contextPath,
					requestURI : request.requestURI,
					method : request.method,
					controllerName : controllerName,
					actionName : actionName,
					session : session?.id,
					json : request.JSON?.toString(),
					params : params?.toString(),
					queryString : request.queryString
				]
				if(!actionName.equals("setUserImage")) jmsService.send(queue: Constant.ANALYTICS_QUEUE, messageMap)
            }
            after = { Map model ->
            }
            afterView = { Exception e ->
				
            }
        }
    }
	
	private String getClientIpAddress(request) {
		String ip = request.getHeader("X-Forwarded-For")
		if(isNullOrUnknown(ip)) {
			ip = request.getHeader("Proxy-Client-IP")
		}
		if(isNullOrUnknown(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP")
		}
		if(isNullOrUnknown(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR")
		}
		if(isNullOrUnknown(ip)) {
			ip = request.getRemoteAddr()
		}

		if(ip.contains(",")) {
			ip = ip.split(",")[0]
		}

		return ip
	}
	
	private boolean isNullOrUnknown(String str) {
		(null == str || str.trim().length() == 0 || "unknown".equalsIgnoreCase(str))
	}
}
