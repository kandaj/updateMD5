package com.ebi.ega;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.util.HashMap;

/**
 * @author imssbora
 */

public class MainApp {

	public static HashMap fileIndex;

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		DataSource audit = context.getBean("audit",DataSource.class);

		DataSource erapro = context.getBean("erapro",DataSource.class);

		GetMD5 getMD5 = context.getBean("getMD5", GetMD5.class);

		UpdateMD5 updateMD5 = context.getBean("updateMD5", UpdateMD5.class);

		fileIndex =  GetMD5.getFileIndex(audit,erapro);

		updateMD5.updateMD5values(fileIndex,audit);

		System.out.println(fileIndex);
	}
}

