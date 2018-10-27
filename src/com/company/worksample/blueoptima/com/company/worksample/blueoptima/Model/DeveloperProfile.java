package com.company.worksample.blueoptima.com.company.worksample.blueoptima.Model;

import com.company.worksample.blueoptima.Main;
import com.fasterxml.jackson.databind.ObjectMapper;
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

/**
 * Class implements Callable as it returns Future instance.
 * @author Jatin Mahajan
 */
public class DeveloperProfile implements Callable<List<?>> {
    private DeveloperRequest request;
    private static final String userSearchURL = "https://api.github.com/search/users?q=";

    public DeveloperProfile(DeveloperRequest request)
    {
        this.request=request;
    }

    @Override
    public List<?> call() throws Exception {
        Developer user = null;
        List<Item> item = null;
        String queryString;

        String loc = request.getLocation();
        if(loc.contains(" ")){
            loc = loc.replace(" ","+");
            queryString = userSearchURL + "fullname:" + request.getFirstName() + "+" + request.getLastName() + "&" + "location:" + loc;
        }else if(loc.contains("N/A")){
            queryString = userSearchURL + "fullname:" + request.getFirstName() + "+" + request.getLastName();
        }else{
            queryString = userSearchURL + "fullname:" + request.getFirstName() + "+" + request.getLastName() + "&" + "location:" + loc;
        }

        URL url;
        String result = "";
        try {
            url = new URL(queryString);
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
            user = mapper.readValue(result, Developer.class);
            item = user.getItems();
        } catch (Exception ex) {
            System.out.println("Exception parsing the object");
        }




        /**
         * encounter multiple users with same name and location attributes.
         * @author Jatin Mahajan
         */
        List<Future<?>> response = new ArrayList<Future<?>>();
        for(Item i:item) {
            response.add(Main.executor.submit(new DeveloperRepos(request, i.getReposUrl())));
        }

        List<Object> list = new ArrayList<>();
        for (Future<?> future :response)
        {
            Object obj = future.get();
            list.add(obj);
        }
        return list;
    }

}
