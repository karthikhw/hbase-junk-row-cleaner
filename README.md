# hbase-junk-row-cleaner

This utility will help us to delete all junk rows that contains any special characters other than char, numeric, space, underscore, dots &amp; hyphen.

The options are show and delete.

SHOW -> will print the list of junk keys from hbase table.

DELETE -> will perform the delete operation on hbase table.

In this example, the row "@123" is a junk character. 
```
hbase(main):008:0> scan 'junk'
ROW                                                          COLUMN+CELL
 123                                                         column=d:d, timestamp=1566968976039, value=2
 @123                                                        column=d:c, timestamp=1566968962311, value=1
 @123                                                        column=d:d, timestamp=1566968971159, value=2
 abc 123                                                     column=d:d, timestamp=1566968983969, value=2
 abc-123                                                    column=d:d, timestamp=1566968993263, value=2
 abc_123                                                      column=d:d, timestamp=1566968980852, value=2
5 row(s) in 0.0790 seconds
```

## How do you run junk cleaner tool?

1. Download "hbase-junk-1.2.jar" from target/hbase-junk-1.2.jar location.
2. Place a jar on one of the HBase node.
3. Run a the commad which you need to pass tablename and commad option.

```
 java -cp `hbase classpath` com.hbase.hwx.Util [--zookpeer-server
            <comma seperated list>] [--zookeper-znode <znode>]
            [--zookeeper-port <zk port>] --table-name <tablename>  --cmd
            <show|delete> &>/tmp/_junk.log
 -c,--cmd <arg>                <show|delete>
 -n,--zookeeper-znode <arg>    zookeeper znode
 -p,--zookeeper-port <arg>     zookeeper port
 -t,--table-name <arg>         table name
 -z,--zookeeper-server <arg>   comma seperated zookeeper servers
```
### example

```
To print a junk keys:

/usr/jdk64/jdk1.8.0_112/bin/java -cp `hbase classpath`:hbase-junk-1.2.jar: com.hbase.hwx.Util --table-name junk --cmd show

========Junk Keys=========
@123
--------------------------
Tablename: junk
Operation: list-junk-keys
Row key scanned: 5
Junk key found: 1
==========================



To delete a junk keys:

/usr/jdk64/jdk1.8.0_112/bin/java -cp `hbase classpath`:hbase-junk-1.2.jar: com.hbase.hwx.Util --table-name junk --cmd delete
========Junk Keys=========
--------------------------
Tablename: junk
Operation: list-junk-keys
Row key scanned: 5
Junk key found: 1
==========================



To check a junk keys after deletion:

/usr/jdk64/jdk1.8.0_112/bin/java -cp `hbase classpath`:hbase-junk-1.2.jar: com.hbase.hwx.Util --table-name junk --cmd show
========Junk Keys=========
--------------------------
Tablename: junk
Operation: list-junk-keys
Row key scanned: 4
Junk key found: 0
==========================

```
