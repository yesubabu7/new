package com.example.insurence.rowmappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.example.insurence.models.CustomerData;

public class CustomerDataRowMapper implements RowMapper<CustomerData> {

	@Override
	public CustomerData mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		CustomerData customer = new CustomerData();
		customer.setCust_id(resultSet.getLong("cust_id"));
		customer.setCust_fname(resultSet.getString("cust_fname"));
		customer.setCust_lname(resultSet.getString("cust_lname"));
		customer.setCust_dob(resultSet.getDate("cust_dob"));
		customer.setCust_address(resultSet.getString("cust_address"));
		customer.setCust_gender(resultSet.getString("cust_gender").charAt(0));
		customer.setCust_cdate(resultSet.getDate("cust_cdate"));
		customer.setCust_user_id(resultSet.getInt("cust_user_id"));
		customer.setCust_aadhar(resultSet.getLong("cust_aadhar"));
		customer.setCust_status(resultSet.getString("cust_status"));
		customer.setCust_luudate(resultSet.getDate("cust_luudate"));
		customer.setCust_luuser(resultSet.getInt("cust_luuser"));

		return customer;
	}
}
