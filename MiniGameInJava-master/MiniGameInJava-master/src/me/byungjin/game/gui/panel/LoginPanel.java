package me.byungjin.game.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

import me.byungjin.db.DBConnection;
import me.byungjin.db.UserSchema;
import me.byungjin.game.gui.ClientWindow;
import me.byungjin.game.gui.ConnectErrorDialog;
import me.byungjin.game.gui.item.PlaceHolderTextField;
import me.byungjin.manager.ENVIRONMENT;
import me.byungjin.manager.NetworkManager;
import me.byungjin.manager.SystemManager;
import me.byungjin.network.Agent;
import me.byungjin.network.PROMISE;
import me.byungjin.network.event.DataComeInEvent;
import resource.ResourceLoader;

public class LoginPanel extends ChildPanel implements Runnable, MouseListener,AddressReceiver {
    private Agent agentToServer;

    private PlaceHolderTextField idField;
    private JPasswordField pwField;
    private JCheckBox showPasswordCheckBox; 
    private PlaceHolderTextField nameField; 
    private PlaceHolderTextField emailField; 
    private JLabel messageLine;
    private JPanel content;
    private STATUS status = STATUS.READY;
    private UserSchema me;
    private JDialog registerDialog,checkDuplicateDialog;    
    private JTextField checkIdField;
    private static JTextField zipField; //�����ȣ �ʵ�
    private static JTextField addressField; // �ּ� �ʵ� �߰�
    private static JTextField extraAddressField; // ���ּ� �ʵ� �߰�
    private String profilePicPath;  // ����ڰ� ������ ������ ������ ��θ� ����
    private JLabel lblProfilePic;  // ������ ������ ǥ���� ��
    private JButton btnChoosePic;  // ������ ������ ��ư
    private JRadioButton maleButton; //���� ���� ���� ��ư
    private JRadioButton femaleButton; //���� ���� ���� ��ư
    private ButtonGroup genderGroup; //���� ���� ���� ��ư
    private JLabel passwordRulesLabel; // ȸ������ â ��й�ȣ ��Ģ ���̺�  
    private Image bk;
    private JTextField birthField;    // 생년월일 입력 필드
    private JPasswordField ssnField;  // 주민등록번호 뒷자리 입력 필드
    DBConnection dbConnection = new DBConnection();
    private JComboBox<String> phonePrefixBox;
    private JTextField phoneMidField;
    private JTextField phoneLastField;
    JComboBox<String> yearBox = new JComboBox<>();
    JComboBox<String> monthBox = new JComboBox<>();
    JComboBox<String> dayBox = new JComboBox<>();
    private JTextArea introTextArea;
    
    enum STATUS { UP, DOWN, RUNNING, READY};
        
    final int UP_POS = 270;
    final int DOWN_POS = 510;

    public LoginPanel(){    	
    	setBackground(new Color(225,239,250));
    	setLayout(null);
        setSize(400,650);        
        addMouseListener(this);            
        /**
         * Panel Setting
         */
        bk = ResourceLoader.readBackground("dice_77887.png");
                                        
        pwField = new JPasswordField(20);
        pwField.setEchoChar('*'); 
               
        showPasswordCheckBox = new JCheckBox("Show password");
        
        showPasswordCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {                   
                    pwField.setEchoChar((char) 0);
                } else {                    
                    pwField.setEchoChar('*');
                }
            }
        });
        
        content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setBounds(50, DOWN_POS, 300, 500);
        content.setLayout(new GridLayout(10, 1, 0, 15));
        content.setBorder(BorderFactory.createEmptyBorder(25,40,50,40));
        
        JLabel j_title = new JLabel("Online Mini Game");
        j_title.setOpaque(true);
        j_title.setBackground(Color.black);
        j_title.setForeground(Color.white);
        j_title.setHorizontalAlignment(SwingConstants.CENTER);
        j_title.setFont(ResourceLoader.H_FONT.deriveFont(20f));        
        content.add(j_title);
        
        idField = new PlaceHolderTextField(20, "Enter your Id...");    
        nameField = new PlaceHolderTextField(20, "Enter your name...");  
        emailField = new PlaceHolderTextField(20, "Enter your email...");
        
        content.add(idField);
        content.add(pwField);                   
        content.add(showPasswordCheckBox);
        
        JButton findIdButton = new JButton("ID 찾기");
        findIdButton.setContentAreaFilled(false);
        findIdButton.setBorderPainted(false);
        findIdButton.setFocusPainted(false);
        findIdButton.setOpaque(true);
        findIdButton.setFont(ResourceLoader.DEFAULT_FONT);
        findIdButton.setBackground(new Color(0xafbdc7)); 
              
        findIdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 새로운 JFrame 생성
                JFrame frame = new JFrame("ID 찾기");
                frame.setSize(300, 250);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setLocationRelativeTo(null);  // 화면 중앙에 창이 나타나도록 설정
                frame.setLayout(null);  // 레이아웃 매니저를 사용하지 않고, 컴포넌트의 위치와 크기를 직접 설정

                // 라벨과 텍스트 필드 생성 및 위치 설정
                JLabel nameLabel = new JLabel("이름: ");
                nameLabel.setBounds(30, 30, 80, 25);
                frame.add(nameLabel);
                JTextField nameField = new JTextField(20);
                nameField.setBounds(100, 30, 160, 25);
                frame.add(nameField);

                JLabel emailLabel = new JLabel("이메일: ");
                emailLabel.setBounds(30, 70, 80, 25);
                frame.add(emailLabel);
                JTextField emailField = new JTextField(20);
                emailField.setBounds(100, 70, 160, 25);
                frame.add(emailField);

                JLabel birthLabel = new JLabel("생년월일: ");
                birthLabel.setBounds(30, 110, 80, 25);
                frame.add(birthLabel);
                JTextField birthField = new JTextField(20);
                birthField.setBounds(100, 110, 160, 25);
                frame.add(birthField);

                // 확인 버튼 생성 및 액션 리스너 설정
                JButton confirmButton = new JButton("확인");
                confirmButton.setBounds(100, 170, 80, 25);
                confirmButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        String name = nameField.getText();
                        String email = emailField.getText();
                        String birth = birthField.getText();

                        String userId = dbConnection.findUserId(name, email, birth);
                        if (userId != null) {
                            JOptionPane.showMessageDialog(null, "찾은 아이디: " + userId);
                        } else {
                            JOptionPane.showMessageDialog(null, "해당하는 아이디를 찾을 수 없습니다.");
                        }
                        frame.dispose();  // 창 닫기
                    }
                });
                frame.add(confirmButton);

                frame.setVisible(true);  // 프레임을 보이게 설정
            }
        });
                  
        JButton findPwButton = new JButton("Pw 찾기");
        findPwButton.setContentAreaFilled(false);
        findPwButton.setBorderPainted(false);
        findPwButton.setFocusPainted(false);
        findPwButton.setOpaque(true);
        findPwButton.setFont(ResourceLoader.DEFAULT_FONT);
        findPwButton.setBackground(new Color(0xafbdc7));

        findPwButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 새로운 JFrame 생성
                JFrame frame = new JFrame("Pw 찾기");
                frame.setSize(350, 350);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setLocationRelativeTo(null);  // 화면 중앙에 창이 나타나도록 설정
                frame.setLayout(null);  // 레이아웃 매니저를 사용하지 않고, 컴포넌트의 위치와 크기를 직접 설정

                // 라벨과 텍스트 필드 생성 및 위치 설정
                JLabel nameLabel = new JLabel("이름: ");
                nameLabel.setBounds(30, 30, 80, 25);
                frame.add(nameLabel);
                JTextField nameField = new JTextField(20);
                nameField.setBounds(130, 30, 130, 25);
                frame.add(nameField);

                JLabel idLabel = new JLabel("아이디: ");
                idLabel.setBounds(30, 70, 80, 25);
                frame.add(idLabel);
                JTextField idField = new JTextField(20);
                idField.setBounds(130, 70, 130, 25);
                frame.add(idField);

                JLabel emailLabel = new JLabel("이메일: ");
                emailLabel.setBounds(30, 110, 80, 25);
                frame.add(emailLabel);
                JTextField emailField = new JTextField(20);
                emailField.setBounds(130, 110, 130, 25);
                frame.add(emailField);

                JLabel birthLabel = new JLabel("생년월일: ");
                birthLabel.setBounds(30, 150, 80, 25);
                frame.add(birthLabel);
                JTextField birthField = new JTextField(20);
                birthField.setBounds(130, 150, 130, 25);
                frame.add(birthField);


                JLabel ssnLabel = new JLabel("주민등록번호: ");
                ssnLabel.setBounds(30, 190, 110, 25); // 가로 길이를 110으로 늘립니다.
                frame.add(ssnLabel);
                JPasswordField ssnField = new JPasswordField(20);
                ssnField.setBounds(130, 190, 130, 25);
                frame.add(ssnField);

                // 확인 버튼 생성 및 액션 리스너 설정
                JButton confirmButton = new JButton("확인");
                confirmButton.setBounds(120, 250, 80, 25);
                confirmButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        String name = nameField.getText();
                        String id = idField.getText();
                        String email = emailField.getText();
                        String birth = birthField.getText();
                        String ssn = new String(ssnField.getPassword());

                        String userPw = dbConnection.findUserPw(name, id, email, birth, ssn);
                        if (userPw != null) {
                            // 비밀번호 재설정 화면을 보여주는 코드
                            showResetPwFrame(id);
                        } else {
                            JOptionPane.showMessageDialog(null, "해당하는 사용자를 찾을 수 없습니다.");
                        }
                        frame.dispose();  // 창 닫기
                    }
                });
                frame.add(confirmButton);

                frame.setVisible(true);  // 프레임을 보이게 설정
            }
        });
               
        JPanel controlPanel = new JPanel();     
        controlPanel.setBackground(Color.white);
        controlPanel.setLayout(new GridLayout(1, 2, 5, 5));
        controlPanel.add(findIdButton);
        controlPanel.add(findPwButton);
        content.add(controlPanel);          
        content.add(findIdButton); 
        content.add(findPwButton);
        
        JButton loginBtn = new JButton("로그인");
        JButton registerBtn = new JButton("회원 가입");
        
        loginBtn.setContentAreaFilled(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.setFont(ResourceLoader.DEFAULT_FONT);
        loginBtn.setBackground(new Color(0xafbdc7));    
        
        ImageIcon settingsIcon = new ImageIcon(ResourceLoader.ICON_SETTING.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));  // 실제 아이콘 파일의 경로로 변경해주세요.
        
        JButton settingsButton = new JButton();
        settingsButton.setIcon(settingsIcon);
        settingsButton.setBounds(365, 10, 30, 30);     
        // 아이콘 버튼 패널에 추가
        add(settingsButton);
        
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {        
                JPasswordField passwordField = new JPasswordField();
                int dialogButton = JOptionPane.showConfirmDialog(null, passwordField, "관리자 로그인", JOptionPane.OK_CANCEL_OPTION);

                if (dialogButton == JOptionPane.OK_OPTION) {
                    String adminPassword = new String(passwordField.getPassword());
                    if (dbConnection.confirmAdminPassword(adminPassword)) { 
                        // 비밀번호가 올바른 경우, 관리자 로그인 데이터를 서버에 전송
                        agentToServer.send(PROMISE.ADMIN_LOGIN_SUC, "admin " + adminPassword); // 적절한 관리자 아이디를 사용해주세요.
                        
                        // 관리자 로그인 창 생성 및 표시
                        new AdminWindow(agentToServer).setVisible(true);
                    } else {
                        // 비밀번호가 틀린 경우, 경고 메시지 출력
                        agentToServer.send(PROMISE.ADMIN_LOGIN_FAIL, "admin " + adminPassword); // 적절한 관리자 아이디를 사용해주세요.
                        JOptionPane.showMessageDialog(null, "잘못된 비밀번호입니다", "경고", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        
        //로그인 버튼 누르면 행동
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	loginBtn.setBackground(new Color(0xafbdc7)); 
            	registerBtn.setBackground(Color.white);           	
                	login();              
            }
        });     
        controlPanel.add(loginBtn);
                   
        registerBtn.setContentAreaFilled(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setFocusPainted(false);
        registerBtn.setOpaque(true);
        registerBtn.setFont(ResourceLoader.DEFAULT_FONT);
        registerBtn.setBackground(Color.white);
                
        registerBtn.addActionListener(new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) {
            	// ȸ�� ���� ���̾�α� ����
                registerDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(content), "회원가입", false);
                registerDialog.setLayout(null);
                registerDialog.setSize(950,900);
                registerDialog.getContentPane().setBackground(new Color(225, 239, 250));     
                // '회원가입'이라는 큰 제목을 추가합니다.
                JLabel titleLabel = new JLabel("회원가입", SwingConstants.CENTER);
                titleLabel.setFont(new Font("SansSerif", Font.BOLD, 40));  // Font size and style
                titleLabel.setForeground(Color.BLACK); // 글씨 색을 흰색으로 설정
                titleLabel.setBackground(Color.decode("#B8D0FA"));
                titleLabel.setOpaque(true); // 배경색이 보이도록 설정
                titleLabel.setBounds(0, 0, 950, 50);  // Position and size     
                registerDialog.add(titleLabel);
               
                JTextField idField = new JTextField();
                idField.setBounds(180, 125, 140, 30);  // x 좌표를 20 줄이고, y 좌표를 15 줄임
                registerDialog.add(idField);
                              
                JButton checkDuplicateButton = new JButton("중복 확인"); 
                checkDuplicateButton.setBounds(330, 125, 100, 30); 
                checkDuplicateButton.addActionListener(new ActionListener() {
               	 @Override
               	 public void actionPerformed(ActionEvent e) {
               	    checkDuplicateDialog = new JDialog(registerDialog,"아이디 중복 확인", true);
               		checkDuplicateDialog.setLayout(new BoxLayout(checkDuplicateDialog.getContentPane(), BoxLayout.Y_AXIS));
               		
               	    checkIdField = new JTextField();
               		checkIdField.setBorder(BorderFactory.createTitledBorder("아이디 10-15자 사이 입력"));
               		checkDuplicateDialog.add(checkIdField);
               		
               		JButton confirmButton = new JButton("확인");
               	    confirmButton.addActionListener(new ActionListener() {
               	    	@Override
                        public void actionPerformed(ActionEvent e) {
                            String id = checkIdField.getText().trim();
                            if (id.length() < 10 || id.length() > 15) {
                                JOptionPane.showMessageDialog(checkDuplicateDialog, "올바르지 않는 길이(10~15)");
                                return;
                            }
                             //������ ���̵� �ߺ� �˻� ��û
                      		 agentToServer.send(PROMISE.CHECK_DUPLICATE, id); 
               	    	               	         
               	         checkDuplicateDialog.dispose();
               	    }
                });
               	checkDuplicateDialog.add(confirmButton);
               	
               	checkDuplicateDialog.setSize(300, 200);
                checkDuplicateDialog.setLocationRelativeTo(null);
                checkDuplicateDialog.setVisible(true);
               	}
             }); 
                registerDialog.add(checkDuplicateButton);               
                               
                JLabel idLbl = new JLabel("아이디");
                idLbl.setBounds(105, 125, 100, 30);  // x 좌표를 20 줄이고, y 좌표를 15 줄임
                registerDialog.add(idLbl);
                
                // ��й�ȣ �Է� �ʵ� ���� �� �߰�
                JPasswordField userPasswordField = new JPasswordField();
                userPasswordField.setBounds(180, 175, 250, 30);
                registerDialog.add(userPasswordField);
                // ��й�ȣ ���̺� �߰�
                JLabel pwdLbl = new JLabel("비밀번호");
                pwdLbl.setBounds(100, 175, 100, 30);
                registerDialog.add(pwdLbl);
                
                JLabel passwordStrengthLabel = new JLabel("");
                passwordStrengthLabel.setBounds(440, 175, 100, 30); 
                registerDialog.add(passwordStrengthLabel);
                
                passwordRulesLabel = new JLabel("<html><ul>" +
                        "<li>비밀번호는 10~15자의 영문,숫자,특수기호만 사용가능(공백입력불가)</li>" +                                                                     
                        "</ul></html>");
                passwordRulesLabel.setBounds(30, 195, 750, 60); 
             
                registerDialog.add(passwordRulesLabel);
                              
                JPasswordField passwordConfirmField = new JPasswordField();
                passwordConfirmField.setBounds(180, 250, 250, 30);
                registerDialog.add(passwordConfirmField);
                
                JLabel pwdcheckLbl = new JLabel("비밀번호 재확인");
                pwdcheckLbl.setBounds(80, 250, 100, 30);
                registerDialog.add(pwdcheckLbl);
                
                JLabel passwordMatchLabel = new JLabel("");
                passwordMatchLabel.setBounds(440, 250, 100, 30);  // 해당 레이블의 위치와 크기를 적절하게 설정합니다.
                registerDialog.add(passwordMatchLabel);
                                            
                JTextField userNameField = new JTextField();
                userNameField.setBounds(180, 295, 250, 30);
                registerDialog.add(userNameField);
                          
                // �̸� ���̺� �߰�
                JLabel nameLbl = new JLabel("이름");
                nameLbl.setBounds(105, 295, 100, 30);
                registerDialog.add(nameLbl);
                
                // ���� ���� ���� ��ư �ʱ�ȭ
                maleButton = new JRadioButton("남성");
                femaleButton = new JRadioButton("여성");
                maleButton.setBackground(new Color(225, 239, 250)); // 배경색 변경
                femaleButton.setBackground(new Color(225, 239, 250)); // 배경색 변경
                genderGroup = new ButtonGroup();
                genderGroup.add(maleButton);
                genderGroup.add(femaleButton);
                // ���� ���� ���� ��ư ���̺� �߰�
                JLabel genderLbl = new JLabel("성별");
                genderLbl.setBounds(105, 335, 100, 30);
                registerDialog.add(genderLbl);
                // ���� ��ư�� �гο� �߰�
                JPanel genderPanel = new JPanel(new GridLayout(1, 2));
                genderPanel.setBounds(180, 335, 250, 30);  // ��ġ ���� �ʿ�
                genderPanel.add(maleButton);
                genderPanel.add(femaleButton);
                registerDialog.add(genderPanel);
                                                    
                JTextField emailField1 = new JTextField(15);  
                JTextField emailField2 = new JTextField(10);
                JLabel atLabel = new JLabel("@");
                JLabel emailLbl = new JLabel("이메일");

                JComboBox<String> emailDomain = new JComboBox<>();
                emailDomain.addItem("직접입력");
                emailDomain.addItem("naver.com");
                emailDomain.addItem("google.com");
                emailDomain.addItem("nate.com");
                emailDomain.addItem("paran.com");
                emailDomain.addItem("hanmail.net");

                // 메일주소 초이스 박스 리스너 구현
                emailDomain.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent ie) {
                        if (emailDomain.getSelectedIndex() == 0) {
                            emailField2.setText("");
                        } else {
                            emailField2.setText((String) ie.getItem());
                        }
                    }
                });

                // 배치
                emailLbl.setBounds(100, 385, 100, 30);
                emailField1.setBounds(180, 385, 110, 30);
                atLabel.setBounds(295, 385, 15, 30);
                emailField2.setBounds(320, 385, 110, 30);
                emailDomain.setBounds(440, 385, 110, 30);

                // 회원 가입 창에 추가
                registerDialog.add(emailLbl);
                registerDialog.add(emailField1);
                registerDialog.add(atLabel);
                registerDialog.add(emailField2);
                registerDialog.add(emailDomain);
                
                // 생년월일 라벨 추가
                JLabel birthLbl = new JLabel("생년월일");
                birthLbl.setBounds(95, 435, 100, 30);
                registerDialog.add(birthLbl);

                // 생년 콤보박스 추가
                JComboBox<String> yearBox = new JComboBox<>();
                for(int i=2023; i>=1900; i--) {  // 2023년부터 1900년까지
                    yearBox.addItem(Integer.toString(i));
                }
                yearBox.setBounds(180, 435, 90, 30);
                registerDialog.add(yearBox);

                // 월 콤보박스 추가
                JComboBox<String> monthBox = new JComboBox<>();
                for(int i=1; i<=12; i++) {  // 1월부터 12월까지
                    monthBox.addItem(String.format("%02d", i));  // 한자리 수일 때 앞에 0을 붙여 두자리로 표시
                }
                monthBox.setBounds(270, 435, 80, 30);
                registerDialog.add(monthBox);

                // 일 콤보박스 추가
                JComboBox<String> dayBox = new JComboBox<>();
                for(int i=1; i<=31; i++) {  // 1일부터 31일까지
                    dayBox.addItem(String.format("%02d", i));  // 한자리 수일 때 앞에 0을 붙여 두자리로 표시
                }
                dayBox.setBounds(350, 435, 80, 30);
                registerDialog.add(dayBox);
                
                JLabel ssnLbl = new JLabel("주민번호 뒷자리");
                ssnLbl.setBounds(75, 485, 100, 30);
                registerDialog.add(ssnLbl);

                // 주민등록번호 뒷자리 입력 필드 추가
                JPasswordField ssnField = new JPasswordField();
                ssnField.setBounds(180, 485, 250, 30);
                registerDialog.add(ssnField);
                
                ((AbstractDocument)ssnField.getDocument()).setDocumentFilter(new DocumentFilter(){
                    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                        String string = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;

                        if (string.length() <= 7) {
                            super.replace(fb, offset, length, text, attrs); //To change body of generated methods, choose Tools | Templates.
                        }
                    }
                });
                
                // 전화번호 라벨 추가
                JLabel phoneLbl = new JLabel("전화번호");
                phoneLbl.setBounds(95, 535, 100, 30);  // 위치와 크기 설정
                registerDialog.add(phoneLbl);

                // 전화번호 앞부분 콤보박스 추가
                JComboBox<String> phonePrefixBox = new JComboBox<>();
                phonePrefixBox.addItem("010");
                phonePrefixBox.addItem("011");
                phonePrefixBox.addItem("016");
                phonePrefixBox.addItem("017");
                phonePrefixBox.addItem("018");
                phonePrefixBox.addItem("019");
                phonePrefixBox.setEditable(true);  // 콤보박스를 편집 가능하게 설정
                phonePrefixBox.setBounds(180, 535, 70, 30);  // 위치와 크기 설정
                registerDialog.add(phonePrefixBox);

                JLabel firstDashLbl = new JLabel("-");
                firstDashLbl.setBounds(260, 535, 10, 30);
                registerDialog.add(firstDashLbl);
                
                // 전화번호 중간 부분 입력 필드 추가
                JTextField phoneMidField = new JTextField();
                phoneMidField.setBounds(273, 535, 70, 30); // 위치와 크기 설정
                registerDialog.add(phoneMidField);

                JLabel secondDashLbl = new JLabel("-");
                secondDashLbl.setBounds(347, 535, 10, 30);
                registerDialog.add(secondDashLbl);
                
                // 전화번호 마지막 부분 입력 필드 추가
                JTextField phoneLastField = new JTextField();
                phoneLastField.setBounds(360, 535, 70, 30);  // 위치와 크기 설정
                registerDialog.add(phoneLastField);
                
                JLabel zipLbl = new JLabel("우편번호");
                zipLbl.setBounds(95, 585, 100, 30);
                registerDialog.add(zipLbl);              
                // �����ȣ �Է� �ʵ� ���� �� �߰�
                LoginPanel.zipField = new JTextField();
                LoginPanel.zipField.setBounds(180, 585, 120, 30);  // x ��ǥ�� �������� 50 �̵�
                registerDialog.add(LoginPanel.zipField);
                // �����ȣ �˻� ��ư �߰�
                JButton searchZipCodeButton = new JButton("우편번호검색");
                searchZipCodeButton.setBounds(310, 585, 120, 30);  // x ��ǥ�� �������� 50 �̵�
                registerDialog.add(searchZipCodeButton);
                
                JLabel addressLbl = new JLabel("주소");
                addressLbl.setBounds(105, 635, 100, 30);  // x ��ǥ�� �������� 50 �̵�
                registerDialog.add(addressLbl);
                
                LoginPanel.addressField = new JTextField();
                LoginPanel.addressField.setBounds(180, 635, 250, 30);  // x ��ǥ�� �������� 50 �̵�
                registerDialog.add(LoginPanel.addressField);
                  
                LoginPanel.extraAddressField = new PlaceHolderTextField(20,"직접 입력해주세요. . .");
                LoginPanel.extraAddressField.setBounds(180, 685, 250, 30);  // x ��ǥ�� �������� 50 �̵�
                registerDialog.add(LoginPanel.extraAddressField);
                // �� �ּ� ���̺� �߰�
                JLabel detailAddressLbl = new JLabel("상세 주소");
                detailAddressLbl.setBounds(95, 685, 100, 30);  // x ��ǥ�� �������� 50 �̵�
                registerDialog.add(detailAddressLbl);
                
                // ������ ������ ǥ���� ���̺� ����
                lblProfilePic = new JLabel();
                lblProfilePic.setBounds(600, 125, 250, 250); // ���̺��� ��ġ�� ũ�⸦ �����մϴ�. �����ϰ� �����ϼ���.
                lblProfilePic.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                //content.add(lblProfilePic);  // content�� �̹� ȸ������ â�� ����ϰ� �ִ� JPanel�̴ϱ� ���⿡ �߰��մϴ�.
                // �̹��� ���� ��ư ����
                btnChoosePic = new JButton("프로필 선택");
                btnChoosePic.setBounds(600, 385, 250, 30); // ��ư�� ��ġ�� ũ�⸦ �����մϴ�. �����ϰ� �����ϼ���
                //content.add(selectImageButton);

                // �̹��� ���� ��ư�� �̺�Ʈ ������ �߰�
                btnChoosePic.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser fileChooser = new JFileChooser();
                        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg");
                        fileChooser.setFileFilter(filter);
                        int returnValue = fileChooser.showOpenDialog(null);
                        if (returnValue == JFileChooser.APPROVE_OPTION) {
                        	 File selectedFile = fileChooser.getSelectedFile();
                             profilePicPath = selectedFile.getAbsolutePath().replace("\\", "/");
                             ResourceLoader.USER_IMAGE_PATH = profilePicPath; 
                             
                             File userImageFile = new File(ResourceLoader.USER_IMAGE_PATH);
                             if (!userImageFile.exists() || !userImageFile.isFile()) {
                            	 ResourceLoader.USER_IMAGE_PATH = "game/default_user.png";
                             }
                             // 이미지를 직접 로드합니다.
                             ResourceLoader.DEFAULT_USER = new ImageIcon(ResourceLoader.USER_IMAGE_PATH);
                             SystemManager.message(ENVIRONMENT.RESOURCELOADER, "join load User Icon");
                             try{
                            	BufferedImage bufferedImage = ImageIO.read(new File(profilePicPath));
                            	Image scaledImage = bufferedImage.getScaledInstance(lblProfilePic.getWidth(), lblProfilePic.getHeight(), Image.SCALE_DEFAULT);
                            	ImageIcon imageIcon = new ImageIcon(scaledImage);
                            	lblProfilePic.setIcon(imageIcon);
                             }catch(IOException ex){
                            	 ex.printStackTrace();
                            	}
                             }                          
                    }
                });               
                registerDialog.add(lblProfilePic);
                registerDialog.add(btnChoosePic);
                
                JLabel introLabel = new JLabel("소개");
                introLabel.setBounds(600, 435, 100, 30); 
                registerDialog.add(introLabel);
                
                JTextArea introTextArea = new JTextArea();
                introTextArea.setBounds(600, 465, 250, 250);
                introTextArea.setBackground(new Color(240, 240, 240));
                introTextArea.setLineWrap(true);
                registerDialog.add(introTextArea);
                
                JPanel idPanel = new JPanel();
                idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.X_AXIS));
                                                                           
                searchZipCodeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {        
                    	Addresswindow dialog = new Addresswindow(LoginPanel.this);
                        //Addresswindow dialog = new Addresswindow();
                        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        dialog.setVisible(true);
                    }
                });       
                                            
                // ��й�ȣ ��Ȯ�� �ʵ忡 DocumentListener �߰�
                userPasswordField.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        check();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        check();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        check();
                    }

                    private void check() {
                        String password = new String(userPasswordField.getPassword());
                        int strength = checkPasswordStrength(password);

                        String strengthString;
                        Color color;
                        if (strength <= 2) {
                            strengthString = "취약";
                            color = Color.RED;
                        } else if (strength == 3) {
                            strengthString = "보통";
                            color = Color.YELLOW;
                        } else {
                            strengthString = "강력";
                            color = Color.GREEN;
                        }

                        userPasswordField.setBorder(BorderFactory.createLineBorder(color));
                        passwordStrengthLabel.setText("보안 : " + strengthString);
                    }
                });
                
                passwordConfirmField.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        check();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        check();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        check();
                    }

                    private void check() {
                        String password = new String(userPasswordField.getPassword());
                        String confirm = new String(passwordConfirmField.getPassword());

                        if (password.equals(confirm)) {
                            passwordMatchLabel.setText("PW 일치 O");                         
                        } else {
                            passwordMatchLabel.setText("PW 일치 X");                          
                        }
                    }
                });

                
                // ��� ��ư ���� �� �߰�
                JButton cancelButton = new JButton("취소");
                cancelButton.setBounds(480, 750, 100, 30);
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // ��� ��ư�� Ŭ���ϸ� �α��� â���� �̵�
                        registerDialog.dispose();
                    }
                });
                registerDialog.add(cancelButton);
                
                JPanel confirmButtonPanel = new JPanel(new BorderLayout());                
                JButton confirmButton = new JButton("확인");
                confirmButton.setBounds(350, 750, 100, 30);           
                registerDialog.add(confirmButtonPanel);
                confirmButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String userName = userNameField.getText();
                        String id = idField.getText();
                        String userPassword = new String(userPasswordField.getPassword());
                        String passwordConfirm = new String(passwordConfirmField.getPassword());
                        String email = emailField1.getText() + "@" + emailField2.getText();                       
                        String zip = zipField.getText();
                        String address = addressField.getText(); // �ּ� �ʵ� ���� �����ɴϴ�.
                        String extraAddress = extraAddressField.getText(); // ���ּ� �ʵ� ���� �����ɴϴ�.
                        String gender = maleButton.isSelected() ? "남성" : "여성"; // ���õ� ���� ��������                         
                        String ssn = new String(ssnField.getPassword());
                        String birth = yearBox.getSelectedItem().toString() + monthBox.getSelectedItem().toString() + dayBox.getSelectedItem().toString();
                        String phone = phonePrefixBox.getSelectedItem().toString() + "-" + phoneMidField.getText() + "-" + phoneLastField.getText();
                        String introduction = introTextArea.getText();
                        
                        if (!userPassword.equals(passwordConfirm)) {
                            JOptionPane.showMessageDialog(registerDialog, "비밀번호와 비밀번호 재확인이 일치하지 않습니다.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        if(checkIdField == null || !id.equals(checkIdField.getText())) {
                            JOptionPane.showMessageDialog(registerDialog, "아이디 중복 확인을 하지 않았습니다.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                         // TODO: ȸ�� ���� ó��
                         me = new UserSchema(id, userPassword, userName, gender, email,zip,address,extraAddress,profilePicPath,birth, ssn,phone,introduction);                                                       
                         agentToServer.send(PROMISE.REGISTER, id + " " + userPassword + " " + userName + " " + gender+" "+email+" "+zip+" "+ address + " " + extraAddress + " " +profilePicPath+ " "+birth + " " + ssn+" "+phone+" "+introduction);                        
                         registerDialog.dispose();
                    }
                }); 
                
                registerDialog.add(confirmButton);            
            	registerDialog.setLocationRelativeTo(null);
            	registerDialog.setVisible(true);                
            }
        });

        controlPanel.add(registerBtn);
                
        messageLine = new JLabel("");
        messageLine.setFont(ResourceLoader.DEFAULT_FONT);
        messageLine.setForeground(Color.RED);
        content.add(messageLine);
        
        content.addMouseListener(new MouseListener() {
    		
    		@Override
    		public void mouseReleased(MouseEvent e) {
    		}
    		
    		@Override
    		public void mousePressed(MouseEvent e) {				
    		}
    		
    		@Override
    		public void mouseExited(MouseEvent e) {				
    		}
    		
    		@Override
    		public void mouseEntered(MouseEvent e) {
    		}
    		
    		@Override
    		public void mouseClicked(MouseEvent e) {
    			playAni(STATUS.UP);
    		}
    	});

        
        add(content);            
        playAni(STATUS.UP);            
    }
    
    public void register() {
        if(checkAgent()) return;
        
        String id = idField.getInnerText().trim();
        String pw = new String(pwField.getPassword()).trim();		        
        String name = nameField.getInnerText().trim(); 
        String email = emailField.getInnerText().trim(); 
        String zip = zipField.getText().trim(); // zip ���� �����ɴϴ�.
        String address = addressField.getText().trim(); // �ּ� �ʵ� ���� �����ɴϴ�.
        String extraAddress = extraAddressField.getText().trim(); // ���ּ� �ʵ� ���� �����ɴϴ�.
        String gender = maleButton.isSelected() ? "Male" : "Female"; // ���� �ʵ� ���� �����ɴϴ�.               
        String birth = yearBox.getSelectedItem().toString() + monthBox.getSelectedItem().toString() + dayBox.getSelectedItem().toString();
        String ssn = new String(ssnField.getPassword()).trim();                
        String phone = phonePrefixBox.getSelectedItem().toString() + "-" + phoneMidField.getText() + "-" + phoneLastField.getText();
        String introduction = introTextArea.getText().trim();
        
        if (profilePicPath == null) {
            profilePicPath = "/game/default_user.png"; // ���⼭ "default.png"�� ������Ʈ�� ��Ʈ ���丮�� �ִ� �⺻ �̹��� �����Դϴ�.
        }
        
        File profilePicFile = new File(profilePicPath); // ������ ���� ����
        if (!profilePicFile.exists() || !profilePicFile.isFile()) {
            message("프로필 사진 파일이 존재하지 않습니다.");
            return;
        }
        
        byte[] profilePicBytes = new byte[(int) profilePicFile.length()];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(profilePicFile);
            fis.read(profilePicBytes);
            fis.close();
        } catch (IOException e) {
        	message("프로필 사진을 읽는 중 오류가 발생했습니다..");
            e.printStackTrace();
            return;
        }
        
        if(id.length() < 10 || id.length() > 15 || pw.length() < 10 || pw.length() > 15) {
            message("아이디와 비밀번호의 길이는 10~15 사이여야 합니다.");
            return;
        };
        message("");
        me = new UserSchema(id, pw, name,gender, email, zip, address, extraAddress, profilePicPath,birth, ssn,phone,introduction);     
        agentToServer.send(PROMISE.REGISTER, id + " " + pw + " " + name +" "+gender +" "+ email + " " + zip + " " + address + " " + extraAddress + " " +profilePicPath+ " "+birth + " " + ssn + " " + phone+" "+introduction);
    }

    @Override
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	
    	g.drawImage(bk, 75, 0, 250,250, this);
    }
    private void showResetPwFrame(String id) {
        // 새로운 JFrame 생성
        JFrame frame = new JFrame("비밀번호 재설정");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);  // 화면 중앙에 창이 나타나도록 설정
        frame.setLayout(null);  // 레이아웃 매니저를 사용하지 않고, 컴포넌트의 위치와 크기를 직접 설정

        // 라벨과 텍스트 필드 생성 및 위치 설정
        JLabel pwLabel = new JLabel("새 비밀번호: ");
        pwLabel.setBounds(30, 30, 80, 25);
        frame.add(pwLabel);
        JPasswordField pwField = new JPasswordField(20);
        pwField.setBounds(130, 30, 120, 25);
        frame.add(pwField);

        JLabel pwConfirmLabel = new JLabel("비밀번호 재확인:");
        pwConfirmLabel.setBounds(15, 70, 105, 25);
        frame.add(pwConfirmLabel);
        JPasswordField pwConfirmField = new JPasswordField(20);
        pwConfirmField.setBounds(130, 70, 120, 25);
        frame.add(pwConfirmField);

        // 확인 버튼 생성 및 액션 리스너 설정
        JButton confirmButton = new JButton("확인");
        confirmButton.setBounds(100, 110, 80, 25);
        confirmButton.addActionListener(new ActionListener() {          
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String originalPw = dbConnection.getUserPw(id);  // DB에서 기존 비밀번호를 가져옵니다.
                String newPw = new String(pwField.getPassword());

                // 입력된 비밀번호가 공백인지 확인
                if (newPw.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "비밀번호는 공백일 수 없습니다.");
                    return;
                }
                
                // 입력된 두 비밀번호가 같은지 확인
                if (Arrays.equals(pwField.getPassword(), pwConfirmField.getPassword())) {
                    // 기존 비밀번호와 새 비밀번호가 동일한지 확인
                    if (originalPw.equals(newPw)) {
                        JOptionPane.showMessageDialog(null, "이전의 사용중인 비밀번호입니다.");
                    } else {
                        // DB에 비밀번호 업데이트
                        dbConnection.updateUserPw(id, newPw);
                        JOptionPane.showMessageDialog(null, "비밀번호가 성공적으로 변경되었습니다.");
                        frame.dispose();  // 창 닫기
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "입력된 두 비밀번호가 일치하지 않습니다.");
                }
            }
        });
        frame.add(confirmButton);

        frame.setVisible(true);  // 프레임을 보이게 설정
    }
    
    public void login(){    	    	
    	if(checkAgent()) return;
    	
        String id = idField.getInnerText().trim();
        String pw = new String(pwField.getPassword()).trim();
        String name = nameField.getInnerText().trim();
        String email = emailField.getInnerText().trim();
        
        if(id.length() < 10 || id.length() > 15 || pw.length() < 10 || pw.length() > 15) {
            message("올바르지 않는 길이(10~15)");
            return;
        };
        message("");
        me = new UserSchema(id, pw, name, email," ", " "," ",null," "," "," "," "," ");
        agentToServer.send(PROMISE.LOGIN, id + " " + pw+" "+name+" "+email);                             
    }

    public void initAgent() {
    	try {
    		this.agentToServer = NetworkManager.getClientToServer();
    		if(this.agentToServer == null) return;
    			
    		this.agentToServer.start();    	    	
    		
    		this.agentToServer.addOtherComeInEvent(new DataComeInEvent() {
    			@Override
    			public void dispatch(Object source, String data) {
    				StringTokenizer tokens = new StringTokenizer(data);
    				switch(PROMISE.valueOf(tokens.nextToken())){
    				case LOGIN_SUC:
    					loginSuc();
    					break;
    				case LOGIN_FAIL:
    					loginFail();
    					break;
    				case REGISTER_SUC:
    					registerSuc();
    					break;
    				case REGISTER_FAIL:
    					registerFail();
    					break;
    				case CHECK_DUPLICATE_SUC:
    					SwingUtilities.invokeLater(() -> {
    						JOptionPane.showMessageDialog(checkDuplicateDialog, "아이디 사용이 가능합니다.");
    	                    checkDuplicateDialog.dispose();
    	                });
    	                break;
    		        case CHECK_DUPLICATE_FAIL:
    		        	SwingUtilities.invokeLater(() -> { 
    	                    JOptionPane.showMessageDialog(checkDuplicateDialog, "아이디가 중복되었습니다.");
    	                    checkDuplicateDialog.dispose();
    	                });
    		        	break;
    				default:
    					break;
    				}
    			}
    		});    	
    		((ClientWindow)SwingUtilities.getWindowAncestor(this)).setAgentToServer(agentToServer);    	
    	} catch (Exception e) { 
    		SystemManager.catchException(ENVIRONMENT.CLIENT, e);
    		new ConnectErrorDialog();			
    	}    	
    }

    public void registerSuc() {
    	message("회원 가입 성공");
    }
    public void registerFail() {
    	message("회원 가입 실패");
    }

    public void loginSuc(){
    	setUser(me);
    	SystemManager.message(ENVIRONMENT.CLIENT, "Login Success, Open Client Window");
    	switchPanel(CHILDPANEL.LANDING);        
    }
    public void loginFail(){
        message("로그인 실패");
    }
    public void message(String msg){
        messageLine.setText(msg);
    }
    public boolean checkAgent() {
    	if(agentToServer == null || !agentToServer.isRunning()) {
    		message("서버와 연결이 올바르지 않습니다.");
    		return true;
    	}    		
    	return false;
    }

    public void playAni(STATUS s) {
    	if(status == STATUS.RUNNING) return;
    	    
    	status = s;
    	
    	Thread t = new Thread(this);
    	t.start();
    }

    @Override
    public void run() {
    	int curY;
    	if(status == STATUS.UP) {
    		status = STATUS.RUNNING;
    		while((curY = content.getY()) > UP_POS) {
    			content.setLocation(50, curY - 4);
    			try {
    				Thread.sleep(5);
    			} catch (InterruptedException e) { 
    				e.printStackTrace();
    			}
    		}
    		
    	}else {
    		status = STATUS.RUNNING;
    		while((curY = content.getY()) < DOWN_POS) {
    			content.setLocation(50, curY + 4);
    			try {
    				Thread.sleep(5);
    			} catch (InterruptedException e) { 
    				e.printStackTrace();
    			}
    		}			
    	}
    	status = STATUS.READY;
    }
    public String findUserId(String name, String email,String birth) {
        return dbConnection.findUserId(name, email,birth);
    }

    public String findUserPw(String name,String id,String email,String birth,String ssn) {
        return dbConnection.findUserPw(name,id,email,birth,ssn);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    	playAni(STATUS.DOWN);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {		
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {		
    }

    @Override
    public void init() {
    	initAgent(); 
    }

    int checkPasswordStrength(String password) {
    int strength=0;

    if (password.length()>=8) strength++;
    if (password.length() >= 8) strength++;
    if (password.matches(".*[0-9].*")) strength++;
    if (password.matches(".*[a-z].*")) strength++;
    if (password.matches(".*[A-Z].*")) strength++;
    if (password.matches(".*[~!@#$%^&*()].*")) strength++;

    return strength;
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
}