package threading;

public class SimpleThread {
	public SimpleThread() {
//		Thread t1 = new AddSubThread("t1");
//		Thread t2 = new AddSubThread("t2");
//		Thread t3 = new AddSubThread("t3");
//	
//		t1.start();
//		t2.start();
//		t3.start();
	}
	
	public static void main(String args[]) {
		new SimpleThread();
	}
}

class AddSubThread extends Thread{
	private String mName;
	
	public AddSubThread(String name) {
		super(name);
		
		mName = name;
	}
	
	@Override
	public void run() {
		long startTime = System.nanoTime();
		
		int c = 0;
		for (long i = 0; i < Long.MAX_VALUE; i++) {
			c += 2;
			c -= 2;
		}
		
		System.out.println("C is: " + c);
		
		long timePassed = System.nanoTime() - startTime;
		System.out.println(mName + " took: " + (timePassed / 1000000000.0) + "s");
	}
}