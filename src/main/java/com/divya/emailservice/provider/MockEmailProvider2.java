package com.divya.emailservice.provider;

import java.util.Random;


public class MockEmailProvider2 {
    private final Random random = new Random();

    public boolean send(String to, String subject, String body){
        //return random.nextBoolean();
        if(to.contains("failAll")){
            return false;
        }
        return to.contains("fallback");
    }
}
