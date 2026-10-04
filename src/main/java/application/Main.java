package application;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Main extends Application {

	public static DoubleLinkedList linkedList = new DoubleLinkedList();
	public static Category category = new Category();
	public static ProductDisplay productDisplay = new ProductDisplay();
	public static CategoryDisplay categoryDisplay = new CategoryDisplay();
	public static ShipmentDisplay shipmentDisplay = new ShipmentDisplay();
	static Alerts alerts = new Alerts();
	private static final Path APP_DATA_DIRECTORY = Path.of(System.getProperty("user.home"), ".smart-warehouse");
	private static final String[] SAMPLE_DATA_FILES = { "categories.csv", "products.csv", "shipments.csv" };

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage stage) {
		initializeApplicationData();
		BorderPane HomeScreen = new BorderPane();

		HomeScreen.setTop(createmenuBar(stage));
		HomeScreen.setCenter(welcomeVbox(stage));
		HomeScreen.setBottom(helpHbox(stage));
		setbackGround(HomeScreen);

		Scene scene = new Scene(HomeScreen, 400, 400);
		applyStyles(scene);
		stage.setScene(scene);
		stage.setTitle("Smart Warehouse");
		stage.setMaximized(true);
		stage.getIcons().add(new Image("/application/images/icons8-warehouse-64.png"));
		stage.show();
	}

	public static MenuBar createmenuBar(Stage stage) {
		MenuBar menuBar = new MenuBar();

		menuBar.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2);" + "-fx-background-insets: 0;"
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);" + "-fx-padding: 5 10 5 10;"
				+ "-fx-background-radius: 10;");

		Menu loadItem1 = new Menu();
		styleMenu(loadItem1);
		MenuItem loadItem = new MenuItem("Load Categories");
		loadItem.setOnAction(x -> {
			LoadCategory load = new LoadCategory();
			load.Display();
		});
		loadItem.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
		MenuItem loadItem2 = new MenuItem("Load Products");
		loadItem2.setOnAction(x -> {
			if (categoryDisplay.categoryList == null || categoryDisplay.categoryList.isEmpty()) {
				alerts.ErrorAlert("Error", "Please load the categories first.");
				return;
			} else {
				LoadProduct loadProduct = new LoadProduct();
				loadProduct.Display();
			}
		});
		loadItem2.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
		MenuItem loadItem3 = new MenuItem("Load Shipments");
		loadItem3.setOnAction(x -> {
			if (categoryDisplay.categoryList == null || categoryDisplay.categoryList.isEmpty()) {
				alerts.ErrorAlert("Error", "Please load the categories first.");
				return;
			}
			if (productDisplay.productList == null || productDisplay.productList.isEmpty()) {
				alerts.ErrorAlert("Error", "Please load the products first.");
				return;
			}
			LoadShipment loadShipment = new LoadShipment();
			loadShipment.Display();
		});
		loadItem3.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
		loadItem1.getItems().addAll(loadItem, loadItem2, loadItem3);
		Image i3 = new Image("/application/images/icons8-load-from-file-48.png");
		ImageView iv3 = new ImageView(i3);
		iv3.setFitHeight(50);
		iv3.setFitWidth(50);
		loadItem1.setGraphic(iv3);

		Menu empty3 = new Menu("|");
		styleMenu(empty3);
		empty3.setStyle("-fx-font-size: 38px; -fx-text-fill: black;");

		Menu saveItem1 = new Menu();
		styleMenu(saveItem1);
		MenuItem saveItem2 = new MenuItem("Export Activity Log");
		saveItem2.setOnAction(x -> {
			LogExport logExport = new LogExport(null);
			logExport.Display();
		});
		saveItem1.getItems().add(saveItem2);
		Image i4 = new Image("/application/images/icons8-save-50.png");
		ImageView iv4 = new ImageView(i4);
		saveItem1.setGraphic(iv4);

		Menu empty4 = new Menu("|");
		styleMenu(empty4);
		empty4.setStyle("-fx-font-size: 38px; -fx-text-fill: black;");

		Menu exit1 = new Menu();
		styleMenu(exit1);
		MenuItem exit2 = new MenuItem("Exit");
		exit2.setOnAction(x -> {
			stage.close();
		});
		exit1.getItems().add(exit2);
		Image i5 = new Image("/application/images/icons8-exit-50.png");
		ImageView iv5 = new ImageView(i5);
		exit1.setGraphic(iv5);

		menuBar.getMenus().addAll(loadItem1, empty3, saveItem1, empty4, exit1);
		return menuBar;
	}

	public static void styleMenu(Menu styleMenus) {
		styleMenus.setStyle("-fx-background-color: #0d1b58;" + "-fx-text-fill: white;" + "-fx-font-size: 26px;"
				+ "-fx-font-family: 'Segoe UI', sans-serif;" + "-fx-font-weight: bold;" + "-fx-padding: 18px 26px;"
				+ "-fx-border-radius: 6px;" + "-fx-background-radius: 6px;" + "-fx-border-color: transparent;"
				+ "-fx-cursor: hand;");
	}

	public static VBox welcomeVbox(Stage stage) {
		VBox welcomeVb = new VBox(50);
		HBox buttons = new HBox(30);
		CustomLabel WelcomeLabel = new CustomLabel("Welcome to Shipment Management System – COMP242");
		IconButton categoryButton = new IconButton("Categories", "/application/images/icons8-category-50.png");
		categoryButton.setOnAction(x -> {
			categoryDisplay.Display();
		});
		IconButton productButton = new IconButton("Products", "/application/images/icons8-products-50.png");
		productButton.setOnAction(x -> {
			productDisplay.Display();
		});
		IconButton shipmentButton = new IconButton("Shipments", "/application/images/icons8-shipment-50.png");
		shipmentButton.setOnAction(x -> {
			shipmentDisplay.Display();
		});
		buttons.getChildren().addAll(categoryButton, productButton, shipmentButton);
		buttons.setAlignment(Pos.CENTER);
		welcomeVb.getChildren().addAll(WelcomeLabel, buttons);
		welcomeVb.setAlignment(Pos.CENTER);

		return welcomeVb;

	}

	public static HBox helpHbox(Stage stage) {
		HBox helpHb = new HBox(25);

		CustomLabel helpLabel = new CustomLabel(
				"We are always looking to improve the Shipment Management System. Share your ideas at:");
		CustomLabel emailLabel = new CustomLabel("Elyasnajeh5@gmail.com");
		helpHb.getChildren().addAll(helpLabel, emailLabel);
		helpHb.setAlignment(Pos.CENTER);

		return helpHb;

	}

	public static void setbackGround(BorderPane HomeScreen) {

		HomeScreen.setStyle(
				"-fx-background-image: url('/application/images/HomeScreen.png');" + "-fx-background-repeat: no-repeat;"
						+ "-fx-background-position: center;" + "-fx-background-size: cover;");
	}

	public static Path getDataDirectory() {
		return APP_DATA_DIRECTORY.resolve("data");
	}

	public static Path getLogFile() {
		return APP_DATA_DIRECTORY.resolve("logs").resolve("actions.log");
	}

	public static void applyStyles(Scene scene) {
		scene.getStylesheets().add(Main.class.getResource("/application/css/application.css").toExternalForm());
	}

	private static void initializeApplicationData() {
		try {
			Files.createDirectories(getDataDirectory());
			Files.createDirectories(getLogFile().getParent());
			for (String fileName : SAMPLE_DATA_FILES) {
				Path destination = getDataDirectory().resolve(fileName);
				if (Files.notExists(destination)) {
					try (InputStream input = Main.class.getResourceAsStream("/data/" + fileName)) {
						if (input != null) {
							Files.copy(input, destination);
						}
					}
				}
			}
		} catch (IOException e) {
			alerts.ErrorAlert("Application Data Error", "Could not prepare the application data folder: " + e.getMessage());
		}
	}
}
