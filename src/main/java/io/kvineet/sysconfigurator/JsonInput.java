package io.kvineet.sysconfigurator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import io.kvineet.sysconfigurator.utils.JsonUtils;

public class JsonInput extends JDialog {
    
    /**
     * 
     */
    private static final long serialVersionUID = -1134464710503377415L;


    private Map<String, String> data;
    

    private final JPanel contentPanel = new JPanel();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            JsonInput dialog = new JsonInput();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public  Map<String, String> getData(){
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    /**
     * Create the dialog.
     */
    public JsonInput() {
        setModalityType(ModalityType.DOCUMENT_MODAL);
        setModal(true);
       
        setTitle("Input JSON");
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);
        
        JEditorPane dtrpnaeskeytest = new JEditorPane();
        dtrpnaeskeytest.setContentType("application/json");
        dtrpnaeskeytest.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        String prefilledText = null;
        if(data != null && !data.isEmpty()) {
            try {
                prefilledText = JsonUtils.toJson(data);
            } catch (JsonProcessingException e) {
                //Do nothing
            }
        }
        if(prefilledText == null) {
            prefilledText = "{\n  \"aesKey\": \"test\",\n  \"dbUrl\": \"jdbc:postgresql://172.17.0.3:5432/postgres\",\n  \"dbUserName\": \"postgres\",\n  \"dbPassword\": \"postgres\",\n  \"tableName\": \"system_config\"\n}";
        }
        dtrpnaeskeytest.setText(prefilledText);
        dtrpnaeskeytest.setBounds(12, 26, 422, 196);
        contentPanel.add(dtrpnaeskeytest);
        
        JLabel lblNotAValid = new JLabel("Not a valid json");
        lblNotAValid.setForeground(Color.RED);
        lblNotAValid.setVisible(false);
        lblNotAValid.setBounds(12, 12, 139, 15);
        contentPanel.add(lblNotAValid);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.setToolTipText("");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String text = dtrpnaeskeytest.getText();
                        try {
                            data = JsonUtils.fromJson(text, new TypeReference<Map<String, String>>() {});
                            Component component = (Component) e.getSource();
                            JDialog dialog = (JDialog) SwingUtilities.getRoot(component);
                            dialog.dispose();
                        } catch (IOException e1) {
                            lblNotAValid.setVisible(true);
                        }
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Component component = (Component) e.getSource();
                        JDialog dialog = (JDialog) SwingUtilities.getRoot(component);
                        dialog.dispose();
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }
}
