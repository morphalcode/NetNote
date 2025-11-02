package client.Exceptions;

public class ConfigFileCorruptedException extends Exception {
    public ConfigFileCorruptedException() {
        super();
    }

    public ConfigFileCorruptedException(String message) {
        super(message);
    }
}
