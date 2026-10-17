package com.crm.backend.common;

/**
 * Thrown when a registration or profile update tries to claim an email
 * address that already belongs to another account. Mapped to HTTP 409.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
