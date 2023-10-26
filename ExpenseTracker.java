import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

class Expense implements Serializable {
    private String description;
    private double amount;
    private String category;

    public Expense(String description, double amount, String category) {
        this.description = description;
        this.amount = amount;
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }
}

public class ExpenseTracker {
    private List<Expense> expenses = new ArrayList<>();
    private JFrame frame;
    private JList<Expense> expenseList;
    private DefaultListModel<Expense> listModel;
    private JTextField descriptionField, amountField, categoryField;

    public ExpenseTracker() {
        frame = new JFrame("Expense Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        expenseList = new JList<>(listModel);
        frame.add(new JScrollPane(expenseList), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        frame.add(inputPanel, BorderLayout.SOUTH);

        descriptionField = new JTextField();
        amountField = new JTextField();
        categoryField = new JTextField();
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryField);

        JButton addExpenseButton = new JButton("Add Expense");
        inputPanel.add(addExpenseButton);

        addExpenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addExpense();
            }
        });

        JButton viewExpensesButton = new JButton("View Expenses");
        inputPanel.add(viewExpensesButton);

        viewExpensesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewExpenses();
            }
        });

        JButton viewSummaryButton = new JButton("View Summary");
        inputPanel.add(viewSummaryButton);

        viewSummaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewExpenseSummaries();
            }
        });

        JButton clearButton = new JButton("Clear Expenses");
        inputPanel.add(clearButton);

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearExpenses();
            }
        });

        JButton saveButton = new JButton("Save");
        inputPanel.add(saveButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveExpenses();
                JOptionPane.showMessageDialog(frame, "Expenses saved. Thank you for using the Expense Tracker.", "Expense Tracker", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        loadExpenses();

        frame.pack();
        frame.setVisible(true);
    }

    private void addExpense() {
        String description = descriptionField.getText();
        double amount = Double.parseDouble(amountField.getText());
        String category = categoryField.getText();

        Expense expense = new Expense(description, amount, category);
        expenses.add(expense);
        listModel.addElement(expense);

        descriptionField.setText("");
        amountField.setText("");
        categoryField.setText("");
    }

    private void viewExpenses() {
        if (expenses.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No expenses recorded yet.", "View Expenses", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder expensesText = new StringBuilder("Expenses List:\n");
            for (Expense expense : expenses) {
                expensesText.append("Description: ").append(expense.getDescription()).append("\n");
                expensesText.append("Amount: ₹").append(expense.getAmount()).append("\n");
                expensesText.append("Category: ").append(expense.getCategory()).append("\n");
                expensesText.append("------------------------\n");
            }
            JOptionPane.showMessageDialog(frame, expensesText.toString(), "View Expenses", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewExpenseSummaries() {
        if (expenses.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No expenses recorded yet.", "Expense Summaries", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String[] options = { "Total Expenses by Category" };
            int choice = JOptionPane.showOptionDialog(frame, "Select a summary type:", "Expense Summaries", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0:
                    viewTotalExpensesByCategory();
                    break;
            }
        }
    }

    private void viewTotalExpensesByCategory() {
        if (expenses.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No expenses recorded yet.", "Total Expenses by Category", JOptionPane.INFORMATION_MESSAGE);
        } else {
            Map<String, Double> categoryTotals = expenses.stream()
                    .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));

            StringBuilder summaryText = new StringBuilder("Total Expenses by Category:\n");
            for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                summaryText.append(entry.getKey()).append(": ₹").append(entry.getValue()).append("\n");
            }

            JOptionPane.showMessageDialog(frame, summaryText.toString(), "Total Expenses by Category", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearExpenses() {
        int confirmation = JOptionPane.showConfirmDialog(frame, "Are you sure you want to clear all expenses?", "Clear Expenses", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            expenses.clear();
            listModel.removeAllElements();
        }
    }

    private void loadExpenses() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("expenses.dat"))) {
            expenses = (List<Expense>) inputStream.readObject();
            for (Expense expense : expenses) {
                listModel.addElement(expense);
            }
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(frame, "No expenses found. Starting with an empty list.", "Expense Tracker", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void saveExpenses() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("expenses.dat"))) {
            outputStream.writeObject(expenses);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to save expenses: " + e.getMessage(), "Expense Tracker", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ExpenseTracker();
            }
        });
    }
}
