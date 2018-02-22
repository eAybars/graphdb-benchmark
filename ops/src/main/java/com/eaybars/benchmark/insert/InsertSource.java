package com.eaybars.benchmark.insert;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

public class InsertSource implements Serializable {
    private File file;
    private int numberOfLines;

    private InsertSource(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public boolean exists() {
        return file.exists();
    }

    public int getNumberOfLines() {
        return numberOfLines;
    }

    public InputStream inputStream() throws IOException {
        try {
            return new GZIPInputStream(new FileInputStream(file), 4096);
        } catch (ZipException e) {
            return new FileInputStream(file);
        }
    }

    private void calculateNumberOfLines() throws IOException {
        try (InputStream is = inputStream()) {
            byte[] c = new byte[1024];
            int readChars;
            boolean charsAfterNewLine = false;
            while ((readChars = is.read(c)) != -1) {
                charsAfterNewLine = true;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        charsAfterNewLine = false;
                        numberOfLines++;
                    }
                }
            }
            if (charsAfterNewLine) {
                numberOfLines++;
            }
        }
    }

    public static InsertSource from(String target) throws IOException {
        InsertSource is = new InsertSource(new File(target));
        if (is.exists()) {
            is.calculateNumberOfLines();
        }
        return is;
    }
}
