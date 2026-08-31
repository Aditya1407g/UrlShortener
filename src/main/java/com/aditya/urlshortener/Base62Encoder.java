package com.aditya.urlshortener;

public class Base62Encoder {



    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";


    public static String encode(Long id){
        if(id==0) return "0";
        StringBuilder str = new StringBuilder();
        long localCopy = id;
        while(localCopy > 0){
            int remainder = (int) (localCopy%62);
            str.append(ALPHABET.charAt(remainder));
            localCopy/=62;
        }
        return str.reverse().toString();
    }
}
