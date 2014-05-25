package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class IPChanger {
	public static final String ADSL_TITLE = "宽带连接";
	public static final String ADSL_USER = "400000264339";
	public static final String ADSL_PASSWORD = "i6m5v7c5";
	

	/**
	 * 执行CMD命令,并返回String字符串
	 */
	public static String executeCmd(String strCmd) throws Exception {
		Process p = Runtime.getRuntime().exec("cmd /c " + strCmd);
		StringBuilder sbCmd = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line;
		while ((line = br.readLine()) != null) {
			sbCmd.append(line + "\n");
		}
		return  new String(sbCmd.toString().getBytes("ISO-8859-1"));
	}

	/**
	 * 连接ADSL
	 */
	public static boolean connAdsl(String adslTitle, String adslName,
			String adslPass) throws Exception {
		System.out.println("正在建立连接.");
		String adslCmd = "rasdial " + adslTitle + " " + adslName + " "
				+ adslPass;
		String tempCmd = executeCmd(adslCmd);
		// 判断是否连接成功
		if (tempCmd.indexOf("已连接") > 0) {
			System.out.println("已成功建立连接.");
			return true;
		} else {
			System.err.println(tempCmd);
			System.err.println("建立连接失败");
			return false;
		}
	}

	/**
	 * 断开ADSL
	 */
	public static boolean cutAdsl(String adslTitle) throws Exception {
		String cutAdsl = "rasdial " + adslTitle + " /disconnect";
		String result = executeCmd(cutAdsl);

		if (result.indexOf("没有连接") != -1) {
			System.err.println(adslTitle + "连接不存在!");
			return false;
		} else {
			System.out.println("连接已断开");
			return true;
		}
	}

	public static void main(String[] args) throws Exception {
		connAdsl(ADSL_TITLE, ADSL_USER, ADSL_PASSWORD);
		Thread.sleep(1000);
//		cutAdsl(ADSL_TITLE);
//		Thread.sleep(1000);
//		// 再连，分配一个新的IP
//		connAdsl(ADSL_TITLE, ADSL_USER, ADSL_PASSWORD);
	}
}
