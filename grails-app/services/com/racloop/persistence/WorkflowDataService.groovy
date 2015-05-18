package com.racloop.persistence

import grails.transaction.Transactional

import com.racloop.domain.Journey
import com.racloop.util.date.DateUtil;

@Transactional
class WorkflowDataService {
	
	def amazonWebService;
	def searchService
	def journeyDataService;
}
