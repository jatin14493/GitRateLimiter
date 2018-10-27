package com.company.worksample.blueoptima.com.company.worksample.blueoptima.Model;

import com.company.worksample.blueoptima.Main;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class DeveloperRepos implements Callable<DeveloperResponse> {

    private String repoUrl;
    private DeveloperRequest request;

    public DeveloperRepos(DeveloperRequest request,String repoUrl)
    {
        repoUrl = repoUrl + "?per_page=100";
        this.repoUrl=repoUrl;
        this.request=request;
    }
    @Override
    public DeveloperResponse call() {
        URL url;
        String result = "";
        try {
            url = new URL(repoUrl);
            HttpURLConnection uc;
            uc = (HttpURLConnection)url.openConnection();
            uc.setRequestProperty("Content-Type", "application/json");
            uc.setRequestProperty("X-Requested-With", "Curl");
            String userpass = "jatin14493" + ":" + "06e97fdfe0f52ea7b3090db8778876aa34509805";
            String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
            uc.setRequestProperty("Authorization", basicAuth);

            BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));

            }
            result = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }


       try {
            ObjectMapper mapper = new ObjectMapper();
           CollectionType typeReference =
                   TypeFactory.defaultInstance().constructCollectionType(List.class, DeveloperReposResponse.class);
           List<DeveloperReposResponse> resultDto = mapper.readValue(result, typeReference);




           List<String> repoNames = new ArrayList<>();
           for(DeveloperReposResponse response:resultDto){
               //Adding repoNames to DeveloperResponse Object
               repoNames.add(response.getName());
           }

           String profileId = repoUrl.substring(repoUrl.indexOf("users/")+"users/".length(), repoUrl.lastIndexOf("/")).trim();
           DeveloperProfileInfo developerProfileInfo = new DeveloperProfileInfo();
           developerProfileInfo = fetchProfile(developerProfileInfo,profileId);
           DeveloperResponse developerResponse = new DeveloperResponse();
           developerResponse.setFirstName(request.getFirstName());
           developerResponse.setLastName(request.getLastName());
           developerResponse.setLocation(request.getLocation());
           developerResponse.setProfileId(profileId);
           developerResponse.setDeveloperProfileInfo(developerProfileInfo);


           developerResponse.setRepoList(new ArrayList<>());
            List<Future<RepoDetail>> futureTasks = new ArrayList<>();
           for(DeveloperReposResponse response : resultDto)
           {
              //developerResponse.getRException parsing the objectepoList().add(response.getName());
              DeveloperCommits developerCommits = new DeveloperCommits(response.getCommitsUrl(),response.getName(),request);
              futureTasks.add(Main.executor.submit(developerCommits));
           }

           List<DeveloperCommitResponse> responseList = new ArrayList<>();

           for(Future<RepoDetail> future:futureTasks)
           {
               RepoDetail response = future.get();
               developerResponse.getRepoList().add(response);
              // System.out.println("**************************** "+response.getRepoName() +"+++++++"+response.getCommitCount()+"*******************************************************************");

           }
           return developerResponse;
           //System.out.println("developerResponse.getRepoList().size()---------" + developerResponse.getRepoList().size());
        } catch (Exception ex) {
            System.out.println("Exception parsing the object");
        }
        return null;
    }


    private DeveloperProfileInfo fetchProfile(DeveloperProfileInfo developerProfileInfo, String profileId) {

        DeveloperProfileInfo profileInfo = new DeveloperProfileInfo();

        URL url;
        String profileUrl = "https://api.github.com/users/" + profileId;
        String result = "";
        try {
            url = new URL(profileUrl);
            HttpURLConnection uc;
            uc = (HttpURLConnection)url.openConnection();
            uc.setRequestProperty("Content-Type", "application/json");
            uc.setRequestProperty("X-Requested-With", "Curl");
            String userpass = "jatin14493" + ":" + "cAO#175rhs";
            String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
            uc.setRequestProperty("Authorization", basicAuth);

            BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            result = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            developerProfileInfo = mapper.readValue(result, DeveloperProfileInfo.class);
        } catch (Exception ex) {
            System.out.println("Exception parsing the object");
        }

        profileInfo.setBio(developerProfileInfo.getBio());
        profileInfo.setBlog(developerProfileInfo.getBlog());
        profileInfo.setCompany(developerProfileInfo.getCompany());
        profileInfo.setEmail(developerProfileInfo.getEmail());
        profileInfo.setFollowers(developerProfileInfo.getFollowers());
        profileInfo.setFollowing(developerProfileInfo.getFollowing());
        profileInfo.setHireable(developerProfileInfo.getHireable());
        profileInfo.setCreated_at(developerProfileInfo.getCreated_at());
        profileInfo.setUpdated_at(developerProfileInfo.getUpdated_at());

        return profileInfo;
    }
}
