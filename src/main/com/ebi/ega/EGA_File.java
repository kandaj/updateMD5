package main.com.ebi.ega;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;

public class EGA_File {

    private final String unencryptedMD5;
    private final String encryptedMD5;
    private final String fileSource;
    private final DataSource erapro;
    private final String stableID;


    String file_name,submitted_file_name,box;
    public EGA_File(String stableID,String file_name, String submitted_file_name, String box, String unencryptedMD5, String encryptedMD5, DataSource erapro) throws IOException {
        this.stableID = stableID;
        this.file_name = file_name;
        this.submitted_file_name = submitted_file_name;
        this.box = box;
        this.fileSource = getFileSource(file_name);
        this.unencryptedMD5 = this.getUnencryptedMD5();
        this.encryptedMD5 = this.getEncryptedMD5();
        this.erapro = erapro;

    }
    public String getFileSource(String file_name) {
        if(file_name.substring(file_name.length()-4).equals(".gpg")){
            file_name = file_name.substring(0, file_name.length() - 4);
        }

        if(file_name.substring(0, 4).equals("EGAZ") ) {
             String[] splitArray = file_name.split("/", 2);
             file_name = splitArray[splitArray.length-1];
        }
        return "/nfs/ega/public/box/"+ this.box+"/"+file_name;
    }


    public String getUnencryptedMD5() throws IOException {
        String file = this.fileSource+".md5";
        String content = "";
        try {
            content =  new String(Files.readAllBytes(Paths.get(file)));
        } catch (NoSuchFileException e) {
            System.out.print(this.stableID+" "+file+" not found");
        }

        return content;

    }
    public String getEncryptedMD5() throws IOException {
        String file = this.fileSource+".gpg.md5\n";
        String content = "";
        try {
            content =  new String(Files.readAllBytes(Paths.get(file)));
        } catch (NoSuchFileException e) {
            System.out.print(" "+file+" not found\n");
        }

        return content;
    }
}
