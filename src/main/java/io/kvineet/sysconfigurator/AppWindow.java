package io.kvineet.sysconfigurator;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.inject.Inject;

import io.kvineet.sysconfigurator.models.Columns;
import io.kvineet.sysconfigurator.models.DbConfig;
import io.kvineet.sysconfigurator.models.EncryptorTableModel;
import io.kvineet.sysconfigurator.services.BasicService;
import io.kvineet.sysconfigurator.utils.AppWindowUtil;
import io.kvineet.sysconfigurator.utils.JsonUtils;

public class AppWindow {

	@Inject
	private ConnectionPool connectionPool;
	
	@Inject
	private BasicService basicService;
	
	private JFrame frame;
	private JTextField aesKey;
	private JTextField dbUrl;
	private JTextField dbUserName;
	private JTextField dbPassword;
	private EncryptorTableModel tableModel;
	JTable table;
	private JTextField tableName;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppWindow window = new AppWindow();
					window.setFrameVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AppWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		splitPane.setLeftComponent(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {4, 4, 4, 4, 4, 4, 4, 4, 4, 4};
		gbl_panel.rowHeights = new int[]{4, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 4};
		gbl_panel.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
		gbl_panel.rowWeights = new double[]{1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel aesLabel = new JLabel("aesKey");
		GridBagConstraints gbc_aesLabel = new GridBagConstraints();
		gbc_aesLabel.gridwidth = 3;
		gbc_aesLabel.insets = new Insets(0, 0, 5, 5);
		gbc_aesLabel.gridx = 0;
		gbc_aesLabel.gridy = 1;
		panel.add(aesLabel, gbc_aesLabel);
		
		aesKey = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.gridwidth = 5;
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 4;
		gbc_textField.gridy = 1;
		panel.add(aesKey, gbc_textField);
		aesKey.setColumns(10);
		
		JLabel lblJson = new JLabel("Json");
		GridBagConstraints gbc_lblJson = new GridBagConstraints();
		gbc_lblJson.insets = new Insets(0, 0, 5, 5);
		gbc_lblJson.gridx = 9;
		gbc_lblJson.gridy = 1;
		panel.add(lblJson, gbc_lblJson);
		
		
		JTextArea json = new JTextArea();
		json.setLineWrap(true);
		json.getDocument().addDocumentListener(new DocumentListener() {
	
			@Override
			public void removeUpdate(DocumentEvent e) {
				try {
					String val = e.getDocument().getText(0, e.getDocument().getLength());
					parseData(val);
				} catch (BadLocationException e1) {
					JOptionPane.showMessageDialog(null, "error: " + e1.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					String val = e.getDocument().getText(0, e.getDocument().getLength());
					parseData(val);
				} catch (BadLocationException e1) {
					JOptionPane.showMessageDialog(null, "error: " + e1.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
				}
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
			}
			
			private void parseData(String val) {
				Map<String, String> data;
				try {
					data = JsonUtils.fromJson(val, new TypeReference<Map<String, String>>() {
					});
					aesKey.setText(data.get("aesKey"));
					dbUrl.setText(data.get("dbUrl"));
					dbUserName.setText(data.get("dbUserName"));
					dbPassword.setText(data.get("dbPassword"));
					tableName.setText(data.get("tableName"));
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "error: " + e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		GridBagConstraints gbc_json = new GridBagConstraints();
		gbc_json.fill = GridBagConstraints.VERTICAL;
		gbc_json.gridwidth = 2;
		gbc_json.gridheight = 6;
		gbc_json.insets = new Insets(0, 0, 5, 0);
		gbc_json.gridx = 9;
		gbc_json.gridy = 2;
		panel.add(json, gbc_json);
		
		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.gridwidth = 10;
		gbc_separator.insets = new Insets(0, 0, 5, 5);
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 2;
		panel.add(separator, gbc_separator);
		
		JLabel lblDburl = new JLabel("dbURL");
		GridBagConstraints gbc_lblDburl = new GridBagConstraints();
		gbc_lblDburl.gridwidth = 3;
		gbc_lblDburl.insets = new Insets(0, 0, 5, 5);
		gbc_lblDburl.gridx = 0;
		gbc_lblDburl.gridy = 3;
		panel.add(lblDburl, gbc_lblDburl);
		
		dbUrl = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.gridwidth = 5;
		gbc_textField_1.insets = new Insets(0, 0, 5, 5);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 4;
		gbc_textField_1.gridy = 3;
		panel.add(dbUrl, gbc_textField_1);
		dbUrl.setColumns(10);
		
		JLabel lblDatabaseUsername = new JLabel("dbUserName");
		GridBagConstraints gbc_lblDatabaseUsername = new GridBagConstraints();
		gbc_lblDatabaseUsername.gridwidth = 3;
		gbc_lblDatabaseUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblDatabaseUsername.gridx = 0;
		gbc_lblDatabaseUsername.gridy = 4;
		panel.add(lblDatabaseUsername, gbc_lblDatabaseUsername);
		
		dbUserName = new JTextField();
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.gridwidth = 5;
		gbc_textField_2.insets = new Insets(0, 0, 5, 5);
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.gridx = 4;
		gbc_textField_2.gridy = 4;
		panel.add(dbUserName, gbc_textField_2);
		dbUserName.setColumns(10);
		
		JLabel lblDbpassword = new JLabel("dbPassword");
		GridBagConstraints gbc_lblDbpassword = new GridBagConstraints();
		gbc_lblDbpassword.gridwidth = 3;
		gbc_lblDbpassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblDbpassword.gridx = 0;
		gbc_lblDbpassword.gridy = 5;
		panel.add(lblDbpassword, gbc_lblDbpassword);
		
		dbPassword = new JPasswordField();
		dbPassword.setColumns(10);
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.gridwidth = 5;
		gbc_passwordField.insets = new Insets(0, 0, 5, 5);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 4;
		gbc_passwordField.gridy = 5;
		panel.add(dbPassword, gbc_passwordField);
		
		JLabel lblTableName = new JLabel("tableName");
		GridBagConstraints gbc_lblTableName = new GridBagConstraints();
		gbc_lblTableName.gridwidth = 3;
		gbc_lblTableName.insets = new Insets(0, 0, 5, 5);
		gbc_lblTableName.gridx = 0;
		gbc_lblTableName.gridy = 6;
		panel.add(lblTableName, gbc_lblTableName);
		
		JToggleButton tglbtnConnect = new JToggleButton("Connect");
		tglbtnConnect.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(tglbtnConnect.isSelected()) {
					DbConfig dbConfig = constructDbConfig();
					try {
						connectionPool.initConnection(dbConfig);
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(null, "Failed to connect to server due to error: \n" + e.getMessage());
					}
					List<Columns> columns;
					try {
						columns = basicService.listAllColumns(tableName.getText());
						List<Map<String, String>> dataSet = basicService.retriveData(tableName.getText(), columns);
						tableModel.reloadData(columns, dataSet);
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(null, "error: " + e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
						return;
					}
					tglbtnConnect.setText("Disconnect");
				}else {
					List<Columns> cols = AppWindowUtil.constructCols(); 
					List<Map<String, String>> dataSet = AppWindowUtil.constructDataSet(); 
					tableModel.reloadData(cols, dataSet);
					try {
						connectionPool.closeDataSource();
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(null, "error: " + e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
						return;
					}
					tglbtnConnect.setText("Connect");
				}
			}
			private DbConfig constructDbConfig() {
				DbConfig dbConfig = new DbConfig();
				dbConfig.setPoolName("test");
				dbConfig.setJdbcUrl(dbUrl.getText());
				dbConfig.setDbUserName(dbUserName.getText());
				dbConfig.setDbPassword(dbPassword.getText());
				dbConfig.setMaximumPoolSize(3);
				dbConfig.setMinimumIdle(1);
				return dbConfig;
			}
		});
		
		
		tableName = new JTextField();
		GridBagConstraints gbc_tableName = new GridBagConstraints();
		gbc_tableName.gridwidth = 5;
		gbc_tableName.insets = new Insets(0, 0, 5, 5);
		gbc_tableName.fill = GridBagConstraints.HORIZONTAL;
		gbc_tableName.gridx = 4;
		gbc_tableName.gridy = 6;
		panel.add(tableName, gbc_tableName);
		tableName.setColumns(10);
		GridBagConstraints gbc_tglbtnConnect = new GridBagConstraints();
		gbc_tglbtnConnect.fill = GridBagConstraints.HORIZONTAL;
		gbc_tglbtnConnect.gridwidth = 3;
		gbc_tglbtnConnect.insets = new Insets(0, 0, 5, 5);
		gbc_tglbtnConnect.gridx = 0;
		gbc_tglbtnConnect.gridy = 8;
		panel.add(tglbtnConnect, gbc_tglbtnConnect);
		
		JButton btnDeleteRow = new JButton("Delete Row");
		btnDeleteRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int rowIndex = table.getSelectedRow();
				if(rowIndex>-1) {
					tableModel.removeRow(rowIndex);
				}
			}
		});
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<Columns> columns;
				try {
					columns = basicService.listAllColumns(tableName.getText());
					List<Map<String, String>> dataSet = tableModel.getDataSet();
					List<Map<String, String>> removedSet = tableModel.getRemovedData();
					basicService.updateConfig(tableName.getText(), dataSet, removedSet, columns);
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null, "error: " + e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.gridwidth = 3;
		gbc_btnSave.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSave.insets = new Insets(0, 0, 5, 5);
		gbc_btnSave.gridx = 6;
		gbc_btnSave.gridy = 8;
		panel.add(btnSave, gbc_btnSave);
		
		JButton btnAddRow = new JButton("Add Row");
		btnAddRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tableModel.addRow();
			}
		});
		
		JButton btnDecrypt = new JButton("Decrypt");
		btnDecrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<Map<String, String>> dataSet = tableModel.getDataSet();
				List<Columns> columns = tableModel.getColumns();
				basicService.decryptData(aesKey.getText(), dataSet, columns);
				tableModel.refreshData();
			}
		});
		GridBagConstraints gbc_btnDecrypt = new GridBagConstraints();
		gbc_btnDecrypt.insets = new Insets(0, 0, 5, 5);
		gbc_btnDecrypt.gridx = 1;
		gbc_btnDecrypt.gridy = 9;
		panel.add(btnDecrypt, gbc_btnDecrypt);
		GridBagConstraints gbc_btnAddRow = new GridBagConstraints();
		gbc_btnAddRow.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddRow.gridx = 9;
		gbc_btnAddRow.gridy = 9;
		panel.add(btnAddRow, gbc_btnAddRow);
		
		JButton btnEncrypt = new JButton("Encrypt");
		btnEncrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<Map<String, String>> dataSet = tableModel.getDataSet();
				List<Columns> columns = tableModel.getColumns();
				basicService.encryptData(aesKey.getText(), dataSet, columns);
				tableModel.refreshData();
 			}
		});
		GridBagConstraints gbc_btnEncrypt = new GridBagConstraints();
		gbc_btnEncrypt.insets = new Insets(0, 0, 0, 5);
		gbc_btnEncrypt.gridx = 1;
		gbc_btnEncrypt.gridy = 10;
		panel.add(btnEncrypt, gbc_btnEncrypt);
		GridBagConstraints gbc_btnDeleteRow = new GridBagConstraints();
		gbc_btnDeleteRow.insets = new Insets(0, 0, 0, 5);
		gbc_btnDeleteRow.gridx = 9;
		gbc_btnDeleteRow.gridy = 10;
		panel.add(btnDeleteRow, gbc_btnDeleteRow);
		
		 
		List<Columns> cols = AppWindowUtil.constructCols(); 
		List<Map<String, String>> dataSet = AppWindowUtil.constructDataSet(); 
		tableModel = new EncryptorTableModel(cols, dataSet);
		this.table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		splitPane.setRightComponent(scrollPane);
	}

	public void setFrameVisible(boolean b) {
		frame.setVisible(b);
	}
}
