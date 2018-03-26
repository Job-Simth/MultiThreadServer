package team.shangkemoyu.loan;

import java.io.*;
import java.net.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Client extends Application {
	DataOutputStream toServer = null;
	DataInputStream fromServer = null;

	private TextField tfAnnualInterestRate = new TextField();
	private TextField tfNumberOfYears = new TextField();
	private TextField tfLoanAmount = new TextField();

	private Button btSubmit = new Button("Submit");

	@Override
	public void start(Stage primaryStage) {
		BorderPane paneForTextField = new BorderPane();

		tfAnnualInterestRate.setAlignment(Pos.BASELINE_RIGHT);
		tfNumberOfYears.setAlignment(Pos.BASELINE_RIGHT);
		tfLoanAmount.setAlignment(Pos.BASELINE_RIGHT);

		GridPane paneForLoanInfo = new GridPane();
		paneForLoanInfo.add(new Label("Annual Interest Rate"), 0, 0);
		paneForLoanInfo.add(tfAnnualInterestRate, 1, 0);
		paneForLoanInfo.add(new Label("Number Of Years"), 0, 1);
		paneForLoanInfo.add(tfNumberOfYears, 1, 1);
		paneForLoanInfo.add(btSubmit, 2, 1);
		paneForLoanInfo.add(new Label("Loan Amount"), 0, 2);
		paneForLoanInfo.add(tfLoanAmount, 1, 2);

		
		TextArea ta = new TextArea();
		paneForTextField.setTop(paneForLoanInfo);
		paneForTextField.setStyle("-fx-border-color:green");
		paneForTextField.setCenter(new ScrollPane(ta));

		Scene scene = new Scene(paneForTextField, 450, 200);
		primaryStage.setTitle("Client");
		primaryStage.setScene(scene);
		primaryStage.show();

		btSubmit.setOnAction(e -> {
			try {
				double annualInterestRate = Double.parseDouble(tfAnnualInterestRate.getText().trim());

				int numberOfYears = Integer.parseInt(tfNumberOfYears.getText().trim());

				double loanAmount = Double.parseDouble(tfLoanAmount.getText().trim());
				toServer.writeDouble(annualInterestRate);
				toServer.writeInt(numberOfYears);
				toServer.writeDouble(loanAmount);
				toServer.flush();

				Double monthlyPayment = fromServer.readDouble();
				Double totalPayment = fromServer.readDouble();
				
				Platform.runLater(() -> {
				ta.appendText("Annual Interest Rate: " + annualInterestRate + '\n');
				ta.appendText("Number Of Years: " + numberOfYears + '\n');
				ta.appendText("Loan Amount: " + loanAmount + '\n');
				ta.appendText("monthlyPayment: " + monthlyPayment + '\n');
				ta.appendText("totalPayment: " + totalPayment + '\n');
				});
			} catch (IOException ex) {
				System.err.println(ex);
			}
		});
		
		try {
			@SuppressWarnings("resource")
			Socket socket = new Socket("localhost", 8080);
			fromServer = new DataInputStream(socket.getInputStream());
			toServer = new DataOutputStream(socket.getOutputStream());
		} catch (IOException ex) {
			ta.appendText(ex.toString() + '\n');
		}
		
	}
	public static void main(String[] args) {
	    launch(args);
	  }
}
