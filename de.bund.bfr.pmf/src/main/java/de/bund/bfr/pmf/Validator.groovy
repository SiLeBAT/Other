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
package de.bund.bfr.pmf

import org.apache.log4j.Level

/**
 * 
 */
class Validator {
	static void main(String[] args) {
		def reader = new PMFReader(validating: true)
		switch(args.size()) {
		case 0:
			println "Usage: ${this.class.simpleName} file1 ... fileN"
			System.exit(1)
		case 1:
			if(args[0] ==~ /.*(?:\.zip|\.pmf)/) {
				reader.read(args[0])
				break
			}
		default:
			reader.readFileSet(args)
		}
		
		println "The provided file${args.size() > 1 ? '(s)' : ''} are ${reader.getParseMessages(Level.ERROR) ? 'NOT ' : ''}valid"
		println "Detailed comments (including warnings): "
		println reader.parseMessages.join('\n')
		println "<Press any key to close>"
		System.in.read()
	}
}
