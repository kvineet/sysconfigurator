package io.kvineet.sysconfigurator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.google.inject.Inject;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import io.kvineet.sysconfigurator.constants.Constants;
import io.kvineet.sysconfigurator.models.Columns;
import io.kvineet.sysconfigurator.models.Configuration;
import io.kvineet.sysconfigurator.models.DbConfig;
import io.kvineet.sysconfigurator.models.EncryptorTableModel;
import io.kvineet.sysconfigurator.services.BasicService;
import io.kvineet.sysconfigurator.services.ExportQueryService;
import io.kvineet.sysconfigurator.services.RecentConfigurationService;
import io.kvineet.sysconfigurator.utils.AppWindowUtil;
import io.kvineet.sysconfigurator.utils.EncryptionUtil;
import io.kvineet.sysconfigurator.utils.StringUtils;

public class AppWindow {

	@Inject
	private ConnectionPool connectionPool;

	@Inject
	private BasicService basicService;

	@Inject
	private ExportQueryService exportQueryService;

	private JFrame frame;
	private JTextField aesKey;
	private JTextField dbUrl;
	private JTextField dbUserName;
	private JTextField dbPassword;
	private EncryptorTableModel tableModel;
	private JComboBox<Integer> tagLength;
	private JComboBox<Configuration> cmbRecentConfiguration;
	private JTextField tableName;
	private JTable table;
	private JButton btnSave;
	private JButton btnExportInserts;
	private JLabel lblConnectionStatusValue;

	private static final List<String> keyLengths = Stream.of("128 Bit", "256 Bit").collect(Collectors.toList());
	private static final List<Integer> keyBits = Stream.of(128, 256).collect(Collectors.toList());
	private static final List<Integer> tagLengths = Stream.of(128, 120, 112, 104, 96).collect(Collectors.toList());

	private List<Configuration> recentConfigurations = new LinkedList<>();
	
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
		this.initialize();
		this.loadRecentConfigurationsFromFile();
	}

	private void loadRecentConfigurationsFromFile() {

		this.recentConfigurations = RecentConfigurationService.getAll();
		reloadRecentConfigurations();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1100, 503);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("SysConfigurator");

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(false);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		splitPane.setLeftComponent(panel);

		panel.setLayout(new FormLayout(
				new ColumnSpec[] { ColumnSpec.decode("20px"), ColumnSpec.decode("130px"),
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("169px"),
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("132px"),
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("133px"),
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("142px"),
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("138px"),
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("138px"), ColumnSpec.decode("20px") },
				new RowSpec[] { FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC, RowSpec.decode("25px"),
						FormSpecs.LINE_GAP_ROWSPEC, RowSpec.decode("19px"), FormSpecs.LINE_GAP_ROWSPEC,
						RowSpec.decode("19px"), FormSpecs.LINE_GAP_ROWSPEC, RowSpec.decode("19px"),
						FormSpecs.LINE_GAP_ROWSPEC, RowSpec.decode("25px"), RowSpec.decode("35px"),
						RowSpec.decode("25px"), FormSpecs.LINE_GAP_ROWSPEC, RowSpec.decode("25px"), }));

		JLabel lblDburl = new JLabel("dbURL");
		panel.add(lblDburl, "2, 2, right, center");

		dbUrl = new JTextField();
		panel.add(dbUrl, "4, 2, 7, 1, fill, center");

		JButton btnFillFromJson = new JButton("Fill From Json");
		btnFillFromJson.addActionListener(getFillFromJsonActionListener());
		dbUrl.setColumns(10);
		panel.add(btnFillFromJson, "12, 2, 3, 1, fill, center");

		JLabel lblDatabaseUsername = new JLabel(Constants.DB_USER_NAME);
		panel.add(lblDatabaseUsername, "2, 4, right, center");

		dbUserName = new JTextField();
		panel.add(dbUserName, "4, 4, 7, 1, fill, center");
		dbUserName.setColumns(10);

		cmbRecentConfiguration = new JComboBox<>();
		cmbRecentConfiguration.addActionListener(getRecentConfiguationClickedActionListener());
		reloadRecentConfigurations();
		panel.add(cmbRecentConfiguration, "12, 4, 3, 1, fill, center");

		JLabel lblDbpassword = new JLabel(Constants.DB_PASSWORD);
		panel.add(lblDbpassword, "2, 6, right, center");

		dbPassword = new JPasswordField();
		dbPassword.setColumns(10);
		panel.add(dbPassword, "4, 6, 7, 1, fill, center");

		JLabel lblTableName = new JLabel(Constants.TABLE_NAME);
		panel.add(lblTableName, "2, 8, right, center");

		tableName = new JTextField();
		panel.add(tableName, "4, 8, 7, 1, fill, center");
		tableName.setColumns(10);

		JLabel lblConnectionStatus = new JLabel("Connection Pool");
		panel.add(lblConnectionStatus, "2, 10, 1, 1, right, center");

		lblConnectionStatusValue = new JLabel(Constants.DISCONNECTED);
		panel.add(lblConnectionStatusValue, "4, 10, 2, 1, left, center");

		JToggleButton tglbtnConnect = new JToggleButton(Constants.CONNECT);
		tglbtnConnect.setBackground(UIManager.getColor("Button.background"));
		tglbtnConnect.addItemListener(getConnectButtonItemListener(tglbtnConnect));
		panel.add(tglbtnConnect, "10, 10, fill, center");

		JLabel aesLabel = new JLabel(Constants.AES_KEY);
		aesLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		aesLabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		panel.add(aesLabel, "2, 12, right, center");

		aesKey = new JTextField();
		aesKey.setColumns(8);
		panel.add(aesKey, "4, 12, 5, 1, fill, center");

		JLabel lblTaglength = new JLabel(Constants.TAG_LENGTH);
		panel.add(lblTaglength, "2, 14, right, center");

		tagLength = new JComboBox<Integer>();
		tagLengths.stream().forEach(item -> tagLength.addItem(item));
		panel.add(tagLength, "4, 14, fill, center");

		JButton btnEncrypt = new JButton("Encrypt");
		btnEncrypt.addActionListener(getEncryptActionListener());
		panel.add(btnEncrypt, "10, 12, fill, center");

		JButton btnAddRow = new JButton("Add Row");
		btnAddRow.addActionListener(getAddRowActionListener());
		panel.add(btnAddRow, "12, 12, fill, center");

		btnExportInserts = new JButton("Export INSERT`s");
		btnExportInserts.setEnabled(false);
		btnExportInserts.addActionListener(getExportInsertsActionListener());
		panel.add(btnExportInserts, "14, 12, fill, center");

		btnSave = new JButton("Save");
		btnSave.setEnabled(false);
		btnSave.addActionListener(getSaveButtonActionListener());
		panel.add(btnSave, "8, 10, fill, center");

		JButton btnNewButton = new JButton("Generate New AES Key");
		btnNewButton.addActionListener(getGenerateButtonActionListener());
		panel.add(btnNewButton, "6, 14, 3, 1, fill, fill");

		JButton btnDecrypt = new JButton("Decrypt");
		btnDecrypt.addActionListener(getDecryptActionListener());
		panel.add(btnDecrypt, "10, 14, fill, center");

		JButton btnDeleteRow = new JButton("Delete Row");
		panel.add(btnDeleteRow, "12, 14, fill, center");
		btnDeleteRow.addActionListener(getDeleteRowActionListener());

		List<Columns> cols = AppWindowUtil.constructCols();
		List<Map<String, String>> dataSet = AppWindowUtil.constructDataSet();
		tableModel = new EncryptorTableModel(cols, dataSet);
		this.table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);

		splitPane.setRightComponent(scrollPane);
	}

	private ActionListener getExportInsertsActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<Map<String, String>> dataSet = tableModel.getDataSet();
				if (dataSet.isEmpty()) {
					showError(null, "No data to export");
					return;
				}
				
				FileDialog fileDialog = new FileDialog(frame, "Export INSERT SQL Statements", FileDialog.SAVE);
				fileDialog.setAlwaysOnTop(true);
				fileDialog.setFile("*.sql");
				fileDialog.setVisible(true);

				if (fileDialog.getFile() != null) {
					List<Columns> columns;
					try {
						columns = basicService.listAllColumns(tableName.getText());
						List<Map<String, String>> removedSet = tableModel.getRemovedData();
						String fileName = fileDialog.getFile().replaceAll("(?i)(.sql)", "") + ".sql";
						exportQueryService.save(tableName.getText(), dataSet, removedSet, columns,
								fileDialog.getDirectory() + fileName);
					} catch (Exception e2) {
						e2.printStackTrace();
 						showError(e2,
								"File cannot be exported. Make sure that the file name is correct and you have write access to location that you have selected.");
					}
				}
			}
		};
	}

	private ActionListener getRecentConfiguationClickedActionListener() {

		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int index = cmbRecentConfiguration.getSelectedIndex();
				if (index > -1 && !recentConfigurations.isEmpty() && recentConfigurations.size() > index) {

					Configuration required = recentConfigurations.get(index);
					setCurrentConfiguration(required);
				}
			}
		};
	}

	private boolean isDatasourceConnected() {
		if (connectionPool == null || connectionPool.isDatasourceConnected())
			return true;
		return false;
	}

	private ActionListener getDeleteRowActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int rowIndex = table.getSelectedRow();
				if (rowIndex > -1) {
					tableModel.removeRow(rowIndex);
				}
			}
		};
	}

	private ActionListener getDecryptActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<Map<String, String>> dataSet = tableModel.getDataSet();
				List<Columns> columns = tableModel.getColumns();
				basicService.decryptData(aesKey.getText(), (Integer) tagLength.getSelectedItem(), dataSet, columns);
				tableModel.refreshData();
			}
		};
	}

	private ActionListener getEncryptActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<Map<String, String>> dataSet = tableModel.getDataSet();
				List<Columns> columns = tableModel.getColumns();
				basicService.encryptData(aesKey.getText(), (Integer) tagLength.getSelectedItem(), dataSet, columns);
				tableModel.refreshData();
			}
		};
	}

	private ActionListener getAddRowActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tableModel.addRow();
			}
		};
	}

	private ActionListener getGenerateButtonActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String input = (String) JOptionPane.showInputDialog(null, "Choose the key length (256 Bit recomended.)",
						"AES Key Length", JOptionPane.QUESTION_MESSAGE, null, keyLengths.toArray(),
						keyLengths.get(Constants.DEFAULT_SELECT)); // Initial choice
				try {

					int index = keyLengths.indexOf(input);
					if (index > -1) {
						String newKey = EncryptionUtil.generateKey(keyBits.get(index));
						aesKey.setText(newKey);
					}

				} catch (NoSuchAlgorithmException e) {
					showError(e);
				}

			}
		};
	}

	private ActionListener getSaveButtonActionListener() {
		return new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				List<Columns> columns;
				try {
					columns = basicService.listAllColumns(tableName.getText());
					List<Map<String, String>> dataSet = tableModel.getDataSet();
					List<Map<String, String>> removedSet = tableModel.getRemovedData();
					basicService.updateConfig(tableName.getText(), dataSet, removedSet, columns);
				} catch (SQLException e) {
					showError(e);
				}
			}
		};
	}

	private ItemListener getConnectButtonItemListener(JToggleButton tglbtnConnect) {
		return new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {

				boolean datasourceConnected = isDatasourceConnected();
				if (!datasourceConnected && tglbtnConnect.getText().equalsIgnoreCase(Constants.CONNECT)) {

					if (!validate(getCurrentConfiguration()))
						return;

					saveCurrentConfiguration();

					DbConfig dbConfig = constructDbConfig();
					try {
						connectionPool.initConnection(dbConfig);
					} catch (Exception e) {
						showError(e, "Failed to connect to server due to error: \n");
					}

					List<Columns> columns;
					try {
						columns = basicService.listAllColumns(tableName.getText());
						List<Map<String, String>> dataSet = basicService.retriveData(tableName.getText(), columns);
						tableModel.reloadData(columns, dataSet);
					} catch (SQLException e) {
						showError(e);
						return;
					}
					setConnectButton(tglbtnConnect, true);

				} else if (tglbtnConnect.getText().equalsIgnoreCase(Constants.DISCONNECT)) {

					List<Columns> cols = AppWindowUtil.constructCols();
					List<Map<String, String>> dataSet = AppWindowUtil.constructDataSet();
					tableModel.reloadData(cols, dataSet);

					try {
						if (!datasourceConnected) {
							showError(null, "Connection is already closed.");
						}
						connectionPool.closeDataSource();
					} catch (SQLException e) {
						showError(e);
						return;
					}
					setConnectButton(tglbtnConnect, false);
				} else {
					setConnectButton(tglbtnConnect, datasourceConnected);
				}

			}

			private void setConnectButton(JToggleButton tglbtnConnect, boolean connected) {
				tglbtnConnect.setSelected(connected);
				tglbtnConnect.setText(connected ? Constants.DISCONNECT : Constants.CONNECT);
				
				btnSave.setEnabled(connected);
				btnExportInserts.setEnabled(connected);
				lblConnectionStatusValue.setText(connected ? Constants.CONNECTED : Constants.CONNECTED);
				lblConnectionStatusValue.setForeground(connected ? Color.GREEN : Color.GRAY);
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
		};
	}

	protected void saveCurrentConfiguration() {

		Configuration configuration = getCurrentConfiguration();
		if (validate(configuration) && !recentConfigurations.contains(configuration)) {
			try {
				recentConfigurations = RecentConfigurationService.save(configuration, recentConfigurations);
			} catch (Exception e) {
				e.printStackTrace();
				recentConfigurations = new ArrayList<>();
			}
			reloadRecentConfigurations();
		}
	}

	private void reloadRecentConfigurations() {
		if (cmbRecentConfiguration != null) {
			cmbRecentConfiguration.removeAllItems();
			recentConfigurations.stream().forEach(cmbRecentConfiguration::addItem);
		}
	}

	private boolean validate(Configuration configuration) {

		if (StringUtils.isNullOrEmpty(configuration.getAesKey())
				|| StringUtils.isNullOrEmpty(configuration.getDbUserName())
				|| StringUtils.isNullOrEmpty(configuration.getTableName())
				|| StringUtils.isNullOrEmpty(configuration.getDbUrl()) || !configuration.getDbUrl().contains("jdbc")) {

			showError(null, "Please re-check your configuration.");
			return false;
		}
		return true;
	}

	private ActionListener getFillFromJsonActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Map<String, String> data = getCurrentConfiguration().toMap();
				JsonInput dialog = new JsonInput();
				dialog.setData(data);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
				data = dialog.getData();
				setCurrentConfiguration(new Configuration(data));
			}
		};
	}

	private void showError(Exception e, String... message) {

		String error = message != null && message.length > 0 ? message[0] : Constants.ERROR_MSG;
		if (e == null) {
			error = error.equalsIgnoreCase(Constants.ERROR_MSG) ? Constants.SOMETHING_WENT_WRONG : error;
			JOptionPane.showMessageDialog(null, error);
			return;
		}

		if (e instanceof SQLException) {
			error = error.equals(Constants.ERROR_MSG) ? "SQL Error: " : Constants.ERROR_MSG;
		}
		String completeMessage = e.getMessage() == null ? Constants.SOMETHING_WENT_WRONG : e.getMessage();
		JOptionPane.showMessageDialog(null, error + completeMessage, Constants.ALERT, JOptionPane.ERROR_MESSAGE);
	}

	private Configuration getCurrentConfiguration() {
		Configuration data = new Configuration();
		data.setAesKey(aesKey.getText());
		data.setDbUrl(dbUrl.getText());
		data.setDbUserName(dbUserName.getText());
		data.setDbPassword(dbPassword.getText());
		data.setTableName(tableName.getText());
		data.setTagLength(tagLength.getSelectedItem().toString());
		return data;
	}

	private void setCurrentConfiguration(Configuration data) {
		if (data != null) {
			aesKey.setText(data.getAesKey());
			dbUrl.setText(data.getDbUrl());
			dbUserName.setText(data.getDbUserName());
			dbPassword.setText(data.getDbPassword());
			tableName.setText(data.getTableName());
			Integer tagL = null;
			try {
				tagL = Integer.parseInt(data.getTagLength());
			} catch (NumberFormatException e) {
				tagL = new Integer(Constants.DEFAULT_TAG_LENGTH);
			}
			tagLength.setSelectedItem(tagL);
		}
	}

	public void setFrameVisible(boolean b) {
		frame.setVisible(b);
	}
}
