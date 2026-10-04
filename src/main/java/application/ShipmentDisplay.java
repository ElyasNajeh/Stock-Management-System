package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ShipmentDisplay {
	Category category = Main.category;
	Queue queue = category.getShipmentProduct();
	Stack stack = category.getUndo();
	IconButton approveButton, cancelButton, backButton, undoButton, addButton, redoButton;
	boolean columnsAdded = false;
	TableView<Shipment> shipmentTable = new TableView<>();
	ObservableList<Shipment> shipmentList = FXCollections.observableArrayList();
	Alerts alerts = new Alerts();

	public void Display() {

		Stage stage = new Stage();
		MenuBar menuBar = Main.createmenuBar(stage);

		// Add table columns only once

		if (!columnsAdded) {
			TableColumn<Shipment, Integer> idCol = new TableColumn<>("Shipment ID");
			idCol.setCellValueFactory(new PropertyValueFactory<>("shipmentId"));

			TableColumn<Shipment, Integer> productCol = new TableColumn<>("Product ID");
			productCol.setCellValueFactory(data ->
					new SimpleIntegerProperty(data.getValue().getProduct().getProductId()).asObject());

			TableColumn<Shipment, Integer> quantityCol = new TableColumn<>("Shipment Quantity");
			quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

			TableColumn<Shipment, String> dateCol = new TableColumn<>("Shipment Date");
			dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

			shipmentTable.getColumns().add(idCol);
			shipmentTable.getColumns().add(productCol);
			shipmentTable.getColumns().add(quantityCol);
			shipmentTable.getColumns().add(dateCol);
			shipmentTable.setStyle("-fx-background-color: white;" + "-fx-border-color: transparent;"
					+ "-fx-table-cell-border-color: transparent;" + "-fx-font-family: 'Segoe UI';"
					+ "-fx-font-size: 14px;");
			shipmentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
			shipmentTable.setItems(shipmentList);
			columnsAdded = true;

		}
		VBox allButtons = new VBox(30);
		HBox someButtons1 = new HBox(30);
		HBox someButtons2 = new HBox(30);

		backButton = new IconButton("Back", "/application/icons8-back-50.png");
		backButton.setOnAction(x -> {
			stage.close();
		});

		addButton = new IconButton("Add Shipment", "/application/icons8-add-50.png");
		addButton.setOnAction(x -> {
			AddShipment addShipment = new AddShipment();
			addShipment.Display();
		});
		// Undo last shipment action (approve or cancel)
		undoButton = new IconButton("Undo", "/application/icons8-undo-48.png");
		undoButton.setOnAction(x -> {
			if (stack.isEmpty()) {
				alerts.ErrorAlert("Error", "No Action to Undo");
				return;
			}
			boolean confirmation = alerts.ConfiramtionAlert("Confirmation",
					"Are you sure you want to Undo the last step you took ?");
			if (!confirmation) {
				return;
			}
			Shipment undo = (Shipment) stack.pop();
			if ("Add Shipment".equalsIgnoreCase(undo.getActionType())) {
				Product product = undo.getProduct();
				product.setQuantity(product.getQuantity() - undo.getQuantity());
			} else if ("Cancel Shipment".equalsIgnoreCase(undo.getActionType())) {
				category.removeShipmentFromCancelCursor(undo);
			}
			category.addShipment(undo);
			shipmentList.add(undo);
			category.addToRedo(undo);
			Main.productDisplay.productTable.refresh();
			alerts.InfoAlert("Success", "The last shipment action was undone.");
		});

		// Redo the last undone shipment
		redoButton = new IconButton("Redo", "/application/icons8-redo-48.png");
		redoButton.setDisable(false);
		redoButton.setOnAction(x -> {
			if (category.getRedo().isEmpty()) {
				alerts.ErrorAlert("Error", "No Action to Redo");
				return;
			}
			boolean confirmation = alerts.ConfiramtionAlert("Confirmation",
					"Are you sure you want to Redo the last step you took ?");
			if (!confirmation) {
				return;
			}
			Shipment redo = (Shipment) category.getRedo().pop();
			queue.remove(redo);
			shipmentList.remove(redo);
			if ("Add Shipment".equalsIgnoreCase(redo.getActionType())) {
				Product product = redo.getProduct();
				product.setQuantity(product.getQuantity() + redo.getQuantity());
			} else if ("Cancel Shipment".equalsIgnoreCase(redo.getActionType())) {
				category.addShipmentToCancelCursor(redo);
			}
			category.addToUndo(redo);
			Main.productDisplay.productTable.refresh();
			alerts.InfoAlert("Success", "The shipment action was redone.");
		});

		approveButton = new IconButton("Approve Shipment", "/application/icons8-accept-50.png");
		approveButton.disableProperty().bind(Bindings.isEmpty(shipmentList));
		approveButton.setOnAction(x -> {
			boolean confirmation = alerts.ConfiramtionAlert("Confirmation",
					"Are you sure you need to Approve this Shipment ?");
			if (!confirmation) {
				return;
			}

			Shipment undo = (Shipment) queue.deQueue();
			if (undo == null) {
				return;
			}

			// Update product quantity
			Product productOfFrontShipment = undo.getProduct();
			int newQuantity = undo.getQuantity() + productOfFrontShipment.getQuantity();
			productOfFrontShipment.setQuantity(newQuantity);

			shipmentList.remove(undo);
			undo.setActionType("Add Shipment");
			category.getRedo().clear();
			category.addToUndo(undo);
			Main.productDisplay.productTable.refresh();
			alerts.InfoAlert("Success", "Shipment Approved and Product quantity updated Successfully, Thanks");
			LogExport logExport = new LogExport(undo);
			logExport.Display();

		});

		cancelButton = new IconButton("Cancel Shipment", "/application/icons8-cancel-50.png");
		cancelButton.disableProperty().bind(Bindings.isEmpty(shipmentList));
		cancelButton.setOnAction(x -> {
			boolean confirmation = alerts.ConfiramtionAlert("Confirmation",
					"Are you sure you need to Cancel this Shipment ?");
			if (!confirmation) {
				return;
			}
			Shipment undo = (Shipment) queue.deQueue();
			if (undo == null) {
				return;
			}
			undo.setActionType("Cancel Shipment");
			category.getRedo().clear();
			category.addToUndo(undo);
			shipmentList.remove(undo);
			category.addShipmentToCancelCursor(undo);
			alerts.InfoAlert("Success", "Shipment Canceled, Thanks");
			LogExport logExport = new LogExport(undo);
			logExport.Display();
		});

		someButtons1.getChildren().addAll(backButton, approveButton, cancelButton);
		someButtons1.setAlignment(Pos.CENTER);

		someButtons2.getChildren().addAll(undoButton, addButton, redoButton);
		someButtons2.setAlignment(Pos.CENTER);

		allButtons.getChildren().addAll(someButtons2, someButtons1);
		allButtons.setAlignment(Pos.CENTER);

		BorderPane shipmentScreen = new BorderPane();
		shipmentScreen.setTop(menuBar);
		shipmentScreen.setCenter(shipmentTable);
		shipmentScreen.setBottom(allButtons);

		Scene scene = new Scene(shipmentScreen, 400, 300);
		Main.applyStyles(scene);
		stage.setScene(scene);
		stage.setTitle("Smart Warehouse");
		stage.setMaximized(true);
		stage.getIcons().add(new Image("/application/images/icons8-warehouse-64.png"));
		stage.show();

	}
}
