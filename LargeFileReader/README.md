# Large File Reader

```
$ javac LargeFileReader.java
```

```
$ java -Xmx64m LargeFileReader
HEAP At beginning : Total 16,252,928 bytes, Max 64,880,640 bytes, Free 15,799,976 bytes
Creating data file test.data.txt with 1,048,576 lines, each line of size 1,024 bytes ...
Created data file test.data.txt with 1,048,576 lines, each line of size 1,024 bytes, total size 1,073,741,824
DATA FILE : test.data.txt 1,073,741,824 bytes [1,048,576 lines each of siize 1,024]
Total file size 1,073,741,824 bytes is 16 times the maxium heap 64,880,640 bytes
HEAP Before reading : Total 16,318,464 bytes, Max 64,880,640 bytes, Free 12,373,192 bytes

Data read by 3 threads each thread reading all of the data

READER_2 : 104857 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_0 : 104857 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_1 : 104857 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_2 : 209714 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_1 : 209714 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_0 : 209714 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_0 : 314571 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_1 : 314571 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_2 : 314571 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_0 : 419428 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_2 : 419428 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_1 : 419428 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_1 : 524285 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_0 : 524285 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_2 : 524285 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_2 : 629142 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_1 : 629142 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_0 : 629142 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_1 : 733999 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_0 : 733999 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_2 : 733999 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_0 : 838856 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_1 : 838856 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_2 : 838856 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_1 : 943713 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_0 : 943713 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_2 : 943713 XXXXXXXXXXXXXXXXXXXXXXXXX ...
READER_2 : 1048570 XXXXXXXXXXXXXXXXXXXXXXXX ...
READER_0 : 1048570 XXXXXXXXXXXXXXXXXXXXXXXX ...
READER_1 : 1048570 XXXXXXXXXXXXXXXXXXXXXXXX ...
Thread READER_1 finished reading 1,048,576 lines of expected 1,048,576 lines data
Thread READER_0 finished reading 1,048,576 lines of expected 1,048,576 lines data
Thread READER_2 finished reading 1,048,576 lines of expected 1,048,576 lines data

Data read distributed across 3 threads each thread reading part of data with no duplication

Thread BUFFER_WRITER starts buffering data using buffer size 100 of total 1,048,576 linesa ...
HEAP After reading : Total 16,318,464 bytes, Max 64,880,640 bytes, Free 11,736,672 bytes
BUFFER_READER_1 : 104855 XXXXXXXXXXXXXXXXXXXXXXXXX ...
BUFFER_READER_2 : 209712 XXXXXXXXXXXXXXXXXXXXXXXXX ...
BUFFER_READER_0 : 314569 XXXXXXXXXXXXXXXXXXXXXXXXX ...
BUFFER_READER_1 : 419426 XXXXXXXXXXXXXXXXXXXXXXXXX ...
BUFFER_READER_2 : 524283 XXXXXXXXXXXXXXXXXXXXXXXXX ...
BUFFER_READER_0 : 629140 XXXXXXXXXXXXXXXXXXXXXXXXX ...
BUFFER_READER_1 : 733997 XXXXXXXXXXXXXXXXXXXXXXXXX ...
BUFFER_READER_2 : 838854 XXXXXXXXXXXXXXXXXXXXXXXXX ...
BUFFER_READER_0 : 943711 XXXXXXXXXXXXXXXXXXXXXXXXX ...
BUFFER_READER_1 : 1048568 XXXXXXXXXXXXXXXXXXXXXXXX ...
Thread BUFFER_READER_1 finished reading 349,535 lines of expected 1,048,576 lines data
Thread BUFFER_READER_2 finished reading 349,434 lines of expected 1,048,576 lines data
Thread BUFFER_READER_0 finished reading 349,607 lines of expected 1,048,576 lines data
Thread BUFFER_WRITER finished buffering 1,048,576 lines of expected 1,048,576 lines data
Data read across 3 threads 349607 349535 349434  = 1048576
Data read across 3 threads 349607 349535 349434  = 1048576
Data read across 3 threads 349607 349535 349434  = 1048576

```

```
$ du -h test.data.txt
1.1G    test.data.txt

$ head -n 1 test.data.txt
1 XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

$ tail -n 1 test.data.txt
1048576 XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
$
```
