package com.example.spring_seatreservation.Bean;

public class Violation {
    private Long vid;
    private Long rid;
    private Long uid;
    private Integer type;
    private Integer deductScore;
    private Long createTime;

    public Violation() {}

    public Violation(Long rid, Long uid, Integer type, Integer deductScore, Long createTime) {
        this.rid = rid;
        this.uid = uid;
        this.type = type;
        this.deductScore = deductScore;
        this.createTime = createTime;
    }

    public Long getVid() { return vid; }
    public void setVid(Long vid) { this.vid = vid; }
    public Long getRid() { return rid; }
    public void setRid(Long rid) { this.rid = rid; }
    public Long getUid() { return uid; }
    public void setUid(Long uid) { this.uid = uid; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public Integer getDeductScore() { return deductScore; }
    public void setDeductScore(Integer deductScore) { this.deductScore = deductScore; }
    public Long getCreateTime() { return createTime; }
    public void setCreateTime(Long createTime) { this.createTime = createTime; }
}