import java.util.*;
import java.io.*;
public class UserPassword implements Serializable{
	private String userName;
	private String passWord;
	
	public UserPassword(String userName,String passWord){
		this.userName = userName;
		this.passWord = passWord;
	}
	
	public String getUser(){
		return this.userName;
	}
	
	public String getPassword(){
		return this.passWord;
	}
}