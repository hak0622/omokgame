package me.byungjin.game.gui.panel;

import javax.swing.*;
import java.awt.*;
import java.util.Base64;

public class ChatPanel extends JPanel {
    private JLabel emojiLabel;

    public ChatPanel() {
        emojiLabel = new JLabel();
        add(emojiLabel);
    }

    public void displayEmoji(String base64ImageData) {
        try {
            // Base64 디코딩하여 이미지 데이터로 변환
            byte[] imageData = Base64.getDecoder().decode(base64ImageData);

            // 이미지 생성
            ImageIcon icon = new ImageIcon(imageData);

            // 이미지 크기 조정
            Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);

            // 이미지를 JLabel에 표시
            emojiLabel.setIcon(new ImageIcon(scaledImage));

            // JLabel 크기 조정
            emojiLabel.setSize(50, 50);
            emojiLabel.setPreferredSize(new Dimension(50, 50));

            // JLabel 수신자 텍스트 가운데 정렬
            emojiLabel.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void displayEmoji(byte[] imageData) {
        try {
            // Base64 디코딩하여 이미지 데이터로 변환
            String base64ImageData = Base64.getEncoder().encodeToString(imageData);
            byte[] decodedImageData = Base64.getDecoder().decode(base64ImageData);

            // 이미지 생성
            ImageIcon icon = new ImageIcon(decodedImageData);

            // 이미지 크기 조정
            Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);

            // 이미지를 JLabel에 표시
            JLabel emojiLabel = new JLabel(new ImageIcon(scaledImage));

            // JLabel 크기 조정
            emojiLabel.setSize(50, 50);
            emojiLabel.setPreferredSize(new Dimension(50, 50));

            // JLabel 수신자 텍스트 가운데 정렬
            emojiLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // ChatPanel에 JLabel 추가
            add(emojiLabel);
            revalidate();
            repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
