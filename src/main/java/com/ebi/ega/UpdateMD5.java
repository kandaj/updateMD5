package com.ebi.ega;

import javax.management.Attribute;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UpdateMD5 {
    public void updateMD5values(TreeMap fileIndex, DataSource audit){
        fileIndex.forEach((key, value) -> {
            String FileStableID = (String) key;
            String UnencryptedMD5 = (String) ((EGAFile)value).unencryptedMD5;
            String encryptedMD5 = (String) ((EGAFile)value).encryptedMD5;
            String query = "select * from audit_file where stable_id= ?";
            Connection conn = null;
            try {
                conn = audit.getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1,  FileStableID);
                ResultSet rs = ps.executeQuery();
                rs.close();
                ps.close();

            } catch (SQLException e) {
                throw new RuntimeException(e);

            }
            System.out.println(FileStableID);
            System.out.println(UnencryptedMD5);
            System.out.println(encryptedMD5);

        });
    }
}
