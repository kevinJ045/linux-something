import java.util.Vector;
import java.lang.Thread;

public class MemoryEater{

	public static void main (String[] args){
		try{
			Vector v = new Vector();
			
			while(true){
				byte b[] = new byte[1048576];
				v.add(b);
				Runtime rt = Runtime.getRuntime();
				System.out.println("\rMemory: "+ rt.freeMemory());
			}
		} catch (OutOfMemoryError e){
			Thread t = new Thread();

			System.out.println("Ooops... Run out of Ram");

			t.start();

			while(true){
				
			}
			
		}
		
	}
	
}
