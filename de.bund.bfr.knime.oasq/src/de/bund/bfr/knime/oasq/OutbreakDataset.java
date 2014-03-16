package de.bund.bfr.knime.oasq;

import java.util.ArrayList;

import org.knime.core.data.DataRow;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.node.BufferedDataTable;

public class OutbreakDataset {
	ArrayList<Integer> mOutbreaks;
	int mNumCases;
	
	public OutbreakDataset(BufferedDataTable outbreak_data) {
		mOutbreaks = new ArrayList<Integer>();
		
		CloseableRowIterator outbreakIterator = outbreak_data.iterator();
		
		mNumCases = 0;
		while(outbreakIterator.hasNext()){
			DataRow currentRow = outbreakIterator.next();
			
			int regCases = Integer.parseInt(currentRow.getCell(0).toString());
			for(int i=0; i < regCases; i++) {
				mOutbreaks.add(Integer.parseInt(currentRow.getKey().toString()));
				mNumCases++;
			}
		}
	}
	
	public int getNumCases() {
		return mNumCases;
	}
	public Integer[] getOutbreaks() {
		Integer[] outbreaks = new Integer[mOutbreaks.size()];
		return mOutbreaks.toArray(outbreaks);
	}
	
	
}
