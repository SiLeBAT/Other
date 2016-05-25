/*******************************************************************************
 * Copyright (c) 2016 Federal Institute for Risk Assessment (BfR), Germany
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Department Biological Safety - BfR
 *******************************************************************************/
package de.bund.bfr.fixcsv

import java.nio.charset.StandardCharsets;
import java.nio.file.Files

class FixCsv {

	static String FILE_NAME = "G:/Abteilung-4/Antibiotikagabe/vetproof/data/160201/Schwein/nicht-behandelte_Quartale_2016-05-20T11-51-27.csv"
	static String SEPERATOR = ";"

	static main(args) {
		def oldFile = new File(FILE_NAME)
		def newFile = new File(FILE_NAME.take(FILE_NAME.lastIndexOf('.')) + "_fixed.csv")
		def text = oldFile.getText('utf-8').replaceAll(/"[^;"]+"/,
				{ it.replace("\"","\"\"") }).replaceAll(/[^;"]"[^;"]/,
				{ it.replace("\"","\"\"") })

		newFile.write(text, 'utf-8')		
	}
}
