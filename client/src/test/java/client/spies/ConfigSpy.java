package client.spies;

import client.Config;
import client.Exceptions.ConfigFileCorruptedException;

import java.io.IOException;

public class ConfigSpy extends Config {

    private boolean initializeCalled = false;
    private boolean saveToFileCalled = false;

    public ConfigSpy() {
        super();
    }

    @Override
    public void initialize() throws ConfigFileCorruptedException {
        initializeCalled = true;
    }

    public boolean isInitializeCalled() {
        return initializeCalled;
    }

    @Override
    public void saveToFile() throws IOException {
        saveToFileCalled = true;
    }

    public boolean isSaveToFileCalled() {
        return saveToFileCalled;
    }
}
