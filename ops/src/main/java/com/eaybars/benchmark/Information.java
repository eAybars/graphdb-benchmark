package com.eaybars.benchmark;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public enum Information {

    TEMPORARY(temp(), false),
    BENCHMARK_RESULT(new File(
            Optional.ofNullable(System.getenv("BENCHMARK_RESULT_DIR"))
                    .orElse(System.getProperty("user.home") + File.separator + "benchmark-results")
    ), true);

    private File parentFolder;
    private Map<String, Serializable> buffer;

    Information(File parentFolder, boolean buffered) {
        this.parentFolder = parentFolder;
        buffer = buffered ? new HashMap<>() : Collections.emptyMap();
    }

    private static File temp() {
        try {
            return Files.createTempDirectory(Information.class.getName()).toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(Serializable object) throws IOException {
        save(object.getClass().getName(), object);
    }

    public void save(String name, Serializable object) throws IOException {
        try (ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(parentFolder, name))))) {
            os.writeObject(object);
            os.flush();
        }
    }


    public void put(String name, Serializable object) throws IOException {
        try {
            buffer.put(name, object);
        } catch (UnsupportedOperationException e) {
            save(name, object);
        }
    }

    public <T extends Serializable> T get(Class<T> type, String name) throws IOException {
        Serializable object = buffer.getOrDefault(name, load(type, name));
        if (object != null && type.isInstance(object)) {
            return type.cast(object);
        }
        return null;
    }

    public <T extends Serializable> T load(Class<T> type) throws IOException {
        return load(type, type.getName());
    }


    public <T extends Serializable> T load(Class<T> type, String name) throws IOException {
        try (ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(parentFolder, name))))) {
            Object o = is.readObject();
            if (type.isInstance(o)) {
                return type.cast(o);
            }
        } catch (FileNotFoundException | ClassNotFoundException e) {
        }
        return null;
    }

    public void flush() throws IOException {
        for (Iterator<Map.Entry<String, Serializable>> iterator = buffer.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Serializable> next = iterator.next();
            save(next.getKey(), next.getValue());
            iterator.remove();
        }
    }

    public Map<String, Serializable> getBuffer() {
        return buffer;
    }
}
