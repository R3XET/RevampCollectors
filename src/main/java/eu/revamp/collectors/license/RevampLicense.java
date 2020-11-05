package eu.revamp.collectors.license;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

public class RevampLicense {

	private String licenseKey;
	private Plugin plugin;
	private String validationServer;
	@Getter
	@Setter
	private String securityKey = "YecoF0I6M05thxLeokoHuW8iUhTdIUInjkfF";
	@Getter @Setter private boolean debug = false;

	public RevampLicense(String licenseKey, String validationServer, Plugin plugin){
		this.licenseKey = licenseKey;
		this.plugin = plugin;
		this.validationServer = validationServer;
	}


	public RevampLicense.ValidationType isValid(){
    /*
    String rand = toBinary(UUID.randomUUID().toString());
    String sKey = toBinary(securityKey);
    String key  = toBinary(licenseKey);
    */

		try{
			String url = validationServer+"?key="+licenseKey+"&pl="+plugin.getName();
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection)obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			InputStream is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			if (!url.startsWith("https://api.revampdev.tk/"))
				return RevampLicense.ValidationType.URL_ERROR;
			String response = br.readLine();//me la printi in console?
			//System.out.println(response);
			if(response == null)
				return RevampLicense.ValidationType.PAGE_ERROR;
			switch(response){
				case "diogay":
					return RevampLicense.ValidationType.VALID;
				case "dioputana":
					return RevampLicense.ValidationType.NOT_VALID_IP;
				case "dioroia":
					return RevampLicense.ValidationType.KEY_OUTDATED;
				case "madonacancara":
					return RevampLicense.ValidationType.KEY_NOT_FOUND;
				case "tomare":
					return RevampLicense.ValidationType.WRONG_RESPONSE;
				case "tosoreapissainpie":
					return RevampLicense.ValidationType.INVALID_PLUGIN;
				case "suca":
					return RevampLicense.ValidationType.PAGE_ERROR;
				default:
					return RevampLicense.ValidationType.URL_ERROR;
			}
      /*
      //con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
      if(isDebug()) System.out.println("RequestURL -> "+url.toString());
      Scanner s = new Scanner(url.openStream());
      if(s.hasNext()){
        String response = s.next();
        s.close();
        try{
          return ValidationType.valueOf(response);
        }catch(IllegalArgumentException exc){
          //String respRand = xor(xor(response, key), sKey);
          //if(rand.substring(0, respRand.length()).equals(respRand)) return ValidationType.VALID;
          if(rand.substring(0, response.length()).equals(response)) return ValidationType.VALID;
          else return ValidationType.WRONG_RESPONSE;
        }
      }else{
        s.close();
        return ValidationType.PAGE_ERROR;
      }*/
		}catch(IOException exc){
			if(isDebug()) exc.printStackTrace();
			return RevampLicense.ValidationType.URL_ERROR;
		}
	}

	/*
    private static String xor(String s1, String s2){
      StringBuilder result = new StringBuilder();
      for(int i = 0; i < (Math.min(s1.length(), s2.length())) ; i++)
        result.append(Byte.parseByte(""+s1.charAt(i))^Byte.parseByte(s2.charAt(i)+""));
      return result.toString();
    }
     */
	public enum ValidationType{
		WRONG_RESPONSE, PAGE_ERROR, URL_ERROR, KEY_OUTDATED, KEY_NOT_FOUND, NOT_VALID_IP, INVALID_PLUGIN, VALID
	}

  /*
  private String toBinary(String s){
    byte[] bytes = s.getBytes();
      StringBuilder binary = new StringBuilder();
      for (byte b : bytes)
      {
         int val = b;
         for (int i = 0; i < 8; i++)
         {
            binary.append((val & 128) == 0 ? 0 : 1);
            val <<= 1;
         }
      }
      return binary.toString();
  }
  */

	public String clientName(){
    /*
    String rand = toBinary(UUID.randomUUID().toString());
    String sKey = toBinary(securityKey);
    String key  = toBinary(licenseKey);
     */
		try {
			//URL url = new URL(validationServer + "?v1=" + xor(rand, sKey) + "&v2=" + xor(rand, key) + "&pl=" + plugin.getName() + "&clientName");
			URL url = new URL(validationServer + "?key=" + licenseKey +"&pl=" + plugin.getName() + "&clientName");
			Scanner s = new Scanner(url.openStream());//questo dovrebbe essere giusto ma ora devo modificare il file php vada
			if(s.hasNext()){
				try {
					String response = s.next();
					s.close();
					return response;
				}catch(IllegalArgumentException exc){
					if (isDebug()) exc.printStackTrace();
					return "Venio catarte casa fioeo de na siora";
				}
			}else{
				s.close();
				return "To mare omo pissa in pie";
			}
		} catch (IOException exc){
			if (isDebug()) exc.printStackTrace();
			return "Va farteo metare recion";
		}
	}
}