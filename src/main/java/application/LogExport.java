package application;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import javafx.stage.FileChooser;

public class LogExport {
	Alerts a = new Alerts();
	Shipment shipment;
	
	// Constructor: takes a shipment to be logged
	public LogExport(Shipment shipment) {
		this.shipment = shipment;
	}

	public void Display() {
		if (shipment == null) {
			exportLog();
			return;
		}
		if (shipment.getActionType() == null || shipment.getProduct() == null) {
			return;
		}

		Path logFile = Main.getLogFile();
		try {
			Files.createDirectories(logFile.getParent());
			try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(logFile, StandardCharsets.UTF_8,
					StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
				String action = shipment.getActionType();
				String sign = action.equalsIgnoreCase("Cancel Shipment") ? "-" : "+";
				writer.println(shipment.getDate() + " | " + action + " | SHP" + shipment.getShipmentId() + " | P"
						+ shipment.getProduct().getProductId() + " | " + sign + shipment.getQuantity());
			}
		} catch (IOException e) {
			a.ErrorAlert("Log Error", "Could not write to the activity log: " + e.getMessage());
		}
	}

	private void exportLog() {
		Path logFile = Main.getLogFile();
		if (Files.notExists(logFile)) {
			a.InfoAlert("Activity Log", "No shipment activity has been recorded yet.");
			return;
		}

		FileChooser chooser = new FileChooser();
		chooser.setTitle("Export Activity Log");
		chooser.setInitialFileName("smart-warehouse-actions.log");
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Log files", "*.log", "*.txt"));
		java.io.File destination = chooser.showSaveDialog(null);
		if (destination == null) {
			return;
		}
		try {
			Files.copy(logFile, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
			a.InfoAlert("Success", "Activity log exported successfully.");
		} catch (IOException e) {
			a.ErrorAlert("Export Error", "Could not export the activity log: " + e.getMessage());
		}
	}
}
