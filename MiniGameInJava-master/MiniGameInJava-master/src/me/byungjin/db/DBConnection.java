package me.byungjin.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import me.byungjin.game.GameKind;
import me.byungjin.manager.ENVIRONMENT;
import me.byungjin.manager.SystemManager;

public class DBConnection {
	public boolean isConnect = false;
	private Connection conn;
	private Statement smt;
	private ResultSet rs;
	private PreparedStatement pstm;
	
	/**
	 * DB占쏙옙 占쏙옙占쏙옙
	 * @throws Exception
	 */
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
	/**
	 * id占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쌍댐옙占쏙옙 확占쏙옙占쏙옙
	 * @param id
	 * @return
	 */
	public boolean isUser(String id) {
		String sql = "SELECT COUNT(*) as cnt From User Where id='" + id + "'";
		try {
			rs = smt.executeQuery(sql);			
			if(rs.next() && rs.getInt(1) > 0) {
				SystemManager.message(ENVIRONMENT.DB, "check User(" + id +")");
				return true;
			}
			return false;
		} catch (SQLException e) {
			SystemManager.catchException(ENVIRONMENT.DB, e);			
		}		
		return false;		
	}
	/**	
	 * @param id
	 * @param password
	 * @return
	 */	
	public boolean isAdmin(String pw) {
        String sql = "SELECT COUNT(*) as cnt From Admin Where pw='" + pw + "'";
        try {
            rs = smt.executeQuery(sql);            
            if(rs.next() && rs.getInt(1) > 0) {
                SystemManager.message(ENVIRONMENT.DB, "check Admin(" + pw +")");
                return true;
            }
            return false;
        } catch (SQLException e) {
            SystemManager.catchException(ENVIRONMENT.DB, e);            
        }       
        return false;      
    }
	
	public boolean confirmUser(String id, String pw) {
	    String sql = "SELECT COUNT(*) as cnt From User Where id='" + id + "'AND Pw='"+pw+ "'";
	    try {
	        rs = smt.executeQuery(sql);        
	        if(rs.next() && rs.getInt(1) > 0) {            
	            SystemManager.message(ENVIRONMENT.DB, "login User(" + id + ", " + pw + ")");
	            return true; // 사용자가 존재하므로 true 반환
	        }           
	    } catch (SQLException e) {
	        SystemManager.catchException(ENVIRONMENT.DB, e);            
	    }       
	    return false; // 사용자가 존재하지 않으므로 false 반환
	}
	
	public boolean confirmAdminPassword(String inputPassword) {
	    String sql = "SELECT pw FROM admin";
	    try {
	        PreparedStatement pstmt = conn.prepareStatement(sql);
	        ResultSet rs = pstmt.executeQuery();
	    
	        if (rs.next()) {
	            String adminPassword = rs.getString("pw");
	            return adminPassword.equals(inputPassword);
	        } else {
	            return false;
	        }
	    } catch (SQLException e) {
	        SystemManager.catchException(ENVIRONMENT.DB, e);
	    }
	    return false;
	}
	
	public String getUserProfilePicPath(String id) {
	    String sql = "SELECT profilePicPath FROM User WHERE id=?";
	    try {
	        pstm = conn.prepareStatement(sql);
	        pstm.setString(1, id);
	        rs = pstm.executeQuery();
	        if (rs.next()) {
	            return rs.getString("profilePicPath");
	        }
	    } catch (SQLException e) {
	        SystemManager.catchException(ENVIRONMENT.DB, e);
	    }
	    return null;
	}

	/**
	 * id占쏙옙 password占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 DB占쏙옙 占쏙옙絿占신�
	 * @param id
	 * @param password
	 * @return
	 */
	public boolean registerUser(String id, String password,String name,String gender, String email,String zip,String address, String extraAddress,String profilePicPath,String birth,String ssn,String phone,String introduction) {
		String sql = "INSERT INTO User VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, id);
			pstm.setString(2, password);
			pstm.setString(3, name); 
			pstm.setString(4, gender);
		    pstm.setString(5, email);
		    pstm.setString(6, zip);
		    pstm.setString(7, address);
		    pstm.setString(8, extraAddress);
	        pstm.setString(9, profilePicPath);	
	        pstm.setString(10, birth);	
	        pstm.setString(11, ssn);
	        pstm.setString(12, phone);
	        pstm.setString(13, introduction);
			if(pstm.executeUpdate() > 0) {
				SystemManager.message(ENVIRONMENT.DB, "new User(" + id + "," + password + ","+ name+","+gender+","+ email+","+ zip+","+address+"," +extraAddress+","+profilePicPath+","+birth+","+phone+")");
				return true;
			}
		} catch (SQLException e) {
			SystemManager.catchException(ENVIRONMENT.DB, e);			
		}		
		return false;
	}
	
	
	public UserSchema getUserById(String name, String email, String birth) {
	    UserSchema user = null;
	    String sql = "SELECT * FROM User WHERE name = ? AND email = ? AND birth = ?";

	    try {
	        pstm = conn.prepareStatement(sql);
	        pstm.setString(1, name);
	        pstm.setString(2, email);
	        pstm.setString(3, birth);
	        ResultSet rs = pstm.executeQuery();

	        if (rs.next()) {
	            user = new UserSchema(rs.getString("Id"), rs.getString("Pw"), rs.getString("name"), rs.getString("gender"), rs.getString("email"), rs.getString("zip"), rs.getString("address"), rs.getString("extraAddress"), rs.getString("profilePicPath"), rs.getString("birth"), rs.getString("ssn"),rs.getString("phone"),rs.getString("introduction"));
	        }

	        rs.close();	       
	    } catch (SQLException e) {
	        SystemManager.catchException(ENVIRONMENT.DB, e);
	    } finally {
	        if (pstm != null) {
	            try {
	                pstm.close();
	            } catch (SQLException e) {
	                e.printStackTrace(); // 혹은 로깅 등의 작업 수행
	            }
	        }
	    }
	    return user;
	}
	public UserSchema getUserByPw(String name, String id, String email, String birth,String ssn) {
	    UserSchema user = null;
	    String sql = "SELECT * FROM User WHERE name = ? AND id =? AND email = ? AND birth = ? AND ssn = ? ";

	    try {
	        pstm = conn.prepareStatement(sql);
	        pstm.setString(1, name);
	        pstm.setString(2, id);
	        pstm.setString(3, email);
	        pstm.setString(4, birth);
	        pstm.setString(5, ssn);
	        ResultSet rs = pstm.executeQuery();

	        if (rs.next()) {
	            user = new UserSchema(rs.getString("Id"), rs.getString("Pw"), rs.getString("name"), rs.getString("gender"), rs.getString("email"), rs.getString("zip"), rs.getString("address"), rs.getString("extraAddress"), rs.getString("profilePicPath"), rs.getString("birth"), rs.getString("ssn"),rs.getString("phone"),rs.getString("introduction"));
	        }

	        rs.close();	       
	    } catch (SQLException e) {
	        SystemManager.catchException(ENVIRONMENT.DB, e);
	    } finally {
	        if (pstm != null) {
	            try {
	                pstm.close();
	            } catch (SQLException e) {
	                e.printStackTrace(); // 혹은 로깅 등의 작업 수행
	            }
	        }
	    }
	    return user;
	}
	
	public void updateUserPw(String id, String newPassword) {
        String sql = "UPDATE User SET Pw = ? WHERE id = ?";
        try {
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, newPassword);
            pstm.setString(2, id);
            pstm.executeUpdate();
        } catch (SQLException e) {
            SystemManager.catchException(ENVIRONMENT.DB, e);
        }
    }
	
	public String getUserPw(String id) {
        String sql = "SELECT Pw FROM User WHERE Id=?";
        try {
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, id);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getString("Pw");
            }
        } catch (SQLException e) {
            SystemManager.catchException(ENVIRONMENT.DB, e);
        }
        return null;
    }
	
	public String findUserId(String name,String email, String birth) {
	    UserSchema user = getUserById(name,email,birth);

	    if (user != null) {
	        return user.getId();
	    } else {
	        return null;
	    }
	}

	public String findUserPw(String name, String id,String email, String birth, String ssn) {
	    UserSchema user = getUserByPw(name,id,email,birth,ssn);

	    if (user != null) {
	        return user.getPassword();
	    } else {
	        return null;
	    }
	}
	
	/**
	 * 占싸깍옙 占쌜쇽옙, 占쏙옙占썩서占쏙옙 SystemManager占쏙옙 占쏙옙占쏙옙 호占쏙옙占싹몌옙 占싫듸옙!!
	 * @param tag
	 * @param str
	 * @param warning
	 */
	public void log(ENVIRONMENT tag, String str, boolean warning) {
		String sql = "INSERT INTO Log (Source, Content, Warning,Time) VALUES (?,?,?,?)";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, tag.toString());
			pstm.setString(2, str);
			pstm.setInt(3, warning ? 1 : 0);
			pstm.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
			int affectedRows = pstm.executeUpdate();
			if (affectedRows == 0) {
				throw new SQLException("Creating log failed, no rows affected.");
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	/**
	 * Rank 占쏙옙占쏙옙占쏙옙占쏙옙
	 * @return
	 */
	public Rank getRank(String id, GameKind kind) {
		Rank rank;
		try {
			rs = smt.executeQuery("SELECT * From user_rank Where id='" + id + "' AND Kind=" + kind.toValue());
			if(rs.next()) {
				rank = new Rank(id, kind, rs.getInt(3), rs.getInt(4));
			}else {
				rank = new Rank(id, kind);
			}
			return rank;
		} catch (SQLException e) {
			SystemManager.catchException(ENVIRONMENT.DB, e);			
		}
		return null;
	}
	/**
	 * 占쏙옙크 占쏙옙占쏙옙占쏙옙트
	 * @param rank 
	 */
	public void updateRank(Rank rank) {
		try {
			if((rank.getVictory() + rank.getLose()) == 1) {
				String sql = "INSERT INTO user_rank (Id, Kind, Victory, Lose) VALUES (?,?,?,?)";
				pstm = conn.prepareStatement(sql);
				pstm.setString(1, rank.getId());
				pstm.setInt(2, rank.getGameKind().toValue());				
				pstm.setInt(3, rank.getVictory());
				pstm.setInt(4, rank.getLose());
				pstm.executeUpdate();				
			}else {
				String sql = "UPDATE user_rank set Victory=?, Lose=? WHERE Id=? AND Kind=?";
				pstm = conn.prepareStatement(sql);
				pstm.setInt(1, rank.getVictory());
				pstm.setInt(2, rank.getLose());
				pstm.setString(3, rank.getId());
				pstm.setInt(4, rank.getGameKind().toValue());				
				pstm.executeUpdate();
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * DB占쏙옙 占쏙옙占쏙옙占� 占싸그몌옙 占쏙옙占쏙옙占쏙옙
	 * @return
	 */
	public ArrayList<LogSchema> getLog() {
		ArrayList<LogSchema> logs = new ArrayList<LogSchema>();
		try {
			rs = smt.executeQuery("SELECT * From Log");			
			while(rs.next())
				logs.add(new LogSchema(rs.getString(1),  rs.getString(2), rs.getBoolean(3), rs.getTime(4)));			
		} catch (SQLException e) {
			SystemManager.catchException(ENVIRONMENT.DB, e);			
		}
		return logs;		
	}
	
	public Connection getConnection() {
	        return this.conn;
	}
	 
	public void close() {
		isConnect = false;
		try {
			if(conn != null && !conn.isClosed())
                conn.close();            
            if(smt != null && !smt.isClosed())
            	smt.close();            
            if(rs != null && !rs.isClosed())
                rs.close();            
            SystemManager.message(ENVIRONMENT.DB, "Connection Close!!");            
		}catch(Exception e){			
			SystemManager.catchException(ENVIRONMENT.DB, e);
		}
	}
}
