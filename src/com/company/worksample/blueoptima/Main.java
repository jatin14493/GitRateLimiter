package com.company.worksample.blueoptima;

import com.company.worksample.blueoptima.com.company.worksample.blueoptima.Model.DeveloperProfile;
import com.company.worksample.blueoptima.com.company.worksample.blueoptima.Model.DeveloperRequest;
import com.company.worksample.blueoptima.com.company.worksample.blueoptima.Model.DeveloperResponse;
import com.company.worksample.blueoptima.com.company.worksample.blueoptima.Model.RepoDetail;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * The class helps listens to the user request
 * And outputs
 * @author Jatin Mahajan
 */


public class Main {

    //initializing thread pool of size 100 threads
    public static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);

    public static void main(String[] args) throws IOException {

       List<DeveloperRequest> developerRequests =new ArrayList<>();

        //Assuming input is taken from console.
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line ="";
        while(!line.equals("EOF") || !line.contains("eof")) {
            line = reader.readLine();
            if(line.contains("EOF") || line.contains("eof")){
                break;
            }
            String []tokens = line.split(" ");
            DeveloperRequest request = new DeveloperRequest();
            request.setFirstName(tokens[0].trim());
            request.setLastName(tokens[1].trim());
            if(tokens.length == 3){
                String loc = tokens[2].trim();
                request.setLocation(loc);
            }else if(tokens.length >= 3){
                int diff = tokens.length;
                int i=2;
                String val = "";
                while(i < diff) {
                    val =  val + " " +tokens[i];
                    i = i+1;
                }
                val = val.trim();
                request.setLocation(val);
            }else{

                request.setLocation("N/A");
            }
            developerRequests.add(request);

        }
        reader.close();
        try {
            executeCurlCommands(developerRequests);
        }catch (Exception ex){

        }

    }

    private static void executeCurlCommands(List<DeveloperRequest> obj) throws ExecutionException, InterruptedException {



        String apiUrl = "https://api.github.com";
        String result ="";
        URL url;
        try {
            url = new URL(apiUrl);
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


        /**
         * Search start point for the program
         * @author Jatin Mahajan
         */

        if(result.contains("user_search_url")){

            /**
             * For getting user Repositories
             */
            List<Future<List<?>>> response = new ArrayList<Future<List<?>>>();

            for(DeveloperRequest developerRequest:obj) {
                response.add(Main.executor.submit(new DeveloperProfile(developerRequest)));
            }

            for (Future<List<?>> future :response)
            {
                List<?> obj1 = future.get();
                for(Object obj2 : obj1){

                String name =((DeveloperResponse)obj2).getFirstName() + " " +((DeveloperResponse)obj2).getLastName();
                String bio = ((DeveloperResponse) obj2).getDeveloperProfileInfo().getBio();
                String blog = ((DeveloperResponse) obj2).getDeveloperProfileInfo().getBlog();
                String company = ((DeveloperResponse) obj2).getDeveloperProfileInfo().getCompany();
                String email = ((DeveloperResponse) obj2).getDeveloperProfileInfo().getEmail();
                String followers = ((DeveloperResponse) obj2).getDeveloperProfileInfo().getFollowers();
                String following = ((DeveloperResponse) obj2).getDeveloperProfileInfo().getFollowing();
                String hireable = ((DeveloperResponse) obj2).getDeveloperProfileInfo().getHireable();
                String createdDate = ((DeveloperResponse) obj2).getDeveloperProfileInfo().getCreated_at();
                String updatedDate = ((DeveloperResponse) obj2).getDeveloperProfileInfo().getUpdated_at();


                    System.out.println("Name : " + name);
                    System.out.println("bio :" + bio);
                    System.out.println("blog :" + blog);
                    System.out.println("company :" + company);
                    System.out.println("email :" + email);
                    System.out.println("followers :" + followers);
                    System.out.println("following :" + following);
                    System.out.println("hireable :" + hireable);
                    System.out.println("created date :" + createdDate);
                    System.out.println("updated date :" + updatedDate);

                List<RepoDetail> list = ((DeveloperResponse)obj2).getRepoList();

                System.out.println("Repository Count :" + list.size());
                for(RepoDetail repoDetail : list){
                    String repoName = repoDetail.getRepoName();
                    int commitCount = repoDetail.getCommitCount();
                    System.out.println("Repository Name: " + repoName + " Count : " + commitCount);
                }

                    System.out.println("***************************************************************END***********************************************************");
                }

                System.out.println("***************************************************************END***********************************************************");
            }
        }

        /**
         * Shutting down executor post execution
         * @author Jatin Mahajan
         */
        Main.executor.shutdown();
    }

   }
