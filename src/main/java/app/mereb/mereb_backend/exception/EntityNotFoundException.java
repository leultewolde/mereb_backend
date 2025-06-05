package app.mereb.mereb_backend.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName) {
        super(entityName + " not found");
    }
}
