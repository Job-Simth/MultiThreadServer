package team.shangkemoyu.loan;

import java.io.*;
import java.net.*;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MultiThreadServer extends Application {
	private TextArea ta = new TextArea();
	private int clientNo = 0;

	@Override
	public void start(Stage primaryStage) {

		Scene scene = new Scene(new VBox(ta), 450, 170);
		primaryStage.setTitle("MultiThreadServer");
		primaryStage.setScene(scene);
		primaryStage.show();

		new Thread(() -> {
			try {
				@SuppressWarnings("resource")
				ServerSocket serverSocket = new ServerSocket(8080);
				ta.appendText("MultiThreadServer started at " + new Date() + "\n");
				while (true) {
					Socket socket = serverSocket.accept();
					clientNo++;
					Platform.runLater(() -> {
						ta.appendText("Starting thread for client " + clientNo + "at "+ new Date() + "\n");
						InetAddress inetaddress = socket.getInetAddress();
						ta.appendText("Client " + clientNo + "'s host name is " + inetaddress.getHostName() + "\n");
						ta.appendText("Client " + clientNo + "at " + inetaddress.getHostAddress() + "\n");
					});
					new Thread(new HandleAClient(socket)).start();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}).start();
	}

	class HandleAClient implements Runnable {
		private Socket socket;

		public HandleAClient(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
				DataOutputStream outputFromClient = new DataOutputStream(socket.getOutputStream());
				while (true) {
					Date dateClientConnected = new Date();
					double annualInterestRate = inputFromClient.readDouble();
					int numberOfYears = inputFromClient.readInt();
					double loanAmount = inputFromClient.readDouble();

					double monthlyInterestRate = annualInterestRate / 1200;
					double monthlyPayment = loanAmount * monthlyInterestRate
							/ (1 - 1 / Math.pow(1 + monthlyInterestRate, numberOfYears * 12));
					double totalPayment = monthlyPayment * numberOfYears * 12;

					outputFromClient.writeDouble(monthlyPayment);
					outputFromClient.writeDouble(totalPayment);

					Platform.runLater(() -> {
						ta.appendText("Connected to a client at " + dateClientConnected + '\n');
						ta.appendText("Annual Interest Rate: " + annualInterestRate + '\n');
						ta.appendText("Number Of Years: " + numberOfYears + '\n');
						ta.appendText("Loan Amount: " + loanAmount + '\n');
						ta.appendText("monthlyPayment: " + monthlyPayment + '\n');
						ta.appendText("totalPayment: " + totalPayment + '\n');
					});
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
