package com.untzuntz.ustackserver;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import com.untzuntz.ustack.data.ExternalAPIParams;
import com.untzuntz.ustack.main.ConfigEnc;
import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;

public class ConfigEncHelper {

	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			System.err.println("ConfigEncHelper [file to encrypt] <outputfile>");
			return;
		}

		String name = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		if (args.length == 2)
		{
			// output to a file
		}
		else
		{
	
			System.out.print("Data Name (unique): ");
			name = br.readLine();
			System.out.print("Database (ex: test@localhost): ");
			String db = br.readLine();
			if (db.indexOf("@") == -1)
			{
				System.err.println("Invalid database name");
				System.exit(1);
			}
			
			String dbName = db.substring(0, db.indexOf("@"));
			String dbLoc = db.substring(db.indexOf("@") + 1);
			
			UOpts.setAppName(dbName);
			System.setProperty(UAppCfg.MONGO_DB_HOST, dbLoc);
		}
		
		System.out.print("Passphrase: ");
		String passphrase = null;

		try {
			passphrase = br.readLine();
		} catch (IOException ioe) {
			System.err.println("IO error trying to the passphrase");
			System.exit(1);
		}
		
		if (passphrase.length() < 10)
		{
			System.err.println("Passphrase must be at least 10 characters");
			System.exit(1);
		}
		

		ConfigEnc ce = new ConfigEnc(passphrase);

		StringBuffer srcData = new StringBuffer();
		File inFile = new File(args[0]);
		FileInputStream in = null;
		try {
			in = new FileInputStream(inFile);
			int readLen = 0;
			int toRead = (int) inFile.length();
			byte[] buf = new byte[8192];
			while ((readLen = in.read(buf, 0, toRead)) != -1) {
				srcData.append(new String(Arrays.copyOfRange(buf, 0, readLen)));
				toRead = toRead - readLen;
				if (toRead == 0)
					break;
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}

		String readBack = null;
		if (name != null)
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ce.encrypt(srcData.toString(), out);
			
			ExternalAPIParams params = ExternalAPIParams.createExternalAPIParams(name, out);
			params.save("ceh");
		
			// readback
			params = ExternalAPIParams.getByName(name);
			
			readBack = ce.decrypt(params.getKeyDataStream());
		}
		else
		{
			// write to the provided file
			File encFile = new File(args[1]);
			FileOutputStream out = new FileOutputStream(encFile);
			ce.encrypt(srcData.toString(), out);
			out.close();
			
			FileInputStream rbin = new FileInputStream(encFile);
			readBack = ce.decrypt(rbin);
		}
		
		if (!srcData.toString().equals(readBack))
		{
			System.out.println("  source data [" + srcData + "] => " + srcData.length());
			System.out.println("readback data [" + readBack + "] => " + readBack.length());
			System.err.println("There was an error during the encryption/decryption test. Data did not match!");
		}
		else
			System.out.println("Data successfully read back => " + readBack);
	}

}
