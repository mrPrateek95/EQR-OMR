package core_modules.cryptography_module;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;

public class Decryptor 
{
	private static String crypto_provider = "BC";
	private static String crypto_algorithm = "PBEWITHSHA256AND256BITAES-CBC-BC";
	private static int iterations = 100000;
	private static int pool_size = 2;
	
	
	public String decrypt(String password,String cipher_text)
	{
		Security.addProvider(new BouncyCastleProvider());
		System.out.println("Inside the Cryptography Module (Decryption)");
		PooledPBEStringEncryptor decryptor = new PooledPBEStringEncryptor();
		decryptor.setProviderName(crypto_provider);
		decryptor.setAlgorithm(crypto_algorithm);
		decryptor.setPoolSize(pool_size);
		decryptor.setKeyObtentionIterations(iterations);
		decryptor.setPasswordCharArray(password.toCharArray());
		
		String plain_text = decryptor.decrypt(cipher_text);
		System.out.println("Cipher Text recieved: "+cipher_text);
		System.out.println("Plain Text generated: "+plain_text);
		System.out.println("Exiting Crytography Module (Decryption)");
		System.out.println("_______________________________________________________________________________________________________________________________________________________________");
		return plain_text;

	}
}
