package me.byungjin.db;

public class UserSchema {
	String id;
	String password;
	String name;
	String gender;
	String email;
	String zip;
	String address;
	String extraAddress;
	String profilePicPath;	
	String birth;
	String encryptedSsn; 
	String phone;
	String introduction;
	boolean isAdmin; 
	
	public UserSchema() { // 기본 생성자 추가
	}

	
	public UserSchema(String id, String pw,String name,String gender,String email,String zip,String address,String extraAddress,String profilePicPath,String birth,String encryptedSsn, String phone,String introduction) {
		this.id = id;
		this.password = pw;
		this.name=name;
		this.gender = gender; 
		this.email=email;
		this.zip = zip;
		this.address = address;
        this.extraAddress = extraAddress;
        this.profilePicPath = profilePicPath;   
        this.birth=birth;
        this.encryptedSsn = encryptedSsn;
        this.phone=phone;
        this.introduction=introduction;
	}
	
	public String getId() {
		return this.id;
	}
	public void setId(String id) { 
		this.id=id;       
	}
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password=password;
	}	
	public String getName() {
		return name;
	}	
	public void setName(String name) {
		this.name=name;
	}	
	public String getGender() { 
        return gender;
    }
    public void setGender(String gender) { 
        this.gender = gender;
    }
	public String getEmail() {
		return email;
	}	
	public void setEmail(String email) {
		this.email=email;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) { 
        this.zip = zip;
    }
	public String getAddress() {
        return address;
    }
	public void setAddress(String address) {
		this.address =address;
	}
    public String getExtraAddress() {
        return extraAddress;
    }
    public void setExtraAddress(String extraAddress ) {
    	this.extraAddress=extraAddress;
    }
	public String getProfilePicPath(){
		return profilePicPath;
	}
	public void setProfilePicPath(String profilePicPath){
        this.profilePicPath = profilePicPath;
    }	
	public String getBirth(){
		return birth;
	}
	public void setBirth(String birth){
        this.birth = birth;
	}
	public String getEncryptedSsn() {
		return encryptedSsn;
	}
	public void setEncryptedSsn(String encryptedSsn) {
		this.encryptedSsn = encryptedSsn;
	}
	public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getIntroduction() {
    	return introduction;
    }
    public void setIntroduction(String introduction) {
    	this.introduction = introduction;
    }
    public boolean getIsAdmin() { // 관리자 여부를 가져오는 메소드 추가
		return this.isAdmin;
	}
	
	public void setAdmin(boolean isAdmin) { // 관리자 여부를 설정하는 메소드 추가
		this.isAdmin = isAdmin;
	}
}
