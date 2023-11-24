package com.avensys.rts.candidate.exception;

/**
 * author: Koh He Xiang
 * This is the exception class for when a user tries to access a resource that he/she does not have permission to
 */
public class PermissionDeniedException extends RuntimeException{
    public PermissionDeniedException(String message) {
        super(message);
    }
}
