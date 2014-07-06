package com.gditc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.gditc.utils.DBConnectionUtil;

public class FileDao {
	
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	
	/**
	 * 插入json文件的路径
	 * @param path
	 * @return
	 */
	public int addJsonPath(String path) {
		int val = 0;
		String sql = "";
		try{
			conn = DBConnectionUtil.getConnection();
			sql = "INSERT INTO tbl_json(path) VALUE(?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, path);
			
			val = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				DBConnectionUtil.close(conn, pstmt, rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return val;
	}

	/**
	 * 获取json文件路径
	 * @return
	 */
	public String getJsonPath() {
		String jsonPath = null;
		String sql = "";
		try{
			conn = DBConnectionUtil.getConnection();
			sql = "SELECT path FROM tbl_json";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()){
				jsonPath = rs.getString("path");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				DBConnectionUtil.close(conn, pstmt, rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jsonPath;
	}

	public int updateJsonPath(String jsonPath) throws Exception {
		int val = 0;
		String sql = "";
		try {
			conn = DBConnectionUtil.getConnection();
			sql = "UPDATE tbl_json SET path=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, jsonPath);
			
			val = pstmt.executeUpdate();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionUtil.close(conn, pstmt, rs);
		}
		return val;
	}
}
