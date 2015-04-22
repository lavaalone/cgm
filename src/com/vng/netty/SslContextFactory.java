package com.vng.netty;

import javax.net.ssl.*;
import java.security.*;

public final class SslContextFactory 
{
    private static final SSLContext _ssl_context;
	
	private SslContextFactory() 
	{
    }

    static 
	{
        try 
		{
            KeyStore ks = SecureKeyInfo.getKeyStore();
           
            // Set up key manager factory to use our key store
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, SecureKeyInfo.getKeyPassword());

            // Initialize the SSLContext to work with our key managers.
            _ssl_context = SSLContext.getInstance("TLS");
            _ssl_context.init(kmf.getKeyManagers(), null, null);
        } 
		catch (Exception e) 
		{
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }
    }

    public static SSLContext getSslContext() 
	{
        return _ssl_context;
    }
}
