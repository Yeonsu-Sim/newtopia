package io.ssafy.p.i13c203.gameserver.domain.member.entity;

public enum Gender {
    MALE("남성"),
    FEMALE("여성"), 
    OTHER("무관");
    
    private final String description;
    
    Gender(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}