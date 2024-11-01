package com.infoman.controller;

import com.infoman.DatabaseConnection;
import com.infoman.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainController {
    @FXML
    private TextField Fname;
    @FXML
    private TextField Mname;
    @FXML
    private TextField Lname;
    @FXML
    private TextField address;
    @FXML
    private TextField Pnumber;
    @FXML
    private TextField Mail;
    @FXML
    private RadioButton male;
    @FXML
    private RadioButton female;
    @FXML
    private TableView<Student> table;
    @FXML
    private TableColumn<Student, String> colFN;
    @FXML
    private TableColumn<Student, String> colMN;
    @FXML
    private TableColumn<Student, String> colLN;
    @FXML
    private TableColumn<Student, String> colA;
    @FXML
    private TableColumn<Student, String> colPN;
    @FXML
    private TableColumn<Student, String> colE;
    @FXML
    private TableColumn<Student, String> colG;

    private DatabaseConnection connection;
    private ObservableList<Student> studentlist = FXCollections.observableArrayList();

    private ToggleGroup genderGroup;

    public void initialize() throws SQLException {
        connection = new DatabaseConnection();

        genderGroup = new ToggleGroup();
        male.setToggleGroup(genderGroup);
        female.setToggleGroup(genderGroup);

        // Ensure these property names match the getter names in the Student class
        colFN.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colMN.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        colLN.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colA.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPN.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colE.setCellValueFactory(new PropertyValueFactory<>("email"));
        colG.setCellValueFactory(new PropertyValueFactory<>("gender"));

        loadStudent();
    }

    public void loadStudent() throws SQLException {
        studentlist.clear();
        String sql = "SELECT * FROM STUDENTS";

        try (Statement stmt = connection.getConnection().createStatement();
             ResultSet result = stmt.executeQuery(sql)) {

            while (result.next()) {
                Student student = new Student(
                        result.getInt("id"),
                        result.getString("first_name"),
                        result.getString("middle_name"),
                        result.getString("last_name"),
                        result.getString("phone_number"),
                        result.getString("email"),
                        result.getString("address"),
                        result.getString("gender")
                );
                studentlist.add(student);
            }
            table.setItems(studentlist);
        }
    }

    private String getSelectedGender() {
        if (male.isSelected()) {
            return "Male";
        } else if (female.isSelected()) {
            return "Female";
        }
        return null; // Return null if nothing is selected
    }

    @FXML
    private void save() throws SQLException {
        String sql = "INSERT INTO students(first_name, middle_name, last_name, address, phone_number, email, gender) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, Fname.getText());
            stmt.setString(2, Mname.getText());
            stmt.setString(3, Lname.getText());
            stmt.setString(4, address.getText());
            stmt.setString(5, Pnumber.getText());
            stmt.setString(6, Mail.getText());
            stmt.setString(7, getSelectedGender());
            if (stmt.executeUpdate() == 1) {
                clearFields();
                loadStudent(); // Refresh the table view
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Show an error message to the user
            showAlert("Error", "Failed to save student: " + e.getMessage());
        }
    }

    @FXML
    private void delete() {
        Student selectedStudent = table.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            String sql = "DELETE FROM STUDENTS WHERE id = ?";
            try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql)) {
                stmt.setInt(1, selectedStudent.getId());
                stmt.executeUpdate(); // Execute the delete statement
                loadStudent(); // Refresh the table view
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete student: " + e.getMessage());
            }
        }
    }

    @FXML
    private void edit() {
        Student selectedStudent = table.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            Fname.setText(selectedStudent.getFirstName());
            Mname.setText(selectedStudent.getMiddleName());
            Lname.setText(selectedStudent.getLastName());
            address.setText(selectedStudent.getAddress());
            Pnumber.setText(selectedStudent.getPhoneNumber());
            Mail.setText(selectedStudent.getEmail());
            if (selectedStudent.getGender().equals("Male")) {
                male.setSelected(true);
            } else {
                female.setSelected(true);
            }
        }
    }

    @FXML
    private void update() throws SQLException {
        Student selectedStudent = table.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            String sql = "UPDATE students SET first_name=?, middle_name=?, last_name=?, address=?, phone_number=?, email=?, gender=? WHERE id=?";
            try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql)) {
                stmt.setString(1, Fname.getText());
                stmt.setString(2, Mname.getText());
                stmt.setString(3, Lname.getText());
                stmt.setString(4, address.getText());
                stmt.setString(5, Pnumber.getText());
                stmt.setString(6, Mail.getText());
                stmt.setString(7, getSelectedGender());
                stmt.setInt(8, selectedStudent.getId()); // Update the correct student by ID

                if (stmt.executeUpdate() == 1) {
                    clearFields(); // Clear input fields after update
                    loadStudent(); // Refresh the table view
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to update student: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        Fname.clear();
        Mname.clear();
        Lname.clear();
        Pnumber.clear();
        Mail.clear();
        address.clear();
        male.setSelected(false);
        female.setSelected(false);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
