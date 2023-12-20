package me.byungjin.game.gui.item;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import me.byungjin.manager.SystemManager;
import resource.ResourceLoader;

public class ChatItem extends JPanel {
    private byte[] imageData; // 이미지 데이터
    private boolean isMine; // 송신자 여부
    private Color textColor;
    private ImageIcon imageIcon;
    //private byte[] photoData;// 사진 데이터
    
    public ChatItem(byte[] imageData, boolean isMine, Color bk, Color textColor) {
    	this.imageData = imageData;
        this.isMine = isMine;
        this.textColor = textColor;

        setBackground(bk);
        setLayout(new BorderLayout());
        JLabel jTime = new JLabel(SystemManager.getTime());
        jTime.setFont(ResourceLoader.DEFAULT_FONT12);

        ImageIcon imageIcon = new ImageIcon(imageData);
        JLabel innerLabel = new JLabel();
        innerLabel.setIcon(imageIcon);
        innerLabel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(8, 4, 8, 4))); // 세로 크기를 약간 늘림

        if (isMine) {
            jTime.setHorizontalAlignment(SwingConstants.RIGHT);
            innerLabel.setOpaque(true);
            innerLabel.setBackground(Color.WHITE);
            innerLabel.setHorizontalAlignment(SwingConstants.RIGHT); // 이미지를 오른쪽 정렬로 설정
        } else {
            innerLabel.setBackground(Color.WHITE);
            innerLabel.setHorizontalAlignment(SwingConstants.LEFT); // 이미지를 왼쪽 정렬로 설정
        }

        // 이미지 크기 조정
        int maxWidth = 100; // 최대 가로 크기 설정
        int maxHeight = 100; // 최대 세로 크기 설정
        int width = imageIcon.getIconWidth();
        int height = imageIcon.getIconHeight();

        // 현재 크기의 반으로 조정
        width = width / 2;
        height = height / 2;

        // 최대 크기를 넘지 않도록 조정
        width = Math.min(width, maxWidth);
        height = Math.min(height, maxHeight);

        Image scaledImage = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        innerLabel.setIcon(new ImageIcon(scaledImage));

        add(jTime, BorderLayout.NORTH);
        add(innerLabel, BorderLayout.CENTER);
        setSize(Math.max(innerLabel.getPreferredSize().width + 17, 77), height + 30); // 세로 크기를 이미지에 맞게 조정
    }
    
    public ChatItem(ImageIcon imageIcon, boolean isMine, Color bk, Color textColor) {
        this.imageIcon = imageIcon;
        this.isMine = isMine;
        this.textColor = textColor;

        setBackground(bk);
        setLayout(new BorderLayout());
        JLabel jTime = new JLabel(SystemManager.getTime());
        jTime.setFont(ResourceLoader.DEFAULT_FONT12);

        JLabel innerLabel = new JLabel();
        innerLabel.setIcon(imageIcon);
        innerLabel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        if (isMine) {
            jTime.setHorizontalAlignment(SwingConstants.RIGHT);
            innerLabel.setOpaque(true);
            innerLabel.setBackground(Color.WHITE);
        } else {
            innerLabel.setBackground(Color.WHITE);
        }

        add(jTime, BorderLayout.NORTH);
        add(innerLabel, BorderLayout.CENTER);
        setSize(Math.max(innerLabel.getPreferredSize().width + 17, 77), 50);
    }
    
    public ChatItem(String text, boolean isMine, Color bk, Color textColor) {
        this.isMine = isMine;
        this.textColor = textColor;

        setBackground(bk);
        setLayout(new BorderLayout());
        JLabel jTime = new JLabel(SystemManager.getTime());
        jTime.setFont(ResourceLoader.DEFAULT_FONT12);

        JLabel innerLabel = new JLabel(text);
        innerLabel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        innerLabel.setForeground(textColor);

        if (isMine) {
            jTime.setHorizontalAlignment(SwingConstants.RIGHT);
            innerLabel.setOpaque(true);
            innerLabel.setBackground(Color.WHITE);
            innerLabel.setHorizontalAlignment(SwingConstants.RIGHT); // 텍스트를 오른쪽 정렬로 설정
        } else {
            innerLabel.setBackground(Color.WHITE);
            innerLabel.setHorizontalAlignment(SwingConstants.LEFT); // 텍스트를 왼쪽 정렬로 설정
        }

        add(jTime, BorderLayout.NORTH);
        add(innerLabel, BorderLayout.CENTER);
        setSize(Math.max(innerLabel.getPreferredSize().width + 17, 77), 50);
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public boolean isMine() {
        return isMine;
    }
}
