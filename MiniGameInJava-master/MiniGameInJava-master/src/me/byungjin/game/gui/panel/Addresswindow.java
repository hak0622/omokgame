package me.byungjin.game.gui.panel;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Addresswindow extends JDialog {

   private static final long serialVersionUID = 1L;
   private JTextField searchField;
   private JList<String> list;
   private AddressReceiver addressReceiver;
   
   /**
    * Launch the application.
    */
   /*public static void main(String[] args) {
      try {
         Addresswindow dialog = new Addresswindow();
         dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
         dialog.setVisible(true);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }*/
   public static void main(String[] args) {
	    try {
	        Addresswindow dialog = new Addresswindow(new AddressReceiver() {
	            @Override
	            public void setzipNoField(String address) {
	                // 테스트를 위한 코드
	                System.out.println("우편번호: " + address);
	            }

	            @Override
	            public void setAddressField(String address) {
	                // 테스트를 위한 코드
	                System.out.println("주소: " + address);
	            }
	        });
	        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	        dialog.setVisible(true);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

   

   /**
    * Create the dialog.
    */
   public Addresswindow(AddressReceiver addressReceiver) {
	   this.addressReceiver = addressReceiver; 
	   
      setBounds(100, 100, 474, 300);
      getContentPane().setLayout(null);
      
      JLabel lblNewLabel = new JLabel("우편번호 찾기");
      lblNewLabel.setFont(new Font("굴림", Font.PLAIN, 12));
      lblNewLabel.setBounds(29, 23, 86, 15);
      getContentPane().add(lblNewLabel);
      
      searchField = new JTextField();
      searchField.setBounds(118, 20, 214, 21);
      getContentPane().add(searchField);
      searchField.setColumns(10);
      
      list = new JList<>();
      list.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) { // ����Ŭ����
               int selectedIndex = list.getSelectedIndex();
               if(selectedIndex >= 0) {
                  String selectedAddress = list.getModel().getElementAt(selectedIndex);
                                  
                  addressReceiver.setzipNoField(selectedAddress);
                  addressReceiver.setAddressField(selectedAddress);
                  dispose();
               }
            }
         }
      });
      list.setFont(new Font("굴림", Font.PLAIN, 12));
      list.setModel(new AbstractListModel<String>() {
         String[] values = new String[] {};
         public int getSize() {
            return values.length;
         }
         public String getElementAt(int index) {
            return values[index];
         }
      });
      
      JScrollPane resultArea = new JScrollPane(list);
      resultArea.setBounds(29, 71, 405, 158);
      getContentPane().add(resultArea);
      
      
      resultArea.setViewportView(list);
      
      JButton searchButton = new JButton("찾기");
      searchButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            String searchKeyword = searchField.getText();
                List<String> results = searchAddress(searchKeyword);
                list.setListData(results.toArray(new String[0]));
         }
      });
      
      searchButton.setFont(new Font("굴림", Font.PLAIN, 12));
      searchButton.setBounds(344, 19, 91, 23);
      getContentPane().add(searchButton);
      
   }
   
   private List<String> searchAddress(String keyword) {
        try {
           // URL�� ����� ���� StringBuilder
            StringBuilder urlBuilder = new StringBuilder("http://openapi.epost.go.kr/postal/retrieveNewAdressAreaCdSearchAllService/retrieveNewAdressAreaCdSearchAllService/getNewAddressListAreaCdSearchAll"); // URL
            // ���� API�� ��û �԰ݿ� �´� �Ķ���� ����, �߱޹��� ����Ű.
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=공공데이터 포털에서 받은 서비스 키 입력"); // ����Ű
            urlBuilder.append("&" + URLEncoder.encode("srchwrd", "UTF-8") + "=" + URLEncoder.encode(keyword, "UTF-8")); // �˻���
            urlBuilder.append("&" + URLEncoder.encode("countPerPage", "UTF-8") + "=" + URLEncoder.encode("50", "UTF-8")); // �������� ��µ� ������ ����(�ִ� 50)
            urlBuilder.append("&" + URLEncoder.encode("currentPage", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); // ��µ� ������ ��ȣ

            // URL ��ü ����
            URL url = new URL(urlBuilder.toString());
            // ��û�ϰ��� �ϴ� URL�� ����ϱ� ���� Connection ��ü ����
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // ����� ���� �޼ҵ� SET
            conn.setRequestMethod("GET");
            // ����� ���� Content-type SET 
            conn.setRequestProperty("Content-type", "application/json");
            // ��� ���� �ڵ� Ȯ��
            System.out.println("Response code: " + conn.getResponseCode());
            // ���޹��� �����͸� BufferedReader ��ü�� ����
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); // ����
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8")); // ����
            }
            // ����� �����͸� ���κ��� �о� StringBuilder ��ü�� ����
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            // ��ü ����
            rd.close();
            conn.disconnect();
            
            return parseXMLGetAddress(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            List<String> errorResult = new ArrayList<>();
            errorResult.add("Error: " + e.getMessage());
            return errorResult;
        }
    }

    public static List<String> parseXMLGetAddress(String xml) {
       List<String> addresses = new ArrayList<>();
       
       try {
          // XML �Ľ��� ���� DocumentBuilder ����
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            // XML ���ڿ� �Ľ�
            Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
            
            // "lnmAdres" ������Ʈ(�ּ�)�� ��������
            NodeList lnmAdresList = doc.getElementsByTagName("lnmAdres");
            // "zipNo" ������Ʈ(�����ȣ)�� ��������
            NodeList zipNoList = doc.getElementsByTagName("zipNo");
            
            for (int i = 0; i < lnmAdresList.getLength(); i++) {
                Element lnmAdresElement = (Element) lnmAdresList.item(i);
                Element zipNoElement = (Element) zipNoList.item(i);
                
                String address = lnmAdresElement.getTextContent();
                String zipNo = zipNoElement.getTextContent();
                
                addresses.add("우편번호: " + zipNo + ", 주소: " + address);
            }

            if (addresses.isEmpty()) {
                addresses.add("주소 정보를 찾을 수 없음");
            }
        } catch (Exception e) {
            e.printStackTrace();
            addresses.add("주소 정보를 찾을 수 없음");
        }
        return addresses;
    }
}