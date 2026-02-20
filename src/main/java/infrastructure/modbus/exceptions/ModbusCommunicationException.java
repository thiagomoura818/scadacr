package infrastructure.modbus.exceptions;

public class ModbusCommunicationException extends Throwable{
    public ModbusCommunicationException(String message) {
        super(message);
    }

    public ModbusCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
