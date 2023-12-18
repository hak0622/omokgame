# omokgame
소켓 통신을 이용한 오목 게임 및 채팅

##  1. 프로젝트 설명

• 이 프로젝트는 네트워크 프로그래밍의 이해와 실용적인 응용을 목표로 한 대학교 기말 프로젝트입니다.

• 이 프로젝트는 소켓통신을 이용하여 두 사용자가 오목 게임을 즐길 수 있도록 만들었습니다.

------------------------------------

##  2. 프로젝트 기능

• 실시간 통신 구축을 통한 1:1 대전 서비스 제공

• 사용자의 승패 정보 DB를 통하여 기록

• 실시간 사용자 간의 채팅 서비스 제공 

• DB 구축을 통해 회원가입 기능 제공 

• 관리자 모드 구축을 통한 사용자 정보를 수정, 등록, 삭제 기능 제공

• 사용자의 프로필 이미지 제공

• 사용자의 아이디 찾기와 비밀번호 찾기 기능 제공

• 관리자 모드에서 사용자 방 목록 조회 기능

• 채팅 글씨 및 배경 색상 변경 기능 제공

• 이모티콘 및 사진 전송 기능

------------------------------------

##  3. 개발 환경

• 윈도우 : Windows 11 home

• 윈도우 비트 : 64bit

• 사용언어 ; Java, MYSQL

• eclipse version : java version “1.8.0_381” 버전 17 

• Mysql version 8.0.34

• JDBC : Mysql-connector-j-8.0.33.jar

------------------------------------

##  4. Git Clone 사용 방법

 1. 위의 링크를 가지고 HTTPS를 클론한다.

 2. Eclipse의 메뉴에서 File을 선택하고 Import를 클릭한다.
  
 3. Import 대화 상자에서 Git 폴더를 클릭한다.
    
 4. Git 아래에서 Projects from Git을 선택한다.
    
 5. Git Repository -> URL(자동으로 설정) ->Branch 선택 -> 로컬 저장소 설정 -> 프로젝트 선택 -> 프로젝트 확인 -> 프로젝트 열기
     
6. 위와 같은 실행을 완료하면 해당 프로젝트가 Eclipse에서 열립니다.

------------------------------------

##  5. 프로젝트 실행 방법

1. ENVIRONMENT에서 자신의 IP와 DB 내용으로 입력한다.
```java
public class ENVIRONMENT {
   //About Server
	static public String SERVER_IP = "자신의 IP 주소";	
	//About DB
	public static final String DB_IP = "자신의 데이터베이스 IP"; // 예) "localhost"
	static public String DB_PORT = "자신의 데이터베이스 PORT ";  // 예) "3306"
	static public String DB_ID = "자신의 데이터베이스 ID"; // 예) "root"
	static public String DB_PW = "자신의 데이터베이스 PW"; 
}
```
2. DBConnection클래스에서 testdb 부분을 자신의 DB로 변경.
```java
public DBConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");				
			conn = DriverManager.getConnection("jdbc:mysql://" +
			ENVIRONMENT.DB_IP + ":" + 
			ENVIRONMENT.DB_PORT + "/testdb?validationQuery=\"SELECT 1\"", 
			ENVIRONMENT.DB_ID, ENVIRONMENT.DB_PW);
			smt = conn.createStatement();
			isConnect = true;
			SystemManager.message(ENVIRONMENT.DB, "Connection Success!!");
		}catch(Exception e) {
			SystemManager.catchException(ENVIRONMENT.DB, e);
		}
	}
```

3. AdminModel 클래스
```java
public AdminModel(){column.add("자신이 사용하는 DB 컬럼");}
```

4. ChatInnerPanel 클래스
```java
String imagePath = "자신이 사용하는 이미지 경로로 변경";
```
5. LoginPanel클래스에 관리자를 들어가기 위해 DB에 Admin테이블 생성 후 하나의 컬럼에 자신이 사용할 비밀번호를 만든다.

6. AdminModel 클래스
```java
public getListAll() {} 등등 자신이 사용하는 DB컬럼을 이용
```

7. Address 클래스
```java
urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=공공데이터 포털에서 받은 서비스 키 입력"); /*Service Key*/
```

------------------------------------

##  6. 공공 데이터 포털에서 우편번호 서비스 키 받는 방법

1. [공공데이터 포털](https://www.data.go.kr/)에 접속합니다.
2. '우정사업본부_우편번호 정보'를 검색합니다.
3. 검색 결과에서 '우정사업본부_우편번호 정보조회' 서비스를 찾아 클릭합니다.
4. 필요한 정보를 입력하고, 데이터를 다운로드 받습니다.
