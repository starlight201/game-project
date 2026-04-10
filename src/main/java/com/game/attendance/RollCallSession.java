package com.game.attendance;

public class RollCallSession {
    int sessionId;
    String callType;
    String strategy;
    int studentCount;
    java.sql.Timestamp startTime;

    public RollCallSession(String callType, String strategy, int studentCount) {
        this.callType = callType;
        this.strategy = strategy;
        this.studentCount = studentCount;
        this.startTime = new java.sql.Timestamp(System.currentTimeMillis());
    }
}
