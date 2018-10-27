package com.company.worksample.blueoptima;

import com.company.worksample.blueoptima.com.company.worksample.blueoptima.Model.Committer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Commit {
    private Committer committer;

    public Committer getCommitter() {
        return committer;
    }

    public void setCommitter(Committer committer) {
        this.committer = committer;
    }
}
