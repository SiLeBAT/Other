package de.bund.bfr.busstopp.util;

import java.io.*;
import java.util.zip.*;

public class ZipArchive {

    // 4MB buffer
    private static final byte[] BUFFER = new byte[4096 * 1024];

    private ZipOutputStream zipStream = null;
	
    public ZipArchive(String zipfilename) throws Exception {
    	zipStream = new ZipOutputStream(new FileOutputStream(zipfilename));
    }
    
    public void add(File file, Long id) throws Exception {
        FileInputStream in = new FileInputStream(file.getAbsolutePath());
        zipStream.putNextEntry(new ZipEntry(id + "_" + file.getName())); 
        copy(in, zipStream);
        zipStream.closeEntry();
        in.close();
    }
    
    public void close() throws IOException {
    	zipStream.close();
    }
    
    private void copy(InputStream input, OutputStream output) throws IOException {
        int bytesRead;
        while ((bytesRead = input.read(BUFFER))!= -1) {
            output.write(BUFFER, 0, bytesRead);
        }
    }
}