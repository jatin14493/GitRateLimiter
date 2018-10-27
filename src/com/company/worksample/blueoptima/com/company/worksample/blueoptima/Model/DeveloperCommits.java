package com.company.worksample.blueoptima.com.company.worksample.blueoptima.Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

public class DeveloperCommits implements Callable<RepoDetail>  {
    private String repoUrl;
    private String repoName;
    private DeveloperRequest request;
    public DeveloperCommits(String url , String repoName,DeveloperRequest request)
    {
        if(url.contains("sha")){
            url = url.substring(0,url.indexOf("sha")-2);
        }
        url=url+"?per_page=100";
        this.repoUrl=url;
        this.repoName = repoName;
        this.request=request;
    }
    @Override
    public RepoDetail call() throws Exception {
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
            //System.out.println(result);
        } catch (IOException e) {
            //e.printStackTrace();
            //Handling to be done here in case for all those repos where there are no commits
            //As of now in such case, Response is coming like this!!!
            /*{
                "message": "Git Repository is empty.",
                    "documentation_url": "https://developer.github.com/v3/repos/commits/#list-commits-on-a-repository"
            }*/

            RepoDetail detail = new RepoDetail();
            detail.setRepoName(repoName);
            detail.setCommitCount(0);
            return detail;
        }

        ObjectMapper mapper = new ObjectMapper();

        CollectionType typeReference =
                TypeFactory.defaultInstance().constructCollectionType(List.class, DeveloperCommitResponse.class);
        List<DeveloperCommitResponse> resultDto = mapper.readValue(result, typeReference);
        int count = 0;
        int i=0;
        for(DeveloperCommitResponse commitResponse:resultDto)
        {
            //System.out.println(commitResponse.getCommit().getCommitter().getName());
            if(commitResponse.getCommit().getCommitter().getName().contains(request.getFirstName()) &&
                    commitResponse.getCommit().getCommitter().getName().contains(request.getLastName())) {
                count++;

            }
           // System.out.println("Commmit Count: " + count + " for repository :" + developerResponse.getRepoList().get(i));
        }
        i++;
        RepoDetail detail = new RepoDetail();
        detail.setRepoName(repoName);
        detail.setCommitCount(count);
        return detail;

    }
}
