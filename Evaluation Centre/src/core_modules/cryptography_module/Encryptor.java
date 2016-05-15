package core_modules.cryptography_module;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.salt.RandomSaltGenerator;

public class Encryptor 
{
	static String crypto_provider = "BC";
	static String crypto_algorithm = "PBEWITHSHA256AND256BITAES-CBC-BC";
	static int iterations = 100000;
	static int pool_size = 2;
	
	public String encrypt(String password,String plain_text)
	{
		Security.addProvider(new BouncyCastleProvider());
		System.out.println("Inside the Cryptography Module (Encryption)");
		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		encryptor.setProviderName(crypto_provider);
		encryptor.setAlgorithm(crypto_algorithm);
		encryptor.setPoolSize(pool_size);
		encryptor.setSaltGenerator(new RandomSaltGenerator());
		encryptor.setKeyObtentionIterations(iterations);
		encryptor.setPasswordCharArray(password.toCharArray());
		
		String cipher_text = encryptor.encrypt(plain_text);
		System.out.println("Plain Text recieved: "+plain_text);
		System.out.println("Cipher Text generated: "+cipher_text);
		System.out.println("Exiting Crytography Module (Encryption)");
		System.out.println("_______________________________________________________________________________________________________________________________________________________________");
		return cipher_text;
	}
}