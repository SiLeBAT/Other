package de.bund.bfr.knime.krise;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class CombinatorialIterator implements Iterator<List<MyChain>>, Iterable<List<MyChain>> {
	 
    private int[] indices;
    private final int[] maxIndices;
    private final MyChain[] elements;

    public CombinatorialIterator(List<MyChain> items, int number) {
        if (items == null) throw new IllegalArgumentException("items is null");
        if (number < 1) throw new IllegalArgumentException("number < 1");
        if (items.size() < number) throw new IllegalArgumentException("items.size < " + number);
        elements = items.toArray(new MyChain[]{});
        indices = new int[number];
        maxIndices = new int[number];
        for (int i=0; i<number; i++) {
            indices[i] = i;
            maxIndices[i] = elements.length+i-indices.length;
        }       
    }
    
    public Iterator<List<MyChain>> iterator() {
    	return this;
    }

    public boolean hasNext() {
        return indices != null;
    }

    private List<MyChain> createFromIndices() {
        List<MyChain> result = new ArrayList<MyChain>(indices.length*2);
        for (int i=0; i<indices.length; i++) {
            result.add(elements[indices[i]]);
        }
        return result;
    }

    private void incrementIndices() {
        if (indices[0]==maxIndices[0]) {
            indices = null;
            return;
        }
        for (int i=indices.length-1;i>=0;i--) {
            if (indices[i]!=maxIndices[i]) {
                int val = ++indices[i];
                for (int j=i+1; j<indices.length; j++) {
                    indices[j] = ++val;
                }
                break;
            }
        }
    }

    public List<MyChain> next() {
        if (indices==null) {
            throw new NoSuchElementException("End of iterator");
        }
        List<MyChain> result = createFromIndices();
        incrementIndices();
        return result;
    }

    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}