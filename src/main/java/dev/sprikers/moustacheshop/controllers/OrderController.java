package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import dev.sprikers.moustacheshop.components.StepNavigator;
import dev.sprikers.moustacheshop.models.ProductModel;
import dev.sprikers.moustacheshop.utils.ReniecFetch;
import dev.sprikers.moustacheshop.utils.TableProducts;

public class OrderController implements Initializable {

    private TableProducts tableProducts;
    private ProductModel productSelected;

    private final StepNavigator stepNavigator = new StepNavigator();

    @FXML
    private Button btnNextTwo, btnNextThree, btnPrevOne, btnPrevTwo, btnSubmit;

    @FXML
    private Button btnReloadProducts;

    @FXML
    private HBox hbProductSpinner;

    @FXML
    private Label lblProducts;

    @FXML
    private Region btnReniec;

    @FXML
    private Tab tabOne, tabTwo, tabThree;

    @FXML
    private TableColumn<ProductModel, String> colProductName;

    @FXML
    private TableColumn<ProductModel, String> colProductPrice;

    @FXML
    private TableColumn<ProductModel, Integer> colProductStock;

    @FXML
    private TableView<ProductModel> tblProducts;

    @FXML
    private TextField txtDNI, txtMaternalSurname, txtNames, txtPaternalSurname, txtSearchProducts;

    @FXML
    private VBox stepOne, stepTwo, stepThree;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableProducts = new TableProducts(tblProducts, txtSearchProducts, lblProducts, btnReloadProducts, hbProductSpinner);
        tableProducts.setColumns(colProductName, colProductPrice, colProductStock);
        tableProducts.setOnProductSelected(this::setProductSelected);
        tableProducts.loadProducts();

        ReniecFetch.configureFields(btnReniec, txtDNI, txtNames, txtPaternalSurname, txtMaternalSurname);

        stepNavigator.setSteps(List.of(stepOne, stepTwo, stepThree));
        stepNavigator.setTabs(List.of(tabOne, tabTwo, tabThree));

        stepNavigator.firstStep(btnNextTwo);
        stepNavigator.addStep(btnPrevOne, btnNextThree);
        stepNavigator.lastStep(btnPrevTwo);
    }

    private void setProductSelected(ProductModel product) {
        productSelected = product;
        System.out.println(productSelected);
    }

}
