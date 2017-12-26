package selenium_test_for_youtube;

enum LogLevel {
    Warnning,
    Info,
    Debug,
    Fatal;
}

public class Utility
{	
	static void waitFor(int timeMiliSec) {
		try {				
			Thread.sleep(timeMiliSec);	
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	static void log(LogLevel logLevel, String message) 
	{
		System.out.println(message);				
	}
}