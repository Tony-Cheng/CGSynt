package cgsynt.loop;

public class CegarRunner {
	CegarRunner(){
		ILoop loop = new VerificationLoop(100);
		
		loop.start();
		System.out.println(loop.getStatus());
	}
	
	public static void main(String args[]) {
		new CegarRunner();
	}
}
