package assignment2;

import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class FileShareServer {
	public static void main(String[]args) {
		try {
			
			//COBRA server startup
			
			//1. Turn COBRA on
			//Start CORBA so the server can communicate with remote clients
			ORB orb = ORB.init(args, null);
			
			//2. Get Root POA
			//Obtain manager responsible for finding correct servant when client request arrives 
			org.omg.CORBA.Object obj =
                    orb.resolve_initial_references("RootPOA");

			//convert COBRA object into a POA java understands
            POA rootPOA = POAHelper.narrow(obj);
            
            //3. Create servant
            FileShareImpl fileShareImpl = new FileShareImpl();
            
            //4. Register servant with COBRA
            //Point of first contact who finds worker "fileShareImpl"
            FileShare fileShare = fileShareImpl._this(orb);
            
            //5. Convert the servant into an IOR string.
            //Create servant's address so client's can find it
            String ref = orb.object_to_string(fileShare);
            
            //6. Publish address so clients can read it later
            PrintWriter writer =
                    new PrintWriter(new FileOutputStream("FileShare.ref"));

            writer.println(ref);
            writer.close();
            
            //7. Activate POA Manager
            //Open business, servant is now ready to accept client requests
            rootPOA.the_POAManager().activate();
            
            //8. Run the ORB and wait for client requests
            orb.run();
			
		} catch(Exception e) {
			System.out.println(e);
		}
	}
}
