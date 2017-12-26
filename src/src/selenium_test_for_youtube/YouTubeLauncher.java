package selenium_test_for_youtube;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

class ServiceFailedException extends Exception
{
	ServiceFailedException(String message)	{
		super(message);		
	}
}

public class YouTubeLauncher {	
	YouTubeLauncher(){		
		
	}	
	
	private ChromeDriver buildChoromeDriver(int port) {		
		/*
		options.addExtensions(new File("C:\\Users\\amila.rathnayake\\Desktop\\Selenium\\Free-Proxy-to-Unblock-any-sites-_-Touch-VPN_v1.5.12.crx"));
		DesiredCapabilities desiredCap = new DesiredCapabilities().chrome();
		desiredCap.setCapability(ChromeOptions.CAPABILITY, options);
		*/
		ChromeOptions option = new ChromeOptions();
		//option.addArguments("--proxy-server=\"socks://localhost:9050\"");
		ChromeDriver driver = new ChromeDriver(option);
		return driver;
	}	
	

	private FirefoxDriver buildFireFoxDriverForConnectingTor(int port) {
		
		System.setProperty("webdriver.gecko.driver", m_applicationRoot + "\\bin\\geckodriver-v0.19.1-win64\\geckodriver.exe");
				
		FirefoxOptions fireFoxStockOption = new FirefoxOptions();
			
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("network.proxy.type", 1);
		profile.setPreference("network.proxy.socks", "127.0.0.1");
		profile.setPreference("network.proxy.socks_port", port);
		fireFoxStockOption.setProfile(profile);
		
		FirefoxDriver driver = new FirefoxDriver(fireFoxStockOption);		
		return driver;
	}

	private void killTor() 
	{		
	}
	
	private void killFireFox() 
	{
	}
	
	private void runTor(int port) throws ServiceFailedException
	{
		String torProcessPath 	= m_applicationRoot + "\\bin\\Tor\\Tor.exe";
		String torConfigFile 	= m_applicationRoot + "\\bin\\Tor\\Data\\torrc-defaults";
		String[] cmd = { torProcessPath, "-f", torConfigFile, "--SocksPort", Integer.toString(port)};
		
       	try {
			
			m_torProcess = Runtime.getRuntime().exec(cmd);	
			
			final float TOR_SERVICE_TIME_OUT_SECONDS = 10.0f;
			
			/** Waiting for Tor to bootup. */
			BufferedReader in = new BufferedReader(new InputStreamReader(m_torProcess.getInputStream()));		
			String line;			

			long startTime = System.currentTimeMillis();
			while ((line = in.readLine()) != null) 
			{
				long currentTime = System.currentTimeMillis();				
				if((currentTime - startTime) >= TOR_SERVICE_TIME_OUT_SECONDS * 1000.0) throw new ServiceFailedException("Starting Tor Failed");			
				
				Utility.log(LogLevel.Debug, line);
				String completionCheckStr = "Bootstrapped 100%: Done";
				
				if(line.toLowerCase().contains(completionCheckStr.toLowerCase())) break;	
			}			
			
		} catch (Exception e) {
			 throw new ServiceFailedException("Starting Tor Failed");	
		}    	
	}

	public void cleanUp() 
	{		
		if(m_driver != null)
			m_driver.close();	
		
		if(m_torProcess != null)
		{
			m_torProcess.destroyForcibly();		
			while(m_torProcess.isAlive());
		}
		
		m_driver 		= null;
		m_torProcess 	= null;
	}

	public void viewUsingQniqueIP(String url)
	{		
		//runTorBrowserBundle();				
		killFireFox();
		killTor();
		
		try {			
			runTor(4500);
	
			m_driver = buildFireFoxDriverForConnectingTor(9050);

			m_driver.get("https://api.ipify.org?format=json");
			Utility.log(LogLevel.Debug, "Waiting for IP response ");

			WebElement elemet = m_driver.findElement(By.tagName("html"));
			String ipText = elemet.getText();
			Utility.log(LogLevel.Debug, "Current ip is : " + ipText);

			m_driver.get(url);
			Utility.waitFor(5000);

			cleanUp();
			
		} catch (ServiceFailedException e) {
			cleanUp();
			e.printStackTrace();			
		}		
	}
	
	private WebDriver 	m_driver 		= null;	
	private Process 	m_torProcess 	= null;
	
	private String 		m_applicationRoot = "\\";
}
