package application;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class LoadShipment {
	Alerts alerts = new Alerts();
	Category category = Main.category;
	ProductDisplay productDisplay = Main.productDisplay;
	ShipmentDisplay shipmentDisplay = Main.shipmentDisplay;

	public void Display() {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select Shipment File");
		if (Files.isDirectory(Main.getDataDirectory())) {
			fc.setInitialDirectory(Main.getDataDirectory().toFile());
		}
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV and text files", "*.csv", "*.txt"));
		File f = fc.showOpenDialog((Stage) null);
		if (f == null) {
			return;
		}

		List<Shipment> loadedShipments = new ArrayList<>();
		Set<Integer> loadedIds = new HashSet<>();
		int skippedLines = 0;
		try (Scanner scanner = new Scanner(f, StandardCharsets.UTF_8)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				if (line.isEmpty()) {
					continue;
				}

				String[] data = line.split(",", 4);
				try {
					if (data.length != 4) {
						throw new IllegalArgumentException("Expected four values");
					}
					int shipmentId = Integer.parseInt(data[0].trim());
					int productId = Integer.parseInt(data[1].trim());
					int amount = Integer.parseInt(data[2].trim());
					String date = data[3].trim();
					LocalDate.parse(date);
					if (shipmentId <= 0 || amount <= 0 || !loadedIds.add(shipmentId)) {
						throw new IllegalArgumentException("Invalid or duplicate shipment");
					}

					Product matchedProduct = null;
					for (int i = 0; i < productDisplay.productList.size(); i++) {
						Product p = productDisplay.productList.get(i);
						if (p.getProductId() == productId) {
							matchedProduct = p;
							break;
						}
					}
					if (matchedProduct == null) {
						throw new IllegalArgumentException("Unknown product");
					}
					loadedShipments.add(new Shipment(shipmentId, amount, date, matchedProduct));
				} catch (Exception e) {
					skippedLines++;
				}
			}
		} catch (IOException e) {
			alerts.ErrorAlert("Error", "Could not read the shipment file: " + e.getMessage());
			return;
		}

		if (loadedShipments.isEmpty()) {
			alerts.ErrorAlert("Error", "The selected file contains no valid shipments.");
			return;
		}

		category.clearShipments();
		for (Shipment loadedShipment : loadedShipments) {
			category.addShipment(loadedShipment);
		}
		shipmentDisplay.shipmentList.setAll(loadedShipments);

		String message = "Loaded " + loadedShipments.size() + " shipments successfully.";
		if (skippedLines > 0) {
			message += " Skipped " + skippedLines + " invalid line(s).";
		}
		alerts.InfoAlert("Success", message);
	}
}
