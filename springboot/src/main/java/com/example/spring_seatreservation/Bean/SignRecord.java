package com.example.spring_seatreservation.Bean;

public class SignRecord {
    private Long sid;
    private Long rid;
    private Long uid;
    private Integer type;
    private String signCode;
    private Long createTime;

    public SignRecord() {}

    public SignRecord(Long rid, Long uid, Integer type, String signCode, Long createTime) {
        this.rid = rid;
        this.uid = uid;
        this.type = type;
        this.signCode = signCode;
        this.createTime = createTime;
    }

    public Long getSid() { return sid; }
    public void setSid(Long sid) { this.sid = sid; }
    public Long getRid() { return rid; }
    public void setRid(Long rid) { this.rid = rid; }
    public Long getUid() { return uid; }
    public void setUid(Long uid) { this.uid = uid; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public String getSignCode() { return signCode; }
    public void setSignCode(String signCode) { this.signCode = signCode; }
    public Long getCreateTime() { return createTime; }
    public void setCreateTime(Long createTime) { this.createTime = createTime; }
}