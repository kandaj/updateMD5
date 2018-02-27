package com.ebi.ega;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class UpdateMD5 {
    public void updateMD5values(HashMap fileIndex, DataSource audit) {

        try {
            Connection conn = audit.getConnection();
            PreparedStatement selectPS = conn.prepareStatement("select * from audit_md5 where file_stable_id=? and process_step=?");
            PreparedStatement updateQueriesPS = conn.prepareStatement("UPDATE audit_md5 set md5_checksum=? where process_step=? and file_stable_id=?");
            PreparedStatement insertQueriesPS = conn.prepareStatement("INSERT into audit_md5 (md5_checksum,process_step,file_stable_id) VALUES(?,?,?)");

            for (Object key : fileIndex.keySet()) {

                String fileStableID = (String) key;
                String unencryptedMD5 = (String) ((EGAFile) fileIndex.get(key)).unencryptedMD5;
                String encryptedMD5 = (String) ((EGAFile) fileIndex.get(key)).encryptedMD5;
                String unencryptedText = "Submitter unencrypted md5";
                String encryptedText = "Submitter encrypted md5";

                if(!unencryptedMD5.equals("") || !encryptedMD5.equals("")){
                    try {
                        selectPS.setString(1, fileStableID);
                        selectPS.setString(2, unencryptedText);
                        ResultSet rs = selectPS.executeQuery();
                        if (rs.next()) {
                            updateQueriesPS.setString(1, unencryptedMD5);
                            updateQueriesPS.setString(2, unencryptedText);
                            updateQueriesPS.setString(3, fileStableID);
                            updateQueriesPS.addBatch();
                        } else {
                            insertQueriesPS.setString(1, unencryptedMD5);
                            insertQueriesPS.setString(2, unencryptedText);
                            insertQueriesPS.setString(3, fileStableID);
                            insertQueriesPS.addBatch();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    try {
                        selectPS.setString(1, fileStableID);
                        selectPS.setString(2, encryptedText);
                        ResultSet rs = selectPS.executeQuery();
                        if (rs.next()) {
                            updateQueriesPS.setString(1, encryptedMD5);
                            updateQueriesPS.setString(2, encryptedText);
                            updateQueriesPS.setString(3, fileStableID);
                            updateQueriesPS.addBatch();
                        } else {
                            insertQueriesPS.setString(1, encryptedMD5);
                            insertQueriesPS.setString(2, encryptedText);
                            insertQueriesPS.setString(3, fileStableID);
                            insertQueriesPS.addBatch();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println(fileStableID+ " not updated");
                }

            }

            try {
                updateQueriesPS.executeBatch();
                insertQueriesPS.executeBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
