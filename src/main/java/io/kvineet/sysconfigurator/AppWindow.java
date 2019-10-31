package io.kvineet.sysconfigurator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
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

import io.kvineet.sysconfigurator.models.Columns;
import io.kvineet.sysconfigurator.models.DbConfig;
import io.kvineet.sysconfigurator.models.EncryptorTableModel;
import io.kvineet.sysconfigurator.services.BasicService;
import io.kvineet.sysconfigurator.utils.AppWindowUtil;
import io.kvineet.sysconfigurator.utils.EncryptionUtil;

public class AppWindow {

	private static final String SOMETHING_WENT_WRONG = "Something went wrong";

	private static final String ERROR_MSG = "Error: ";

	private static final String ALERT = "Alert";

	private static final String CONNECT = "Connect";

	private static final String DISCONNECT = "Disconnect";

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
	private JComboBox<Integer> tagLength;
	private JTextField tableName;
	JTable table;

	private static final List<String> keyLengths = Stream.of("128 Bit", "256 Bit").collect(Collectors.toList());
	private static final List<Integer> keyBits = Stream.of(128, 256).collect(Collectors.toList());
	private static final List<Integer> tagLengths = Stream.of(128, 120, 112, 104, 96).collect(Collectors.toList());
	private static final int DEFAULT_SELECT = 1;
	private static final Integer DEFAULT_TAG_LENGTH = 128;

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
		frame.setBounds(100, 100, 950, 503);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("SysConfigurator");

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
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
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("170px"), ColumnSpec.decode("10px") },
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
		panel.add(btnFillFromJson, "12, 2, fill, center");

		JLabel lblDatabaseUsername = new JLabel("dbUserName");
		panel.add(lblDatabaseUsername, "2, 4, right, center");

		dbUserName = new JTextField();
		panel.add(dbUserName, "4, 4, 7, 1, fill, center");
		dbUserName.setColumns(10);

		JLabel lblDbpassword = new JLabel("dbPassword");
		panel.add(lblDbpassword, "2, 6, right, center");

		dbPassword = new JPasswordField();
		dbPassword.setColumns(10);
		panel.add(dbPassword, "4, 6, 7, 1, fill, center");

		JLabel lblTableName = new JLabel("tableName");
		panel.add(lblTableName, "2, 8, right, center");

		tableName = new JTextField();
		panel.add(tableName, "4, 8, 7, 1, fill, center");
		tableName.setColumns(10);
		
		JLabel lblConnectionStatus = new JLabel("Connection Status");
		panel.add(lblConnectionStatus, "2, 10, 1, 1, right, center");
		
		JLabel lblConnectionStatusValue = new JLabel(isConnectionClosed() ? "Disconnected" : "Connected");
		lblConnectionStatusValue.setForeground(isConnectionClosed() ? Color.GRAY : Color.GREEN);
		panel.add(lblConnectionStatusValue, "4, 10, 2, 1, left, center");

		JToggleButton tglbtnConnect = new JToggleButton(CONNECT);
		tglbtnConnect.setBackground(UIManager.getColor("Button.background"));
		tglbtnConnect.addItemListener(getConnectButtonItemListener(tglbtnConnect));
		panel.add(tglbtnConnect, "10, 10, fill, center");

		JLabel aesLabel = new JLabel("aesKey");
		aesLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		aesLabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		panel.add(aesLabel, "2, 12, right, center");

		aesKey = new JTextField();
		aesKey.setColumns(8);
		panel.add(aesKey, "4, 12, 5, 1, fill, center");


		JLabel lblTaglength = new JLabel("tagLength");
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

		JButton btnSave = new JButton("Save");
		btnSave.setEnabled(!isConnectionClosed());
		btnSave.addActionListener(getSaveButtonActionListener());
		panel.add(btnSave, "8, 10, fill, center");

		JButton btnNewButton = new JButton("Generate");
		btnNewButton.addActionListener(getGenerateButtonActionListener());
		panel.add(btnNewButton, "6, 14, fill, fill");

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

	private boolean isConnectionClosed() {

		if (connectionPool == null || connectionPool.getConnection() == null)
			return true;
		
		try {
			if (connectionPool.getConnection().isClosed())
				return true;
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
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
						keyLengths.get(DEFAULT_SELECT)); // Initial choice
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
				if (tglbtnConnect.isSelected() && tglbtnConnect.getText().equalsIgnoreCase(CONNECT)) {
					DbConfig dbConfig = constructDbConfig();

					if (!validateDBConfig(dbConfig))
						return;

					try {
						connectionPool.initConnection(dbConfig);
					} catch (SQLException e) {
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

					tglbtnConnect.setText(DISCONNECT);

				} else if (tglbtnConnect.getText().equalsIgnoreCase(DISCONNECT)) {

					List<Columns> cols = AppWindowUtil.constructCols();
					List<Map<String, String>> dataSet = AppWindowUtil.constructDataSet();
					tableModel.reloadData(cols, dataSet);

					try {
						if (isConnectionClosed()) {
							showError(null, "Connection is already closed.");
						}
						connectionPool.closeDataSource();
					} catch (SQLException e) {
						showError(e);
						return;
					}
					tglbtnConnect.setText(CONNECT);
				}
			}

			private boolean validateDBConfig(DbConfig dbConfig) {
				if (dbConfig.getDbUserName().isEmpty() || dbConfig.getDbUserName().isEmpty()
						|| dbConfig.getJdbcUrl().isEmpty() || !dbConfig.getJdbcUrl().contains("jdbc")) {
					showError(null, "Failed to connect to server, please re-check your configuration.");
					return false;
				}
				return true;
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

	private ActionListener getFillFromJsonActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Map<String, String> data = new HashMap<>();
				data.put("aesKey", aesKey.getText());
				data.put("dbUrl", dbUrl.getText());
				data.put("dbUserName", dbUserName.getText());
				data.put("dbPassword", dbPassword.getText());
				data.put("tableName", tableName.getText());
				data.put("tagLength", tagLength.getSelectedItem().toString());
				JsonInput dialog = new JsonInput();
				dialog.setData(data);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
				data = dialog.getData();
				if (data != null) {
					aesKey.setText(data.get("aesKey"));
					dbUrl.setText(data.get("dbUrl"));
					dbUserName.setText(data.get("dbUserName"));
					dbPassword.setText(data.get("dbPassword"));
					tableName.setText(data.get("tableName"));
					Integer tagL = null;
					try {
						tagL = Integer.parseInt(data.get("tagLength"));
					} catch (NumberFormatException e) {
						tagL = new Integer(DEFAULT_TAG_LENGTH);
					}
					tagLength.setSelectedItem(tagL);
				}
			}
		};
	}

	private void showError(Exception e, String... message) {

		String error = message != null && message.length > 0 ? message[0] : ERROR_MSG;
		if (e == null) {
			error = error.equals(ERROR_MSG) ? SOMETHING_WENT_WRONG : ERROR_MSG;
			JOptionPane.showMessageDialog(null, error);
			return;
		}

		if (e instanceof SQLException) {
			error = error.equals(ERROR_MSG) ? "SQL Error: " : ERROR_MSG;
		}
		String completeMessage = e.getMessage() == null ? SOMETHING_WENT_WRONG : e.getMessage();
		JOptionPane.showMessageDialog(null, error + completeMessage, ALERT, JOptionPane.ERROR_MESSAGE);
	}

	public void setFrameVisible(boolean b) {
		frame.setVisible(b);
	}
}
