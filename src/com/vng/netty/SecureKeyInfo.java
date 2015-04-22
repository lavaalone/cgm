package com.vng.netty;

import javax.net.ssl.*;
import java.security.*;
import java.io.InputStream;

/**
 * A bogus key store which provides all the required information to
 * create an example SSL connection.
 *
 * To generate a bogus key store:
 * <pre>
 * keytool  -genkey -alias skygarden -keysize 1024 -validity 3650
 *          -keyalg RSA -dname "CN=skygarden"
 *          -keypass sgd2013@)!#cer -storepass sgd2013@)!#ksr
 *          -keystore skygardenkeystore.jks
 * </pre>
 */
public final class SecureKeyInfo 
{
	private static final KeyStore _keystore;
	
	static
	{
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("sgkeystore.jks");
		
		try
		{
			_keystore = KeyStore.getInstance("JKS");
			_keystore.load(is, SecureKeyInfo.getStorePassword());
			
			is.close();
		}
		catch (Exception ex)
		{
			throw new Error("Failed to load file \"sgkeystore.jks\"", ex);
		}
	}
	
    public static KeyStore getKeyStore() 
	{
        return _keystore;
    }

    public static char[] getKeyPassword() 
	{
        return "sgd2013@)!#".toCharArray();
    }

    public static char[] getStorePassword() 
	{
        return "sgd2013@)!#".toCharArray();
    }
}
