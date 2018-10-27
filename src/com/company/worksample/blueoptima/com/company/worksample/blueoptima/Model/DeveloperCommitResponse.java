package com.company.worksample.blueoptima.com.company.worksample.blueoptima.Model;

import com.company.worksample.blueoptima.Commit;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeveloperCommitResponse {

private Commit commit;

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }
}
