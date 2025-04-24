import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

public class PageReplacementGUI extends JFrame {
    private JTextField inputField, slotField;
    private JButton runButton;
    private JTable fifoTable, lruTable, lfuTable, mfuTable;
    private JLabel resultLabel;

    public PageReplacementGUI() {
        setTitle("Page Replacement Algorithm Comparison");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputField = new JTextField(20);
        slotField = new JTextField(5);
        runButton = new JButton("Run Analysis");
        inputPanel.add(new JLabel("Page String (space-separated):"));
        inputPanel.add(inputField);
        inputPanel.add(new JLabel("Slots:"));
        inputPanel.add(slotField);
        inputPanel.add(runButton);

        JPanel resultPanel = new JPanel(new GridLayout(2, 2));
        resultPanel.add(createTablePanel("FIFO", fifoTable = createTable()));
        resultPanel.add(createTablePanel("LRU", lruTable = createTable()));
        resultPanel.add(createTablePanel("LFU", lfuTable = createTable()));
        resultPanel.add(createTablePanel("MFU", mfuTable = createTable()));

        resultLabel = new JLabel("Best Algorithm: ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));

        add(inputPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);
        add(resultLabel, BorderLayout.SOUTH);

        runButton.addActionListener(e -> runAlgorithms());
    }

    private JPanel createTablePanel(String title, JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JTable createTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Page Requests");
        return new JTable(model);
    }

    private void runAlgorithms() {
        String[] pages = inputField.getText().trim().split(" ");
        int slots = Integer.parseInt(slotField.getText().trim());

        int fifoFaults = runFIFO(pages, slots, fifoTable);
        int lruFaults = runLRU(pages, slots, lruTable);
        int lfuFaults = runLFU(pages, slots, lfuTable);
        int mfuFaults = runMFU(pages, slots, mfuTable);

        int minFaults = Math.min(Math.min(fifoFaults, lruFaults), Math.min(lfuFaults, mfuFaults));
        String bestAlgo = "";
        if (fifoFaults == minFaults) bestAlgo += "FIFO ";
        if (lruFaults == minFaults) bestAlgo += "LRU ";
        if (lfuFaults == minFaults) bestAlgo += "LFU ";
        if (mfuFaults == minFaults) bestAlgo += "MFU ";

        resultLabel.setText("FIFO: " + fifoFaults + " faults | LRU: " + lruFaults + " faults | LFU: " + lfuFaults + " faults | MFU: " + mfuFaults + " faults | Best: " + bestAlgo.trim());
    }

    private int runFIFO(String[] pages, int slots, JTable table) {
        Queue<String> memory = new LinkedList<>();
        Set<String> set = new HashSet<>();
        int pageFaults = 0;
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (String page : pages) {
            if (!set.contains(page)) {
                if (memory.size() == slots) {
                    set.remove(memory.poll());
                }
                memory.add(page);
                set.add(page);
                pageFaults++;
            }
            model.addRow(new Object[]{memory.toString()});
        }
        return pageFaults;
    }

    private int runLRU(String[] pages, int slots, JTable table) {
        List<String> memory = new LinkedList<>();
        int pageFaults = 0;
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (String page : pages) {
            if (!memory.contains(page)) {
                if (memory.size() == slots) {
                    memory.remove(0);
                }
                pageFaults++;
            } else {
                memory.remove(page);
            }
            memory.add(page);
            model.addRow(new Object[]{memory.toString()});
        }
        return pageFaults;
    }

    private int runLFU(String[] pages, int slots, JTable table) {
        Map<String, Integer> frequency = new HashMap<>();
        LinkedHashSet<String> memory = new LinkedHashSet<>();
        int pageFaults = 0;
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (String page : pages) {
            if (!memory.contains(page)) {
                if (memory.size() >= slots) {
                    String lfuPage = Collections.min(memory, Comparator.comparingInt(frequency::get));
                    memory.remove(lfuPage);
                    frequency.remove(lfuPage);
                }
                memory.add(page);
                pageFaults++;
            }
            frequency.put(page, frequency.getOrDefault(page, 0) + 1);
            model.addRow(new Object[]{memory.toString()});
        }
        return pageFaults;
    }

    private int runMFU(String[] pages, int slots, JTable table) {
        Map<String, Integer> frequency = new HashMap<>();
        LinkedHashSet<String> memory = new LinkedHashSet<>();
        int pageFaults = 0;
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (String page : pages) {
            if (!memory.contains(page)) {
                if (memory.size() >= slots) {
                    String mfuPage = Collections.max(memory, Comparator.comparingInt(frequency::get));
                    memory.remove(mfuPage);
                    frequency.remove(mfuPage);
                }
                memory.add(page);
                pageFaults++;
            }
            frequency.put(page, frequency.getOrDefault(page, 0) + 1);
            model.addRow(new Object[]{memory.toString()});
        }
        return pageFaults;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PageReplacementGUI().setVisible(true));
    }
}
