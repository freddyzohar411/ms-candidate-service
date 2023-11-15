package com.avensys.rts.candidate.enums;

public enum Permission {
	
	private final String permission;
	
	Permission(String permission) {
        this.permission = permission;
    }

    public String toString() {
        return this.permission;
    }

}
