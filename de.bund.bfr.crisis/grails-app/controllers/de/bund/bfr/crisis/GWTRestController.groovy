/*******************************************************************************
 * Copyright (c) 2014 Federal Institute for Risk Assessment (BfR), Germany
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.bund.bfr.crisis;

import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.web.converters.ConverterUtil
import org.grails.datastore.gorm.query.criteria.DetachedAssociationCriteria
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.types.Association;
import org.grails.datastore.mapping.query.Query
import org.springframework.dao.DataIntegrityViolationException

/**
 * @author heisea
 *
 */
abstract class GWTRestController<T> {
	protected GrailsApplication grailsApplication
	protected Class<T> type
	protected GrailsDomainClass domainClass
	protected Set<String> associationProperties
	protected Set<String> volatileProperties

	static int STATUS_VALIDATION_ERROR = -4 // com.smartgwt.client.rpc.RPCResponse.STATUS_VALIDATION_ERROR

	public GWTRestController() {
		this.type = this.class.genericSuperclass.actualTypeArguments[0]
	}

	/**
	 * Sets the grailsApplication to the specified value.
	 *
	 * @param grailsApplication the grailsApplication to set
	 */
	public void setGrailsApplication(GrailsApplication grailsApplication) {
		if (grailsApplication == null)
			throw new NullPointerException("grailsApplication must not be null");

		this.grailsApplication = grailsApplication;
		String name = ConverterUtil.trimProxySuffix(type.name)
		domainClass = grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, name)
		associationProperties = domainClass.properties.findAll { it.association }*.name.toSet()
		volatileProperties = domainClass.properties.findAll { !it.persistent }*.name.toSet()
	}

	protected respondJson(payload) {
		// retain only foreign keys if one domain class references another
		if(payload.data != null)
			payload.data = payload.data.collect { dataObject ->
				// if not instance of the type, we probably have an error message
				if(!(type.isInstance(dataObject)))
					return dataObject

				[id: dataObject.id] +
				dataObject.properties.collectEntries { key, value ->					
					if(value != null && associationProperties.contains(key)) {
						if(value instanceof Collection)
							return [(key): value*.id]
						return [(key): value.id]
					} else if(volatileProperties.contains(key))
						return [:]
					[(key): value]
				}
			}

		render(contentType: "application/json") { response = payload }
	}

	def suggest() {		
		println "suggest $params"
		
		def condition = params.find { key, value ->
			!key.startsWith('_') &&					!key.startsWith('isc_') &&
					!(key in [
						'format',
						'controller',
						'action'
					])
		}
		
		def criteria = this.type.where {
			if(params.get('_textMatchStyle') == 'exact')
				eq(condition.key, condition.value)
			else
				ilike(condition.key, condition.value + '%')
		}
		respondJson([data: criteria.findAll()*."${condition.key}"])
	}
	
	def fetch() {
		if(params.get('_operationId') == 'suggest')
			return suggest()
//		
		println "fetch $params"
		def conditions = params.findAll { key, value ->
			!key.startsWith('_') &&					!key.startsWith('isc_') &&
					!(key in [
						'format',
						'controller',
						'action'
					])
		}

		if(conditions) {
			// resolve conditions, such as id=1, station.id=2, lot.product.id=3
			def criteria = conditions.inject(this.type) { c, String field, value ->
				// station.id=2 will also be mapped to station: [id: 2], which we ignore here
				if(value instanceof Map)
					return c
				c.where { criterion ->
					PersistentEntity persistentEntity = persistentEntity
					def lastCriteria = field.split('\\.').inject(criterion) { subCriterion, subField ->
						def property = persistentEntity.getPropertyByName(subField)
						if(property instanceof Association) {
							persistentEntity = property.getAssociatedEntity()
							def dac = new DetachedAssociationCriteria(property.associatedEntity.javaClass, property)
							subCriterion.add(dac)																				
							return dac
						}
						subCriterion.eq(subField, value)
					}
					if(!(lastCriteria instanceof Query.Equals))
						lastCriteria = lastCriteria.eq('id', value)
				}
			}
			
			respondJson([data: criteria.findAll()])
		}
		else
			respondJson([data: this.type.list([min: params._startRow, max: params._endRow])])
	}

	def update() {
		println "update $params"
		T instance = params.containsKey('id') ? type.findById(params.id) : type.newInstance()
		instance.properties = params
		if (instance.validate(deepValidate: false)) {
			instance.save(validate: false, flush: true)
			respondJson([data: [instance]])
		} else
			respondJson([status: STATUS_VALIDATION_ERROR, errors: instance.errors])
	}

	def add() {
		println "add $params"
		T instance = type.newInstance()
		instance.properties = params
		respondJson([data: [instance]])
	}

	def remove() {
		println "remove $params"
		T instance = type.findById(params.id)
		try {
			instance.delete(flush:true)
			respondJson([data: [[id: params.id]]])
		} catch(DataIntegrityViolationException e) {
			respondJson([status: STATUS_VALIDATION_ERROR, errors: 'This record is directly or indirectly referenced by another record'])
		} catch(e) {
			respondJson([status: STATUS_VALIDATION_ERROR, errors: e.message])
		}
	}
}
