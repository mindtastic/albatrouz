package de.mindtastic.albatrouz.openapi;

public class DuplicatePathItemOperationException extends IllegalStateException {
    @Override
    public String getMessage() {
        return "Duplicate Operation for PathItem method";
    }
}
