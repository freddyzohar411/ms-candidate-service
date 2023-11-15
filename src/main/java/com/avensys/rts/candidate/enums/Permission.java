package com.avensys.rts.candidate.enums;
/**
 * This enum is used to specify the permissions
 */
public enum Permission {
	
	CANDIDATE_READ("Candidates:Read"),
	CANDIDATE_WRITE("Candidates:Write"),
	CANDIDATE_DELETE("Candidates:Delete"),
	CANDIDATE_EDIT("Candidates:Edit"),

	CANDIDATE_NOACCESS("Candidates:NoAccess");
	
	private final String permission;
	
	Permission(String permission) {
        this.permission = permission;
    }

    public String toString() {
        return this.permission;
    }

}
