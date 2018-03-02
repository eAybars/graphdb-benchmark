package com.eaybars.benchmark;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public enum Information {

    TEMPORARY(temp(), false),
    BENCHMARK_RESULT(benchmarks(), true);

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

    private static File benchmarks() {
        File file = System.getenv("BENCHMARK_RESULT_DIR") == null ?
                temp() : new File(System.getenv("BENCHMARK_RESULT_DIR"));
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new RuntimeException("Cannot create folder for benchmark results at: "+file.getAbsolutePath());
            }
        }
        return file;
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

    public Iterator<Map.Entry<String, Serializable>> objects() {
        File[] files = parentFolder.listFiles();
        if (files == null) {
            return Collections.emptyIterator();
        }

        return new Iterator<Map.Entry<String, Serializable>>() {
            int index = -1;

            @Override
            public boolean hasNext() {
                return index + 1 < files.length;
            }

            @Override
            public Map.Entry<String, Serializable> next() {
                index++;
                try {
                    return new AbstractMap.SimpleImmutableEntry<>(files[index].getName(),
                            load(Serializable.class, files[index].getName()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

}
