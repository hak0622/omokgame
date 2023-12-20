package resource;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;

import me.byungjin.db.DBConnection;
import me.byungjin.manager.ENVIRONMENT;
import me.byungjin.manager.SystemManager;

public class ResourceLoader {
	private static File env;
	
	/**
	 * �ý��� �⺻ ���� ��Ʈ
	 */
	static public Font DEFAULT_FONT;
	static public Font DEFAULT_FONT12;
	static public Font DEFAULT_FONT14;
	static public ImageIcon DEFAULT_USER;
	static public Font H_FONT;
	
	static public ImageIcon ICON_APP;
	static public ImageIcon ICON_MONITER;
	static public ImageIcon ICON_MONITER_SELECTED;
	static public ImageIcon ICON_LOG;
	static public ImageIcon ICON_LOG_SELECTED;
	static public ImageIcon ICON_SETTING;
	static public ImageIcon ICON_SETTING_SELECTED;	
	static public ImageIcon ICON_SWITCH_ON;
	static public ImageIcon ICON_SWITCH_OFF;
	static public ImageIcon ICON_WHITE_STONE;
	static public ImageIcon ICON_BLACK_STONE;
	static public ImageIcon ICON_BLACK_STONE_OP;
	static public ImageIcon ICON_WHITE_STONE_OP;
	
	static public String USER_IMAGE_PATH; //="game/default_user.png";

	static public void init() {
		readFont();
		readIcons();		
	}
	/**
	 * ��Ʈ ������ �о��
	 */
	static public void readFont() {					
		InputStream is = ResourceLoader.class.getResourceAsStream("fonts/NanumSquareRoundB.ttf");
		InputStream kis = ResourceLoader.class.getResourceAsStream("fonts/Hahmlet-Regular.ttf");
		try {
			DEFAULT_FONT = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(15f).deriveFont(Font.BOLD);
			DEFAULT_FONT12 =  DEFAULT_FONT.deriveFont(12f);
			DEFAULT_FONT14 = DEFAULT_FONT.deriveFont(14f);
			H_FONT = Font.createFont(Font.TRUETYPE_FONT, kis).deriveFont(15f).deriveFont(Font.PLAIN);
			SystemManager.message(ENVIRONMENT.RESOURCELOADER, "load Font");
		} catch (Exception e) {			
			SystemManager.catchException(ENVIRONMENT.RESOURCELOADER, e);
		}
	}
	
	static public void readDefaultUser(String id) {
		// DBConnection 객체를 생성합니다.
	    DBConnection dbConnection = new DBConnection();
	    // DB에서 사용자의 이미지 경로를 불러옵니다.
	    USER_IMAGE_PATH = dbConnection.getUserProfilePicPath(id);
		if (USER_IMAGE_PATH == null || USER_IMAGE_PATH.isEmpty()) {
	        USER_IMAGE_PATH = "game/default_user.png";
	    }
		File userImageFile = new File(USER_IMAGE_PATH);
		if (!userImageFile.exists() || !userImageFile.isFile()) {
	        USER_IMAGE_PATH = "game/default_user.png";
	    }
		// ImageIcon 객체를 생성합니다
		ImageIcon userImageIcon = new ImageIcon(USER_IMAGE_PATH);
		// 이미지 크기를 조절합니다. 여기서는 100x100으로 설정합니다.
	    Image image = userImageIcon.getImage();
	    Image resizedImage = image.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
	    // 조절된 크기의 이미지로 ImageIcon 객체를 다시 생성합니다.
	    DEFAULT_USER = new ImageIcon(resizedImage);
		//DEFAULT_USER = new ImageIcon(USER_IMAGE_PATH);	
		SystemManager.message(ENVIRONMENT.RESOURCELOADER, "load User Icon");
		dbConnection.close();
	}
	/**
	 * ������ ������ �о��
	 */
	static public void readIcons() {
		ICON_MONITER = new ImageIcon(Toolkit.getDefaultToolkit().createImage(ResourceLoader.class.getResource("icons/icon_monitor.png")).getScaledInstance(25, 25, Image.SCALE_SMOOTH));
		ICON_MONITER_SELECTED = new ImageIcon(Toolkit.getDefaultToolkit().createImage(ResourceLoader.class.getResource("icons/icon_monitor_full.png")).getScaledInstance(25, 25, Image.SCALE_SMOOTH));
		ICON_LOG = new ImageIcon(Toolkit.getDefaultToolkit().createImage(ResourceLoader.class.getResource("icons/icon_chat.png")).getScaledInstance(25, 25, Image.SCALE_SMOOTH));
		ICON_LOG_SELECTED = new ImageIcon(Toolkit.getDefaultToolkit().createImage(ResourceLoader.class.getResource("icons/icon_chat_full.png")).getScaledInstance(25, 25, Image.SCALE_SMOOTH));
		ICON_SETTING = new ImageIcon(Toolkit.getDefaultToolkit().createImage(ResourceLoader.class.getResource("icons/icon_settings.png")).getScaledInstance(25, 25, Image.SCALE_SMOOTH));
		ICON_SETTING_SELECTED = new ImageIcon(Toolkit.getDefaultToolkit().createImage(ResourceLoader.class.getResource("icons/icon_settings_full.png")).getScaledInstance(25, 25, Image.SCALE_SMOOTH));
		ICON_SWITCH_ON= new ImageIcon(Toolkit.getDefaultToolkit().createImage(ResourceLoader.class.getResource("icons/icon_switch_on.png")).getScaledInstance(25, 25, Image.SCALE_SMOOTH));
		ICON_SWITCH_OFF= new ImageIcon(Toolkit.getDefaultToolkit().createImage(ResourceLoader.class.getResource("icons/icon_switch_off.png")).getScaledInstance(25, 25, Image.SCALE_SMOOTH));
		ICON_APP= new ImageIcon(ResourceLoader.class.getResource("game/diceIcon.png"));
		SystemManager.message(ENVIRONMENT.RESOURCELOADER, "load Icons");
	}	
	static public void readOmokIcons() {
		ICON_WHITE_STONE = new ImageIcon(Toolkit.getDefaultToolkit().createImage(ResourceLoader.class.getResource("game/white.png")).getScaledInstance(35, 35, Image.SCALE_SMOOTH));
		ICON_BLACK_STONE = new ImageIcon(Toolkit.getDefaultToolkit().createImage(ResourceLoader.class.getResource("game/black.png")).getScaledInstance(35, 35, Image.SCALE_SMOOTH));
		ICON_BLACK_STONE_OP = new ImageIcon(Toolkit.getDefaultToolkit().createImage(ResourceLoader.class.getResource("game/black_op.png")).getScaledInstance(35, 35, Image.SCALE_SMOOTH));
		ICON_WHITE_STONE_OP = new ImageIcon(Toolkit.getDefaultToolkit().createImage(ResourceLoader.class.getResource("game/white_op.png")).getScaledInstance(35, 35, Image.SCALE_SMOOTH));
		SystemManager.message(ENVIRONMENT.RESOURCELOADER, "load Omok Icons");
	}
	static public Image readBoard() {
		SystemManager.message(ENVIRONMENT.RESOURCELOADER, "load Board");
		return new ImageIcon(ResourceLoader.class.getResource("game/baduk_board.png")).getImage();		
	}
	static public Image readBackground(String name) {
		SystemManager.message(ENVIRONMENT.RESOURCELOADER, "load Background : " + name);
		return new ImageIcon(ResourceLoader.class.getResource("background/"+ name)).getImage();
	}
	static public ImageIcon readIcon(String name) {
		SystemManager.message(ENVIRONMENT.RESOURCELOADER, "load Icon : " + name);
		return new ImageIcon(ResourceLoader.class.getResource("background/"+ name));
	}
	/**
	 * IP �� ȯ�� ���� ������ ����(������ ���� ��� �⺻ ���� ����)
	 * @param 
	 * @return
	 */
	static public boolean getEnv(boolean admin) {
		try {
			env = new File("env.txt");						
			if(env.exists()) {
				readEnv(admin);
				return true;
			}else {
				env.createNewFile();
				writeDefaultEnv(admin);
				return false;
			}
		}catch(Exception e) {
			SystemManager.catchException(ENVIRONMENT.RESOURCELOADER, e);
			return false;
		}		
	}
	/**
	 * �⺻ ȯ�� ���� ������ ����
	 * @param admin true�� ��� Admin�� ȯ�� ���� ���� ����
	 * @throws Exception
	 */
	static private void writeDefaultEnv(boolean admin) throws Exception{
		saveEnv(admin);
	}	
	/**
	 * ȯ�� ���� ������ ����  
	 * @param admin true�� ��� Admin ������ ����
	 * @throws Exception
	 */
	static private void readEnv(boolean admin) throws Exception {
		SystemManager.message(ENVIRONMENT.RESOURCELOADER, "read Env");
		BufferedReader reader = new BufferedReader(new FileReader(env));
		ENVIRONMENT.SERVER_IP = reader.readLine();		
		ENVIRONMENT.SERVER_IP = ENVIRONMENT.SERVER_IP.substring(ENVIRONMENT.SERVER_IP.indexOf("=") + 1);
		
		if(!admin) {
			reader.close();
			return;
		}
		
		ENVIRONMENT.DB_IP = reader.readLine();
		ENVIRONMENT.DB_IP = ENVIRONMENT.DB_IP.substring(ENVIRONMENT.DB_IP.indexOf("=") + 1);
		ENVIRONMENT.DB_PORT = reader.readLine();
		ENVIRONMENT.DB_PORT = ENVIRONMENT.DB_PORT.substring(ENVIRONMENT.DB_PORT.indexOf("=") + 1);
		ENVIRONMENT.DB_ID = reader.readLine();
		ENVIRONMENT.DB_ID = ENVIRONMENT.DB_ID.substring(ENVIRONMENT.DB_ID.indexOf("=") + 1);
		ENVIRONMENT.DB_PW = reader.readLine();		
		ENVIRONMENT.DB_PW = ENVIRONMENT.DB_PW.substring(ENVIRONMENT.DB_PW.indexOf("=") + 1);
		reader.close();
	}	
	/**
	 * ȯ�� ���� ���� ����
	 */
	static public void saveEnv(boolean admin) {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(env));
			writer.println("SERVER_IP=" + ENVIRONMENT.SERVER_IP);
			if(!admin) {				
				writer.close();
				return;
			}
			writer.println("DB_IP=" + ENVIRONMENT.DB_IP);
			writer.println("DB_PORT=" + ENVIRONMENT.DB_PORT);
			writer.println("DB_ID=" + ENVIRONMENT.DB_ID);
			writer.println("DB_PW=" + ENVIRONMENT.DB_PW);			
			writer.close();
			SystemManager.message(ENVIRONMENT.RESOURCELOADER, "save Env");
		}catch(Exception e) {
			SystemManager.catchException(ENVIRONMENT.RESOURCELOADER, e);
		}
	}
	/**
	 * ȿ���� ���
	 * @param file
	 */
	static public void playWav(String file) {
		try {						
			AudioInputStream ais = AudioSystem.getAudioInputStream(ResourceLoader.class.getResource(file));			
			Clip clip = AudioSystem.getClip();
			clip.stop();
			clip.open(ais);
			clip.start();			
		}catch(Exception e) {
			SystemManager.catchException(ENVIRONMENT.RESOURCELOADER, e);
		}
	}
}