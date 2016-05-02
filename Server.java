import java.util.*;
import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.concurrent.locks.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Server extends Application{
	private int clientNo = 0;
	Hashtable<String, String> userpw = new Hashtable<>();
	Hashtable<String, SocketNode> usersk = new Hashtable<>();
	
	@Override
	public void start(Stage primaryStage){
		new Thread(()-> {  //Thread listening ro user signup
			try{
				ServerSocket serverSocket = new ServerSocket(8001);
								
				while(true){
					Socket socket = serverSocket.accept();
					System.out.println("a new user wants to sign up at " + new Date());
					ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
					DataOutputStream responseToClient = new DataOutputStream(socket.getOutputStream());
					try{
						UserPassword loginPair = (UserPassword)fromClient.readObject();
						String userName = loginPair.getUser();
						String passWord = loginPair.getPassword();
					
						if(userpw.containsKey(userName)){
							responseToClient.writeBoolean(false);
							System.out.println("already registered");
						}
						else{
							userpw.put(userName,passWord);
							responseToClient.writeBoolean(true);
							System.out.println("registered successfully!");	
						}
					}
					catch(IOException ex){
						try{
							fromClient.close();
							responseToClient.close();
							socket.close();
				 			System.err.println("Unable to register");
						}
						catch(IOException es) {
							es.printStackTrace();
						}					
					}
					catch(ClassNotFoundException e){
							System.err.println(e);
					}
				}
			}
			catch(IOException ex){
				 System.err.println(ex);
			}		
				
		}).start();	
			
		new Thread(()-> {   //Thread listening for user login
			try {
				ServerSocket serverSocket = new ServerSocket(8000);
				System.out.println("MultiThreadServer started at " + new Date());
				
				while(true){
				 	Socket socket = serverSocket.accept();
				 	ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
				 	DataOutputStream responseToClient = new DataOutputStream(socket.getOutputStream());
				 	try{
				 		UserPassword loginPair = (UserPassword)fromClient.readObject();
				 		String userName = loginPair.getUser();
				 		String passWord = loginPair.getPassword();
				 		if(userpw.containsKey(userName)){
				 			if(userpw.get(userName).equals(passWord)){
				 					clientNo++;
				 	
				 					System.out.println("Starting thread for client " + clientNo + " at " + new Date());		
				 					InetAddress inetAddress = socket.getInetAddress();
				 					System.out.println("Clients" + clientNo +  "'s host name is " + inetAddress.getHostName());
				 					System.out.println("Client " + clientNo + "'s IP Address is " + inetAddress.getHostAddress());
				 	
				 					DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
									SocketNode socketNode = new SocketNode(socket,inputFromClient,responseToClient);
				 					usersk.put(userName,socketNode);
				 					responseToClient.writeBoolean(true);
				 					
				 					//Thread for message passing of this client
				 					new Thread(new HandleAClient(userName,socketNode)).start();
							}
							else{
								System.out.println("username or password incorrect");
								responseToClient.writeBoolean(false);
							}
						}
						else{
							System.out.println("user does not exist");
							responseToClient.writeBoolean(false);
						}
					}	
					catch(ClassNotFoundException e){
						System.err.println(e);
					}
					catch(IOException ex){
						try{
							fromClient.close();
							responseToClient.close();
							socket.close();
				 			System.err.println(ex);
						}
						catch(IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
			
		}).start();
	}
	
	class SocketNode{
		private Socket socket;
		private DataInputStream inputFromClient;
		private DataOutputStream outputToClient;
		
		public SocketNode(Socket socket,DataInputStream input, DataOutputStream out){
			this.socket = socket;
			this.inputFromClient = input;
			this.outputToClient = out;
		}
		
		public Socket getSocket(){
			return this.socket;
		}
		
		public DataInputStream getInputStream(){
			return this.inputFromClient;
		}
		
		public DataOutputStream getOutputStream(){
			return this.outputToClient;
		}
	}
			
	class HandleAClient implements Runnable { 
		private SocketNode socketNode;
		private String userName;
		
		public HandleAClient(String userName,SocketNode socketNode){
			this.userName = userName;
			this.socketNode = socketNode;
		}
		
		public void run() {
				try {
					DataInputStream inputFromClient = socketNode.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputFromClient));
					while(true) {
						try{
							String message = reader.readLine();
							if(message == null){
								throw new NullPointerException();
							}
						
							System.out.println(message);
							Iterator<String> keys = usersk.keySet().iterator();
							while(keys.hasNext()){
								String key = keys.next();
								SocketNode node = usersk.get(key);
								PrintWriter writer = new PrintWriter(node.getOutputStream(),true);
								writer.println(message);
							}
							
						}
						catch(NullPointerException e){
							try{
								socketNode.getInputStream().close();
								socketNode.getOutputStream().close();
								socketNode.getSocket().close();
								usersk.remove(userName);
								clientNo--;
								break;
							}
							catch(IOException ex) {
								ex.printStackTrace();
							}
						}
					}
				}
				catch(IOException e) {
					e.printStackTrace();
				}
		}
	}
}			 		
	