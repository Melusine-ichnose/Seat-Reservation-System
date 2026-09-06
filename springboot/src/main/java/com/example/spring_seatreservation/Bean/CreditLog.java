package com.example.spring_seatreservation.Bean;

public class CreditLog {
    private Long clid;
    private Long uid;
    private Integer changeType;
    private Integer changeScore;
    private String reason;
    private String operator;
    private Long createTime;

    public CreditLog() {}

    public CreditLog(Long uid, Integer changeType, Integer changeScore, String reason, String operator, Long createTime) {
        this.uid = uid;
        this.changeType = changeType;
        this.changeScore = changeScore;
        this.reason = reason;
        this.operator = operator;
        this.createTime = createTime;
    }

    public Long getClid() { return clid; }
    public void setClid(Long clid) { this.clid = clid; }
    public Long getUid() { return uid; }
    public void setUid(Long uid) { this.uid = uid; }
    public Integer getChangeType() { return changeType; }
    public void setChangeType(Integer changeType) { this.changeType = changeType; }
    public Integer getChangeScore() { return changeScore; }
    public void setChangeScore(Integer changeScore) { this.changeScore = changeScore; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public Long getCreateTime() { return createTime; }
    public void setCreateTime(Long createTime) { this.createTime = createTime; }
}