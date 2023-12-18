# omokgame
소켓 통신을 이용한 오목 게임 및 채팅

##  1. 프로젝트 설명

• 이 프로젝트는 네트워크 프로그래밍의 이해와 실용적인 응용을 목표로 한 대학교 기말 프로젝트입니다.

• 이 프로젝트는 소켓통신을 이용하여 두 사용자가 오목 게임을 즐길 수 있도록 만들었습니다.

------------------------------------
##  2. 개발 환경

• 윈도우 : Windows 11 home

• 윈도우 비트 : 64bit

• 사용언어 ; Java, MYSQL

• eclipse version : java version “1.8.0_381” 버전 17 

• Mysql version 8.0.34

• JDBC : Mysql-connector-j-8.0.33.jar

------------------------------------

##  3. 깃클론 사용 방법

 1. 위의 링크를 가지고 HTTPS를 클론한다.

 2. Eclipse의 메뉴에서 File을 선택하고 Import를 클릭한다.
  
 3. Import 대화 상자에서 Git 폴더를 클릭한다.
    
 4. Git 아래에서 Projects from Git을 선택한다.
    
 5. Git Repository -> URL(자동으로 설정) ->Branch 선택 -> 로컬 저장소 설정 -> 프로젝트 선택 -> 프로젝트 확인 -> 프로젝트 열기
     
6. 위와 같은 실행을 완료하면 해당 프로젝트가 Eclipse에서 열립니다.

------------------------------------

##  4. 프로젝트 실행 방법

1. ENVIRONMENT에서 자신의 IP와 DB 내용으로 입력한다.
```java
public class ENVIRONMENT {
   //About Server
	static public String SERVER_IP = "자신의 IP 주소";	
	//About DB
	public static final String DB_IP = "자신의 데이터베이스 IP"; // 예) "localhost"
	static public String DB_PORT = "자신의 데이터베이스 ";
	static public String DB_ID = "자신의 데이터베이스 정보";
	static public String DB_PW = "자신의 데이터베이스 정보";
}
