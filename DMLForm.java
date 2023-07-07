import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DMLForm extends JFrame {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USERNAME = "lokesh";
    private static final String PASSWORD = "Lokesh";

    private DefaultTableModel tableModel;
    private JTable table;

    public DMLForm() {
        setTitle("DML Form");
        setLayout(new FlowLayout());

        tableModel = new DefaultTableModel(new String[]{"Action", "Table", "ID", "Field 1", "Field 2", "Field 3"}, 0);
        table = new JTable(tableModel);

        JButton matchesButton = createStyledButton("Matches");
        matchesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MatchesPage matchesPage = new MatchesPage(tableModel, table);
                matchesPage.setVisible(true);
            }
        });
        add(matchesButton);

        JButton teamsButton = createStyledButton("Teams");
        teamsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TeamsPage teamsPage = new TeamsPage(tableModel, table);
                teamsPage.setVisible(true);
            }
        });
        add(teamsButton);

        JButton playersButton = createStyledButton("Players");
        playersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PlayersPage playersPage = new PlayersPage(tableModel, table);
                playersPage.setVisible(true);
            }
        });
        add(playersButton);

        JButton coachesButton = createStyledButton("Coaches");
        coachesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CoachesPage coachesPage = new CoachesPage(tableModel, table);
                coachesPage.setVisible(true);
            }
        });
        add(coachesButton);

        JButton displayAllTeamsButton = createStyledButton("Display All Teams");
        displayAllTeamsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayAllTeams(tableModel, table);
            }
        });
        add(displayAllTeamsButton);

        JButton displayAllCoachesButton = createStyledButton("Display All Coaches");
        displayAllCoachesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayAllCoaches(tableModel, table);
            }
        });
        add(displayAllCoachesButton);

        JButton displayAllMatchesButton = createStyledButton("Display All Matches");
        displayAllMatchesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayAllMatches(tableModel, table);
            }
        });
        add(displayAllMatchesButton);

        JButton refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });
        add(refreshButton);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        add(scrollPane);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null); // Center the frame on the screen
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void displayAllTeams(DefaultTableModel tableModel, JTable table) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM Team";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int teamId = rs.getInt("team_id");
                String teamName = rs.getString("team_name");
                int coachId = rs.getInt("coach_id");

                tableModel.addRow(new Object[]{"Display", "Teams", teamId, teamName, coachId});
            }

            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void displayAllCoaches(DefaultTableModel tableModel, JTable table) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM Coach";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int coachId = rs.getInt("coach_id");
                String coachName = rs.getString("coach_name");

                tableModel.addRow(new Object[]{"Display", "Coaches", coachId, coachName});
            }

            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void displayAllMatches(DefaultTableModel tableModel, JTable table) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM Match";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int matchId = rs.getInt("match_id");
                String matchLocation = rs.getString("match_location");
                int team1Id = rs.getInt("team1_id");
                int team2Id = rs.getInt("team2_id");

                tableModel.addRow(new Object[]{"Display", "Matches", matchId, matchLocation, team1Id, team2Id});
            }

            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0); // Clear all rows in the table
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DMLForm().setVisible(true);
            }
        });
    }
}

class TeamsPage extends JFrame {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USERNAME = "lokesh";
    private static final String PASSWORD = "Lokesh";

    private JTextField teamIdField;
    private JTextField teamNameField;
    private JTextField coachIdField;
    private DefaultTableModel tableModel;
    private JTable table;

    public TeamsPage(DefaultTableModel tableModel, JTable table) {
        this.tableModel = tableModel;
        this.table = table;
        setTitle("Teams Page");
        setSize(400, 150);

        JPanel panel = new JPanel(new FlowLayout());

        panel.add(new JLabel("Team ID:"));
        teamIdField = new JTextField(10);
        panel.add(teamIdField);

        panel.add(new JLabel("Team Name:"));
        teamNameField = new JTextField(10);
        panel.add(teamNameField);

        panel.add(new JLabel("Coach ID:"));
        coachIdField = new JTextField(10);
        panel.add(coachIdField);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int teamId = Integer.parseInt(teamIdField.getText());
                fetchTeamDetails(teamId);
            }
        });
        panel.add(okButton);

        JButton insertButton = new JButton("Insert");
        insertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                insertTeam();
            }
        });
        panel.add(insertButton);

        JButton modifyButton = new JButton("Modify");
        modifyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifyTeam();
            }
        });
        panel.add(modifyButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteTeam();
            }
        });
        panel.add(deleteButton);
        setLocationRelativeTo(null);

        add(panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void fetchTeamDetails(int teamId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM Team WHERE team_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, teamId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String teamName = rs.getString("team_name");
                int coachId = rs.getInt("coach_id");

                teamIdField.setText(String.valueOf(teamId));
                teamNameField.setText(teamName);
                coachIdField.setText(String.valueOf(coachId));
            } else {
                JOptionPane.showMessageDialog(this, "Team not found!");
                clearFields();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void insertTeam() {
        int teamId = Integer.parseInt(teamIdField.getText());
        String teamName = teamNameField.getText();
        int coachId = Integer.parseInt(coachIdField.getText());

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO Team (team_id, team_name, coach_id) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, teamId);
            stmt.setString(2, teamName);
            stmt.setInt(3, coachId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Team inserted successfully!");

            // Update the table
            tableModel.addRow(new Object[]{"Insert", "Teams", teamId, teamName, coachId});
            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));

            clearFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void modifyTeam() {
        int teamId = Integer.parseInt(teamIdField.getText());
        String teamName = teamNameField.getText();
        int coachId = Integer.parseInt(coachIdField.getText());

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "UPDATE Team SET team_name = ?, coach_id = ? WHERE team_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, teamName);
            stmt.setInt(2, coachId);
            stmt.setInt(3, teamId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Team modified successfully!");

            // Update the table
            tableModel.addRow(new Object[]{"Modify", "Teams", teamId, teamName, coachId});
            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));

            clearFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteTeam() {
        int teamId = Integer.parseInt(teamIdField.getText());

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "DELETE FROM Team WHERE team_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, teamId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Team deleted successfully!");

            // Update the table
            tableModel.addRow(new Object[]{"Delete", "Teams", teamId, "", ""});
            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));

            clearFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        teamIdField.setText("");
        teamNameField.setText("");
        coachIdField.setText("");
    }
}

// Implement the other pages (MatchesPage, PlayersPage, CoachesPage) in a similar manner

class MatchesPage extends JFrame {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USERNAME = "lokesh";
    private static final String PASSWORD = "Lokesh";

    private JTextField matchIdField;
    private JTextField locationField;
    private JTextField team1IdField;
    private JTextField team2IdField;
    private DefaultTableModel tableModel;
    private JTable table;

    public MatchesPage(DefaultTableModel tableModel, JTable table) {
        this.tableModel = tableModel;
        this.table = table;
        setTitle("Matches Page");
        setSize(400, 200);

        JPanel panel = new JPanel(new FlowLayout());

        panel.add(new JLabel("Match ID:"));
        matchIdField = new JTextField(10);
        panel.add(matchIdField);

        panel.add(new JLabel("Location:"));
        locationField = new JTextField(10);
        panel.add(locationField);

        panel.add(new JLabel("Team 1 ID:"));
        team1IdField = new JTextField(10);
        panel.add(team1IdField);

        panel.add(new JLabel("Team 2 ID:"));
        team2IdField = new JTextField(10);
        panel.add(team2IdField);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int matchId = Integer.parseInt(matchIdField.getText());
                fetchMatchDetails(matchId);
            }
        });
        panel.add(okButton);

        JButton insertButton = new JButton("Insert");
        insertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                insertMatch();
            }
        });
        panel.add(insertButton);

        JButton modifyButton = new JButton("Modify");
        modifyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifyMatch();
            }
        });
        panel.add(modifyButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteMatch();
            }
        });
        panel.add(deleteButton);
        setLocationRelativeTo(null);

        add(panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void fetchMatchDetails(int matchId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM Match WHERE match_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, matchId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String matchLocation = rs.getString("match_location");
                int team1Id = rs.getInt("team1_id");
                int team2Id = rs.getInt("team2_id");

                locationField.setText(matchLocation);
                team1IdField.setText(String.valueOf(team1Id));
                team2IdField.setText(String.valueOf(team2Id));
            } else {
                JOptionPane.showMessageDialog(this, "Match not found!");
                clearFields();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private void insertMatch() {
        int matchId = Integer.parseInt(matchIdField.getText());
        String location = locationField.getText();
        int team1Id = Integer.parseInt(team1IdField.getText());
        int team2Id = Integer.parseInt(team2IdField.getText());

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO Match (match_id, match_location, team1_id, team2_id) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, matchId);
            stmt.setString(2, location);
            stmt.setInt(3, team1Id);
            stmt.setInt(4, team2Id);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Match inserted successfully!");

            // Update the table
            tableModel.addRow(new Object[]{"Insert", "Matches", matchId, location, team1Id, team2Id});
            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));

            clearFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void modifyMatch() {
        int matchId = Integer.parseInt(matchIdField.getText());
        String location = locationField.getText();
        int team1Id = Integer.parseInt(team1IdField.getText());
        int team2Id = Integer.parseInt(team2IdField.getText());

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "UPDATE Match SET match_location = ?, team1_id = ?, team2_id = ? WHERE match_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, location);
            stmt.setInt(2, team1Id);
            stmt.setInt(3, team2Id);
            stmt.setInt(4, matchId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Match modified successfully!");

            // Update the table
            tableModel.addRow(new Object[]{"Modify", "Matches", matchId, location, team1Id, team2Id});
            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));

            clearFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteMatch() {
        int matchId = Integer.parseInt(matchIdField.getText());

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "DELETE FROM Match WHERE match_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, matchId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Match deleted successfully!");

            // Update the table
            tableModel.addRow(new Object[]{"Delete", "Matches", matchId, "", "", ""});
            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));

            clearFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        matchIdField.setText("");
        locationField.setText("");
        team1IdField.setText("");
        team2IdField.setText("");
    }
}

class PlayersPage extends JFrame {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USERNAME = "lokesh";
    private static final String PASSWORD = "Lokesh";

    private JTextField playerIdField;
    private JTextField playerNameField;
    private JTextField teamIdField;
    private DefaultTableModel tableModel;
    private JTable table;

    public PlayersPage(DefaultTableModel tableModel, JTable table) {
        this.tableModel = tableModel;
        this.table = table;
        setTitle("Players Page");
        setSize(400, 150);

        JPanel panel = new JPanel(new FlowLayout());

        panel.add(new JLabel("Player ID:"));
        playerIdField = new JTextField(10);
        panel.add(playerIdField);

        panel.add(new JLabel("Player Name:"));
        playerNameField = new JTextField(10);
        panel.add(playerNameField);

        panel.add(new JLabel("Team ID:"));
        teamIdField = new JTextField(10);
        panel.add(teamIdField);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int playerId = Integer.parseInt(playerIdField.getText());
                fetchPlayerDetails(playerId);
            }
        });
        panel.add(okButton);

        JButton insertButton = new JButton("Insert");
        insertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                insertPlayer();
            }
        });
        panel.add(insertButton);

        JButton modifyButton = new JButton("Modify");
        modifyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifyPlayer();
            }
        });
        panel.add(modifyButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deletePlayer();
            }
        });
        panel.add(deleteButton);
        setLocationRelativeTo(null);

        add(panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void fetchPlayerDetails(int playerId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM Player WHERE player_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, playerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String playerName = rs.getString("player_name");
                int teamId = rs.getInt("team_id");

                playerIdField.setText(String.valueOf(playerId));
                playerNameField.setText(playerName);
                teamIdField.setText(String.valueOf(teamId));
            } else {
                JOptionPane.showMessageDialog(this, "Player not found!");
                clearFields();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void insertPlayer() {
        int playerId = Integer.parseInt(playerIdField.getText());
        String playerName = playerNameField.getText();
        int teamId = Integer.parseInt(teamIdField.getText());

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO Player (player_id, player_name, team_id) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, playerId);
            stmt.setString(2, playerName);
            stmt.setInt(3, teamId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Player inserted successfully!");

            // Update the table
            tableModel.addRow(new Object[]{"Insert", "Players", playerId, playerName, teamId});
            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));

            clearFields();
} catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void modifyPlayer() {
        int playerId = Integer.parseInt(playerIdField.getText());
        String playerName = playerNameField.getText();
        int teamId = Integer.parseInt(teamIdField.getText());

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "UPDATE Player SET player_name = ?, team_id = ? WHERE player_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, playerName);
            stmt.setInt(2, teamId);
            stmt.setInt(3, playerId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Player modified successfully!");

            // Update the table
            tableModel.addRow(new Object[]{"Modify", "Players", playerId, playerName, teamId});
            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));

            clearFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deletePlayer() {
        int playerId = Integer.parseInt(playerIdField.getText());

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "DELETE FROM Player WHERE player_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, playerId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Player deleted successfully!");

            // Update the table
            tableModel.addRow(new Object[]{"Delete", "Players", playerId, "", ""});
            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));

            clearFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        playerIdField.setText("");
        playerNameField.setText("");
        teamIdField.setText("");
    }
}

class CoachesPage extends JFrame {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USERNAME = "lokesh";
    private static final String PASSWORD = "Lokesh";

    private JTextField coachIdField;
    private JTextField coachNameField;
    private DefaultTableModel tableModel;
    private JTable table;

    public CoachesPage(DefaultTableModel tableModel, JTable table) {
        this.tableModel = tableModel;
        this.table = table;
        setTitle("Coaches Page");
        setSize(400, 150);

        JPanel panel = new JPanel(new FlowLayout());

        panel.add(new JLabel("Coach ID:"));
        coachIdField = new JTextField(10);
        panel.add(coachIdField);

        panel.add(new JLabel("Coach Name:"));
        coachNameField = new JTextField(10);
        panel.add(coachNameField);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int coachId = Integer.parseInt(coachIdField.getText());
                fetchCoachDetails(coachId);
            }
        });
        panel.add(okButton);

        JButton insertButton = new JButton("Insert");
        insertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                insertCoach();
            }
        });
        panel.add(insertButton);

        JButton modifyButton = new JButton("Modify");
        modifyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifyCoach();
            }
        });
        panel.add(modifyButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteCoach();
            }
        });
        panel.add(deleteButton);
        setLocationRelativeTo(null);

        add(panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void fetchCoachDetails(int coachId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM Coach WHERE coach_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, coachId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String coachName = rs.getString("coach_name");

                coachIdField.setText(String.valueOf(coachId));
                coachNameField.setText(coachName);
            } else {
                JOptionPane.showMessageDialog(this, "Coach not found!");
                clearFields();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void insertCoach() {
        int coachId = Integer.parseInt(coachIdField.getText());
        String coachName = coachNameField.getText();

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO Coach (coach_id, coach_name) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, coachId);
            stmt.setString(2, coachName);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Coach inserted successfully!");

            // Update the table
            tableModel.addRow(new Object[]{"Insert", "Coaches", coachId, coachName});
            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));

            clearFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void modifyCoach() {
        int coachId = Integer.parseInt(coachIdField.getText());
        String coachName = coachNameField.getText();

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "UPDATE Coach SET coach_name = ? WHERE coach_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, coachName);
            stmt.setInt(2, coachId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Coach modified successfully!");

            // Update the table
            tableModel.addRow(new Object[]{"Modify", "Coaches", coachId, coachName});
            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));

            clearFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteCoach() {
        int coachId = Integer.parseInt(coachIdField.getText());

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "DELETE FROM Coach WHERE coach_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, coachId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Coach deleted successfully!");

            // Update the table
            tableModel.addRow(new Object[]{"Delete", "Coaches", coachId, ""});
            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));

            clearFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        coachIdField.setText("");
        coachNameField.setText("");
    }
}
