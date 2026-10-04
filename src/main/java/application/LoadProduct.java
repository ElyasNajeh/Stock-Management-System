package application;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class LoadProduct {
	Alerts alerts = new Alerts();
	Category category = Main.category;
	ProductDisplay productDisplay = Main.productDisplay;
	CategoryDisplay categoryTable = Main.categoryDisplay;

	public void Display() {
		if (Main.category.hasShipmentData()) {
			alerts.ErrorAlert("Load Order",
					"Products cannot be replaced while shipments are loaded. Restart the application to load a different data set.");
			return;
		}
		FileChooser fc = new FileChooser();
		fc.setTitle("Select Product File");
		if (Files.isDirectory(Main.getDataDirectory())) {
			fc.setInitialDirectory(Main.getDataDirectory().toFile());
		}
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV and text files", "*.csv", "*.txt"));
		File f = fc.showOpenDialog((Stage) null);
		if (f == null) {
			return;
		}

		List<Product> loadedProducts = new ArrayList<>();
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
					int productId = Integer.parseInt(data[0].trim());
					String productName = data[1].trim();
					int categoryId = Integer.parseInt(data[2].trim());
					String status = data[3].trim();
					if (status.equalsIgnoreCase("In Active")) {
						status = "Inactive";
					}
					if (productId <= 0 || productName.isEmpty() || !loadedIds.add(productId)
							|| !(status.equalsIgnoreCase("Active") || status.equalsIgnoreCase("Inactive"))) {
						throw new IllegalArgumentException("Invalid or duplicate product");
					}
					status = status.equalsIgnoreCase("Active") ? "Active" : "Inactive";

					Category matchedCategory = null;
					for (int i = 0; i < categoryTable.categoryList.size(); i++) {
						Category c = categoryTable.categoryList.get(i);
						if (c.getCategoryId() == categoryId) {
							matchedCategory = c;
							break;
						}
					}

					if (matchedCategory == null) {
						throw new IllegalArgumentException("Unknown category");
					}
					loadedProducts.add(new Product(productId, productName, status, matchedCategory));
				} catch (Exception e) {
					skippedLines++;
				}
			}
		} catch (IOException e) {
			alerts.ErrorAlert("Error", "Could not read the product file: " + e.getMessage());
			return;
		}

		if (loadedProducts.isEmpty()) {
			alerts.ErrorAlert("Error", "The selected file contains no valid products.");
			return;
		}

		category.clearProducts();
		for (Product loadedProduct : loadedProducts) {
			category.addProduct(loadedProduct);
		}
		productDisplay.productList.setAll(loadedProducts);

		String message = "Loaded " + loadedProducts.size() + " products successfully.";
		if (skippedLines > 0) {
			message += " Skipped " + skippedLines + " invalid line(s).";
		}
		alerts.InfoAlert("Success", message);
	}
}
