package me.byungjin.game.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import me.byungjin.game.gui.item.ChatItem;
import me.byungjin.network.Agent;
import me.byungjin.network.PROMISE;
import me.byungjin.network.event.DataComeInEvent;

public class ChatInnerPanel extends JPanel {
    private Agent agentToHost;
    private JPanel input;
    private JTextArea input_area;
    private Vector<ChatItem> chats = new Vector<ChatItem>();
    private Color bkColor = new Color(240, 240, 240); // 채팅 배경색을 변경하고자 하는 색으로 설정
    private Color textColor = Color.BLACK; // 채팅 글씨 색상을 변경하고자 하는 색으로 설정
    private int chatY = 10;
    private boolean mute = false;
    private CustomScrollPane c_scroll;
    final short LIMITE = 20;
    private ImageIcon selectedEmojiIcon;
    private ChatPanel chatPanel; // ChatPanel 추가

    public ChatInnerPanel(Agent agentToHost) {
        this.agentToHost = agentToHost;

        setSize(500, 600); // 가로 길이 조정
        setBackground(Color.white);
        setLayout(new BorderLayout());
        JPanel inner = new JPanel();
        inner.setLayout(null);
        inner.setBackground(bkColor);

        input = new JPanel();
        input.setLayout(null);
        input.setBackground(Color.white);
        input.setPreferredSize(new Dimension(getWidth(), 130)); // 높이 조정

        JPanel input_right = new JPanel();
        input_right.setBounds(416, 8, 80, 130); // 가로 길이 조정
        input_right.setBackground(Color.white);
        input_right.setLayout(new BorderLayout());

        JPanel input_inner_right = new JPanel();
        input_inner_right.setBackground(Color.WHITE);
        input_inner_right.setLayout(new GridLayout(0, 1));

        JButton btn_send = new JButton("전송");
        btn_send.setFont(new Font("Default", Font.PLAIN, 12));
        btn_send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendChat();
            }
        });
        input_inner_right.add(btn_send);

        JLabel label_len = new JLabel("0/" + LIMITE, SwingConstants.CENTER);
        label_len.setFont(new Font("Default", Font.PLAIN, 12));
        input_inner_right.add(label_len);

        JButton colorButton = new JButton("색상");
        colorButton.setFont(new Font("Default", Font.PLAIN, 12));
        colorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(null, "Choose a color", Color.BLACK);
                if (newColor != null) {
                    textColor = newColor;
                    input_area.setForeground(textColor);
                }
            }
        });
        input_inner_right.add(colorButton);

        JButton bgColorButton = new JButton("배경색");
        bgColorButton.setFont(new Font("Default", Font.PLAIN, 12));
        bgColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(null, "Choose a color", Color.WHITE);
                if (newColor != null) {
                    bkColor = newColor;
                    inner.setBackground(bkColor);
                }
            }
        });
        input_inner_right.add(bgColorButton);

        JButton emojiButton = new JButton("이모티콘");
        emojiButton.setFont(new Font("Default", Font.PLAIN, 12));
        emojiButton.setBackground(btn_send.getBackground());
        emojiButton.setForeground(Color.BLACK);
        emojiButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        emojiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEmojiDialog();
            }
        });
        input_inner_right.add(emojiButton);

        JButton photoButton = new JButton("사진");
        photoButton.setFont(new Font("Default", Font.PLAIN, 12));
        photoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectPhoto();
            }
        });
        input_inner_right.add(photoButton);
        
        input_inner_right.setPreferredSize(new Dimension(80, 100)); // 세로 길이 조정

        input_right.add(input_inner_right, BorderLayout.NORTH);
        input.add(input_right);

        input_area = new JTextArea(4, 50);
        input_area.setBounds(8, 8, 400, 100); // 높이 조정
        input_area.setFont(new Font("Default", Font.PLAIN, 14));
        input_area.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        input_area.setForeground(textColor);

        input_area.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int len = input_area.getText().length();

                if (len > LIMITE)
                    label_len.setText("Over");
                else
                    label_len.setText(len + "/" + LIMITE);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendChat();
                    e.consume();
                }
            }
        });

        input.add(input_area);

        c_scroll = new CustomScrollPane(inner, new Color(240, 240, 240));

        add(c_scroll, BorderLayout.CENTER);
        add(input, BorderLayout.SOUTH);
        agentChatSetting();

        // ChatPanel 생성 및 추가
        chatPanel = new ChatPanel();
        add(chatPanel, BorderLayout.NORTH);
    }

    public void agentChatSetting() {
        if (this.agentToHost == null || !this.agentToHost.isRunning()) {
            return;
        }

        this.agentToHost.addChatComeInEvent(new DataComeInEvent() {
            @Override
            public void dispatch(Object source, String data) {
                if (data.startsWith(PROMISE.EMOJI.toString())) {
                    String base64ImageData = data.substring(6);
                    byte[] imageData = Base64.getDecoder().decode(base64ImageData);
                    assignChats(new ChatItem(imageData, false, bkColor, textColor));
                } else if (data.startsWith(PROMISE.PHOTO.toString())) { // 사진인 경우
                    String base64PhotoData = data.substring(6);
                    byte[] photoData = Base64.getDecoder().decode(base64PhotoData);
                    assignChats(new ChatItem(photoData, false, bkColor, textColor));
                } else {
                    assignChats(new ChatItem(data, false, bkColor, textColor));
                }
            }
        });
    }

    public void sendChat() {
        String text = input_area.getText().trim();

        if (text.isEmpty() || text.length() > LIMITE) {
            return;
        }

        if (text.equals("mute")) {
            mute = true;
        } else if (text.equals("unmute")) {
            mute = false;
            return;
        }

        if (mute) {
            return;
        }

        ChatItem item = new ChatItem(text, true, bkColor, textColor);
        assignChats(item);

        input_area.setText("");
        this.agentToHost.chat(text);
    }

    public void assignChats(ChatItem item) {
        if (mute) {
            return;
        }

        chats.add(item);

        if (item.isMine()) {
            item.setAlignmentX(Component.RIGHT_ALIGNMENT);
            item.setLocation(435 - item.getWidth(), chatY);
        } else {
            item.setAlignmentX(Component.LEFT_ALIGNMENT);
            item.setLocation(10, chatY);
        }

        chatY = chatY + item.getHeight() + 5;
        c_scroll.addInnerItem(item);
        c_scroll.setInnerSize(new Dimension(getWidth(), chatY));
        c_scroll.revalidate();
        c_scroll.repaint();
    }



    // 이모티콘 전송 메서드 
    public void sendEmoji(ImageIcon emojiIcon) {
        try {
            String iconPath = emojiIcon.getDescription();
            byte[] imageData = convertImageToByteArray(iconPath);

            ChatItem item = new ChatItem(imageData, true, bkColor, textColor);
            assignChats(item);

            input_area.setText("");
            this.agentToHost.chatEmoji(imageData);

            // ChatPanel에 이모티콘 표시
            chatPanel.displayEmoji(imageData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // 사진 전송 메서드
    public void sendPhoto(byte[] photoData) {
        ChatItem item = new ChatItem(photoData, true, bkColor, textColor);
        assignChats(item);

        input_area.setText("");
        this.agentToHost.chatPhoto(photoData);
    }
    
    private byte[] convertImageToByteArray(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }


    // 이모티콘 다이얼로그를 보여주는 메서드
    private void showEmojiDialog() {
        JDialog emojiDialog = new JDialog();
        emojiDialog.setTitle("이모티콘 선택");
        emojiDialog.setSize(300, 200);
        emojiDialog.setResizable(false);
        emojiDialog.setModal(true);

        JPanel emojiPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        for (int i = 1; i <= 8; i++) {
            String imagePath = "자신이 사용하는 이미지 경로로 변경";
            String imagePathWithNumber = imagePath + i + ".png";
            ImageIcon emojiIcon = new ImageIcon(imagePathWithNumber);
            Image image = emojiIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            ImageIcon scaledEmojiIcon = new ImageIcon(image);
            JButton emojiButton = new JButton(scaledEmojiIcon);
            emojiButton.setPreferredSize(new Dimension(50, 50));
            emojiButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendEmoji(emojiIcon);
                    emojiDialog.dispose();
                }
            });
            emojiPanel.add(emojiButton);
        }

        emojiDialog.add(emojiPanel);

        emojiDialog.setLocationRelativeTo(this);
        emojiDialog.setVisible(true);
    }
    private void selectPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                byte[] imageData = convertImageToByteArray(selectedFile.getAbsolutePath());
                ChatItem item = new ChatItem(imageData, true, bkColor, textColor);
                assignChats(item);
                input_area.setText("");
                this.agentToHost.chatPhoto(imageData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
