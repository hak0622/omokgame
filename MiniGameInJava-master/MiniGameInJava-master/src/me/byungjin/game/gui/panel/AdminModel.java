package me.byungjin.game.gui.panel;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class AdminModel extends AbstractTableModel{
	  Vector column = new Vector();// 컬럼정보를 가진 백터
	  Vector list = new Vector(); // 레코드를 담을 백터(사람들의 정보를 담을 백터 )세로백터

	  public AdminModel() {
	  column.add("이름");
	  column.add("아이디");
	  column.add("비밀번호");
	  column.add("성별");
	  column.add("생년월일");
	  column.add("주민번호");
	  column.add("우편번호");
	  column.add("주소");
	  column.add("상세주소");
	  column.add("이메일");
	  column.add("전화번호");
	  column.add("이미지경로");
	  column.add("소개");
	 }
	  // 컬럼명을 넣어준다.
	  public String getColumnName(int index) {
	   return String.valueOf(column.get(index));
	  }
	  
	  public void setList(Vector list) {
	   this.list = list;
	  }

	  @Override
	  public int getColumnCount() {
	   // 컬럼 사이즈 현재 add 11개했으니 11개.
	   
	   return column.size();
	  }

	  @Override
	  public int getRowCount() {
	   
	   return list.size(); // 레코드 사이즈..
	   
	  }

	  @Override
	  public Object getValueAt(int row, int col) {
	   Vector vec = (Vector) list.get(row);
	   return vec.get(col);
	  }
}