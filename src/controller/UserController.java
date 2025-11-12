package controller;

import dao.UserDAO;
import model.User;

import java.sql.SQLException;

public class UserController {

    private final UserDAO userDAO;

    public UserController() {
        this.userDAO = new UserDAO();
    }

    public boolean login(String username, String password) throws SQLException {
        User userFromDB = userDAO.getUserByUsername(username);

        return userFromDB != null && userFromDB.getPassword().equals(password);
    }
    
}
