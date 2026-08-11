package com.aditya.urlshortener;

public class Base62Encoder {

    public static void main(String[] args) {
        System.out.println(encode(0L));
        System.out.println(encode(1L));
        System.out.println(encode(10L));
        System.out.println(encode(61L));
        System.out.println(encode(62L));
        System.out.println(encode(125L));
        System.out.println(encode(3844L));
    }

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
