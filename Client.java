import java.io.*;
import java.net.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Client extends Application {
	DataOutputStream toServer = null;
	DataInputStream fromServer = null;
	ObjectOutputStream requestToServer = null;
	DataInputStream responseFromServer = null;
	PrintWriter writer = null;
	VBox vbox = new VBox(5);
	ListView clientListView = new ListView();
	TextField inputMessage = new TextField();
	TextField userTextField = new TextField();
	Text signup = new Text("Sign Up");	
	Button loginbtn = new Button("Sign in");
	Button signbtn = new Button("Sign up");
	PasswordField pwBox = new PasswordField();
	PasswordField repwBox = new PasswordField();
	
	public Pane getMessagePane(String message){
			FlowPane flow = new FlowPane();
			Text username = new Text("User");
			username.setFont(Font.font("Times New Roman",15));
			username.setFill(Color.web("#C0C0C0"));
			Text text = new Text(message);
			text.setFont(Font.font("Courier",15));
			text.setFill(Color.web("#0000FF"));
			TextFlow textflow = new TextFlow(text);
			textflow.setBackground(new Background(
			new BackgroundFill(Color.web("#CCFFFF"),new CornerRadii(3),new Insets(0,0,0,0))));
			textflow.setPadding(new Insets(10));
			BorderPane messagePane = new BorderPane();
			messagePane.setTop(username);
			messagePane.setCenter(textflow);
			messagePane.setMaxWidth(350);
			messagePane.setMargin(username,new Insets(0,0,10,0));
			Circle circle = new Circle(13,Color.RED);
			flow.getChildren().addAll(circle,messagePane);
			flow.setAlignment(Pos.CENTER_LEFT);
			FlowPane.setMargin(messagePane,new Insets(0,0,0,10));
			flow.setRowValignment(VPos.TOP);
			return flow;
	}
	
	public Scene getClientScene(){
			BorderPane paneForInput = new BorderPane();
			paneForInput.setPadding(new Insets(10,10,10,10));
			paneForInput.setStyle("-fx-border-color:green");
		
			inputMessage.setAlignment(Pos.BOTTOM_RIGHT);
			inputMessage.setPromptText("message");
			paneForInput.setCenter(inputMessage);
		
			BorderPane messagePane = new BorderPane();
			ScrollPane scrollPane = new ScrollPane(vbox);
			scrollPane.setPrefWidth(800);
			vbox.heightProperty().addListener((ov, oldVal, newVal) ->{
				scrollPane.setVvalue(((Double)newVal).doubleValue());
			});
			messagePane.setCenter(scrollPane);
			messagePane.setBottom(paneForInput);
		
			clientListView.setEditable(false);
			clientListView.setPrefWidth(120);
			BorderPane mainPane = new BorderPane();
			mainPane.setLeft(clientListView);
			mainPane.setCenter(messagePane);
			mainPane.setPrefSize(600,600);
			Scene scene = new Scene(mainPane);
			return scene;
	}
	
	public Scene getLoginScene(){
			GridPane grid = new GridPane();
			grid.setAlignment(Pos.CENTER);
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(25,25,25,25));
			
			Text welcomeTitle = new Text("Welcome");
			welcomeTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL,20));
			grid.add(welcomeTitle,1,0,2,1);
			
			Label username = new Label("User Name:");
			grid.add(username,0,2);
			grid.add(userTextField,1,2);
			
			Label pw = new Label("Password:");
			grid.add(pw,0,3);
			grid.add(pwBox,1,3);
			
			HBox hbBtn = new HBox(10);
			hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
			hbBtn.getChildren().add(loginbtn);
			grid.add(hbBtn,1,5);
			
			signup.setFont(Font.font(10));
			signup.setUnderline(true);
			signup.setFill(Color.BLUE);
			grid.add(signup,0,5);
			return new Scene(grid,400,375);
				
	}
	
	public Scene getSignUpScene(){
			GridPane grid = new GridPane();
			grid.setAlignment(Pos.CENTER);
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(25,25,25,25));
			
			Text welcomeTitle = new Text("User Registration"); 
			welcomeTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL,20));
			grid.add(welcomeTitle,0,0,2,1);
			
			Label username = new Label("User Name:");
			grid.add(username,0,2);
			grid.add(userTextField,1,2);
			
			Text pw = new Text("Enter your password:");
			pw.setFont(Font.font(10));
			grid.add(pw,0,3);
			grid.add(pwBox,1,3);
			
			Text repw = new Text("Re-enter your password:");
			repw.setFont(Font.font(10));
			grid.add(repw,0,4);
			grid.add(repwBox,1,4);
			
			HBox hbBtn = new HBox(10);
			hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
			hbBtn.getChildren().add(signbtn);
			grid.add(hbBtn,1,6);
			
			return new Scene(grid,400,375);
				
	}
	
	public void showLoginPage(Stage primaryStage){
		primaryStage.setTitle("Chatting Time!");
		primaryStage.setScene(getLoginScene());
		primaryStage.show();
	}
	
	public void showSignUpPage(Stage primaryStage){
		primaryStage.setTitle("Sign Up");
		primaryStage.setScene(getSignUpScene());
		primaryStage.show();	
	}
	
	public void showClientPage(Stage primaryStage){
		primaryStage.setTitle("Client");
		primaryStage.setScene(getClientScene());
		primaryStage.show();
	}
	
	
	@Override
	public void start(Stage primaryStage) {
				
		showLoginPage(primaryStage);
		
		inputMessage.setOnAction(e -> {
				String message = inputMessage.getText();
				writer.println(message);
				inputMessage.clear();
		});
		
		signup.setOnMouseClicked(e -> {
				showSignUpPage(primaryStage);
		});
		
		signbtn.setOnAction(e ->{
				String pw1 = pwBox.getText();
				String pw2 = repwBox.getText();
				if(pw1.equals(pw2)){
					try{
						Socket socket = new Socket("localhost",8001);
						responseFromServer = new DataInputStream(socket.getInputStream());	
						requestToServer = new ObjectOutputStream(socket.getOutputStream());
						
						String userName = userTextField.getText();
						UserPassword userpw = new UserPassword(userName,pw1);
						requestToServer.writeObject(userpw);
						Boolean response = responseFromServer.readBoolean();
						if(response){
							pwBox.clear();
							repwBox.clear();
							showLoginPage(primaryStage);
						}
						else{
							pwBox.clear();
							repwBox.clear();
						}
					}
					catch(IOException ex){
						System.err.println(ex);
					}			
				}
				else{
					pwBox.clear();
					repwBox.clear();
					System.out.println("unmatched password!");
				}
		});
		
		loginbtn.setOnAction(e -> {
			try{
                Socket socket = new Socket("localhost",8000);
				responseFromServer = new DataInputStream(socket.getInputStream());	
				requestToServer = new ObjectOutputStream(socket.getOutputStream());
				
				String userName = userTextField.getText();
				String passWord = pwBox.getText();
				UserPassword userpw = new UserPassword(userName,passWord);
				requestToServer.writeObject(userpw);
				Boolean response = responseFromServer.readBoolean();
				if(response){
					showClientPage(primaryStage);
					toServer = new DataOutputStream(socket.getOutputStream());
					writer = new PrintWriter(toServer,true);
					new Thread(new PortListening(socket)).start();		
				}
				else{
					userTextField.clear();
					pwBox.clear();				
				}
			}
			catch(IOException ex){
				System.err.println(ex);
			}		
		});			
	}
		
	class PortListening implements Runnable{
		private Socket socket;
		private DataInputStream fromServer;
		private BufferedReader reader;
			
		public PortListening(Socket socket){
			this.socket = socket;	
		}
			
		@Override
		public void run(){
			while(true){
				try{
					fromServer = new DataInputStream(socket.getInputStream());
					reader = new BufferedReader(new InputStreamReader(this.fromServer));		
					String message = reader.readLine();
					if(message == null){
						throw new NullPointerException();
					}
					Platform.runLater(() ->{
						Pane messagePane = getMessagePane(message);
						VBox.setMargin(messagePane,new Insets(5,5,20,5));
						vbox.getChildren().add(messagePane);
					});
				}
				catch(NullPointerException e){
					try{
						fromServer.close();
						socket.close();
						System.out.println("close connection");
						break;
					}
					catch(IOException ex){
					System.err.println(ex);
					break;
					}
				}
				catch(IOException ex){
					System.err.println(ex);
					break;
				}
			}
		}
	}
}	
