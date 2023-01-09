package com.example.coin_panion.classes.utility;

//import com.vonage.client.VonageClient;
//import com.vonage.client.sms.MessageStatus;
//import com.vonage.client.sms.SmsSubmissionResponse;
//import com.vonage.client.sms.messages.TextMessage;

import java.util.Random;

public class SendSMS {
    private static final String ACCOUNT_SID = "AC198f0f9dad48804aaabb3a43506b396e";
    private static final String AUTH_TOKEN = "0c084a317f78c7afa8e4a6c2a971767c";

    public static void sendOTP(String phoneNumber, String OTP){
//        VonageClient client = VonageClient.builder().apiKey("e60ff3ce").apiSecret("fZpH8wAMzHXOf3pt").build();
//
//        TextMessage message = new TextMessage("Nazrin",
//                phoneNumber.substring(1),
//                String.format("Your OTP for Coinpanion account is %s", OTP)
//        );
//
//        SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);
//
//        if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
//            System.out.println("Message sent successfully.");
//        } else {
//            System.out.println("Message failed with error: " + response.getMessages().get(0).getErrorText());
//        }
    }

    public static String generateOTP(){
        String numbers = "0123456789";
        Random random = new Random();

        char[] otp = new char[6];

        for(int i = 0; i < 6; i++){
            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
        }
        return String.valueOf(otp);
    }

    // Call to send otp
//    String phoneNumber = "+1234567890";
//    String OTP = generateOTP();
//    sendOTP(phoneNumber, OTP);

}
