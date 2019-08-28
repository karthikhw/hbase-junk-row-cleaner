package com.hbase.hwx;

import org.apache.commons.cli.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.regex.Pattern;


public class Util {

	private static Connection connection = null;
	private static Configuration conf = null;
	private static final String OPTZOOKEEPERSERVER = "zookeeper-server";
	private static final String OPTZOOKEEPERPORT = "zookeeper-port";
	private static final String OPTZOOKEEPERZNODE = "zookeeper-znode";
	private static final String OPTTABLENAME = "table-name";
	private static final String CMD = "cmd";
	private static TableName tName = null;


	public void setConf(CommandLine cmdline) {
		if (cmdline.hasOption("z")) {
			conf.set(HConstants.ZOOKEEPER_QUORUM, cmdline.getOptionValue(OPTZOOKEEPERSERVER));
		}
		if (cmdline.hasOption("p")) {
			conf.set(HConstants.ZOOKEEPER_CLIENT_PORT, cmdline.getOptionValue(OPTZOOKEEPERPORT));
		}
		if (cmdline.hasOption("n")) {
			conf.set(HConstants.ZOOKEEPER_ZNODE_PARENT, cmdline.getOptionValue(OPTZOOKEEPERZNODE));
		}
	}


	public static void usage(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(
				"java -cp `hbase classpath`:hbase-junk-1.2.jar: com.hbase.hwx.Util [--zookpeer-server <comma seperated list>] [--zookeper-znode <znode>] [--zookeeper-port <zk port>] --table-name <tablename>  --cmd <show|delete> &>/tmp/_junk.log\n",
				options);
		System.exit(-1);
	}

	public void deleteJunkRows(boolean isDelete) throws IOException{
		Pattern p = Pattern.compile("(?:[-_ .\\p{Digit}\\p{IsAlphabetic}]*)");
		String regex = "(?:[-_ .\\p{Digit}\\p{IsAlphabetic}]*)";
		Table table = connection.getTable(tName);
		Scan scan = new Scan();
		scan.setCaching(2000);
		ResultScanner resultScanner = table.getScanner(scan);
		long rKeyCount = 0;
		long junkKeyCount = 0l;
		Result result = null;
		while ((result = resultScanner.next())!=null) {
			byte[] row = result.getRow();
			String rkey = Bytes.toStringBinary(row);
			rKeyCount++;
			boolean valid = rkey.matches(regex);
			if (!valid) {
				junkKeyCount++;
				if(isDelete) {
                Delete delete = new Delete(row);
                table.delete(delete);
				} else {
					System.out.println(rkey);
				}
			}
		}
		System.out.println("--------------------------");
		System.out.println("Tablename: "+tName);
		String msg = isDelete?"delete-junk-keys":"list-junk-keys";
		System.out.println("Operation: "+msg);
		System.out.println("Row key scanned: "+rKeyCount);
		System.out.println("Junk key found: "+junkKeyCount);
		System.out.println("==========================");
	}


	public boolean isTableAvailable(TableName tname) throws IOException {
        return  connection.getAdmin().isTableAvailable(tName);
	}


	public static void main(String[] args) {
		Options options = new Options();
		try {
			Util status = new Util();
			CommandLineParser parser = new GnuParser();
			options.addOption("z", OPTZOOKEEPERSERVER, true, "comma seperated zookeeper servers");
			options.addOption("p", OPTZOOKEEPERPORT, true, "zookeeper port");
			options.addOption("n", OPTZOOKEEPERZNODE, true, "zookeeper znode");
			options.addOption("t", OPTTABLENAME, true, "table name");
			options.addOption("c", CMD, true, "<show|delete>");
			CommandLine line = parser.parse(options, args);
			if (!line.hasOption("t") || !line.hasOption("c")
					|| !(line.getOptionValue("c").equalsIgnoreCase("show")
					|| line.getOptionValue("c").equalsIgnoreCase("delete"))) {
				usage(options);
			}
			status.setConf(line);
			conf = HBaseConfiguration.create();
			status.setConf(line);
			connection = ConnectionFactory.createConnection(conf);
			tName = TableName.valueOf(line.getOptionValue("t"));
			boolean isTableAvailable = status.isTableAvailable(tName);
			if (!isTableAvailable) {
				System.out.println("Table "+tName+" is not available");
			}
			String cmd = line.getOptionValue("c");
			if (cmd.equalsIgnoreCase("delete")) {
				status.deleteJunkRows(true);
			} else {
				System.out.println("========Junk Keys=========");
				status.deleteJunkRows(false);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
