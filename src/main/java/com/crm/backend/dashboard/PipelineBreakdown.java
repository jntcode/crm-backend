package com.crm.backend.dashboard;

public class PipelineBreakdown {
    private long newLeads;
    private long contacted;
    private long qualified;
    private long proposal;
    private long won;
    private long lost;

    public PipelineBreakdown() {}

    // Getters & Setters
    public long getNew() {
        return newLeads;
    }

    public void setNew(long newLeads) {
        this.newLeads = newLeads;
    }

    public long getContacted() {
        return contacted;
    }

    public void setContacted(long contacted) {
        this.contacted = contacted;
    }

    public long getQualified() {
        return qualified;
    }

    public void setQualified(long qualified) {
        this.qualified = qualified;
    }

    public long getProposal() {
        return proposal;
    }

    public void setProposal(long proposal) {
        this.proposal = proposal;
    }

    public long getWon() {
        return won;
    }

    public void setWon(long won) {
        this.won = won;
    }

    public long getLost() {
        return lost;
    }

    public void setLost(long lost) {
        this.lost = lost;
    }
}
