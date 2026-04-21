// Contributor(s): Robbie
// Main: Robbie - Thrown when a requested resource id does not exist.

package com.sait.peelin.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
