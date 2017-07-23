package Components;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

@SuppressWarnings("deprecation")
public class Utils {
	public static String homePath = System.getProperty("user.home") +
			"/Students-Management-System/";

	public static String encode(String data) throws Exception {
	    if (data == null || data.isEmpty())
	        throw new IllegalArgumentException("Null value provided for " + "MD5 Encoding");

	    MessageDigest m = MessageDigest.getInstance("MD5");
	    m.update(data.getBytes(), 0, data.length());
	    String digest = String.format("%032x", new BigInteger(1, m.digest()));

	    return digest;
	}

	/**
	 * Incarca pe server si redenumeste imaginea.
	 * Intoarce numele de pe server al imaginii incarcate.
	 */
	public static String uploadImage(String localPath) throws Exception {
	     HttpClient httpclient = new DefaultHttpClient();
	     httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
	     HttpPost httppost = new HttpPost("http://cristi.xyz/ip/file_upload.php");
	     File file = new File(localPath);
	     MultipartEntity mpEntity = new MultipartEntity();
	     ContentBody cbFile = new FileBody(file, "image/" + localPath.substring(localPath.lastIndexOf(".")));
	     mpEntity.addPart("userfile", cbFile); 
	     httppost.setEntity(mpEntity);
	     System.out.println("executing request " + httppost.getRequestLine());
	     HttpResponse response = httpclient.execute(httppost);
	     String result = EntityUtils.toString(response.getEntity(), "UTF-8");

	     if((response.getStatusLine().toString()).equals("HTTP/1.1 200 OK")
	    		 && !result.equals("ERROR"))
	    	 System.out.println("UPLOAD OK!");
	     else
	    	 System.out.println("UPLOAD FAILED!");

	     httpclient.getConnectionManager().shutdown();

	     return result ;
	}

	/**
	 * Intoarce True pentru descarcare reusita, False altfel
	 */
	public static boolean downloadImage(String imageURL) {
		String localPath = homePath + imageURL;
		URL website = null;
		ReadableByteChannel rbc;
		FileOutputStream fos;

		try {
			website = new URL("http://cristi.xyz/ip/ip_upl/" + imageURL);
			rbc = Channels.newChannel(website.openStream());
			fos = new FileOutputStream(localPath);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}

		return true;
	}
}