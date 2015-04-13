package com.racloop.util.domain

import org.elasticsearch.common.joda.time.DateTime
import org.hibernate.engine.SessionImplementor
import org.hibernate.id.IdentifierGenerator

import com.racloop.Journey
import com.racloop.elasticsearch.IndexNameResolver


class JourneyIdGenerator implements IdentifierGenerator  {
	private IndexNameResolver indexNameResolver
	public JourneyIdGenerator(){
		indexNameResolver = new IndexNameResolver()
	}
	
	@Override
	public Serializable generate(SessionImplementor session, Object object){
		Journey journey = (Journey)object
		return indexNameResolver.generateIndexNameFromDate(new DateTime(journey.dateOfJourney))+ IndexNameResolver.INDEX_DELIMETER +UUID.randomUUID().toString()
		
	}
	
}
