package movieApp;

import movieApp.utils.DataBaseUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;

@SpringBootApplication
public class Application {

//	public static void main(String[] args) {
//
//		Connection connection = null;
//		try {
//			connection = DataBaseUtil.getConnection();
//		} catch (Exception ex) {
//			System.out.println("Connection Failed! Check output console");
//		}
//		if (connection != null) {
//			System.out.println("You made it, take control your database now!");
//		} else {
//			System.out.println("Failed to make connection!");
//		}
//	}

	public static void main(String[] args) {




		SpringApplication.run(Application.class, args);
	}
}