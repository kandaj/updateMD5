package com.ebi.ega;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;


public class GetMD5 {
    static ArrayList stableIds;
    static TreeMap getFileIndex(DataSource audit, DataSource erapro){
        stableIds = readInputFile();
        TreeMap fileIndex = new TreeMap();
        String query = "select * from audit_file where stable_id= ?";
        Connection conn = null;
        fileIndex=new TreeMap();
        for (int i = 0; i < stableIds.size(); i++) {
            String stableID  = (String) stableIds.get(i);
            try {
                conn = audit.getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1,  stableID);
                ResultSet rs = ps.executeQuery();
                while ( rs.next() )
                {
                    EGAFile egaf=new EGAFile(stableID,rs.getString("file_name"),rs.getString("staging_source"),"","",rs.getString("run_analysis_accession"),erapro);
                    fileIndex.put(stableID,egaf);
                }
                rs.close();
                ps.close();

            } catch (SQLException e) {
                throw new RuntimeException(e);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {}
                }
            }
        }
        return (TreeMap) fileIndex;
    }
    static ArrayList<String> readInputFile() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(System.getProperty("file")));

            String str=null;
            while((str = br.readLine()) != null){
                list.add(str);
            }
            br.close();

        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        } catch (IOException e) {
            System.err.println("Unable to read the file.");
        }
        return ( list );
    }
}

 class EGAFile {

     public String unencryptedMD5;
     public String encryptedMD5;
     public String fileSource;
     public String stableID;
     public String baseName;
     public String fileAccession;
     public String box;
     public DataSource erapro;

    public EGAFile(String stableID,String file_name, String box, String unencryptedMD5, String encryptedMD5, String fileAccession,DataSource erapro) throws IOException {
        this.stableID = stableID;
        this.baseName = this.getFileBaseName(file_name);
        this.box = box;
        this.fileSource = this.getFileSource(file_name);
        this.fileAccession = fileAccession;
        this.erapro = erapro;
        this.unencryptedMD5 = null;
        this.encryptedMD5 = null;
        getMD5values();

    }
    public String getFileSource(String file_name) {
        if(file_name.substring(file_name.length()-4).equals(".gpg")){
            file_name = file_name.substring(0, file_name.length() - 4);
        }

        if(file_name.substring(0, 3).equals("EGA") ) {
            String[] splitArray = file_name.split("/", 2);
            file_name = splitArray[splitArray.length-1];
        }
        return "/nfs/ega/public/box/"+ this.box+"/"+file_name;
    }

    public String getFileBaseName(String file_name){
        String baseNameArray[] = file_name.split("/");
        String baseName = "";
        if(baseNameArray != null && baseNameArray.length > 0) {
            baseName = baseNameArray[baseNameArray.length - 1];
            if(baseName.substring(baseName.length()-4).equals(".gpg")){
                baseName = baseName.substring(0, baseName.length() - 4);
            }
        }
        return baseName;
    }

     public void getMD5values() throws IOException {
         String unencryptFile = this.fileSource+".md5";
         try {
             this.unencryptedMD5 =  new String(Files.readAllBytes(Paths.get(unencryptFile)));
         } catch (NoSuchFileException e) {
             System.out.print(this.stableID+" "+unencryptFile+" not found will check in XML");
         }

         String encryptFile = this.fileSource+".gpg.md5";
         try {
             System.out.println(encryptFile);
             this.encryptedMD5 =  new String(Files.readAllBytes(Paths.get(encryptFile)));
         } catch (NoSuchFileException e) {
             System.out.print(" "+encryptFile+" not found will check in XML\n");
         }

         if ((this.unencryptedMD5 == null) || (this.unencryptedMD5 == null)){
             Connection conn = null;
             String query = null;

             if(this.fileAccession.substring(0, 4).equals("EGAR") ) {
                 query = "SELECT * from ERA.RUN an,EGA.VW_RUN_FILES_XML vrx  where an.ega_id=? and vrx.RUN_ID=an.run_id AND vrx.BASE_NAME=?";
             } else {
                 query = "SELECT * from ERA.ANALYSIS an,EGA.VW_ANALYSIS_FILE_XML vafx  where an.ega_id=? and vafx.ANALYSIS_ID=an.analysis_id AND vafx.BASE_NAME=?";
             }

             try {
                 conn = this.erapro.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query);
                 ps.setString(1,  this.fileAccession);
                 ps.setString(2,  this.baseName);
                 ResultSet rs = ps.executeQuery();
                 while ( rs.next() )
                 {
                    this.encryptedMD5 = rs.getString("CHECKSUM");
                    this.unencryptedMD5 = rs.getString("UNENCRYPTED_CHECKSUM");
                 }
                 rs.close();
                 ps.close();

             } catch (SQLException e) {
                 throw new RuntimeException(e);
             } finally {
                 if (conn != null) {
                     try {
                         conn.close();
                     } catch (SQLException e) {}
                 }
             }
         }

     }
}