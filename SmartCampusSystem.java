package smart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SmartCampusSystem {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm");
    private static final Color PRIMARY_COLOR = new Color(113,154,191);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    //data storage
    private List<Student> students = new ArrayList<>();
    private List<Classroom> classrooms = new ArrayList<>();
    private List<Resource> resources = new ArrayList<>();
    
    public SmartCampusSystem() {
    	//load data from files
        students = SaveData.loadData("students.dat");
        classrooms = SaveData.loadData("classrooms.dat");
        resources = SaveData.loadData("resources.dat");

        //if no data exists, initialise with the sample data
        if (students.isEmpty() && classrooms.isEmpty() && resources.isEmpty()) {
            initSampleData();
        }
        
        createMainFrame();
        createMenuBar();
        createPanels();
        mainFrame.setVisible(true);
        mainFrame.setResizable(false);
        
    }
   
    private void initSampleData() {
        //sample students
        students.add(new Student("S1001", "Josh Williams", "Animation", "JWilliams@SmartUni.edu"));
        students.add(new Student("S1002", "Maria Kool", "Engineering", "MKool@SmartUni.edu"));
        students.add(new Student("S1003", "Nico Robin", "Ancient History", "NRobin@SmartUni.edu"));
        students.add(new Student("S1004", "Ben Leslie", "Culinary Arts", "BLeslie@SmartUni.edu"));
        
        //sample classrooms
        classrooms.add(new Classroom("R101", "Lecture Hall", 120));
        classrooms.add(new Classroom("R202", "Computer Lab", 30));
        classrooms.add(new Classroom("R305", "Seminar Room", 20));
        
        //sample bookings
        classrooms.get(0).addBooking("CS101", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2));
        classrooms.get(1).addBooking("ENG201", LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(3));
        
        //sample resources
        resources.add(new Resource("B001", "Advanced Java Programming", "Book", "Available"));
        resources.add(new Resource("L002", "Microscope", "Lab Equipment", "Checked Out"));
        resources.add(new Resource("C003", "Arduino Kit", "Electronics", "Available"));
        
        saveAllData();
    }
    
    private void saveAllData() {
    	SaveData.saveData("students.dat", students);
    	SaveData.saveData("classrooms.dat", classrooms);
    	SaveData.saveData("resources.dat", resources);
    }
    
    private Student searchForStudent(String searchTerm) {
        return students.stream()
            .filter(student -> 
                student.getId().equalsIgnoreCase(searchTerm) || 
                student.getName().toLowerCase().contains(searchTerm.toLowerCase()))
            .findFirst()
            .orElse(null);
    }
    
    private Resource searchForResource(String searchTerm) {
        return resources.stream()
            .filter(resource -> 
            	resource.getId().equalsIgnoreCase(searchTerm) || 
            	resource.getName().toLowerCase().contains(searchTerm.toLowerCase()))
            .findFirst()
            .orElse(null);
    }
    
    private String getCheckedOutResources(String studentId) {
        return resources.stream()
        	.filter(r -> studentId.equals(r.getCheckedOutBy()))
            .map(r -> "- " + r.getName() + " (" + r.getId() + ")")
            .collect(Collectors.joining("\n"));
    }
    
    private void createMainFrame() {
        mainFrame = new JFrame("Smart Campus Management System");
        mainFrame.setSize(850, 800);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        //modern icon handling 
        URL iconURL = getClass().getResource("/icon1.png");
        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            mainFrame.setIconImage(icon.getImage());
        }
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveAllData();
            }
        });
    }
    
    private String currentPanel = "welcome";
    
    private void updateWindowResizable() {
        boolean shouldResize = !currentPanel.equals("welcome");
        if (mainFrame.isResizable() != shouldResize) {
            mainFrame.setResizable(shouldResize);
            //sets frame back to original size regardless of dimensions of the previous window
            mainFrame.setSize(850, 800); 
            
            //maintain window position 
            if (shouldResize) {
                mainFrame.pack();
            } else {
            	//lock current size
                mainFrame.setSize(mainFrame.getSize()); 
            }
        }
    }
    
    public void switchPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
        currentPanel = panelName;
        updateResizableState();
    }
    
    private void updateResizableState() {
        mainFrame.setResizable(!currentPanel.equals("welcome"));
    }

    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(PRIMARY_COLOR);
        menuBar.setForeground(Color.WHITE);
        
        //home menu
        JMenu HomeMenu = new JMenu("Home");
        HomeMenu.setForeground(Color.BLACK);
        JMenuItem homeItem = new JMenuItem("Return to Dashboard");
        homeItem.addActionListener(_ -> cardLayout.show(cardPanel, "welcome"));
        HomeMenu.add(homeItem);
        

        //navigation menu
        JMenu navMenu = new JMenu("Navigation");
        navMenu.setForeground(Color.BLACK);
        
        JMenuItem studentsItem = new JMenuItem("Student Records");
        studentsItem.addActionListener(_ -> {
            cardLayout.show(cardPanel, "students");
            currentPanel = "students";
            updateWindowResizable();
        });
      
        JMenuItem classroomsItem = new JMenuItem("Classroom Scheduling");
        classroomsItem.addActionListener(_ -> {
            cardLayout.show(cardPanel, "classrooms");
            currentPanel = "classrooms";
            updateWindowResizable();
        });

        
        JMenuItem resourcesItem = new JMenuItem("Resource Management");     
        resourcesItem.addActionListener(_ -> {
            cardLayout.show(cardPanel, "resources");
            currentPanel = "resources";
            updateWindowResizable();
        });

        
        navMenu.add(studentsItem);
        navMenu.add(classroomsItem);
        navMenu.add(resourcesItem);
        
        //help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(Color.BLACK);
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(_ -> showAboutDialog());
        helpMenu.add(aboutItem);
        JMenuItem howItem = new JMenuItem("How To");
        howItem.addActionListener(_ -> showHowDialog());
        helpMenu.add(howItem);
        
        menuBar.add(HomeMenu);
        menuBar.add(navMenu);
        menuBar.add(helpMenu);

        
        mainFrame.setJMenuBar(menuBar);
    }
    
    private void createPanels() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        //create and add all panels
        cardPanel.add(createWelcomePanel(), "welcome");
        cardPanel.add(createStudentsPanel(), "students");
        cardPanel.add(createClassroomsPanel(), "classrooms");
        cardPanel.add(createResourcesPanel(), "resources");
        
        mainFrame.add(cardPanel);
    }
    
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(SECONDARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        //title
        JLabel titleLabel = new JLabel("Smart Campus Management System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        //main content panel, description on the left and screenshots on the right
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        
        //left side
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setCaretColor(Color.WHITE);
        infoArea.setWrapStyleWord(true);
        infoArea.setBackground(SECONDARY_COLOR);
        infoArea.setText("Welcome to the Smart Campus Management System.\n\n\n" +
        		"This system has been developed for:\n"
        		+ "CIS1703 - Programming 2 2024\n" +
        		"by 25774077 - Amelia Kool\n\n" +
                "This system aims to assist a user in managing:\n" +
                "    - Student records\n" +
                "    - Classroom scheduling and booking\n" +
                "    - General resources\n\n" +
                "You can achieve this by using the navigation menu to access these features. If you need further guidance, click on help located in the top left.");
              
        infoArea.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JScrollPane textScroll = new JScrollPane(infoArea);
        textScroll.setBorder(null);
        contentPanel.add(textScroll, BorderLayout.CENTER);
        
        //right side - vertical stack of screenshots
        JPanel screenshotPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        screenshotPanel.setBackground(SECONDARY_COLOR);
        screenshotPanel.setPreferredSize(new Dimension(350, 0));
        
        //student records screenshot
        screenshotPanel.add(createScreenshotCard(
            "Student Records", 
            "resources/RecordManage.png"
        ));
        
        //classroom scheduling screenshot
        screenshotPanel.add(createScreenshotCard(
            "Classroom Scheduling",
            "resources/ClassroomSchedule.png"
        ));
        
        //resource management screenshot
        screenshotPanel.add(createScreenshotCard(
            "Resource Management",
            "resources/ResourceManage.png"
        ));
        
        contentPanel.add(screenshotPanel, BorderLayout.EAST);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        //status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel statusLabel = new JLabel("Ready");
        statusPanel.add(statusLabel);
        panel.add(statusPanel, BorderLayout.SOUTH);
        
        panel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (panel.isShowing()) {
                    currentPanel = "welcome";
                    updateWindowResizable();
                }
            }
        });
        
        return panel;
    }
    

    private JPanel createScreenshotCard(String title, String imagePath) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
            
        ));
        
        //title
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        //scaled screenshot
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(300, -1, Image.SCALE_SMOOTH);
        JLabel screenshotLabel = new JLabel(new ImageIcon(scaledImage));
        screenshotLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(screenshotLabel, BorderLayout.CENTER);
       
        return panel;
    }


    
    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        //title
        JLabel titleLabel = new JLabel("Student Records Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        //table for displaying students
        String[] columnNames = {"Student ID", "Name", "Degree", "Email"};
        Object[][] data = new Object[students.size()][4];
        
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            data[i][0] = student.getId();
            data[i][1] = student.getName();
            data[i][2] = student.getDegree();
            data[i][3] = student.getEmail();
        }
        
        JTable studentTable = new JTable(data, columnNames);
        studentTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        
        //button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(_ -> showAddStudentDialog());
        
        JButton searchButton = new JButton("Search for Student");
        searchButton.addActionListener(_ -> showStudentSearchDialog());
        
        JButton editButton = new JButton("Edit Student");
        editButton.addActionListener(_ -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow >= 0) {
                showEditStudentDialog(students.get(selectedRow));
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a student to edit.", "No Selection.", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton deleteButton = new JButton("Delete Student");
        deleteButton.addActionListener(_ -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(panel, 
                    "Are you sure you want to delete this student?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                	deleteStudent(selectedRow);
                    refreshStudentsTable(studentTable);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a student to delete.", "No Selection.", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    
    private boolean isStudentIdUnique(String id) {
        return students.stream().noneMatch(s -> s.getId().equalsIgnoreCase(id));
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.edu$");
    }
    
    private void showAddStudentDialog() {
        JDialog dialog = new JDialog(mainFrame, "Add New Student", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(mainFrame);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel idLabel = new JLabel("Student ID:");
        JTextField idField = new JTextField();
        
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        
        JLabel degreeLabel = new JLabel("Degree:");
        JTextField degreeField = new JTextField();
        
        JLabel emailLabel = new JLabel("Email (must end with .edu):");
        JTextField emailField = new JTextField();
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(_ -> {
            String id = idField.getText();
            String name = nameField.getText();
            String degree = degreeField.getText();
            String email = emailField.getText();
            
            if (id.isEmpty() || name.isEmpty() || degree.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (!isStudentIdUnique(id)) {
                JOptionPane.showMessageDialog(dialog, "Student ID already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                //cancel save
                return;
            }
            
            //ensure it is the correct email format
            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(dialog, "Email must be a valid .edu address!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            //add students and save
            students.add(new Student(id, name, degree, email));
            saveAllData();
            refreshStudentsPanel();
            dialog.dispose();
        });
        
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dialog.dispose());
        
        panel.add(idLabel);
        panel.add(idField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(degreeLabel);
        panel.add(degreeField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(saveButton);
        panel.add(cancelButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showStudentSearchDialog() {
        JDialog dialog = new JDialog(mainFrame, "Search Student", true);
        dialog.setSize(400, 150);
        dialog.setLayout(new BorderLayout(10, 10));

        //search panel
        JPanel searchPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("ID or Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        //results
        JTextArea resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        //the search action
        searchButton.addActionListener(_ -> {
            Student found = searchForStudent(searchField.getText().trim());
            if (found != null) {
                resultArea.setText(formatStudentDetails(found));
            } else {
                resultArea.setText("No student found matching: " + searchField.getText());
            }
        });

        //add components
        dialog.add(searchPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }
    
    //here I format the student's details to display to the user who is searching for an individual
    private String formatStudentDetails(Student student) {
        String checkedOutResources = getCheckedOutResources(student.getId());
        if (checkedOutResources.isEmpty()) {
            checkedOutResources = "None";
        }
        
        return String.format(
            "Student ID: %s\nName: %s\nDegree: %s\nEmail: %s\n\n" +
            "Checked Out Resources:\n%s",
            student.getId(),
            student.getName(),
            student.getDegree(),
            student.getEmail(),
            checkedOutResources
        );
    }
    
    private void showEditStudentDialog(Student student) {
        JDialog dialog = new JDialog(mainFrame, "Edit Student", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(mainFrame);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel idLabel = new JLabel("Student ID:");
        JTextField idField = new JTextField(student.getId());
        idField.setEditable(false);
        
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(student.getName());
        
        JLabel degreeLabel = new JLabel("Degree:");
        JTextField degreeField = new JTextField(student.getDegree());
        
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(student.getEmail());
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(_ -> {
            String name = nameField.getText();
            String degree = degreeField.getText();
            String email = emailField.getText();
            
            if (name.isEmpty() || degree.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(dialog, "Email must be a valid .edu address!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            student.setName(name);
            student.setDegree(degree);
            student.setEmail(email);
            saveAllData();
            refreshStudentsPanel();
            dialog.dispose();

        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dialog.dispose());
        
        panel.add(idLabel);
        panel.add(idField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(degreeLabel);
        panel.add(degreeField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(saveButton);
        panel.add(cancelButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void deleteStudent(int index) {
        int confirm = JOptionPane.showConfirmDialog(mainFrame, 
            "Are you sure you want to delete this student? This cannot be undone!", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            students.remove(index);
            //ensure all changes apply
            saveAllData();
            refreshStudentsPanel();
        }
    }
    
    
    private void refreshStudentsTable(JTable table) {
        Object[][] data = new Object[students.size()][4];
        
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            data[i][0] = student.getId();
            data[i][1] = student.getName();
            data[i][2] = student.getDegree();
            data[i][3] = student.getEmail();
        }
        
        table.setModel(new javax.swing.table.DefaultTableModel(data, new String[]{"Student ID", "Name", "Degree", "Email"}));
    }
    
    private void refreshStudentsPanel() {
    	//remove students panel
        cardPanel.remove(1);
        cardPanel.add(createStudentsPanel(), "students", 1); //add new students panel at same position
        cardLayout.show(cardPanel, "students");
    }
    
    private JPanel createClassroomsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        //title
        JLabel titleLabel = new JLabel("Classroom Scheduling", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        //tabbed pane for classrooms and bookings
        JTabbedPane tabbedPane = new JTabbedPane();
        
        //classroom list tab
        JPanel classroomListPanel = new JPanel(new BorderLayout());
        
        String[] classroomColumns = {"Room Number", "Type", "Capacity"};
        Object[][] classroomData = new Object[classrooms.size()][3];
        
        for (int i = 0; i < classrooms.size(); i++) {
            Classroom room = classrooms.get(i);
            classroomData[i][0] = room.getRoomNumber();
            classroomData[i][1] = room.getType();
            classroomData[i][2] = room.getCapacity();
        }
        
        JTable classroomTable = new JTable(classroomData, classroomColumns);
        JScrollPane classroomScrollPane = new JScrollPane(classroomTable);
        
        //button panel for classrooms
        JPanel classroomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton addClassroomButton = new JButton("Add Classroom");
        addClassroomButton.addActionListener(_ -> showAddClassroomDialog());
        
        JButton viewBookingsButton = new JButton("View Bookings");
        viewBookingsButton.addActionListener(_ -> {
            int selectedRow = classroomTable.getSelectedRow();
            if (selectedRow >= 0) {
                showClassroomBookingsDialog(classrooms.get(selectedRow));
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a classroom to view bookings.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        classroomButtonPanel.add(addClassroomButton);
        classroomButtonPanel.add(viewBookingsButton);
        
        classroomListPanel.add(classroomScrollPane, BorderLayout.CENTER);
        classroomListPanel.add(classroomButtonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Classrooms", classroomListPanel);
        
        //booking management tab
        JPanel bookingPanel = new JPanel(new BorderLayout());
        
        //create a list of all bookings from all classrooms
        List<Booking> allBookings = new ArrayList<>();
        for (Classroom room : classrooms) {
            allBookings.addAll(room.getBookings());
        }
        
        String[] bookingColumns = {"Room", "Course", "Start Time", "End Time"};
        Object[][] bookingData = new Object[allBookings.size()][4];
        
        for (int i = 0; i < allBookings.size(); i++) {
            Booking booking = allBookings.get(i);
            bookingData[i][0] = booking.getClassroom().getRoomNumber();
            bookingData[i][1] = booking.getCourse();
            bookingData[i][2] = booking.getStartTime().format(TIME_FORMAT);
            bookingData[i][3] = booking.getEndTime().format(TIME_FORMAT);
        }
        
        JTable bookingTable = new JTable(bookingData, bookingColumns);
        JScrollPane bookingScrollPane = new JScrollPane(bookingTable);
        
        //button panel for bookings
        JPanel bookingButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton addBookingButton = new JButton("Add Booking");
        addBookingButton.addActionListener(_ -> showAddBookingDialog(null));
        
        JButton editBookingButton = new JButton("Edit Booking");
        editBookingButton.addActionListener(_ -> {
            int selectedRow = bookingTable.getSelectedRow();
            if (selectedRow >= 0) {
                showEditBookingDialog(allBookings.get(selectedRow));
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a booking to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton deleteBookingButton = new JButton("Delete Booking");
        deleteBookingButton.addActionListener(_ -> {
            int selectedRow = bookingTable.getSelectedRow();
            if (selectedRow >= 0) {
                Booking booking = allBookings.get(selectedRow);
                int confirm = JOptionPane.showConfirmDialog(panel, 
                    "Are you sure you want to delete this booking?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    booking.getClassroom().removeBooking(booking);
                    refreshClassroomsPanel();
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a booking to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        bookingButtonPanel.add(addBookingButton);
        bookingButtonPanel.add(editBookingButton);
        bookingButtonPanel.add(deleteBookingButton);
        
        bookingPanel.add(bookingScrollPane, BorderLayout.CENTER);
        bookingPanel.add(bookingButtonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("All Bookings", bookingPanel);
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void showAddClassroomDialog() {
        JDialog dialog = new JDialog(mainFrame, "Add New Classroom", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(mainFrame);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel roomLabel = new JLabel("Room Number:");
        JTextField roomField = new JTextField();
        
        JLabel typeLabel = new JLabel("Type:");
        JTextField typeField = new JTextField();
        
        JLabel capacityLabel = new JLabel("Capacity:");
        JTextField capacityField = new JTextField();
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(_ -> {
            String roomNumber = roomField.getText();
            String type = typeField.getText();
            String capacityStr = capacityField.getText();
            
            if (roomNumber.isEmpty() || type.isEmpty() || capacityStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                int capacity = Integer.parseInt(capacityStr);
                classrooms.add(new Classroom(roomNumber, type, capacity));
                dialog.dispose();
                refreshClassroomsPanel();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid number for capacity.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
            saveAllData();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dialog.dispose());
        
        panel.add(roomLabel);
        panel.add(roomField);
        panel.add(typeLabel);
        panel.add(typeField);
        panel.add(capacityLabel);
        panel.add(capacityField);
        panel.add(saveButton);
        panel.add(cancelButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showClassroomBookingsDialog(Classroom classroom) {
        JDialog dialog = new JDialog(mainFrame, "Bookings for " + classroom.getRoomNumber(), true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(mainFrame);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        //title
        JLabel titleLabel = new JLabel("Bookings for " + classroom.getRoomNumber() + " (" + classroom.getType() + ")", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        //bookings table
        List<Booking> bookings = classroom.getBookings();
        String[] columns = {"Course", "Start Time", "End Time"};
        Object[][] data = new Object[bookings.size()][3];
        
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            data[i][0] = booking.getCourse();
            data[i][1] = booking.getStartTime().format(TIME_FORMAT);
            data[i][2] = booking.getEndTime().format(TIME_FORMAT);
        }
        
        JTable bookingsTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        //button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton addButton = new JButton("Add Booking");
        addButton.addActionListener(_ -> {
            dialog.dispose();
            showAddBookingDialog(classroom);
        });
        
        JButton editButton = new JButton("Edit Booking");
        editButton.addActionListener(_ -> {
            int selectedRow = bookingsTable.getSelectedRow();
            if (selectedRow >= 0) {
                dialog.dispose();
                showEditBookingDialog(bookings.get(selectedRow));
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a booking to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton deleteButton = new JButton("Delete Booking");
        deleteButton.addActionListener(_ -> {
            int selectedRow = bookingsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(dialog, 
                    "Are you sure you want to delete this booking?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                	removeClassroomBooking(classroom, bookings.get(selectedRow));
                    dialog.dispose();
                    refreshClassroomsPanel();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a booking to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showAddBookingDialog(Classroom specificClassroom) {
        JDialog dialog = new JDialog(mainFrame, "Add New Booking", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(mainFrame);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        //classroom selection (only if not specific classroom)
        JLabel roomLabel = new JLabel("Classroom:");
        JComboBox<Classroom> roomCombo = new JComboBox<>();
        
        if (specificClassroom != null) {
            roomCombo.addItem(specificClassroom);
            roomCombo.setEnabled(false);
        } else {
            for (Classroom room : classrooms) {
                roomCombo.addItem(room);
            }
        }
        
        JLabel courseLabel = new JLabel("Course:");
        JTextField courseField = new JTextField();
        
        JLabel startLabel = new JLabel("Start Time (dd-mm-yyyy hh:mm):");
        JTextField startField = new JTextField(LocalDateTime.now().format(TIME_FORMAT));
        
        JLabel endLabel = new JLabel("End Time (dd-mm-yyyy hh:mm):");
        JTextField endField = new JTextField(LocalDateTime.now().plusHours(2).format(TIME_FORMAT));
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(_ -> {
            Classroom selectedRoom = (Classroom) roomCombo.getSelectedItem();
            String course = courseField.getText();
            String startStr = startField.getText();
            String endStr = endField.getText();
            //different format for booking conflict 
            LocalDateTime sTime = LocalDateTime.parse(startField.getText(), TIME_FORMAT);
            LocalDateTime eTime = LocalDateTime.parse(endField.getText(), TIME_FORMAT);
            
            if (isBookingConflict(selectedRoom, sTime, eTime)) { 
                JOptionPane.showMessageDialog(dialog, 
                    "This timeslot conflicts with an existing booking!", 
                    "Conflict Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (course.isEmpty() || startStr.isEmpty() || endStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                LocalDateTime startTime = LocalDateTime.parse(startStr, TIME_FORMAT);
                LocalDateTime endTime = LocalDateTime.parse(endStr, TIME_FORMAT);
                
                if (endTime.isBefore(startTime)) {
                    JOptionPane.showMessageDialog(dialog, "End time must be after start time.", "Invalid Time", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                //check for overlapping bookings
                for (Booking existing : selectedRoom.getBookings()) {
                    if ((startTime.isBefore(existing.getEndTime()) && endTime.isAfter(existing.getStartTime()))) {
                        JOptionPane.showMessageDialog(dialog, 
                            "This booking overlaps with an existing booking:\n" +
                            existing.getCourse() + " from " + 
                            existing.getStartTime().format(TIME_FORMAT) + " to " + 
                            existing.getEndTime().format(TIME_FORMAT), 
                            "Booking Conflict", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                
                selectedRoom.addBooking(course, startTime, endTime);
                dialog.dispose();
                refreshClassroomsPanel();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid date/time in the correct format.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
            selectedRoom.addBooking(courseField.getText(), sTime, eTime);
            saveAllData();
            refreshClassroomsPanel();
            dialog.dispose();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dialog.dispose());
        
        panel.add(roomLabel);
        panel.add(roomCombo);
        panel.add(courseLabel);
        panel.add(courseField);
        panel.add(startLabel);
        panel.add(startField);
        panel.add(endLabel);
        panel.add(endField);
        panel.add(saveButton);
        panel.add(cancelButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void removeClassroomBooking(Classroom classroom, Booking booking) {
        classroom.removeBooking(booking);
        //ensure all changes apply and update UI
        saveAllData(); 
        refreshClassroomsPanel(); 
        
        //show confirmation
        JOptionPane.showMessageDialog(mainFrame, 
            "Booking deleted successfully.", 
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean isBookingConflict(Classroom room, LocalDateTime start, LocalDateTime end) {
        for (Booking existing : room.getBookings()) {
            if (start.isBefore(existing.getEndTime()) && end.isAfter(existing.getStartTime())) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Conflict with:\n" +
                    "Course: " + existing.getCourse() + "\n" +
                    "Time: " + existing.getStartTime().format(TIME_FORMAT) + " to " + 
                           existing.getEndTime().format(TIME_FORMAT),
                    "Booking Conflict Details",
                    JOptionPane.WARNING_MESSAGE);
                return true;
            }
        }
        return false;
    }
    
    private void showEditBookingDialog(Booking booking) {
        JDialog dialog = new JDialog(mainFrame, "Edit Booking", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(mainFrame);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        //classroom display (not editable)
        JLabel roomLabel = new JLabel("Classroom:");
        JLabel roomValue = new JLabel(booking.getClassroom().getRoomNumber());
        
        JLabel courseLabel = new JLabel("Course:");
        JTextField courseField = new JTextField(booking.getCourse());
        
        JLabel startLabel = new JLabel("Start Time (dd-mm-yyyy hh:mm):");
        JTextField startField = new JTextField(booking.getStartTime().format(TIME_FORMAT));
        
        JLabel endLabel = new JLabel("End Time (dd-mm-yyyy hh:mm):");
        JTextField endField = new JTextField(booking.getEndTime().format(TIME_FORMAT));
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(_ -> {
            String course = courseField.getText();
            String startStr = startField.getText();
            String endStr = endField.getText();
            
            if (course.isEmpty() || startStr.isEmpty() || endStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                LocalDateTime startTime = LocalDateTime.parse(startStr, TIME_FORMAT);
                LocalDateTime endTime = LocalDateTime.parse(endStr, TIME_FORMAT);
                
                if (endTime.isBefore(startTime)) {
                    JOptionPane.showMessageDialog(dialog, "End time must be after start time.", "Invalid Time", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                //temporarily remove the booking to check for conflicts with itself
                booking.getClassroom().removeBooking(booking);
                
                //check for overlapping bookings
                for (Booking existing : booking.getClassroom().getBookings()) {
                    if ((startTime.isBefore(existing.getEndTime()) && endTime.isAfter(existing.getStartTime()))) {
                        //add the booking back
                        booking.getClassroom().addBooking(booking);
                        JOptionPane.showMessageDialog(dialog, 
                            "This booking overlaps with an existing booking:\n" +
                            existing.getCourse() + " from " + 
                            existing.getStartTime().format(TIME_FORMAT) + " to " + 
                            existing.getEndTime().format(TIME_FORMAT), 
                            "Booking Conflict", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                
                //update the booking
                booking.setCourse(course);
                booking.setStartTime(startTime);
                booking.setEndTime(endTime);
                booking.getClassroom().addBooking(booking);
                
                dialog.dispose();
                refreshClassroomsPanel();
            } catch (Exception ex) {
                //add the booking back if there was an error
                booking.getClassroom().addBooking(booking);
                JOptionPane.showMessageDialog(dialog, "Please enter valid date/time in the correct format.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dialog.dispose());
        
        panel.add(roomLabel);
        panel.add(roomValue);
        panel.add(courseLabel);
        panel.add(courseField);
        panel.add(startLabel);
        panel.add(startField);
        panel.add(endLabel);
        panel.add(endField);
        panel.add(saveButton);
        panel.add(cancelButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void refreshClassroomsPanel() {
        cardPanel.remove(2); //remove classrooms panel
        cardPanel.add(createClassroomsPanel(), "classrooms", 2); //add new classrooms panel at same position
        cardLayout.show(cardPanel, "classrooms");
    }
    
    private JPanel createResourcesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        //title
        JLabel titleLabel = new JLabel("Resource Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        //table for displaying resources
        String[] columnNames = {"Resource ID", "Name", "Type", "Status"};
        Object[][] data = new Object[resources.size()][4];
        
        for (int i = 0; i < resources.size(); i++) {
            Resource resource = resources.get(i);
            data[i][0] = resource.getId();
            data[i][1] = resource.getName();
            data[i][2] = resource.getType();
            data[i][3] = resource.getStatus();
        }
        
        JTable resourceTable = new JTable(data, columnNames);
        resourceTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(resourceTable);
        
        //button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton addButton = new JButton("Add Resource");
        addButton.addActionListener(_ -> showAddResourceDialog());
        
        JButton searchButton = new JButton("Search Resources");
        searchButton.addActionListener(_ -> showResourceSearchDialog());
        
        JButton editButton = new JButton("Edit Resource");
        editButton.addActionListener(_ -> {
            int selectedRow = resourceTable.getSelectedRow();
            if (selectedRow >= 0) {
                showEditResourceDialog(resources.get(selectedRow));
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a resource to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton deleteButton = new JButton("Delete Resource");
        deleteButton.addActionListener(_ -> {
            int selectedRow = resourceTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(panel, 
                    "Are you sure you want to delete this resource?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    resources.remove(selectedRow);
                    refreshResourcesTable(resourceTable);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a resource to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton checkOutButton = new JButton("Check Out");
        checkOutButton.addActionListener(_ -> {
            int selectedRow = resourceTable.getSelectedRow();
            if (selectedRow >= 0) {
                Resource resource = resources.get(selectedRow);
                if (resource.getStatus().equals("Available")) {
                    checkOutResource(resource);
                } else {
                    JOptionPane.showMessageDialog(panel, 
                        "Resource is not available for checkout.", 
                        "Not Available", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(panel, 
                    "Please select a resource to check out.", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton returnButton = new JButton("Return");
        returnButton.addActionListener(_ -> {
            int selectedRow = resourceTable.getSelectedRow();
            if (selectedRow >= 0) {
                Resource resource = resources.get(selectedRow);
                if (resource.getStatus().startsWith("Checked Out")) {
                    resource.setStatus("Available");
                    refreshResourcesTable(resourceTable);
                } else {
                    JOptionPane.showMessageDialog(panel, "Resource is not checked out.", "Not Checked Out", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a resource to return.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(checkOutButton);
        buttonPanel.add(returnButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private boolean isValidStudentId(String studentId) {
        return students.stream()
               .anyMatch(student -> student.getId().equalsIgnoreCase(studentId.trim()));
    }
    
    private void checkOutResource(Resource resource) {
        String studentId = JOptionPane.showInputDialog(
            mainFrame,
            "Enter student ID for checkout:",
            "Resource Checkout",
            JOptionPane.QUESTION_MESSAGE
        );

        if (studentId == null || studentId.trim().isEmpty()) {
        	//user is cancelled
            return; 
        }

        if (!isValidStudentId(studentId)) {
            JOptionPane.showMessageDialog(
                mainFrame,
                "Error: Student ID " + studentId + " not found!\n" +
                "Only registered students can check out resources.",
                "Invalid Student ID",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        //proceed with the checkout
        resource.setStatus("Checked Out to " + studentId);
        resource.checkOut(studentId);
        saveAllData();
        refreshResourcesPanel();
        
        JOptionPane.showMessageDialog(
            mainFrame,
            "Resource successfully checked out to " + studentId,
            "Checkout Complete",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void showAddResourceDialog() {
        JDialog dialog = new JDialog(mainFrame, "Add New Resource", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(mainFrame);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel idLabel = new JLabel("Resource ID:");
        JTextField idField = new JTextField();
        
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        
        JLabel typeLabel = new JLabel("Type:");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Book", "Lab Equipment", "Electronics", "Other"});
        
        JLabel statusLabel = new JLabel("Initial Status:");
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Available", "Maintenance"});
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(_ -> {
            String id = idField.getText();
            String name = nameField.getText();
            String type = (String) typeCombo.getSelectedItem();
            String status = (String) statusCombo.getSelectedItem();
            
            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            resources.add(new Resource(id, name, type, status));
            dialog.dispose();
            refreshResourcesPanel();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dialog.dispose());
        
        panel.add(idLabel);
        panel.add(idField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(typeLabel);
        panel.add(typeCombo);
        panel.add(statusLabel);
        panel.add(statusCombo);
        panel.add(saveButton);
        panel.add(cancelButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showResourceSearchDialog() {
        JDialog dialog = new JDialog(mainFrame, "Search Resources", true);
        dialog.setSize(400, 150);
        dialog.setLayout(new BorderLayout(10, 10));

        //search panel
        JPanel searchPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("ID or Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        //results panel
        JTextArea resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        //search action
        searchButton.addActionListener(_ -> {
            Resource found = searchForResource(searchField.getText().trim());
            if (found != null) {
                resultArea.setText(formatResourceDetails(found));
            } else {
                resultArea.setText("No such resource has been found: " + searchField.getText());
            }
        });

        //add in components
        dialog.add(searchPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }
    
    //here I format the resource details to display to the user who is searching
    private String formatResourceDetails(Resource resource) {
        return String.format(
            "Resource ID: %s\nName: %s\nType: %s\nStatus: %s\nChecked out by: %s\n\n",
            resource.getId(),
            resource.getName(),
            resource.getType(),
            resource.getStatus(),
            resource.getCheckedOutBy()
       
        );
    }

    
    private void showEditResourceDialog(Resource resource) {
        JDialog dialog = new JDialog(mainFrame, "Edit Resource", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(mainFrame);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel idLabel = new JLabel("Resource ID:");
        JTextField idField = new JTextField(resource.getId());
        idField.setEditable(false);
        
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(resource.getName());
        
        JLabel typeLabel = new JLabel("Type:");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Book", "Lab Equipment", "Electronics", "Other"});
        typeCombo.setSelectedItem(resource.getType());
        
        JLabel statusLabel = new JLabel("Status:");
        JLabel statusValue = new JLabel(resource.getStatus());
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(_ -> {
            String name = nameField.getText();
            String type = (String) typeCombo.getSelectedItem();
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            resource.setName(name);
            resource.setType(type);
            dialog.dispose();
            refreshResourcesPanel();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dialog.dispose());
        
        panel.add(idLabel);
        panel.add(idField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(typeLabel);
        panel.add(typeCombo);
        panel.add(statusLabel);
        panel.add(statusValue);
        panel.add(saveButton);
        panel.add(cancelButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void refreshResourcesTable(JTable table) {
        Object[][] data = new Object[resources.size()][4];
        
        for (int i = 0; i < resources.size(); i++) {
            Resource resource = resources.get(i);
            data[i][0] = resource.getId();
            data[i][1] = resource.getName();
            data[i][2] = resource.getType();
            data[i][3] = resource.getStatus();
        }
        
        table.setModel(new javax.swing.table.DefaultTableModel(data, new String[]{"Resource ID", "Name", "Type", "Status"}));
    }
    
    private void refreshResourcesPanel() {
        cardPanel.remove(3); //remove resources panel
        cardPanel.add(createResourcesPanel(), "resources", 3); //add new resources panel at same position
        cardLayout.show(cardPanel, "resources");
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(mainFrame, 
            "This is the Smart Campus Management System.\n\n" +
            "This was developed to assist student record,\n"
            + "classroom scheduling and classroom resource(s) management\n" +
            "for staff and students alike.\n\n"
            + "Amelia Kool - 25774077 - for CIS1703 - Programming 2.", 
            "About", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showHowDialog() {
        JOptionPane.showMessageDialog(mainFrame, 
            "Access management features via the Navigation button in the toolbar.\n" +
            "    -Student Records\n" +
            "    -Classroom Scheduling\n" +
            "    -Classroom Resources\n" +
            "Return home by clicking the home button in the top left, and then\n" +
            "selecting 'Return to dashboard'.",
            "How To", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                //here I set the system's 'look and feel'
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                //changing some UI colours
                UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 14));
                UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 12));
                
                new SmartCampusSystem();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}


