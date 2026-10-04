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

public class LoadCategory {
	Alerts alerts = new Alerts();
	DoubleLinkedList category = Main.linkedList;
	CategoryDisplay categoryTable = Main.categoryDisplay;

	public void Display() {
		if (!Main.productDisplay.productList.isEmpty() || !Main.shipmentDisplay.shipmentList.isEmpty()) {
			alerts.ErrorAlert("Load Order",
					"Categories cannot be replaced while products or shipments are loaded. Restart the application to load a different data set.");
			return;
		}
		FileChooser fc = new FileChooser();
		fc.setTitle("Select Category File");
		if (Files.isDirectory(Main.getDataDirectory())) {
			fc.setInitialDirectory(Main.getDataDirectory().toFile());
		}
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV and text files", "*.csv", "*.txt"));
		File f = fc.showOpenDialog((Stage) null);
		if (f == null) {
			return;
		}

		List<Category> loadedCategories = new ArrayList<>();
		Set<Integer> loadedIds = new HashSet<>();
		int skippedLines = 0;
		try (Scanner scanner = new Scanner(f, StandardCharsets.UTF_8)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				if (line.isEmpty()) {
					continue;
				}

				String[] data = line.split(",", 3);
				try {
					if (data.length != 3) {
						throw new IllegalArgumentException("Expected three values");
					}
					int categoryId = Integer.parseInt(data[0].trim());
					String categoryName = data[1].trim();
					String categoryDescription = data[2].trim();
					if (categoryId <= 0 || categoryName.isEmpty() || categoryDescription.isEmpty()
							|| !loadedIds.add(categoryId)) {
						throw new IllegalArgumentException("Invalid or duplicate category");
					}
					loadedCategories.add(new Category(categoryId, categoryName, categoryDescription));
				} catch (Exception e) {
					skippedLines++;
				}
			}
		} catch (IOException e) {
			alerts.ErrorAlert("Error", "Could not read the category file: " + e.getMessage());
			return;
		}

		if (loadedCategories.isEmpty()) {
			alerts.ErrorAlert("Error", "The selected file contains no valid categories.");
			return;
		}

		category.clear();
		for (Category loadedCategory : loadedCategories) {
			category.addLast(loadedCategory);
		}
		categoryTable.categoryList.setAll(loadedCategories);

		String message = "Loaded " + loadedCategories.size() + " categories successfully.";
		if (skippedLines > 0) {
			message += " Skipped " + skippedLines + " invalid line(s).";
		}
		alerts.InfoAlert("Success", message);
	}
}
