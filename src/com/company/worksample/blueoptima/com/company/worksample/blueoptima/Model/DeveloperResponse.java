package com.company.worksample.blueoptima.com.company.worksample.blueoptima.Model;

import java.util.List;

public class DeveloperResponse {
    private String firstName;
    private String lastName;
    private String location;
    private String profileId;
    private List<RepoDetail> repoList;
    DeveloperProfileInfo developerProfileInfo;

    public DeveloperProfileInfo getDeveloperProfileInfo() {
        return developerProfileInfo;
    }

    public void setDeveloperProfileInfo(DeveloperProfileInfo developerProfileInfo) {
        this.developerProfileInfo = developerProfileInfo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<RepoDetail> getRepoList() {
        return repoList;
    }

    public void setRepoList(List<RepoDetail> repoList) {
        this.repoList = repoList;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }
}
