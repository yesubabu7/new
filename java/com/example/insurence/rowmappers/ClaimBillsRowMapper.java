package com.example.insurence.rowmappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.example.insurence.models.ClaimBills;

public class ClaimBillsRowMapper implements RowMapper<ClaimBills> {

	public ClaimBills mapRow(ResultSet rs, int rowNum) throws SQLException {
		ClaimBills claimBills = new ClaimBills();
		claimBills.setClam_id(rs.getInt("clam_id"));
		claimBills.setClbl_billindex(rs.getInt("clbl_bill_index"));
		claimBills.setClbl_document_title(rs.getString("clbl_document_title"));
		claimBills.setClbl_document_path(rs.getString("clbl_document_path"));
		claimBills.setClbl_claim_amount(rs.getDouble("clbl_claim_amount"));
		claimBills.setClbl_processed_amount(rs.getDouble("clbl_processed_amount"));
		claimBills.setClbl_processed_Date(rs.getString("clbl_processed_Date"));
		claimBills.setClbl_processed_by(rs.getInt("clbl_processed_by"));
		claimBills.setClbl_remarks(rs.getString("clbl_remarks"));
		claimBills.setClbl_status(rs.getString("clbl_status"));

		return claimBills;
	}
}
