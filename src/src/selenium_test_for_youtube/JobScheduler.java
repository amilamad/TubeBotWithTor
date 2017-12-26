package selenium_test_for_youtube;

public class JobScheduler
{
	public static void main(String[] args) {	
		 
		YouTubeLauncher youTubeStramer = new YouTubeLauncher();
			
		Runtime.getRuntime().addShutdownHook(new Thread() {
		      public void run() {
		        System.out.println("Running Shutdown Hook");
		        youTubeStramer.cleanUp();
		      }
		 });
	
		for (int i = 0; i < 2; i++)
		{		
			youTubeStramer.viewUsingQniqueIP("https://www.youtube.com/watch?v=6M-CDtq2Jlg");
		}	
		
		youTubeStramer.cleanUp();
	}	
}