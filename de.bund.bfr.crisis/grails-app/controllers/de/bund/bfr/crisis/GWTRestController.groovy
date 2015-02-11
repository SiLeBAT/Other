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

import javax.management.InstanceOfQueryExp;
import javax.persistence.Persistence;

import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.converters.ConverterUtil
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.spi.PersistentCollection;

/**
 * @author heisea
 *
 */
abstract class GWTRestController<T> {
	GrailsApplication grailsApplication
	Class<T> type

	public GWTRestController() {
		this.type = this.class.genericSuperclass.actualTypeArguments[0]
	}

	def respondJson(dataObjects) {
		render(contentType: "application/json") {
			response = {
				status = 0
				data = dataObjects.collect { dataObject ->
					[id: dataObject.id] + 
					dataObject.properties.collectEntries { key, value ->
						if(value instanceof PersistentCollection) {
							return [(key): value*.id]
						}
						if(value != null) {
							String name = ConverterUtil.trimProxySuffix(value.class.name)
							if(grailsApplication.isArtefactOfType(DomainClassArtefactHandler.TYPE, name))
								return [(key): value.id]
						}
						[(key): value]
					}
				}
			}
		}
	}

	def fetch() {
		println params
		if(params.id)
			respondJson([
				this.type.findById(params.id)
			])
		else
			respondJson(this.type.list([min: params._startRow, max: params._endRow]))
	}

	def save(T instance) {
		def project = projectService.save(params)
		render(contentType: 'application/xml') {
			response() {
				status(0)
				data {
					record {
						id(project.id)
						name(project.name)
						title(project.title)
						description(project.description)
						isPublic(project.isPublic)
					}
				}
			}
		}
	}

	def remove(T instance) {
		//		stationInstance.delete flush:true
		respondJson([id: params.id])
	}
}
