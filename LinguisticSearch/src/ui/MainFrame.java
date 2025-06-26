package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    // 组件声明
    private JTextField urlField;
    private JTextField searchField;
    private JButton browseButton;
    private JButton searchButton;
    private JCheckBox wholeWordCheck;
    private JCheckBox caseSensitiveCheck;
    private JCheckBox regexCheck;
    private JTextArea[] resultAreas;

    public MainFrame() {

        initFrame();
        initComponents();
        layoutComponents();
    }

    private void initFrame() {
        setTitle("Linguistic Text Search Analysis");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(LayoutConstants.FRAME_SIZE);
        setMinimumSize(new Dimension(1000, 700));
        getContentPane().setBackground(Colors.LIGHT_BG);
    }

    private void initComponents() {
        // 顶部标题栏
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBackground(Colors.PRIMARY);
        topPanel.setPreferredSize(new Dimension(
                LayoutConstants.FRAME_SIZE.width,
                LayoutConstants.TOP_BAR_HEIGHT
        ));

        JLabel titleLabel = new JLabel("Linguistic Text Search Analysis ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, LayoutConstants.FONT_H2));
        titleLabel.setForeground(Colors.LIGHT_TEXT);
        topPanel.add(titleLabel);

        // 左侧面板组件
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Colors.LIGHT_BG);
        leftPanel.setPreferredSize(new Dimension(
                LayoutConstants.SIDEBAR_WIDTH,
                LayoutConstants.FRAME_SIZE.height - LayoutConstants.TOP_BAR_HEIGHT
        ));
        leftPanel.setBorder(new EmptyBorder(
                LayoutConstants.SPACE_XL,
                LayoutConstants.SPACE_LG,
                LayoutConstants.SPACE_XL,
                LayoutConstants.SPACE_LG
        ));

        // 过滤器标签
        JLabel filterLabel = new JLabel("Filter Options");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, LayoutConstants.FONT_H4));
        filterLabel.setForeground(Colors.DARK_TEXT);

        // 复选框
        wholeWordCheck = createCheckBox("Whole word matching");
        caseSensitiveCheck = createCheckBox("Case sensitive");
        regexCheck = createCheckBox("Use regular expression");

        // URL输入区域
        JLabel urlLabel = new JLabel("Search URL:");
        urlLabel.setFont(new Font("Segoe UI", Font.PLAIN, LayoutConstants.FONT_BASE));

        urlField = new JTextField();
        urlField.setPreferredSize(new Dimension(
                LayoutConstants.SIDEBAR_WIDTH - LayoutConstants.SPACE_LG * 2,
                LayoutConstants.INPUT_HEIGHT
        ));
        urlField.setMinimumSize(urlField.getPreferredSize());
        urlField.setMaximumSize(urlField.getPreferredSize());

        browseButton = createButton("Browse...", Colors.SECONDARY);

        // 搜索输入区域
        JLabel searchLabel = new JLabel("Search Text:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, LayoutConstants.FONT_BASE));

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(
                LayoutConstants.SIDEBAR_WIDTH - LayoutConstants.SPACE_LG * 2,
                LayoutConstants.INPUT_HEIGHT
        ));
        searchField.setMinimumSize(searchField.getPreferredSize());
        searchField.setMaximumSize(searchField.getPreferredSize());

        searchButton = createButton("Search", Colors.ACCENT);

        // 右侧结果面板
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(new EmptyBorder(
                LayoutConstants.SPACE_XL,
                LayoutConstants.SPACE_LG,
                LayoutConstants.SPACE_XL,
                LayoutConstants.SPACE_LG
        ));

        JLabel resultsLabel = new JLabel("Search Results");
        resultsLabel.setFont(new Font("Segoe UI", Font.BOLD, LayoutConstants.FONT_H3));
        resultsLabel.setForeground(Colors.DARK_TEXT);
        resultsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 初始化结果区域
        resultAreas = new JTextArea[3];
        for (int i = 0; i < resultAreas.length; i++) {
            resultAreas[i] = createResultArea();
        }

        // 组装左侧面板
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 关键设置
        leftPanel.add(filterLabel);
        leftPanel.add(Box.createVerticalStrut(LayoutConstants.SPACE_MD));
        leftPanel.add(wholeWordCheck);
        leftPanel.add(caseSensitiveCheck);
        leftPanel.add(regexCheck);
        leftPanel.add(Box.createVerticalStrut(LayoutConstants.SPACE_XL));
        leftPanel.add(urlLabel);
        leftPanel.add(Box.createVerticalStrut(LayoutConstants.SPACE_SM));
        leftPanel.add(urlField);
        leftPanel.add(Box.createVerticalStrut(LayoutConstants.SPACE_SM));
        leftPanel.add(browseButton);
        leftPanel.add(Box.createVerticalStrut(LayoutConstants.SPACE_XL));
        leftPanel.add(searchLabel);
        leftPanel.add(Box.createVerticalStrut(LayoutConstants.SPACE_SM));
        leftPanel.add(searchField);
        leftPanel.add(Box.createVerticalStrut(LayoutConstants.SPACE_MD));
        leftPanel.add(searchButton);
        // 为所有子组件添加左对齐约束
        filterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        wholeWordCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        caseSensitiveCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        regexCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        urlLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        urlField.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 组装右侧面板
        rightPanel.add(resultsLabel);
        rightPanel.add(Box.createVerticalStrut(LayoutConstants.SPACE_LG));
        for (JTextArea area : resultAreas) {
            rightPanel.add(createResultCard(area));
            rightPanel.add(Box.createVerticalStrut(LayoutConstants.SPACE_MD));
        }

        // 主布局
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(leftPanel, BorderLayout.WEST);
        contentPanel.add(rightPanel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void layoutComponents() {
        // 已经在上面的initComponents中完成布局
    }

    // ======================== 辅助方法 ========================
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, LayoutConstants.FONT_BASE));
        button.setBackground(bgColor);
        button.setForeground(Colors.LIGHT_TEXT);
        button.setPreferredSize(LayoutConstants.BUTTON_SIZE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(
                        LayoutConstants.SPACE_SM,
                        LayoutConstants.SPACE_MD,
                        LayoutConstants.SPACE_SM,
                        LayoutConstants.SPACE_MD
                )
        ));
        button.setFocusPainted(false);
        return button;
    }

    private JCheckBox createCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFont(new Font("Segoe UI", Font.PLAIN, LayoutConstants.FONT_BASE));
        checkBox.setBackground(Colors.LIGHT_BG);
        checkBox.setForeground(Colors.DARK_TEXT);
        checkBox.setBorder(BorderFactory.createEmptyBorder(
                LayoutConstants.SPACE_XS,
                0,
                LayoutConstants.SPACE_XS,
                0
        ));
        return checkBox;
    }

    private JTextArea createResultArea() {
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, LayoutConstants.FONT_BASE));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setBackground(Color.WHITE);
        return textArea;
    }

    private JPanel createResultCard(JTextArea textArea) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Colors.RESULT_BOX_BG);
        card.setMaximumSize(LayoutConstants.CARD_SIZE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xeeeeee), 1),
                BorderFactory.createEmptyBorder(
                        LayoutConstants.SPACE_MD,
                        LayoutConstants.SPACE_MD,
                        LayoutConstants.SPACE_MD,
                        LayoutConstants.SPACE_MD
                )
        ));

        // 添加阴影效果
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(
                        LayoutConstants.SHADOW_OFFSET,
                        LayoutConstants.SHADOW_OFFSET,
                        LayoutConstants.SHADOW_OFFSET * 2,
                        LayoutConstants.SHADOW_OFFSET * 2
                ),
                card.getBorder()
        ));

        // 文本区域
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);
        card.add(scrollPane, BorderLayout.CENTER);

        // 标签区域
        JPanel tagPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, LayoutConstants.SPACE_SM, LayoutConstants.SPACE_XS));
        tagPanel.setBackground(Color.WHITE);

        String[] tags = {"POS tag", "Lemma", "Syntax", "Semantic"};
        for (String tag : tags) {
            JLabel tagLabel = new JLabel(tag);
            tagLabel.setFont(new Font("Segoe UI", Font.PLAIN, LayoutConstants.FONT_SMALL));
            tagLabel.setOpaque(true);
            tagLabel.setBackground(Colors.TAG_BG);
            tagLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xb2ebf2), 1),
                    BorderFactory.createEmptyBorder(
                            LayoutConstants.SPACE_XS,
                            LayoutConstants.SPACE_SM,
                            LayoutConstants.SPACE_XS,
                            LayoutConstants.SPACE_SM
                    )
            ));
            tagPanel.add(tagLabel);
        }

        card.add(tagPanel, BorderLayout.SOUTH);
        return card;
    }

    // ======================== 公共访问方法 ========================
    public JButton getBrowseButton() {
        return browseButton;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JTextField getUrlField() {
        return urlField;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JCheckBox getWholeWordCheck() {
        return wholeWordCheck;
    }

    public JCheckBox getCaseSensitiveCheck() {
        return caseSensitiveCheck;
    }

    public JCheckBox getRegexCheck() {
        return regexCheck;
    }

    public JTextArea[] getResultAreas() {
        return resultAreas;
    }
}