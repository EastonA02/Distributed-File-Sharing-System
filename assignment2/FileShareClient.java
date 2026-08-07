package assignment2;

import java.util.Scanner;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;

import org.omg.CORBA.ORB;

import assignment2.FileSharePackage.ClientInfo;

public class FileShareClient {
	public static void main(String[]args) {
		
		try {
			
			//COBRA client startup 
			
			//1. Turn COBRA on
			//Allow client to send requests
			ORB orb = ORB.init(args, null);
			
			//2. Read the servers IOR (address) - from server class
			Scanner reader = new Scanner(new File("FileShare.ref"));
			String ref = reader.nextLine();
			reader.close();
			
			//3, Convert the IOR address string into a CORBA object
			org.omg.CORBA.Object obj = //text version of server's address
                    orb.string_to_object(ref);
			
			//4. Narrow object into FileShare reference
			FileShare fileShare = //generic obj created from server address
                    FileShareHelper.narrow(obj);
			
			// We now have:
            // 	the client ORB
            //	a connection to the server
            //	a FileShare remote reference
			
			//5. Invoke remote methods
			
			Scanner input = new Scanner(System.in);
			
			// Identify which client is running.
            System.out.println("Which client are you?");

            System.out.println("1. Client1");
            System.out.println("2. Client2");

            int clientSelection = input.nextInt();

            String ownerID = "";
            int myPort = 0;

            switch (clientSelection) {

                case 1:
                    ownerID = "Client1";
                    myPort = 5001;
                    break;

                case 2:
                    ownerID = "Client2";
                    myPort = 5002;
                    break;

                default:
                    System.out.println("Invalid client selection.");
                    input.close();
                    return;
            }
            
            final int listeningPort = myPort;
            
            Thread serverThread = new Thread() {
            	@Override
            	public void run() {
            		try {
            			
            			//Create server socket
            			ServerSocket serverSocket = new ServerSocket(listeningPort);
            			
            			while(true) {
            				
            				//connect to requesting client
            				Socket ownerSocket = serverSocket.accept();
            				
            				//to read request file name
            				BufferedReader reader = new BufferedReader
            						(new InputStreamReader
            								(ownerSocket.getInputStream()));
            				
            				//save file name
            				String requestedFile = reader.readLine();
            				
            				//File object representing requested file
            				File file = new File("src/assignment2/shared/" + requestedFile);

            				//send file bytes to requesting client
            				OutputStream sout = ownerSocket.getOutputStream();
            				
            				if (file.exists()) {

            					//read bytes from file
            				    FileInputStream fileInput = new FileInputStream(file);

            				    //File input stream reads files in small bytes
            				    //Used to temporary store those bytes 
            				    byte[] buffer = new byte[4096];

            				    int bytesRead;

            				    //keep reading until end of file
            				    while ((bytesRead = fileInput.read(buffer)) != -1) {

            				    	//send bytes to requesting client
            				        sout.write(buffer, 0, bytesRead);
            				    }

            				    sout.flush();

            				    fileInput.close();
            				    sout.close();
            				    ownerSocket.close();

            				    System.out.println(
            				            requestedFile + " was sent."
            				    );

            				} else {

            				    System.out.println(
            				            requestedFile + " was not found."
            				    );

            				    ownerSocket.close();
            				}
            				
            			}
            			
            		} catch(Exception e) {
            			System.out.println(e);
            		}
            	}
            };
            
            serverThread.start();
            
            int choice = 0;
			
			while(choice!= 5) {
				
				System.out.println("----File Sharing----");
				
				System.out.println("1. Register File");
				System.out.println("2. Update File");
				System.out.println("3. Search File");
				System.out.println("4. Download File");
				System.out.println("5. Exit\n");
				
				System.out.println("Choice: ");
				choice = input.nextInt();
			
				switch(choice) {
				case 1:
			        // Ask for filename
					System.out.println("Which file would you like to register?");
					
					System.out.println("1. assignment2PDF.pdf");
					System.out.println("2. hello.txt");
					System.out.println("3. notes.txt");
					System.out.println("4. puppy.jpeg");
					
					int registerSelection = input.nextInt();
					
					//convert selection into filename
					String fileName = "";
					
					switch(registerSelection) {
						case 1:
					        fileName = "assignment2PDF.pdf";
					        break;
		
					    case 2:
					        fileName = "hello.txt";
					        break;
		
					    case 3:
					        fileName = "notes.txt";
					        break;
		
					    case 4:
					        fileName = "puppy.jpeg";
					        break;
		
					    default:
					        System.out.println("Invalid file selection.");
					        break;
					}
					
			        // Call fileShare.registerFile()
					if (!fileName.equals("")) {
	
			            // Remote CORBA call:
			            // tell the server to save this file under this owner
			            fileShare.registerFile(ownerID, fileName, true);
	
			            System.out.println(fileName + " was registered.");
			        }
					
			        break;
	
			    case 2:
			        // Ask for filename and shared status
			    	System.out.println("Which file would you like to update?");
					
					System.out.println("1. assignment2PDF.pdf");
					System.out.println("2. hello.txt");
					System.out.println("3. notes.txt");
					System.out.println("4. puppy.jpeg");
					
					int updateSelection = input.nextInt();
					String updateFileName = "";
					
					switch(updateSelection) {
						case 1:
					        updateFileName = "assignment2PDF.pdf";
					        break;
		
					    case 2:
					        updateFileName = "hello.txt";
					        break;
		
					    case 3:
					        updateFileName = "notes.txt";
					        break;
		
					    case 4:
					        updateFileName = "puppy.jpeg";
					        break;
		
					    default:
					        System.out.println("Invalid file selection.");
					        break;
					}
					
					System.out.println("Share this file? \n 1. Yes\n 2. No");
					int shareChoice = input.nextInt();
					
					boolean shared = false;
					
					if(shareChoice ==1) {
						shared = true;
					} else {
						shared = false;
					}
					
			        // Call fileShare.updateFile(...)
					if(!updateFileName.equals("")) {
						
						fileShare.updateFile(ownerID, updateFileName, shared);
					}
					
			        break;
	
			    case 3:
			        // Ask for filename
			    	System.out.println("Which file would you like to search for?");
					
					System.out.println("1. assignment2PDF.pdf");
					System.out.println("2. hello.txt");
					System.out.println("3. notes.txt");
					System.out.println("4. puppy.jpeg");
					
					int searchSelection = input.nextInt();
					
					String searchFileName = "";
					
					switch(searchSelection) {
						case 1:
					        searchFileName = "assignment2PDF.pdf";
					        break;
		
					    case 2:
					        searchFileName = "hello.txt";
					        break;
		
					    case 3:
					        searchFileName = "notes.txt";
					        break;
		
					    case 4:
					        searchFileName = "puppy.jpeg";
					        break;
		
					    default:
					        System.out.println("Invalid file selection.");
					        break;
					}
			    	
			        // Call fileShare.searchFile(...)
					if(!searchFileName.equals("")) {
						boolean found = fileShare.searchFile(searchFileName);
						
						if(found) {
							System.out.println(searchFileName + " is availble for download");
						} else {
							System.out.println(
				                    searchFileName + " was not found or is not shared.");
						}
					}
					
			        break;
	
			    case 4:
			        // Ask for filename
			    	System.out.println("Which file would you like to download?");
			    	
			    	System.out.println("1. assignment2PDF.pdf");
			    	System.out.println("2. hello.txt");
			    	System.out.println("3. notes.txt");
			    	System.out.println("4. puppy.jpeg");
			    	
			    	int downloadSelection = input.nextInt();
					
					String downloadFileName = "";
					
					switch(downloadSelection) {
						case 1:
					        downloadFileName = "assignment2PDF.pdf";
					        break;
		
					    case 2:
					        downloadFileName = "hello.txt";
					        break;
		
					    case 3:
					        downloadFileName = "notes.txt";
					        break;
		
					    case 4:
					        downloadFileName = "puppy.jpeg";
					        break;
		
					    default:
					        System.out.println("Invalid file selection.");
					        break;
					}
			        // Call fileShare.downloadFile(...)
					if(!downloadFileName.equals("")) {
						
						//downloadFile() returns clientInfo object
						ClientInfo clientInfo =
						        fileShare.downloadFile(downloadFileName);
						
						if(clientInfo!= null) {
							
						//read its fields and save ip-addr + port
						String ipAddress = clientInfo.ipAddress;
						int port = clientInfo.port;
						
						System.out.println("File owner found.");
			            System.out.println("IP address: " + ipAddress);
			            System.out.println("Port: " + port + "\n");
			            
			            //Down-loader creates Socket using ipAddress and port
			            Socket requestingSocket = new Socket(ipAddress, port);
			            
			            //request to download file from other client
			            PrintWriter output =
			                    new PrintWriter(requestingSocket.getOutputStream(), true);

			            output.println(downloadFileName);
			            
			            //read incoming file bytes from owner's socket
			            BufferedInputStream in = new BufferedInputStream
        						((requestingSocket.getInputStream()));
			            
			          //Create downloaded copy inside assignment2.received
			            File downloadedFile =
			                    new File("src/assignment2/received/" + downloadFileName);
			            
			          //Open file for writing
			            FileOutputStream fileOutput =
			                    new FileOutputStream(downloadedFile);
			            
			            //buffer for incoming bytes
			            byte[] buffer = new byte[4096];

			            int bytesRead;

			            //receive bytes and write them into downloaded file
			            while ((bytesRead = in.read(buffer)) != -1) {

			            	fileOutput.write(buffer, 0, bytesRead);
			            }

			            System.out.println(downloadFileName + " was downloaded.");

			            fileOutput.close();
			            in.close();
			            requestingSocket.close();
			            
						}
						
					}
					
			        break;
	
			    case 5:
			        System.exit(0);
						
				}
				
				
			}	
		} catch(Exception e) {
			System.out.println(e);
		}
	}
}
