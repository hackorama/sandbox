# FileSync

Sync files across folders watching for incremental changes using a file change watcher.


```
$ ./test.sh

Testing initial sync ...

2018-12-11 20:24:16 INFO : SOURCE : /tmp/fsync/src
2018-12-11 20:24:17 INFO : DESTINATION : /tmp/fsync/dst
2018-12-11 20:24:17 INFO : SYNC INTERVAL MILLISECS  : 2000
2018-12-11 20:24:17 DEBUG : COPY /tmp/fsync/src/2 -> /tmp/fsync/dst/2
2018-12-11 20:24:17 DEBUG : COPY /tmp/fsync/src/2/21 -> /tmp/fsync/dst/2/21
2018-12-11 20:24:17 DEBUG : COPY /tmp/fsync/src/2/21/21.txt -> /tmp/fsync/dst/2/21/21.txt
2018-12-11 20:24:17 DEBUG : COPY /tmp/fsync/src/2/21/22 -> /tmp/fsync/dst/2/21/22
2018-12-11 20:24:17 DEBUG : COPY /tmp/fsync/src/0.txt -> /tmp/fsync/dst/0.txt
2018-12-11 20:24:17 DEBUG : COPY /tmp/fsync/src/1 -> /tmp/fsync/dst/1
2018-12-11 20:24:17 DEBUG : COPY /tmp/fsync/src/1/11.txt -> /tmp/fsync/dst/1/11.txt
2018-12-11 20:24:17 DEBUG : COPY /tmp/fsync/src/3 -> /tmp/fsync/dst/3
2018-12-11 20:24:17 DEBUG : COPY /tmp/fsync/src/3/31 -> /tmp/fsync/dst/3/31
2018-12-11 20:24:17 DEBUG : COPY /tmp/fsync/src/3/31/32 -> /tmp/fsync/dst/3/31/32
2018-12-11 20:24:17 DEBUG : COPY /tmp/fsync/src/3/31/32/33 -> /tmp/fsync/dst/3/31/32/33
2018-12-11 20:24:17 DEBUG : COPY /tmp/fsync/src/3/31/32/33/33.txt -> /tmp/fsync/dst/3/31/32/33/33.txt
2018-12-11 20:24:17 DEBUG : COPY /tmp/fsync/src/3/1 -> /tmp/fsync/dst/3/1

Checking /tmp/fsync/src <-> /tmp/fsync/dst : PASS


Testing incremental sync ...

2018-12-11 20:24:18 DEBUG : Tracked event : ENTRY_MODIFY /tmp/fsync/src/3/31/32/33/33.txt
2018-12-11 20:24:18 DEBUG : COPY /tmp/fsync/src/3/31/32/33/33.txt -> /tmp/fsync/dst/3/31/32/33/33.txt
2018-12-11 20:24:18 DEBUG : Tracked event : ENTRY_MODIFY /tmp/fsync/src/3/31/32/33/33.txt
2018-12-11 20:24:18 DEBUG : COPY /tmp/fsync/src/3/31/32/33/33.txt -> /tmp/fsync/dst/3/31/32/33/33.txt
2018-12-11 20:24:18 DEBUG : Tracked event : ENTRY_CREATE /tmp/fsync/src/3/31/32/33/333.txt
2018-12-11 20:24:18 DEBUG : COPY /tmp/fsync/src/3/31/32/33/333.txt -> /tmp/fsync/dst/3/31/32/33/333.txt
2018-12-11 20:24:18 DEBUG : Tracked event : ENTRY_MODIFY /tmp/fsync/src/3/31/32/33/333.txt
2018-12-11 20:24:18 DEBUG : COPY /tmp/fsync/src/3/31/32/33/333.txt -> /tmp/fsync/dst/3/31/32/33/333.txt
2018-12-11 20:24:18 DEBUG : Tracked event : ENTRY_DELETE /tmp/fsync/src/2/21/21.txt
2018-12-11 20:24:18 DEBUG : DELETE /tmp/fsync/dst/2/21/21.txt
2018-12-11 20:24:18 DEBUG : Tracked event : ENTRY_DELETE /tmp/fsync/src/2/21/22
2018-12-11 20:24:18 DEBUG : DELETE /tmp/fsync/dst/2/21/22
2018-12-11 20:24:18 DEBUG : Tracked event : ENTRY_DELETE /tmp/fsync/src/2/21
2018-12-11 20:24:18 DEBUG : DELETE /tmp/fsync/dst/2/21
2018-12-11 20:24:18 DEBUG : Tracked event : ENTRY_CREATE /tmp/fsync/src/2/222
2018-12-11 20:24:18 DEBUG : COPY /tmp/fsync/src/2/222 -> /tmp/fsync/dst/2/222

Checking /tmp/fsync/src <-> /tmp/fsync/dst : PASS

```
