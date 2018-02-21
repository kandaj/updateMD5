package main.com.ebi.ega;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author imssbora
 */

public class MainApp {
	public static ArrayList stableIds;
	public static TreeMap fileIndex;
	public static TreeMap fileIndex1;

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(DatasourceConfig.class);
		DataSource audit = context.getBean("audit",DataSource.class);
		DataSource erapro = context.getBean("erapro",DataSource.class);
		ReadFile readFile = context.getBean("readFile",ReadFile.class);
		stableIds = readFile.getFileStableIDs();
		fileIndex = getFileIndex(stableIds,audit,erapro);
		System.out.println(fileIndex);
	}

	public static TreeMap getFileIndex(ArrayList stableIds,DataSource audit, DataSource erapro) {
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
					EGA_File egaf=new EGA_File(stableID,rs.getString("file_name"),rs.getString("submitted_file_name"),rs.getString("staging_source"),"","",erapro);
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

		return fileIndex;

	}


}

