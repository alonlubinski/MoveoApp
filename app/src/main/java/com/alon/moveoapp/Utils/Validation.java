package com.alon.moveoapp.Utils;

import java.util.regex.Pattern;

public class Validation {

    private static Pattern emailPattern = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+");

    public static boolean validateIsNotEmpty(String value){
        if(value.isEmpty()){
            return false;
        }
        return true;
    }

    public static boolean validateEmail(String email){
        if(!emailPattern.matcher(email).matches()){
            return false;
        }
        return true;
    }

    public static boolean validatePasswordLength(String password){
        if(password.length() < 6){
            return false;
        }
        return true;
    }

    public static boolean validatePasswordsMatch(String password, String password2){
        if(!password.equals(password2)){
            return false;
        }
        return true;
    }
}
