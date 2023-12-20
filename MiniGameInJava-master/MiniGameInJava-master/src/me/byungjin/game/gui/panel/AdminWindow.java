package me.byungjin.game.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import me.byungjin.db.DBConnection;
import me.byungjin.game.gui.MiniDialog;
import me.byungjin.network.Agent;
import me.byungjin.network.Client;
import me.byungjin.network.PROMISE;


public class AdminWindow extends JFrame implements ActionListener,AddressReceiver{
	JPanel north, center, south, p1, p2, p3, p4, p5, p6;
    JLabel m_label,nameLabel, idLabel,pwLabel, addressLabel,zipLabel,extraAddressLabel,modeLabel, emailLabel, genderLabel,imageLabel,ssnLabel;
    JTextField keyword,nameField, idField,extraAddressField,emailField1,emailField2,phoneField2,phoneField3;
    private JTextField zipField;
    private JTextField addressField;
    JRadioButton male, female;
    ButtonGroup genderGroup;
    JComboBox<String> emailDomain;
    JComboBox<String> ch_category;
    JComboBox<String> phonePrefixBox;
    JComboBox<String> yearBox;
    JComboBox<String> monthBox;
    JComboBox<String> dayBox;
    JButton roomListButton,registerButton, modifyButton, deleteButton, selectButton, getAllButton, exitButton, btnChoosePic,checkIdButton;
    JTable memberTable;
    JScrollPane scrollPane;
    String g,profilePicPath;
    DBConnection dbConnection = new DBConnection();
    Vector<String> column = new Vector<>();
    AdminModel model = new AdminModel();
    String category;
    int selRow;// 마우스 이벤트. 어떤 레코드를 선택했는지 알수 있는 변수명.
    int idx;
    JScrollPane scroll;
    JPanel imagePanel;
    ImageIcon userImage;
    Agent toServer;
    JPasswordField pwField,ssnField;   
    JTextArea introArea;
    
    public AdminWindow(Agent toServer) {
    	super("회원 관리 시스템");
    	this.toServer = toServer; 
    	   	
    	if (toServer == null || !toServer.isRunning()) {
            System.out.println("서버와의 연결이 없거나 서버가 실행 중이 아닙니다.");  // 로그 찍기
            return;
        }

        // 관리자로서의 신원을 서버에게 전송합니다.
        toServer.send(PROMISE.ADMIN_LOGIN_SUC, "admin " + "1111");  // "adminPassword"는 관리자 비밀번호입니다.
        
    	this.getContentPane().setFont(new Font("굴림", Font.PLAIN, 20));
    	this.setBounds(100, 100, 1280, 820); // 크기 변경 
    	this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	this.getContentPane().setLayout(null);
    	this.getContentPane().setBackground(new Color(225, 239, 250));
    	
    	JLabel titleLabel = new JLabel("관리자 프로그램", SwingConstants.CENTER);
        titleLabel.setFont(new Font("굴림", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK); // 글씨 색을 흰색으로 설정
        //titleLabel.setBackground(Color.#B8D0FA); // 배경색을 검은색으로 설정
        titleLabel.setBackground(Color.decode("#B8D0FA"));
        titleLabel.setOpaque(true); // 배경색이 보이도록 설정
        titleLabel.setBounds(0, 0, 1280, 30); // 창의 크기에 맞게 위치와 크기를 설정
        this.getContentPane().add(titleLabel);
        
    	JPanel panel = new JPanel();
        panel.setBorder(new LineBorder(new Color(0, 0, 0)));
        panel.setForeground(new Color(240, 240, 240));
        panel.setBackground(new Color(225, 239, 250));
        panel.setBounds(802, 40, 452, 730);
        panel.setLayout(null); // 레이아웃 매니저를 사용하지 않고, 직접 위치와 크기를 설정하기 위해 null로 설정

        
        nameLabel = new JLabel("이름:");
        nameField = new JTextField(20);
        nameLabel.setBounds(22, 10, 50, 15); // 위치 변경
        nameField.setBounds(22, 35, 228, 21);
        panel.add(nameLabel);
        panel.add(nameField);

        idLabel = new JLabel("아이디:");
        idField = new JTextField(20);
        idLabel.setBounds(22, 65, 50, 15); 
        idField.setBounds(22, 90, 120, 21); 
        checkIdButton = new JButton("중복확인"); 
        checkIdButton.setBounds(150, 90, 100, 21);
        panel.add(idLabel);
        panel.add(idField);
        panel.add(checkIdButton);
        
        checkIdButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "아이디를 입력하세요.");
                    return;
                }

                DBConnection dbConnection = new DBConnection();
                if (dbConnection.isUser(id)) {
                    JOptionPane.showMessageDialog(null, "이미 사용중인 아이디입니다.");
                } else {
                    JOptionPane.showMessageDialog(null, "사용 가능한 아이디입니다.");
                }
                dbConnection.close();
            }
        });
        
        pwLabel = new JLabel("패스워드:");
        pwField = new JPasswordField(20);
        pwLabel.setBounds(22, 120, 69, 15);
        pwField.setBounds(22, 145, 120, 21); 
        panel.add(pwLabel);
        panel.add(pwField);

        JButton showPasswordButton1 = new JButton("보기");
        showPasswordButton1.setBounds(150, 145, 100, 21); // 위치 변경
        panel.add(showPasswordButton1);

        showPasswordButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (((JButton)e.getSource()).getText().equals("보기")) {
                    pwField.setEchoChar((char)0); // password 보이게
                    showPasswordButton1.setText("숨기기");
                } else {
                    pwField.setEchoChar('•'); // password 가리게
                    showPasswordButton1.setText("보기");
                }
            }
        });
        
        JLabel birthLabel = new JLabel("생년월일:");
        birthLabel.setBounds(22, 175, 100, 30);
        panel.add(birthLabel);
        
        yearBox = new JComboBox<>();
        for(int i=2023; i>=1900; i--) {  // 2023년부터 1900년까지
            yearBox.addItem(Integer.toString(i));
        }
        yearBox.setEditable(true);
        yearBox.setBounds(22, 200, 70, 21);
        panel.add(yearBox);
        
        // 월 콤보박스 추가
        monthBox = new JComboBox<>();
        for(int i=1; i<=12; i++) {  // 1월부터 12월까지
            monthBox.addItem(String.format("%02d", i));  // 한자리 수일 때 앞에 0을 붙여 두자리로 표시
        }
        monthBox.setEditable(true);
        monthBox.setBounds(102, 200, 70, 21);
        panel.add(monthBox);

        // 일 콤보박스 추가
        dayBox = new JComboBox<>();
        for(int i=1; i<=31; i++) {  // 1일부터 31일까지
            dayBox.addItem(String.format("%02d", i));  // 한자리 수일 때 앞에 0을 붙여 두자리로 표시
        }
        dayBox.setEditable(true);
        dayBox.setBounds(182, 200, 70, 21);
        panel.add(dayBox);
        
        ssnLabel = new JLabel("주민등록번호 뒷자리");
        ssnLabel.setBounds(22, 230, 130, 30);  // 위치와 크기 설정. 실제 값은 화면 구성에 따라 다를 수 있습니다.
        panel.add(ssnLabel);  
       
        //주민등록번호
        ssnField = new JPasswordField(7);
        ssnField.setBounds(22, 255, 120, 21);
        panel.add(ssnField);
        
        ((AbstractDocument)ssnField.getDocument()).setDocumentFilter(new DocumentFilter(){
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String string = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;

                if (string.length() <= 7) {
                    super.replace(fb, offset, length, text, attrs); //To change body of generated methods, choose Tools | Templates.
                }
            }
        });
             
        JButton showPasswordButton2 = new JButton("보기");
        showPasswordButton2.setBounds(150, 255, 100, 21);
        panel.add(showPasswordButton2);
        
        showPasswordButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (((JButton)e.getSource()).getText().equals("보기")) {
                	ssnField.setEchoChar((char)0); // password 보이게
                    showPasswordButton2.setText("숨기기");
                } else {
                	ssnField.setEchoChar('•'); // password 가리게
                    showPasswordButton2.setText("보기");
                }
            }
        });
        
        // 성별 라디오 버튼 추가
        genderLabel = new JLabel("성별:");
        male = new JRadioButton("남성");
        female = new JRadioButton("여성");
        male.setBackground(new Color(225, 239, 250)); // 배경색 변경
        female.setBackground(new Color(225, 239, 250)); // 배경색 변경
        
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(male);
        genderGroup.add(female);
        genderLabel.setBounds(22, 285, 50, 15); // 위치 변경
        male.setBounds(22, 310, 75, 23); // 위치 변경
        female.setBounds(100, 310, 91, 23); // 위치 변경
        panel.add(genderLabel);
        panel.add(male);
        panel.add(female);
    	
        emailLabel = new JLabel("이메일:");
        emailField1 = new JTextField(15);
        emailField2 = new JTextField(15);
        JLabel atLabel = new JLabel("@");
        emailDomain = new JComboBox<>();
        emailDomain.addItem("직접입력");
        emailDomain.addItem("naver.com");
        emailDomain.addItem("google.com");
        emailDomain.addItem("nate.com");
        emailDomain.addItem("paran.com");
        emailDomain.addItem("hanmail.net");
        emailLabel.setBounds(22, 340, 50, 15);
        emailField1.setBounds(22, 365, 97, 21);
        atLabel.setBounds(123, 365, 16, 15);
        emailField2.setBounds(141, 365, 109, 23);
        emailDomain.setBounds(254, 365, 120, 23);
        panel.add(emailLabel);
        panel.add(emailField1);
        panel.add(atLabel);
        panel.add(emailField2);
        panel.add(emailDomain);

        zipLabel = new JLabel("우편번호:");
        zipField = new JTextField(20);
        zipLabel.setBounds(22, 395, 75, 15); // 위치 변경
        zipField.setBounds(22, 420, 101, 21); // 위치 변경
        panel.add(zipLabel);
        panel.add(zipField);

        JButton searchZipCodeButton = new JButton("우편번호검색");
        searchZipCodeButton.setBounds(135, 420, 115, 21);    // 버튼 위치 조정
        panel.add(searchZipCodeButton);
        
        // 우편번호 검색 버튼 클릭 이벤트 추가
        searchZipCodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {        
                // 주소 검색 창 생성
            	Addresswindow dialog = new Addresswindow(AdminWindow.this);                
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setVisible(true);
            }
        });   
        
        addressLabel = new JLabel("주소:");
        addressField = new JTextField(20);
        addressLabel.setBounds(22, 450, 42, 15);
        addressField.setBounds(22, 475, 227, 21);
        panel.add(addressLabel);
        panel.add(addressField);
        
        extraAddressLabel = new JLabel("상세주소:");
        extraAddressField = new JTextField(20);
        extraAddressLabel.setBounds(22, 505, 75, 15);
        extraAddressField.setBounds(22, 530, 227, 21);
        panel.add(extraAddressLabel);
        panel.add(extraAddressField);
        
        JLabel phoneLabel = new JLabel("전화번호:");
        phoneLabel.setBounds(22, 560, 75, 15); // 위치 조정
        panel.add(phoneLabel);
        
        phonePrefixBox = new JComboBox<>();
        phonePrefixBox.addItem("010");
        phonePrefixBox.addItem("011");
        phonePrefixBox.addItem("016");
        phonePrefixBox.addItem("017");
        phonePrefixBox.addItem("018");
        phonePrefixBox.addItem("019");
        phonePrefixBox.setEditable(true);
        phonePrefixBox.setBounds(22, 585, 70, 21); // 위치 조정
        panel.add(phonePrefixBox);
        
        JLabel phoneDashLabel1 = new JLabel("-");
        phoneDashLabel1.setBounds(99, 585, 15, 15); // 위치 조정
        panel.add(phoneDashLabel1);

        phoneField2 = new JTextField(4);
        phoneField2.setBounds(116, 585, 59, 21); // 위치 조정
        panel.add(phoneField2);

        JLabel phoneDashLabel2 = new JLabel("-");
        phoneDashLabel2.setBounds(178, 585, 15, 15); // 위치 조정
        panel.add(phoneDashLabel2);

        phoneField3 = new JTextField(4);
        phoneField3.setBounds(186, 585, 59, 21); // 위치 조정
        panel.add(phoneField3);
        
        JLabel introLabel = new JLabel("소개:");
        introLabel.setBounds(22, 615, 42, 15); 
        panel.add(introLabel);
        
        introArea = new JTextArea();
        introArea.setBounds(22, 640, 403, 70); 
        panel.add(introArea);
        
        imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBounds(275, 10, 150, 150);
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        imageLabel = new JLabel() {
            public Dimension getPreferredSize() {
                return new Dimension(150, 150);  // imageLabel의 기본 크기를 설정합니다.
            }
            public void setIcon(Icon icon) {
                if (icon instanceof ImageIcon) {
                    ImageIcon imageIcon = (ImageIcon) icon;
                    Image image = imageIcon.getImage();
                    if (image.getWidth(null) > getWidth() || image.getHeight(null) > getHeight()) {
                        image = image.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
                        imageIcon = new ImageIcon(image);
                    }
                    super.setIcon(imageIcon);
                } else {
                    super.setIcon(icon);
                }
            }
        };
        btnChoosePic = new JButton("프로필 선택");         
        btnChoosePic.setBounds(275, 180, 150, 23);
        
        imageLabel.setHorizontalAlignment(JLabel.CENTER); // 이미지를 중앙에 배치
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        panel.add(imagePanel);
        panel.add(btnChoosePic);
        
        this.getContentPane().add(panel);
        
        ch_category = new JComboBox<>();
        ch_category.addItem("카테고리");
        ch_category.addItem("name");
        ch_category.addItem("Id");
        ch_category.addItem("gender");
        ch_category.addItem("zip");
        ch_category.addItem("address");
        ch_category.addItem("extraAddress");
        ch_category.addItem("email");
        ch_category.addItem("birth");
        ch_category.addItem("ssn");
        ch_category.addItem("phone");
        ch_category.addItem("introduction");
        keyword = new JTextField(10);
        selectButton = new JButton("조회");
        getAllButton = new JButton("전체 조회");       
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        categoryPanel.setBackground(Color.decode("#B8D0FA"));       
        categoryPanel.add(ch_category);
        categoryPanel.add(keyword);
        categoryPanel.add(selectButton);
        categoryPanel.add(getAllButton);   
        categoryPanel.setBounds(0, 40, 800, 35);
        this.getContentPane().add(categoryPanel);
        
        registerButton = new JButton("등록");
        modifyButton = new JButton("수정");
        deleteButton = new JButton("삭제");
        roomListButton = new JButton("방 목록");
        exitButton = new JButton("종료");
               
        memberTable = new JTable(model);
        scrollPane = new JScrollPane(memberTable);
        scrollPane.setBounds(0, 75, 800, 635);  // 위치와 크기를 설정합니다.
        this.getContentPane().add(scrollPane);
                        
        registerButton.setBounds(0, 715, 160, 55);
        modifyButton.setBounds(160, 715, 160, 55);
        deleteButton.setBounds(320, 715, 160, 55);
        roomListButton.setBounds(480, 715, 160, 55);
        exitButton.setBounds(640, 715, 160, 55);
        this.getContentPane().add(registerButton);
        this.getContentPane().add(modifyButton);
        this.getContentPane().add(deleteButton);
        this.getContentPane().add(roomListButton);
        this.getContentPane().add(exitButton);              
                 
        registerButton.addActionListener(this);
        modifyButton.addActionListener(this);
        deleteButton.addActionListener(this);
        selectButton.addActionListener(this);
        getAllButton.addActionListener(this);
        roomListButton.addActionListener(this);
        exitButton.addActionListener(this);
        
        roomListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {        
            	try {
                    // 새로운 창 생성
                    DisplayRoomPanel dialog = new DisplayRoomPanel(toServer);
                                 
                    MiniDialog dialogWindow = new MiniDialog(dialog, "Room List", null);
                } catch (Exception ex) {
                    // Client 객체 생성에 실패했을 경우의 예외 처리
                    ex.printStackTrace();
                }
            }
        }); 
               
        // 메일주소 초이스 박스 리스너 구현..
        emailDomain.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if (emailDomain.getSelectedIndex() == 0) {
                	emailField2.setText("");
                } else {
                	emailField2.setText((String) ie.getItem());
                }
            }
        });
        
        // 검색 카테고리 초이스 박스 리스너 구현..
        ch_category.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) { 
                category = (String) ie.getItem();
            }
        });
        
       // 성별 radio button 리스너 구현
        male.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (male.isSelected()) {
                    g = "남성";
                }
            }
        });

        female.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (female.isSelected()) {
                    g = "여성";
                }
            }
        });
       
        
        // 이미지 선택 버튼에 액션 리스너를 추가합니다.
        btnChoosePic.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg");
                fileChooser.setFileFilter(filter);
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    profilePicPath = selectedFile.getAbsolutePath();
                    ImageIcon icon = new ImageIcon(profilePicPath);
                    Image image = icon.getImage();
                    if (image.getWidth(null) > imageLabel.getWidth() || image.getHeight(null) > imageLabel.getHeight()) {
                        image = image.getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH);
                    }
                    imageLabel.setIcon(new ImageIcon(image));  
                }
            }
        });
        
        memberTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selRow = memberTable.getSelectedRow();  // 선택한 행의 인덱스를 가져옵니다.

                // 선택한 행이 테이블 범위 내에 있는지 확인합니다.
                if (selRow >= 0 && selRow < memberTable.getRowCount()) {
                    // 선택한 행의 정보를 텍스트 필드에 표시합니다.
                    nameField.setText((String) memberTable.getValueAt(selRow, 0));
                    idField.setText((String) memberTable.getValueAt(selRow, 1));
                    pwField.setText((String) memberTable.getValueAt(selRow, 2));
                    String birth = (String) memberTable.getValueAt(selRow, 4);
                    ssnField.setText((String) memberTable.getValueAt(selRow, 5));
                    zipField.setText((String) memberTable.getValueAt(selRow, 6));
                    addressField.setText((String) memberTable.getValueAt(selRow, 7));
                    extraAddressField.setText((String) memberTable.getValueAt(selRow, 8));
                    String email = (String) memberTable.getValueAt(selRow, 9);
                    String phone = (String) memberTable.getValueAt(selRow, 10);
                    String introduction = (String) memberTable.getValueAt(selRow, 12); // 12번째 컬럼이 소개라고 가정합니다.
                    introArea.setText(introduction);
                    if (email.contains("@")) {
                        emailField1.setText(email.split("@")[0]);
                        emailField2.setText(email.split("@")[1]);
                    } else {
                        emailField1.setText(email);
                        emailField2.setText("");
                    }
                    if(birth != null && birth.contains("-")) {
                        String[] birthParts = birth.split("-");
                        if(birthParts.length == 3) {
                            yearBox.setSelectedItem(birthParts[0]);
                            monthBox.setSelectedItem(birthParts[1]);
                            dayBox.setSelectedItem(birthParts[2]);
                        }
                    }
                    if (phone != null && phone.contains("-")) {
                        String[] phoneParts = phone.split("-");
                        if (phoneParts.length == 3) {
                            phonePrefixBox.setSelectedItem(phoneParts[0]);
                            phoneField2.setText(phoneParts[1]);
                            phoneField3.setText(phoneParts[2]);
                        }
                    } else if (phone != null) {
                        phonePrefixBox.setSelectedItem(phone);
                    }
                    String gender = (String) memberTable.getValueAt(selRow, 3);
                    if (gender != null && gender.equals("남성")) {
                        male.setSelected(true);
                        female.setSelected(false);
                    } else if (gender != null) {
                        female.setSelected(true);
                        male.setSelected(false);
                    }                 
                    String imagePath = (String) memberTable.getValueAt(selRow, 11); // 이미지 경로를 가져옵니다. (9는 이미지 경로가 있는 컬럼의 인덱스입니다. 실제 값은 데이터에 따라 다를 수 있습니다.)
                    if (imagePath != null && !imagePath.isEmpty()) {
                        ImageIcon icon = new ImageIcon(imagePath);
                        imageLabel.setIcon(icon); 
                    }
                }
            }
        });
    }
    
    // 회원 검색 메서드
    public void searchProcess(String category,String keyword) {
    	if (dbConnection.isConnect) {
            String sql = "select * from User where " + category + " like ?";
            try {
                PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql);
                pstmt.setString(1, "%" + keyword + "%");
                ResultSet rs = pstmt.executeQuery();
                Vector<Vector<Object>> list = new Vector<>();
                while (rs.next()) {
                    Vector<Object> record = new Vector<>();
                    record.add(rs.getString("name"));
                    record.add(rs.getString("Id"));
                    record.add(rs.getString("Pw"));
                    record.add(rs.getString("gender"));
                    record.add(rs.getString("birth"));                  
                    record.add(rs.getString("ssn"));  
                    record.add(rs.getString("zip"));
                    record.add(rs.getString("address"));
                    record.add(rs.getString("extraAddress"));                  
                    record.add(rs.getString("email"));  
                    record.add(rs.getString("phone"));
                    record.add(rs.getString("profilePicPath")); 
                    record.add(rs.getString("introduction"));                  
                    list.add(record);
                }
                model.setList(list);
                model.fireTableDataChanged();               
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == registerButton) {
            String name = nameField.getText();
            String id = idField.getText();
            String pw = pwField.getText();
            String zip = zipField.getText();
            String address = addressField.getText();
            String extraAddress = extraAddressField.getText();
            String email = emailField1.getText() + "@" + emailField2.getText();
            String gender = male.isSelected() ? "남성" : "여성";  
            String ssn = ssnField.getText();
            String phone = phonePrefixBox.getSelectedItem().toString() + "-" + phoneField2.getText() + "-" + phoneField3.getText();
            String introduction = introArea.getText();
            
            if (yearBox.getSelectedItem() != null && monthBox.getSelectedItem() != null && dayBox.getSelectedItem() != null) {
                String birth = yearBox.getSelectedItem().toString() + monthBox.getSelectedItem().toString() + dayBox.getSelectedItem().toString();
   
                int result = regist(name, id, pw, gender,zip,address,extraAddress,email,profilePicPath,birth,ssn, phone, introduction);
                if (result == 1) {
                    JOptionPane.showMessageDialog(this, "회원이 성공적으로 등록되었습니다.");
                    getListAll(); 
                    memberTable.updateUI();
                } else {
                    JOptionPane.showMessageDialog(this, "회원 등록에 실패하였습니다.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "생년월일을 선택해주세요.");
            }
        } else if (source == modifyButton) {
            if (memberTable.getSelectedRow() != -1) {
                String name = nameField.getText();
                String id = idField.getText();
                String pw = pwField.getText();
                String zip = zipField.getText();
                String address = addressField.getText();
                String extraAddress = extraAddressField.getText();
                String email = emailField1.getText() + "@" + emailField2.getText();
                String gender = male.isSelected() ? "남성" : "여성"; 
                String birth = yearBox.getSelectedItem().toString() + monthBox.getSelectedItem().toString() + dayBox.getSelectedItem().toString();
                String ssn = ssnField.getText();
                String phone = phonePrefixBox.getSelectedItem().toString() + "-" + phoneField2.getText() + "-" + phoneField3.getText();
                String introduction = introArea.getText();

                int result = edit(name, id, pw,gender,zip,address,extraAddress, email,profilePicPath,birth,ssn, phone, introduction);
                if (result == 1) {
                    JOptionPane.showMessageDialog(this, "회원 정보가 성공적으로 수정되었습니다.");
                    getListAll();
                    memberTable.updateUI();
                } else {
                    JOptionPane.showMessageDialog(this, "회원 정보 수정에 실패하였습니다.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "수정할 행을 선택하세요.");
            }
        } else if (source == deleteButton) {
            if (memberTable.getSelectedRow() != -1) {
                int result = delete();
                if (result == 1) {
                    JOptionPane.showMessageDialog(this, "회원이 성공적으로 삭제되었습니다.");
                    getListAll();
                } else {
                    JOptionPane.showMessageDialog(this, "회원 삭제에 실패하였습니다.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "삭제할 행을 선택하세요.");
            }
        } else if (source == selectButton) {
            String selectedCategory = (String) ch_category.getSelectedItem();
            String keywordInput = keyword.getText();
            if (selectedCategory.equals("카테고리")) {
                JOptionPane.showMessageDialog(this, "카테고리를 선택해주세요.");
            } else if (keywordInput.isEmpty()) {
                JOptionPane.showMessageDialog(this, "검색어를 입력해주세요.");
            } else {
                searchProcess(selectedCategory, keywordInput);
            }
        } else if (source == getAllButton) {
            getListAll();
        } else if (source == exitButton) {
        	 dispose();
        }
    }
   
    public void getListAll() {
        String sql = "Select * from User";
        try {
            PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            //AdminModel am = new AdminModel();
            Vector<Vector<Object>> list = new Vector<Vector<Object>>();  // 회원 정보를 담을 백터를 생성합니다.
            
            // 데이터베이스의 모든 회원 정보를 백터에 추가합니다.
            while (rs.next()) {
                Vector<Object> row = new Vector<Object>();
                row.add(rs.getString("name"));
                row.add(rs.getString("Id"));
                row.add(rs.getString("Pw"));
                row.add(rs.getString("gender"));
                row.add(rs.getString("birth"));                  
                row.add(rs.getString("ssn")); 
                row.add(rs.getString("zip"));
                row.add(rs.getString("address"));
                row.add(rs.getString("extraAddress"));
                row.add(rs.getString("email"));         
                row.add(rs.getString("phone"));
                row.add(rs.getString("profilePicPath"));
                row.add(rs.getString("introduction"));
                list.add(row);
            }
            model.setList(list);  
            model.fireTableDataChanged();           
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int regist(String name, String id,String pw,String gender,String zip, String address,String extraAddress,String email,String profilePicPath,String birth,String ssn,String phone,String introduction) {
        int result = 0;
        String sql = "Insert into User(name, Id, Pw, gender, zip,address,extraAddress,email,profilePicPath,birth,ssn,phone,introduction) Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, id);
            pstmt.setString(3, pw);
            pstmt.setString(4, gender);
            pstmt.setString(5, zip);
            pstmt.setString(6, address);
            pstmt.setString(7, extraAddress);   
            pstmt.setString(8, email);
            pstmt.setString(9, profilePicPath);
            pstmt.setString(10, birth);
            pstmt.setString(11, ssn);
            pstmt.setString(12, phone);
            pstmt.setString(13, introduction);
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // 회원 정보 수정 메서드
    public int edit(String name, String id, String pw,String gender, String zip, String address,String extraAddress,String email,String profilePicPath,String birth,String ssn,String phone,String introduction) {
        int result = 0;
        String sql = "Update User set name=?, Id=?, Pw=?,gender=?,zip=?, address=?,extraAddress=?,email=?,profilePicPath=?,birth=?,ssn=?,phone=?,introduction=? where Id=?";
        try {
        	PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql);
        	 pstmt.setString(1, name);
        	 pstmt.setString(2, id);
        	 pstmt.setString(3, pw);
             pstmt.setString(4, gender);
             pstmt.setString(5, zip);
             pstmt.setString(6, address);
             pstmt.setString(7, extraAddress);           
             pstmt.setString(8, email);
             pstmt.setString(9, profilePicPath);
             pstmt.setString(10, birth);
             pstmt.setString(11, ssn);
             pstmt.setString(12, phone);
             pstmt.setString(13, introduction);
             pstmt.setString(14, id); // where 절에 사용될 id 파라미터를 추가합니다.
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 회원 삭제 메서드
    public int delete() {
        int result = 0;
        String sql = "Delete from User where Id=?";
        try {
        	PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql);
            pstmt.setString(1, (String)memberTable.getValueAt(memberTable.getSelectedRow(), 1));
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public void setzipNoField(String address) {
        try {
           String zipNo = address.substring(6, 11);
           zipField.setText(zipNo); 
        } catch (Exception e) {
           e.printStackTrace();
        }
     }

     public void setAddressField(String address) {
        try {
           String inputAddress = address.substring(16,address.indexOf(")")+1).replaceAll("\\s","");//, address.indexOf("("));
           addressField.setText(inputAddress); 
        } catch (Exception e) {
           e.printStackTrace();
        }
     }
    public static void main(String[] args) {
    	try {
            // Client 객체 생성
            Agent toServer = new Client();  // 기본 생성자를 사용하여 Client 객체 생성

            // AdminWindow 생성
            new AdminWindow(toServer);
        } catch (Exception e) {
            // Client 객체 생성에 실패했을 경우의 예외 처리
            e.printStackTrace();
        }
    }
}