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
        System.out.println("Conta de Energia para o mês de " + mes);
        System.out.println("Consumo: " + consumo + " kWh");
        System.out.println("Custo: R$" + custo);
        return;
    }
}

class ContaAgua extends Conta {
    public ContaAgua(String mes, double consumo, double custo) {
        super(mes, consumo, custo);
    }

    public void exibirConta() {
        System.out.println("Conta de Água para o mês de " + mes);
        System.out.println("Consumo: " + consumo + " metros cúbicos");
        System.out.println("Custo: R$" + custo);
        return;
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

    public void carregarContas(String nomeArquivo) {
        try (BufferedReader leitor = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                String[] partes = linha.split(",");
                if (partes.length != 4) {
                    System.out.println("Formato de linha inválido: " + linha);
                    continue;
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
            System.out.println("Contas carregadas com sucesso.");
            calcularMediaConsumo();
        } catch (IOException | NumberFormatException e) {
            System.out.println("Erro ao carregar as contas: " + e.getMessage());
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

        lastConsumo = contas.get(contas.size() - 1).consumo;
        lastCusto = contas.get(contas.size() - 1).custo;
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
    
        // Primeiro Painel
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

        // Segundo Painel
        String[] columnNames2 = {"Custo Médio", "Consumo Médio", "Consumo Mínimo (KWh ou m3)", "Consumo Máximo (KWh ou m3)", "Custo Mínimo", "Custo Máximo"};
        DefaultTableModel tableModel2 = new DefaultTableModel(columnNames2, 0);
    
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
        
        JTextArea messageArea = new JTextArea(comparadorContas.getMessage());
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        
        // Painel para caixa de mensagem
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.add(scrollPane2);
        innerPanel.add(messageScrollPane);
        
        panel.add(innerPanel, BorderLayout.CENTER);
        
        // Cria um botão para voltar pro menu
        JButton backButton = new JButton("Voltar ao Menu");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableModel.setRowCount(0);
                cardLayout.show(mainPanel, "menu");
            }
        });
        panel.add(backButton, BorderLayout.PAGE_END);
        
        mainPanel.add(panel, tipoConta);
        cardLayout.show(mainPanel, tipoConta);
        
    }
}

public class PjBL {
    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.exibirJanela();
    }
}
