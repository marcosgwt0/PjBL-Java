import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;


abstract class Conta {
    protected String mes;
    protected double consumo;
    protected double custo;

    public Conta(String mes, double consumo, double custo) {
        this.mes = mes;
        this.consumo = consumo;
        this.custo = custo;
    }

    public abstract void exibirConta();
}

class ContaEnergia extends Conta {
    public ContaEnergia(String mes, double consumo, double custo) {
        super(mes, consumo, custo);
    }

    public void exibirConta() {
        System.out.println("Conta de Energia - " + mes);
        System.out.println("Consumo: " + consumo + " kWh");
        System.out.println("Custo: R$" + custo);
        System.out.println("----------------------------");
    }
}

class ContaAgua extends Conta {
    public ContaAgua(String mes, double consumo, double custo) {
        super(mes, consumo, custo);
    }

    public void exibirConta() {
        System.out.println("Conta de Água - " + mes);
        System.out.println("Consumo: " + consumo + " m³");
        System.out.println("Custo: R$" + custo);
        System.out.println("----------------------------");
    }
}


class ComparadorContas {
    private List<Conta> contas;
    private List<String[]> comparacoes;
    private double averageConsumo;
    private double lastConsumo;
    private double lastCusto;
    private StringBuilder message;

    public ComparadorContas() {
        contas = new ArrayList<>();
        comparacoes = new ArrayList<>();
        message = new StringBuilder();
    }

    class FormatoInvalidoException extends Exception {
        public FormatoInvalidoException(String mensagem) {
            super(mensagem);
        }
    }

    public void carregarContas(String nomeArquivo) {
        contas.clear();
        comparacoes.clear();
        try (BufferedReader leitor = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                String[] partes = linha.split(",");
                if (partes.length != 4) {
                    throw new FormatoInvalidoException("Formato de linha inválido: " + linha);
                }
                String tipoConta = partes[0];
                String mes = partes[1];
                double consumo = Double.parseDouble(partes[2]);
                double custo = Double.parseDouble(partes[3]);

                if (tipoConta.equals("Energia")) {
                    contas.add(new ContaEnergia(mes, consumo, custo));
                } else if (tipoConta.equals("Água")) {
                    contas.add(new ContaAgua(mes, consumo, custo));
                }
            }

            // Display the loaded data
            System.out.println("----------------------------");
            for (Conta conta : contas) {
                conta.exibirConta();
            }

            System.out.println("Contas carregadas com sucesso.");
            calcularMediaConsumo();
        } catch (IOException | NumberFormatException e) {
            System.out.println("Erro ao carregar as contas: " + e.getMessage());
        } catch (FormatoInvalidoException e) {
            System.out.println("Erro de formato: " + e.getMessage());
        }
    }

    public void compararContas() {
        if (contas.size() < 2) {
            System.out.println("Número insuficiente de contas para comparar.");
            return;
        }

        for (int i = 1; i < contas.size(); i++) {
            Conta contaAtual = contas.get(i);
            Conta contaAnterior = contas.get(i - 1);

            String[] resultado = new String[6];
            resultado[0] = contaAnterior.mes;
            resultado[1] = contaAtual.mes;
            resultado[2] = Double.toString(contaAnterior.consumo);
            resultado[3] = Double.toString(contaAtual.consumo);
            resultado[4] = Double.toString(contaAnterior.custo);
            resultado[5] = Double.toString(contaAtual.custo);

            comparacoes.add(resultado);
        }

        // Compare the last account with the previous one
        Conta lastConta = contas.get(contas.size() - 1);
        Conta previousConta = contas.get(contas.size() - 2);

        String[] resultado = new String[6];
        resultado[0] = previousConta.mes;
        resultado[1] = lastConta.mes;
        resultado[2] = Double.toString(previousConta.consumo);
        resultado[3] = Double.toString(lastConta.consumo);
        resultado[4] = Double.toString(previousConta.custo);
        resultado[5] = Double.toString(lastConta.custo);

        comparacoes.add(resultado);

        lastConsumo = lastConta.consumo;
        lastCusto = lastConta.custo;
    }

    private void calcularMediaConsumo() {
        double totalConsumo = 0;
        for (Conta conta : contas) {
            totalConsumo += conta.consumo;
        }
        averageConsumo = totalConsumo / contas.size();
    }

    public double getAverageConsumo() {
        return averageConsumo;
    }

    public List<String[]> getComparacoes() {
        return comparacoes;
    }

    public boolean precisaEconomizar() {
        return lastConsumo > averageConsumo;
    }

    public boolean estaEconomizando() {
        return lastConsumo < averageConsumo;
    }

    public String getMessage() {
        message.setLength(0);

        if (precisaEconomizar()) {
            message.append("Você precisa economizar mais! \n");
        } else if (estaEconomizando()) {
            message.append("Você está economizando! \n");
        } else {
            message.append("Seu consumo está na média. \n");
        }

        if (lastConsumo > contas.get(contas.size() - 2).consumo) {
            message.append("Você consumiu mais no último mês. \n");
        } else {
            message.append("Você consumiu menos no último mês. \n");
        }

        if (lastCusto > contas.get(contas.size() - 2).custo) {
            message.append("Você gastou mais no último mês. \n");
        } else {
            message.append("Você gastou menos no último mês. \n");
        }

        return message.toString();
    }
}

    class GUI {
    private ComparadorContas comparadorContas;
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private DefaultTableModel tableModel;
    private DefaultTableModel tableModel2;
    private JFileChooser fileChooser;

    public GUI() {
        comparadorContas = new ComparadorContas();
        fileChooser = new JFileChooser();
    }

    public void exibirJanela() {
        frame = new JFrame("Comparador de Contas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);

        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        JPanel menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, "menu");

        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JLabel label = new JLabel("Selecione o tipo de conta:");
        panel.add(label);

        JButton buttonLuz = new JButton("Luz");
        buttonLuz.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                carregarContas("Luz");
            }
        });
        panel.add(buttonLuz);

        JButton buttonAgua = new JButton("Água");
        buttonAgua.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                carregarContas("Água");
            }
        });
        panel.add(buttonAgua);

        return panel;
    }

    private void carregarContas(String tipoConta) {
        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String nomeArquivo = fileChooser.getSelectedFile().getPath();
            comparadorContas.carregarContas(nomeArquivo);
            comparadorContas.compararContas();
            exibirResultado(tipoConta);
        }
    }

    private void exibirResultado(String tipoConta) {
        JPanel panel = new JPanel(new BorderLayout());

        // First Table
        String[] columnNames = {"Mês Anterior", "Mês Atual", "Consumo Anterior (KWh ou m3)", "Consumo Atual (KWh ou m3)", "Custo Anterior", "Custo Atual"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tableModel.setRowCount(0);
        List<String[]> comparacoes = comparadorContas.getComparacoes();
        for (String[] comparacao : comparacoes) {
            tableModel.addRow(comparacao);
        }
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.PAGE_START);

        // Second Table
        String[] columnNames2 = {"Custo Médio", "Consumo Médio", "Consumo Mínimo (KWh ou m3)", "Consumo Máximo (KWh ou m3)", "Custo Mínimo", "Custo Máximo"};
        tableModel2 = new DefaultTableModel(columnNames2, 0);
        String[] row = new String[6];

        double averageCost = comparadorContas.getComparacoes().stream()
                .mapToDouble(comparacao -> Double.parseDouble(comparacao[5]))
                .average()
                .orElse(0.0);
        row[0] = String.format("%.2f", averageCost);

        double averagePreviousConsumption = comparadorContas.getComparacoes().stream()
                .mapToDouble(comparacao -> Double.parseDouble(comparacao[3]))
                .average()
                .orElse(0.0);
        row[1] = String.format("%.2f", averagePreviousConsumption);

        double minCurrentConsumption = comparadorContas.getComparacoes().stream()
                .mapToDouble(comparacao -> Double.parseDouble(comparacao[2]))
                .min()
                .orElse(0.0);
        row[2] = String.format("%.2f", minCurrentConsumption);

        double maxCurrentConsumption = comparadorContas.getComparacoes().stream()
                .mapToDouble(comparacao -> Double.parseDouble(comparacao[2]))
                .max()
                .orElse(0.0);
        row[3] = String.format("%.2f", maxCurrentConsumption);

        double minVariationConsumption = comparadorContas.getComparacoes().stream()
                .mapToDouble(comparacao -> Double.parseDouble(comparacao[4]))
                .min()
                .orElse(0.0);
        row[4] = String.format("%.2f", minVariationConsumption);

        double maxVariationConsumption = comparadorContas.getComparacoes().stream()
                .mapToDouble(comparacao -> Double.parseDouble(comparacao[4]))
                .max()
                .orElse(0.0);
        row[5] = String.format("%.2f", maxVariationConsumption);

        tableModel2.addRow(row);

        JTable table2 = new JTable(tableModel2);
        JScrollPane scrollPane2 = new JScrollPane(table2);

        // Message Box
        JTextArea messageArea = new JTextArea(comparadorContas.getMessage());
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        panel.add(messageScrollPane, BorderLayout.CENTER);

        // Create a panel for the tables and message box
        JPanel tablesPanel = new JPanel(new GridLayout(3, 1));
        tablesPanel.add(scrollPane);
        tablesPanel.add(scrollPane2);
        tablesPanel.add(messageScrollPane);
        panel.add(tablesPanel, BorderLayout.CENTER);

        // Create a panel for the buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Salvar");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveTableData();
            }
        });
        buttonsPanel.add(saveButton);

        JButton backButton = new JButton("Voltar ao Menu");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableModel.setRowCount(0);
                tableModel2.setRowCount(0); // Clear the second table as well
                cardLayout.show(mainPanel, "menu");
            }
        });
        buttonsPanel.add(backButton);


        panel.add(buttonsPanel, BorderLayout.PAGE_END);

        mainPanel.add(panel, tipoConta);
        cardLayout.show(mainPanel, tipoConta);

        
    }

    private void saveTableData() {
        int returnValue = fileChooser.showSaveDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String nomeArquivo = fileChooser.getSelectedFile().getPath();
            try (PrintWriter writer = new PrintWriter(nomeArquivo)) {
                writer.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                writer.println("Mês Anterior     Mês Atual        Consumo Anterior Consumo Atual   Custo Anterior   Custo Atual");
                writer.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        String value = tableModel.getValueAt(i, j).toString();
                        writer.printf("%-17s", value);
                    }
                    writer.println();
                }

                writer.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                writer.println("custo médio      consumo médio    consumo mínimo   consumo máximo  custo mínimo     custo máximo");
                writer.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                for (int i = 0; i < tableModel2.getRowCount(); i++) {
                    for (int j = 0; j < tableModel2.getColumnCount(); j++) {
                        String value = tableModel2.getValueAt(i, j).toString();
                        writer.printf("%-17s", value);
                    }
                    writer.println();
                }

                System.out.println("Data saved successfully to the file: " + nomeArquivo);
            } catch (IOException ex) {
                System.out.println("Error saving table data: " + ex.getMessage());
            }
        }
    }
}

public class PjBL {
    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.exibirJanela();
    }
}
