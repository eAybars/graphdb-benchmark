package com.eaybars.benchmark;

import java.io.*;
import java.nio.file.Files;

public enum Information {

    BROKER;

    private File parentFolder;

    Information() {
        try {
            this.parentFolder = Files.createTempDirectory(getClass().getName()).toFile();
        } catch (IOException e) {
        }
    }

    public void save(Serializable object) throws IOException {
        save(object.getClass().getName(), object);
    }

    public void save(String name, Serializable object) throws IOException {
        try (ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(parentFolder, name))))){
            os.writeObject(object);
            os.flush();
        }
    }

    public <T extends Serializable> T load(Class<T> type) throws IOException {
        return load(type, type.getName());
    }

    public <T extends Serializable> T load(Class<T> type, String name) throws IOException {
        try (ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(parentFolder, name))))){
            Object o = is.readObject();
            if (type.isInstance(o)) {
                return type.cast(o);
            }
        } catch (FileNotFoundException | ClassNotFoundException e) {
        }
        return null;
    }
}
