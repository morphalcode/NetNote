package client.Exceptions;

public class NoCollectionsConfiguredException extends Exception {
    public NoCollectionsConfiguredException() {
        super();
    }

    public NoCollectionsConfiguredException(String message) {
        super(message);
    }
}
