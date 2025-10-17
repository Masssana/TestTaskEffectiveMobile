package com.example.bankcards.util;

import com.example.bankcards.exception.WrongCardLengthException;

public class EncryptUtil {
    public static String encrypt(String cardNumber){
        if(cardNumber.length()!=16){
            throw new WrongCardLengthException("Длина номера карты должна быть 16 символов");
        }
        int digits = cardNumber.replaceAll("\\D", "").length();
        if(digits <= 4){
            return cardNumber.replaceAll("\\D", "*");
        }

        StringBuilder sb = new StringBuilder();
        int digitCount = 0;
        for(int i = cardNumber.length() - 1; i >= 0; i--){
            char c = cardNumber.charAt(i);
            if(Character.isDigit(c)){
                digitCount++;
                if(digitCount <= 4){
                    sb.append(c);
                }else {
                    sb.append('*');
                }
            }else {
                sb.append(c);
            }
        }
        return sb.reverse().toString();
    }
}
