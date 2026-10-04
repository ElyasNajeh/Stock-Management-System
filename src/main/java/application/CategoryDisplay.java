package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CategoryDisplay {
	IconButton searchButton, refreshButton, backButton, addButton, viewButton, updateButton, removeButton;
	CustomTextField searchField;
	boolean columnsAdded = false;
	TableView<Category> categoryTable = new TableView<>();
	ObservableList<Category> categoryList = FXCollections.observableArrayList();
	Alerts alerts = new Alerts();
	AddCategory addCategory;
	UpdateCategory updateCategory;
	Category category = Main.category;
	ProductDisplay productDisplay = Main.productDisplay;
	DoubleLinkedList linkedList = Main.linkedList;

	// Force deletes a category and all its products
	public void forceDelete(int categoryIndex) {
		Category selectedCategory = categoryList.get(categoryIndex);
		for (int i = productDisplay.productList.size() - 1; i >= 0; i--) {
			Product allProducts = productDisplay.productList.get(i);
			if (allProducts.getCategory().getCategoryId() == selectedCategory.getCategoryId()) {
				category.removeProduct(allProducts);
				productDisplay.productList.remove(allProducts);
			}

		}
	}

	// Shows dialog with 3 options before deleting a category that has products
	public void showDeleteOptions(int categoryIndex) {
		VBox allChoices = new VBox(20);

		Stage stage = new Stage();

		CustomLabel choices = new CustomLabel(
				"This Category has Products, Choose an Action before Deleting, Be Careful");

		// Reassign products to another existing category
		IconButton reassignButton = new IconButton(
				"Reassign Products to Another existing Category before deleting them",
				"/application/icons8-change-50.png");
		reassignButton.setOnAction(x -> {
			Stage boxStage = new Stage();
			boxStage.setTitle("Reassign Category");

			VBox boxVbox = new VBox(20);
			boxVbox.setAlignment(Pos.CENTER);

			CustomLabel message = new CustomLabel("Choose a Category to Reassign Products to ");

			// Filter the categories to delete the same category
			Category selectedCategory = categoryList.get(categoryIndex);
			ObservableList<Category> filterdList = FXCollections.observableArrayList();
			for (Category category : categoryList) {
				if (category.getCategoryId() != selectedCategory.getCategoryId()) {
					filterdList.add(category);
				}
			}
			ComboBox<Category> categoryBox = new ComboBox<>();
			categoryBox.setItems(filterdList);
			categoryBox.setPrefWidth(500);
			categoryBox.setPromptText("Select a Category:");

			IconButton confirm = new IconButton("Confirm Reassign", "/application/icons8-confirm-50.png");
			confirm.setOnAction(e -> {
				Category selected = categoryBox.getSelectionModel().getSelectedItem();
				if (selected == null) {
					alerts.ErrorAlert("Error", "Please Choose a Category to Reassign Products to");
					return;
				}
				boolean confirmation = alerts.ConfiramtionAlert("Confirmation",
						"Are you sure you want to reassign these products to this category?");
				if (!confirmation) {
					return;
				}

				Category newCategoryBox = categoryBox.getValue();
				for (int i = productDisplay.productList.size() - 1; i >= 0; i--) {
					Product p = productDisplay.productList.get(i);
					if (p.getCategory().getCategoryId() == selectedCategory.getCategoryId()) {
						p.setCategory(newCategoryBox); // move product to new category
						productDisplay.productList.set(i, p);
					}
				}
				// After reassignment, remove the original category
				linkedList.remove(categoryIndex);
				categoryList.remove(categoryIndex);
				alerts.InfoAlert("Success", "Product Category Data has been Updated Successfully");
				boxStage.close();
				stage.close();
			});
			IconButton back = new IconButton("Back", "/application/icons8-back-50.png");
			back.setOnAction(y -> {
				boxStage.close();
			});

			boxVbox.getChildren().addAll(message, categoryBox, confirm, back);

			Scene scene = new Scene(boxVbox, 500, 300);
			Main.applyStyles(scene);
			boxStage.setScene(scene);
			boxStage.initModality(Modality.APPLICATION_MODAL);
			boxStage.getIcons().add(new Image("/application/images/icons8-warehouse-64.png"));
			boxStage.setResizable(false);
			boxStage.showAndWait();
		});
		// Let user manually reassign products before delete
		IconButton preventButton = new IconButton(" Removed Product reassigned manually",
				"/application/icons8-stop-sign-50.png");
		preventButton.setPrefSize(830, 75);
		preventButton.setOnAction(x -> {
			stage.close();
		});
		// Force delete category and its products
		IconButton forceButton = new IconButton(
				"Force Deletes will permanently remove the Category and all its Products",
				"/application/icons8-remove-50.png");

		forceButton.setOnAction(x -> {
			Category selectedCategory = categoryList.get(categoryIndex);
			for (Product product : productDisplay.productList) {
				if (product.getCategory() == selectedCategory && category.hasShipmentForProduct(product)) {
					alerts.ErrorAlert("Category In Use",
							"This category has shipment history and cannot be deleted during this session.");
					return;
				}
			}
			boolean confirmation = alerts.ConfiramtionAlert("Dangerous Action",
					"Are you sure you want to delete this category and all of its products?");
			if (!confirmation) {
				return;
			}
			forceDelete(categoryIndex);
			linkedList.remove(categoryIndex);
			categoryList.remove(categoryIndex);

			alerts.InfoAlert("Success", "Category and its products deleted successfully.");
			stage.close();
		});

		IconButton back = new IconButton("Back", "/application/icons8-back-50.png");
		back.setOnAction(x -> {
			stage.close();
		});

		allChoices.getChildren().addAll(choices, reassignButton, preventButton, forceButton, back);
		allChoices.setAlignment(Pos.CENTER);

		Scene scene = new Scene(allChoices, 1000, 600);
		Main.applyStyles(scene);
		stage.getIcons().add(new Image("/application/images/icons8-warehouse-64.png"));
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		stage.setTitle("Delete Category Options");
		stage.setResizable(false);
		stage.showAndWait();
	}

	public void Display() {

		Stage stage = new Stage();
		MenuBar menuBar = Main.createmenuBar(stage);

		if (!columnsAdded) {
			TableColumn<Category, Integer> idCol = new TableColumn<>("Category ID");
			idCol.setCellValueFactory(new PropertyValueFactory<>("categoryId"));

			TableColumn<Category, String> nameCol = new TableColumn<>("Category Name");
			nameCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

			TableColumn<Category, String> descriptionCol = new TableColumn<>("Category Description");
			descriptionCol.setCellValueFactory(new PropertyValueFactory<>("categoryDescription"));

			categoryTable.getColumns().add(idCol);
			categoryTable.getColumns().add(nameCol);
			categoryTable.getColumns().add(descriptionCol);
			categoryTable.setStyle("-fx-background-color: white;" + "-fx-border-color: transparent;"
					+ "-fx-table-cell-border-color: transparent;" + "-fx-font-family: 'Segoe UI';"
					+ "-fx-font-size: 14px;");
			categoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
			categoryTable.setItems(categoryList);
			columnsAdded = true;

		}


		VBox allButtons = new VBox(30);
		HBox someButtons = new HBox(30);
		HBox searchButtons = new HBox(30);

		backButton = new IconButton("Back", "/application/icons8-back-50.png");
		backButton.setOnAction(x -> {
			stage.close();
		});
		// Add new category
		addButton = new IconButton("Add Category", "/application/icons8-add-50.png");
		addButton.setOnAction(x -> {
			addCategory = new AddCategory();
			addCategory.Display();
		});
		// view all categories
		viewButton = new IconButton("View Categories", "/application/icons8-view-50.png");
		viewButton.setDisable(true);
		viewButton.setOnAction(x -> {
			int selectedIndex = categoryList.indexOf(categoryTable.getSelectionModel().getSelectedItem());
			if (selectedIndex < 0) {
				return;
			}
			ViewCategory viewCategory = new ViewCategory(selectedIndex, categoryList);
			viewCategory.Display();
		});
		// Update category
		updateButton = new IconButton("Update Category", "/application/icons8-update-64.png");
		updateButton.setDisable(true);
		updateButton.setOnAction(x -> {
			int selectedIndex = categoryList.indexOf(categoryTable.getSelectionModel().getSelectedItem());
			if (selectedIndex < 0) {
				return;
			}
			updateCategory = new UpdateCategory(selectedIndex, categoryList);
			updateCategory.Display();
		});
		// Remove category with checks for products
		removeButton = new IconButton("Remove Category", "/application/icons8-remove-50.png");
		removeButton.setDisable(true);
		removeButton.setOnAction(x -> {
			int selectedIndex = categoryList.indexOf(categoryTable.getSelectionModel().getSelectedItem());
			boolean hasProducts = false;
			if (selectedIndex >= 0) {
				Category selectedCategory = categoryList.get(selectedIndex);
				for (Product product : productDisplay.productList) {
					if (product.getCategory().getCategoryId() == selectedCategory.getCategoryId()) {
						showDeleteOptions(selectedIndex);
						hasProducts = true;
						break;
					}
				}
				if (!hasProducts) {
					boolean confirmation = alerts.ConfiramtionAlert("Confirmation",
							"Are you sure you want to delete this category?");
					if (!confirmation) {
						return;
					}
					linkedList.remove(selectedIndex);
					categoryList.remove(selectedIndex);
					alerts.InfoAlert("Success", "Category deleted successfully.");
				}

			}
		});

		categoryTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedCategory) -> {
			boolean noSelection = selectedCategory == null;
			viewButton.setDisable(noSelection);
			updateButton.setDisable(noSelection);
			removeButton.setDisable(noSelection);
		});

		searchButton = new IconButton("Search", "/application/icons8-search-50.png");
		searchButton.setOnAction(x -> {
			ObservableList<Category> resultOfSearch = linkedList.searchCategorybyName(searchField.getText());
			if (resultOfSearch.isEmpty()) {
				categoryTable.setItems(categoryList);
			} else {
				categoryTable.setItems(resultOfSearch);
			}
		});
		searchField = new CustomTextField();
		searchField.setPromptText("Search here ..");

		refreshButton = new IconButton("Refresh", "/application/icons8-refresh-50.png");
		refreshButton.setOnAction(x -> {
			if (categoryList == null || categoryList.isEmpty()) {
				alerts.ErrorAlert("Error", "No Data in Table to Refresh,Sorry..");
				return;
			}
			categoryTable.setItems(categoryList);
		});

		searchButtons.getChildren().addAll(refreshButton, searchField, searchButton);
		searchButtons.setAlignment(Pos.CENTER);

		someButtons.getChildren().addAll(backButton, addButton, viewButton, updateButton, removeButton);
		someButtons.setAlignment(Pos.CENTER);

		allButtons.getChildren().addAll(searchButtons, someButtons);
		allButtons.setAlignment(Pos.CENTER);

		BorderPane categoryScreen = new BorderPane();
		categoryScreen.setTop(menuBar);
		categoryScreen.setCenter(categoryTable);
		categoryScreen.setBottom(allButtons);

		Scene scene = new Scene(categoryScreen, 400, 300);
		Main.applyStyles(scene);
		stage.setScene(scene);
		stage.setTitle("Smart Warehouse");
		stage.setMaximized(true);
		stage.getIcons().add(new Image("/application/images/icons8-warehouse-64.png"));
		stage.show();
	}
}
