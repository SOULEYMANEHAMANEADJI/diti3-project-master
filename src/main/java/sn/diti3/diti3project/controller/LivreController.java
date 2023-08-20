package sn.diti3.diti3project.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import sn.diti3.diti3project.DB.DBConnexion;
import sn.diti3.diti3project.entity.Livre;
import sn.diti3.diti3project.tools.Notification;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;

public class LivreController implements Initializable {
    private FilteredList<Livre> filteredLivres;

    @FXML
    private TextField searchField; // Ajoutez ce champ


    @FXML
    private TableColumn<Livre, String> auteurCol;

    @FXML
    private Button emprunterBtn;

    @FXML
    private Button rendreBtn;

    @FXML
    private TextField auteurTfd;

    @FXML
    private Button effacerBtn;

    @FXML
    private Button enregistrerBtn;

    @FXML
    private TableColumn<Livre, Integer> idCol;

    @FXML
    private TableColumn<Livre, String> isbnCol;

    @FXML
    private TextField isbnTfd;

    @FXML
    private TableView<Livre> livreTb;

    @FXML
    private Button modifierBtn;

    @FXML
    private TableColumn<Livre, Integer> nbPagesCol;

    @FXML
    private TextField nbPagesTfd;

    @FXML
    private Button supprimerBtn;

    @FXML
    private TableColumn<Livre, String> titreCol;

    @FXML
    private TextField titreTfd;

    @FXML
    private Label availableBooksLabel; // Add this line

    @FXML
    private Label borrowedBooksLabel; // Add this line

    @FXML
    private TextField livresDisponiblesField;

    @FXML
    private TextField livresEmpruntesField;

    @FXML
    private TableColumn<Livre, Void> actionsCol;

    private DBConnexion db = new DBConnexion();
    private int id = 0;

    public ObservableList<Livre> getLivres(){
        ObservableList<Livre> livres = FXCollections.observableArrayList();
        String sql = "SELECT * FROM livre ORDER BY auteur";
        try {
            db.initPrepar(sql);
            ResultSet rs = db.executeSelect();
            while(rs.next()){
                Livre livre = new Livre();
                livre.setId(rs.getInt("id"));
                livre.setTitre(rs.getString("titre"));
                livre.setAuteur(rs.getString("auteur"));
                livre.setIsbn(rs.getString("isbn"));
                livre.setEtatEmprunt(rs.getInt("etat_emprunt"));
                livre.setNbPages(rs.getInt("nb_pages"));
                livres.add(livre);
            }
            db.closeConnection();
        }catch (SQLException e){
            throw new RuntimeException();
        }
        return livres;
    }

    public void loadTable(){
        ObservableList<Livre> livres = getLivres();
        livreTb.setItems(livres);
        idCol.setCellValueFactory(new PropertyValueFactory<Livre, Integer>("id"));
        titreCol.setCellValueFactory(new PropertyValueFactory<Livre, String>("titre"));
        auteurCol.setCellValueFactory(new PropertyValueFactory<Livre, String>("auteur"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<Livre, String>("isbn"));
        nbPagesCol.setCellValueFactory(new PropertyValueFactory<Livre, Integer>("nbPages"));
    }

    @FXML
    void delete(ActionEvent event) {
        Livre selectedLivre = livreTb.getSelectionModel().getSelectedItem();
        if (selectedLivre == null) {
            Notification.NotifError("Erreur !", "Sélectionnez un livre à supprimer.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Supprimer le livre ?");
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer le livre '" + selectedLivre.getTitre() + "' ?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM livre WHERE id = ?";
            try {
                db.initPrepar(sql);
                db.getPstm().setInt(1, selectedLivre.getId());
                int ok = db.executeMaj();
                db.closeConnection();
                loadTable();
                clearFields();
                Notification.NotifSuccess("Succès !", "Le livre a été bien supprimé");
                enregistrerBtn.setDisable(false);
            } catch (SQLException e) {
                throw new RuntimeException();
            }
        }
    }


    @FXML
    void save(ActionEvent event) {
        // Vérifier si les champs obligatoires sont vides
        if (titreTfd.getText().isEmpty() || auteurTfd.getText().isEmpty() ||
                isbnTfd.getText().isEmpty() || nbPagesTfd.getText().isEmpty()) {
            Notification.NotifError("Erreur !", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        // Vérifier si nbPagesTfd contient une valeur numérique
        if (!isNumeric(nbPagesTfd.getText())) {
            Notification.NotifError("Erreur !", "Le nombre de pages doit être une valeur numérique.");
            return;
        }
        // Vérifier si l'auteur contient uniquement des lettres et des espaces
        if (!isAlphabetic(auteurTfd.getText())) {
            Notification.NotifError("Erreur !", "L'auteur doit contenir uniquement des lettres et des espaces.");
            return;
        }
        // Vérifier si le titre contient uniquement des lettres, des chiffres et des espaces
        if (!isAlphabeticOrNumeric(titreTfd.getText())) {
            Notification.NotifError("Erreur !", "Le titre doit contenir uniquement des lettres, des chiffres et des espaces.");
            return;
        }

        String sql = "INSERT INTO livre(id,titre,auteur,isbn,nb_pages) VALUES(null, ?, ?, ?, ?)";
        try {
            db.initPrepar(sql);
            //Passage de valeurs
            db.getPstm().setString(1, titreTfd.getText());
            db.getPstm().setString(2, auteurTfd.getText());
            db.getPstm().setString(3, isbnTfd.getText());
            db.getPstm().setInt(4, Integer.parseInt(nbPagesTfd.getText()));
            int ok = db.executeMaj();
            db.closeConnection();
            // Après avoir ajouté un livre, mettez à jour les labels
            updateBookCountLabels();
            loadTable();
            clearFields();
            Notification.NotifSuccess("Succès !", "Le livre a été bien enregistré");
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private boolean isAlphabetic(String str) {
        return str.matches("^[a-zA-Z ]+$");
    }
    private boolean isAlphabeticOrNumeric(String str) {
        return str.matches("^[a-zA-Z0-9 ]+$");
    }

    public void clearFields(){
        titreTfd.setText("");
        auteurTfd.setText("");
        isbnTfd.setText(generateIsbnWithUniqueId());
        nbPagesTfd.setText("");
    }

    @FXML
    void clear(ActionEvent event) {
        clearFields();
        enregistrerBtn.setDisable(false);
    }

    @FXML
    void getData(MouseEvent event) {
        Livre livre = livreTb.getSelectionModel().getSelectedItem();
        id = livre.getId();
        titreTfd.setText(livre.getTitre());
        auteurTfd.setText(livre.getAuteur());
        isbnTfd.setText(livre.getIsbn());
        nbPagesTfd.setText(String.valueOf(livre.getNbPages()));
        enregistrerBtn.setDisable(true);
        if (getLivre(id).getEtatEmprunt() == 0){
            rendreBtn.setDisable(true);
            emprunterBtn.setDisable(false);
        } else{
            rendreBtn.setDisable(false);
            emprunterBtn.setDisable(true);
       }
    }

    @FXML
    void update(ActionEvent event) {
        // Vérifier si les champs obligatoires sont vides
        if (titreTfd.getText().isEmpty() || auteurTfd.getText().isEmpty() ||
                isbnTfd.getText().isEmpty() || nbPagesTfd.getText().isEmpty()) {
            Notification.NotifError("Erreur !", "Veuillez remplir tous les champs obligatoires.");
            return;
        }
        // Vérifier si nbPagesTfd contient une valeur numérique
        if (!isNumeric(nbPagesTfd.getText())) {
            Notification.NotifError("Erreur !", "Le nombre de pages doit être une valeur numérique.");
            return;
        }
        // Vérifier si l'auteur contient uniquement des lettres et des espaces
        if (!isAlphabetic(auteurTfd.getText())) {
            Notification.NotifError("Erreur !", "L'auteur doit contenir uniquement des lettres et des espaces.");
            return;
        }
        // Vérifier si le titre contient uniquement des lettres, des chiffres et des espaces
        if (!isAlphabeticOrNumeric(titreTfd.getText())) {
            Notification.NotifError("Erreur !", "Le titre doit contenir uniquement des lettres, des chiffres et des espaces.");
            return;
        }

        String sql = "UPDATE livre SET titre = ?, auteur = ?, isbn = ?, nb_pages = ? WHERE id = ?";
        try {
            db.initPrepar(sql);
            //Passage de valeurs
            db.getPstm().setString(1, titreTfd.getText());
            db.getPstm().setString(2, auteurTfd.getText());
            db.getPstm().setString(3, isbnTfd.getText());
            db.getPstm().setInt(4, Integer.parseInt(nbPagesTfd.getText()));
            db.getPstm().setInt(5, id);
            int ok = db.executeMaj();
            db.closeConnection();
            loadTable();
            clearFields();
            Notification.NotifSuccess("Succès !", "Le livre a été bien modifié");
            enregistrerBtn.setDisable(false);
        }catch (SQLException e){
            throw new RuntimeException();
        }
    }

    public Livre getLivre(int id){
        Livre livre = null;
        String sql = "SELECT * FROM livre WHERE id = ?";
        try {
            db.initPrepar(sql);
            db.getPstm().setInt(1, id);
            ResultSet rs = db.executeSelect();
            if(rs.next()){
                livre = new Livre();
                livre.setId(rs.getInt("id"));
                livre.setTitre(rs.getString("titre"));
                livre.setAuteur(rs.getString("auteur"));
                livre.setIsbn(rs.getString("isbn"));
                livre.setEtatEmprunt(rs.getInt("etat_emprunt"));
                livre.setNbPages(rs.getInt("nb_pages"));
            }
            db.closeConnection();
        }catch (SQLException e){
            throw new RuntimeException();
        }
        return livre;
    }

    @FXML
    void emprunt(ActionEvent event) {
        Livre selectedLivre = livreTb.getSelectionModel().getSelectedItem();
        if (selectedLivre == null) {
            Notification.NotifError("Erreur !", "Sélectionnez un livre avant d'emprunter.");
            return;
        }

        String sql = "UPDATE livre SET etat_emprunt = 1 WHERE id = ?";
        try {
            db.initPrepar(sql);
            db.getPstm().setInt(1, id);
            int ok = db.executeMaj();
            db.closeConnection();
            loadTable();
            clearFields();
            enregistrerBtn.setDisable(false);
            rendreBtn.setDisable(false);
            // Après avoir effectué un emprunt, mettez à jour les labels
            updateBookCountLabels();
            Notification.NotifSuccess("Succès !", "Le livre a été emprunté.");
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @FXML
    void rendre(ActionEvent event) {
        Livre selectedLivre = livreTb.getSelectionModel().getSelectedItem();
        if (selectedLivre == null) {
            Notification.NotifError("Erreur !", "Sélectionnez un livre avant de le rendre.");
            return;
        }

        String sql = "UPDATE livre SET etat_emprunt = 0 WHERE id = ?";
        try {
            db.initPrepar(sql);
            db.getPstm().setInt(1, id);
            int ok = db.executeMaj();
            db.closeConnection();
            loadTable();
            clearFields();
            enregistrerBtn.setDisable(false);
            emprunterBtn.setDisable(false);
            // Après avoir effectué un retour de livre, mettez à jour les labels
            updateBookCountLabels();
            Notification.NotifSuccess("Succès !", "Le livre a été rendu.");
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTable();
        // Mettez en place le filtrage automatique lorsque le texte de recherche change
        setupSearchFilter();
        // Générer un ISBN et le remplir dans le champ
        String generatedIsbn = generateIsbnWithUniqueId();
        isbnTfd.setText(generatedIsbn);
        isbnTfd.setEditable(false); // Pour rendre le champ en lecture seule
        // Définir les styles des boutons
        enregistrerBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        modifierBtn.setStyle("-fx-background-color: #FFC107; -fx-text-fill: white;");
        supprimerBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        effacerBtn.setStyle("-fx-background-color: gray; -fx-text-fill: white;");
        emprunterBtn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        rendreBtn.setStyle("-fx-background-color: purple; -fx-text-fill: white;");
        // Update the labels with book counts
        updateBookCountLabels();
    }

    // Add this method to update the labels with book counts
    private void updateBookCountLabels() {
        int availableCount = getAvailableBooksCount();
        int borrowedCount = getBorrowedBooksCount();

        livresEmpruntesField.setText(String.valueOf(borrowedCount));
        livresDisponiblesField.setText(String.valueOf(availableCount));

    }
    // Add these methods to calculate and display counts
    private int getAvailableBooksCount() {
        return (int) getLivres().stream().filter(livre -> livre.getEtatEmprunt() == 0).count();
    }

    private int getBorrowedBooksCount() {
        return (int) getLivres().stream().filter(livre -> livre.getEtatEmprunt() == 1).count();
    }

    public void displayBookCounts() {
        int availableCount = getAvailableBooksCount();
        int borrowedCount = getBorrowedBooksCount();
        System.out.println("Nombre de livres disponibles : " + availableCount);
        System.out.println("Nombre de livres empruntés : " + borrowedCount);
    }

    private void setupSearchFilter() {
        filteredLivres = new FilteredList<>(getLivres(), p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredLivres.setPredicate(livre -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (String.valueOf(livre.getId()).contains(lowerCaseFilter)) {
                    return true;
                } else if (livre.getTitre().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (livre.getAuteur().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (livre.getIsbn().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else return String.valueOf(livre.getNbPages()).contains(lowerCaseFilter);
            });

            loadFilteredTable();
        });
    }

    private void loadFilteredTable() {
        ObservableList<Livre> filteredList = FXCollections.observableArrayList(filteredLivres);
        livreTb.setItems(filteredList);
    }
    private String generateIsbnWithUniqueId() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyMMdd");
        String formattedDate = currentDate.format(dateFormatter);

        Random random = new Random();
        int uniqueId = random.nextInt(100); // Génère un identifiant unique de 0 à 99

        return formattedDate + "-" + String.format("%02d", uniqueId);
    }


}
