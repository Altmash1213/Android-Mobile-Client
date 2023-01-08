package org.intelehealth.app.ayu.visit.model;

import java.util.List;

public class ReasonGroupData {
    private String alphabet;
    private List<ReasonData> reasons;

    public List<ReasonData> getReasons() {
        return reasons;
    }

    public void setReasons(List<ReasonData> reasons) {
        this.reasons = reasons;
    }

    public String getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(String alphabet) {
        this.alphabet = alphabet;
    }
}