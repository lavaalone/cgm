///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.vng.skygarden.game;
//
//import com.vng.netty.*;
//import com.vng.util.*;
//import com.vng.taskqueue.*;
//import com.vng.skygarden.*;
//import com.vng.db.*;
//import com.vng.log.LogHelper;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//
//import java.util.List;
//import java.util.Arrays;
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.gson.GsonFactory;
//
//public class GoogleAuthenticateTask extends Task
//{
//	private final List mClientIDs;
//    private final String mAudience;
//    private final GoogleIdTokenVerifier mVerifier;
//    private final JsonFactory mJFactory;
//    private String mProblem = "Verification failed. (Time-out?)";
//	
//	private String[] clientIDs = {"615795432163-u5i40kc5949ds5hqnorigq45gsm9kb50.apps.googleusercontent.com"
//			, "615795432163.apps.googleusercontent.com"};
//	
//	Client		_client;
//	FBEncrypt	_encrypt;
//	
////	public GoogleAuthenticateTask(String[] clientIDs, String audience)
////	{
////		super();
////		
////		mClientIDs = Arrays.asList(clientIDs);
////        mAudience = audience;
////        NetHttpTransport transport = new NetHttpTransport();
////        mJFactory = new GsonFactory();
////        mVerifier = new GoogleIdTokenVerifier(transport, mJFactory);
////	}
//	
//	public GoogleAuthenticateTask(Client client, FBEncrypt encrypt)
//	{
//		super();
//		
//		_client = client;
//		_encrypt = encrypt;
//		
//		mClientIDs = Arrays.asList(clientIDs);
//		mAudience = _encrypt.getString("gmail_account");
//		NetHttpTransport transport = new NetHttpTransport();
//        mJFactory = new GsonFactory();
//        mVerifier = new GoogleIdTokenVerifier(transport, mJFactory);
//	}
//	
//	@Override
//	protected void HandleTask() 
//	{
//		try
//		{
//			Thread.sleep(1000);
//		}
//		catch (Exception ex)
//		{
//		}
//	
//		String tokenString = _encrypt.getString("google_token_string");
//		GoogleIdToken.Payload pl = check(tokenString);
//		
//	}
//	
//	public GoogleIdToken.Payload check(String tokenString) 
//	{
//        GoogleIdToken.Payload payload = null;
//        try 
//		{
//            GoogleIdToken token = GoogleIdToken.parse(mJFactory, tokenString);
//            if (mVerifier.verify(token)) 
//			{
//                GoogleIdToken.Payload tempPayload = token.getPayload();
//                if (!tempPayload.getAudience().equals(mAudience))
//				{
//					mProblem = "Audience mismatch";
//				}
//                else if (!mClientIDs.contains(tempPayload.getIssuee()))
//				{
//					mProblem = "Client ID mismatch";
//				}
//                else
//				{
//					payload = tempPayload;
//				}
//            }
//        } 
//		catch (GeneralSecurityException e) 
//		{
//            mProblem = "Security issue: " + e.getLocalizedMessage();
//        } 
//		catch (IOException e)
//		{
//            mProblem = "Network problem: " + e.getLocalizedMessage();
//        }
//        return payload;
//    }
//
//    public String problem() 
//	{
//        return mProblem;
//    }
//}
