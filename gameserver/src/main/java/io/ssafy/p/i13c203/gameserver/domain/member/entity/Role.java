package io.ssafy.p.i13c203.gameserver.domain.member.entity;

public enum Role {
    ADMIN("관리자"),
    MEMBER("회원");
    
    private final String description;
    
    Role(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}