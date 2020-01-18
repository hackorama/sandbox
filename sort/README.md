# FileSorter

Sorts a given file limiting memory use bounded by given batch size

```
$ javac FileSorter.java

$ java FileSorter
unsorted-3327749799354388706-tmp.txt: b y n l j g f t p u k d q a z o i r h x s m v w e c
batch-6123780136447873041-tmp.txt: b j l n y
batch-632257014276112411-tmp.txt: f g p t u
batch-6822675074302991506-tmp.txt: a d k q z
batch-8287896925272767692-tmp.txt: h i o r x
batch-3137078241039961191-tmp.txt: e m s v w
batch-5916193979669551820-tmp.txt: c
Sorting batch in memory: a e b h f c
Sorting batch in memory: b e c h f j
Sorting batch in memory: c e g h f j
Sorting batch in memory: d f e h j g
Sorting batch in memory: e f g h j i
Sorting batch in memory: f h g i j m
Sorting batch in memory: f h g i j m
Sorting batch in memory: g h l i j m
Sorting batch in memory: h i l m j p
Sorting batch in memory: i j k m p l
Sorting batch in memory: j l k m p o
Sorting batch in memory: k l o m p s
Sorting batch in memory: k l o m p s
Sorting batch in memory: l m n s p o
Sorting batch in memory: m o n s p t
Sorting batch in memory: n o q s p t
Sorting batch in memory: o p q s t r
Sorting batch in memory: p r q s t v
Sorting batch in memory: p r q s t v
Sorting batch in memory: q r v s t y
Sorting batch in memory: r s u y t v
Sorting batch in memory: s t u y v z
Sorting batch in memory: t v u y z x
Sorting batch in memory: u v w y z x
Sorting batch in memory: u v w y z x
Sorting batch in memory: u v w y z x
Sorting batch in memory: u v w y z x
sorted-2315883012691698590-tmp.txt: a b c d e f g h i j k l m n o p q r s t u v w x y z
```
