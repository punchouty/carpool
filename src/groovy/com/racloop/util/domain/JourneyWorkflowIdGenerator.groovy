package com.racloop.util.domain

import org.elasticsearch.common.joda.time.DateTime
import org.hibernate.engine.SessionImplementor
import org.hibernate.id.IdentifierGenerator

import com.racloop.elasticsearch.IndexNameResolver
import com.racloop.workflow.JourneyWorkflow


class JourneyWorkflowIdGenerator implements IdentifierGenerator  {
	private IndexNameResolver indexNameResolver
	public JourneyWorkflowIdGenerator(){
		indexNameResolver = new IndexNameResolver()
	}

	@Override
	public Serializable generate(SessionImplementor session, Object object){
		JourneyWorkflow journeyWorkflow = (JourneyWorkflow)object
		return indexNameResolver.generateWorkflowIndexNameFromDate(new DateTime(journeyWorkflow.requestedDateTime))+ IndexNameResolver.INDEX_DELIMETER +UUID.randomUUID().toString()
	}
}
