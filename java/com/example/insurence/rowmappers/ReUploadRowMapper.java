package com.example.insurence.rowmappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.example.insurence.models.ReUpload;

public class ReUploadRowMapper implements RowMapper<ReUpload> {

	public ReUpload mapRow(ResultSet rs, int rowNum) throws SQLException {
		ReUpload reUpload = new ReUpload();

		reUpload.setUploadId(rs.getInt("uploadId"));
		reUpload.setClaimId(rs.getInt("claimId"));
		reUpload.setName(rs.getString("name"));
		reUpload.setType(rs.getString("type"));
		reUpload.setStatus(rs.getString("Status"));
		reUpload.setDescription(rs.getString("description"));

		return reUpload;
	}
}