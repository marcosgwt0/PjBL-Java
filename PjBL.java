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


// Classe abstrata Conta que serve como base para as classes ContaEnergia e ContaAgua
abstract class Conta {
    protected String mes; // Mês da conta
    protected double consumo; // Consumo em unidades (kWh para ContaEnergia e m³ para ContaAgua)
    protected double custo; // Custo da conta

    // Construtor da classe Conta
    public Conta(String mes, double consumo, double custo) {
        this.mes = mes;
        this.consumo = consumo;
        this.custo = custo;
    }

    // Método abstrato a ser implementado nas subclasses
    public abstract void exibirConta();
}

// Subclasse de Conta que representa uma conta de energia
class ContaEnergia extends Conta {
    // Construtor da classe ContaEnergia que chama o construtor da classe Conta
    public ContaEnergia(String mes, double consumo, double custo) {
        super(mes, consumo, custo);
    }
    
    // Implementação do método exibirConta da classe ContaEnergia
    @Override
    public void exibirConta() {
        System.out.println("Conta de Energia - " + mes);
        System.out.println("Consumo: " + consumo + " kWh");
        System.out.println("Custo: R$" + custo);
        System.out.println("----------------------------");
    }
}

// Subclasse de Conta que representa uma conta de água
class ContaAgua extends Conta {
    // Construtor da classe ContaAgua que chama o construtor da classe Conta
    public ContaAgua(String mes, double consumo, double custo) {
        super(mes, consumo, custo);
    }

    // Implementação do método exibirConta da classe ContaAgua
    @Override
    public void exibirConta() {
        System.out.println("Conta de Água - " + mes);
        System.out.println("Consumo: " + consumo + " m³");
        System.out.println("Custo: R$" + custo);
        System.out.println("----------------------------");
    }
}


class ComparadorContas {
    private List<Conta> contas; // Lista de contas carregadas
    private List<String[]> comparacoes; // Lista de comparações entre contas
    private double averageConsumo; // Consumo médio
    private double lastConsumo; // Último consumo registrado
    private double lastCusto; // Último custo registrado
    private StringBuilder message; // Mensagem a ser exibida

    // Construtor da classe ComparadorContas
    public ComparadorContas() {
        contas = new ArrayList<>();
        comparacoes = new ArrayList<>();
        message = new StringBuilder();
    }

    // Classe interna que representa uma exceção personalizada para formato inválido
    class FormatoInvalidoException extends Exception {
        public FormatoInvalidoException(String mensagem) {
            super(mensagem);
        }
    }

    // Método para carregar as contas a partir de um arquivo
    public void carregarContas(String nomeArquivo) {
        contas.clear(); // Limpa a lista de contas existente
        comparacoes.clear(); // Limpa a lista de comparações existente
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
            // Exibe as contas carregadas
            System.out.println("----------------------------");
            for (Conta conta : contas) {
                conta.exibirConta();
            }
            System.out.println("Contas carregadas com sucesso.");
            calcularMediaConsumo(); // Calcula o consumo médio das contas carregadas
        } catch (IOException | NumberFormatException e) {
            System.out.println("Erro ao carregar as contas: " + e.getMessage());
        } catch (FormatoInvalidoException e) {
            System.out.println("Erro de formato: " + e.getMessage());
        }
    }

    // Método para comparar as contas
    public void compararContas() {
        if (contas.size() < 2) {
            System.out.println("Número insuficiente de contas para comparar.");
            return;
        }

    for (int i = 1; i < contas.size() - 1; i++) {
        Conta contaAtual = contas.get(i);
        Conta contaAnterior = contas.get(i - 1);

        String[] resultado = new String[6]; // Cria um novo array de strings para armazenar os resultados
        resultado[0] = contaAnterior.mes;
        resultado[1] = contaAtual.mes;
        resultado[2] = Double.toString(contaAnterior.consumo);
        resultado[3] = Double.toString(contaAtual.consumo);
        resultado[4] = Double.toString(contaAnterior.custo);
        resultado[5] = Double.toString(contaAtual.custo);

        comparacoes.add(resultado); // Adiciona o array resultado à lista de comparações
    }


        // Comparar a última conta com a anterior
        Conta lastConta = contas.get(contas.size() - 1);
        Conta previousConta = contas.get(contas.size() - 2);

        String[] resultado = new String[6];
        resultado[0] = previousConta.mes;
        resultado[1] = lastConta.mes;
        resultado[2] = Double.toString(previousConta.consumo);
        resultado[3] = Double.toString(lastConta.consumo);
        resultado[4] = Double.toString(previousConta.custo);
        resultado[5] = Double.toString(lastConta.custo);

        comparacoes.add(resultado); // Adiciona o array resultado à lista de comparações

        lastConsumo = lastConta.consumo; // Atribui o valor do consumo da última conta à variável lastConsumo
        lastCusto = lastConta.custo; // Atribui o valor do custo da última conta à variável lastCusto

    }

    // Método para calcular o consumo médio
    private void calcularMediaConsumo() {
        double totalConsumo = 0;
        for (Conta conta : contas) {
            totalConsumo += conta.consumo;
        }
        averageConsumo = totalConsumo / contas.size();
    }

    // obter o consumo médio
    public double getAverageConsumo() {
        return averageConsumo;
    }

    // obter as comparações entre contas
    public List<String[]> getComparacoes() {
        return comparacoes;
    }

    // Verifica se é necessário economizar com base no último consumo registrado
    public boolean precisaEconomizar() {
        return lastConsumo > averageConsumo;
    }

    // Verifica se está economizando com base no último consumo registrado
    public boolean estaEconomizando() {
        return lastConsumo < averageConsumo;
    }

    // obter a mensagem com o resumo das informações
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

        JPanel menuPanel = createMenuPanel(); // Criação do painel do menu
        mainPanel.add(menuPanel, "menu"); // Adição do painel do menu ao painel principal com o nome "menu"

        frame.getContentPane().add(mainPanel); // Adição do painel principal ao content pane do frame
        frame.setVisible(true); // Torna o frame visível
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(); // Criação do painel para o menu
        panel.setLayout(new FlowLayout()); // Definição do layout de fluxo para o painel

        JLabel label = new JLabel("Selecione o tipo de conta:"); // Criação do rótulo para selecionar o tipo de conta
        panel.add(label);

        JButton buttonLuz = new JButton("Luz");
        buttonLuz.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                carregarContas("Luz"); // Chama o método carregarContas com o argumento "luz"
            }
        });
        panel.add(buttonLuz);

        JButton buttonAgua = new JButton("Água");
        buttonAgua.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                carregarContas("Água"); // Chama o método carregarContas com o argumento "Água"
            }
        });
        panel.add(buttonAgua);

        return panel;
    }

    private void carregarContas(String tipoConta) {
        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String nomeArquivo = fileChooser.getSelectedFile().getPath();
            comparadorContas.carregarContas(nomeArquivo); // Carrega as contas do arquivo usando o ComparadorContas
            comparadorContas.compararContas(); // Realiza a comparação das contas usando o ComparadorContas
            exibirResultado(tipoConta);
        }
    }


        private void exibirResultado(String tipoConta) {
        // Cria um painel para exibir os resultados
        JPanel panel = new JPanel(new BorderLayout());

        // Primeira tabela
        String[] columnNames = {"Mês Anterior", "Mês Atual", "Consumo Anterior (KWh ou m3)", "Consumo Atual (KWh ou m3)", "Custo Anterior", "Custo Atual"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tableModel.setRowCount(0);
        List<String[]> comparacoes = comparadorContas.getComparacoes();

        // Preenche a tabela com os dados das comparações
        for (String[] comparacao : comparacoes) {
            tableModel.addRow(comparacao);
        }

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Adiciona a tabela ao painel na posição superior
        panel.add(scrollPane, BorderLayout.PAGE_START);

        // Segunda tabela
        String[] columnNames2 = {"Custo Médio", "Consumo Médio", "Consumo Mínimo (KWh ou m3)", "Consumo Máximo (KWh ou m3)", "Custo Mínimo", "Custo Máximo"};
        tableModel2 = new DefaultTableModel(columnNames2, 0);
        String[] row = new String[6];

        // Cálculo do custo médio
        double averageCost = comparadorContas.getComparacoes().stream()
                .mapToDouble(comparacao -> Double.parseDouble(comparacao[5]))
                .average()
                .orElse(0.0);
        row[0] = String.format("%.2f", averageCost);

        // Cálculo do consumo médio anterior
        double averagePreviousConsumption = comparadorContas.getComparacoes().stream()
                .mapToDouble(comparacao -> Double.parseDouble(comparacao[3]))
                .average()
                .orElse(0.0);
        row[1] = String.format("%.2f", averagePreviousConsumption);

        // Cálculo do consumo mínimo atual
        double minCurrentConsumption = comparadorContas.getComparacoes().stream()
                .mapToDouble(comparacao -> Double.parseDouble(comparacao[2]))
                .min()
                .orElse(0.0);
        row[2] = String.format("%.2f", minCurrentConsumption);

        // Cálculo do consumo máximo atual
        double maxCurrentConsumption = comparadorContas.getComparacoes().stream()
                .mapToDouble(comparacao -> Double.parseDouble(comparacao[2]))
                .max()
                .orElse(0.0);
        row[3] = String.format("%.2f", maxCurrentConsumption);

        // Cálculo do custo mínimo de variação
        double minVariationConsumption = comparadorContas.getComparacoes().stream()
                .mapToDouble(comparacao -> Double.parseDouble(comparacao[4]))
                .min()
                .orElse(0.0);
        row[4] = String.format("%.2f", minVariationConsumption);

        // Cálculo do custo máximo de variação
        double maxVariationConsumption = comparadorContas.getComparacoes().stream()
                .mapToDouble(comparacao -> Double.parseDouble(comparacao[4]))
                .max()
                .orElse(0.0);
        row[5] = String.format("%.2f", maxVariationConsumption);

        // Adiciona a linha com os cálculos à tabela
        tableModel2.addRow(row);

        JTable table2 = new JTable(tableModel2);
        JScrollPane scrollPane2 = new JScrollPane(table2);

        // Área de mensagem
        JTextArea messageArea = new JTextArea(comparadorContas.getMessage());
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);

        // Adiciona as tabelas e a área de mensagem ao painel no centro
        JPanel tablesPanel = new JPanel(new GridLayout(3, 1));
        tablesPanel.add(scrollPane);
        tablesPanel.add(scrollPane2);
        tablesPanel.add(messageScrollPane);
        panel.add(tablesPanel, BorderLayout.CENTER);

        // Cria um painel para os botões
        JPanel buttonsPanel = new JPanel(new FlowLayout());

        // Botão "Salvar"
        JButton saveButton = new JButton("Salvar");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveTableData();
            }
        });
        buttonsPanel.add(saveButton);

        // Botão "Voltar ao Menu"
        JButton backButton = new JButton("Voltar ao Menu");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableModel.setRowCount(0);
                tableModel2.setRowCount(0);
                cardLayout.show(mainPanel, "menu");
            }
        });
        buttonsPanel.add(backButton);

        // Adiciona o painel de botões ao painel na posição inferior
        panel.add(buttonsPanel, BorderLayout.PAGE_END);

        // Adiciona o painel com os resultados ao painel principal
        mainPanel.add(panel, tipoConta);
        cardLayout.show(mainPanel, tipoConta);
    }

    private void saveTableData() {
        // Exibe a janela de salvar arquivo
        int returnValue = fileChooser.showSaveDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String nomeArquivo = fileChooser.getSelectedFile().getPath();
            try (PrintWriter writer = new PrintWriter(nomeArquivo)) {
                // Escreve a primeira linha com os títulos das colunas da tabela 1
                writer.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                writer.println("Mês Anterior     Mês Atual        Consumo Anterior Consumo Atual   Custo Anterior   Custo Atual");
                writer.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

                // Escreve os dados da tabela 1
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        String value = tableModel.getValueAt(i, j).toString();
                        writer.printf("%-17s", value); // Formata o valor com espaçamento fixo
                    }
                    writer.println(); // Pula para a próxima linha
                }

                // Escreve a linha de separação entre as tabelas
                writer.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

                // Escreve a primeira linha com os títulos das colunas da tabela 2
                writer.println("custo médio      consumo médio    consumo mínimo   consumo máximo  custo mínimo     custo máximo");
                writer.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

                // Escreve os dados da tabela 2
                for (int i = 0; i < tableModel2.getRowCount(); i++) {
                    for (int j = 0; j < tableModel2.getColumnCount(); j++) {
                        String value = tableModel2.getValueAt(i, j).toString();
                        writer.printf("%-17s", value); // Formata o valor com espaçamento fixo
                    }
                    writer.println(); // Pula para a próxima linha
                }

                System.out.println("Dados salvos com sucesso no arquivo: " + nomeArquivo);
            } catch (IOException ex) {
                System.out.println("Erro ao salvar os dados da tabela: " + ex.getMessage());
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
