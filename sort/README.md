# FileSorter

Sorts a given file limiting memory use bounded by given batch size

```
$ javac FileSorter.java

$ java FileSorter
unsorted-7968055055835202289-tmp.txt: t q a h c z f e n s p r y k m w v i d j u o b g l x
batch-3873681165930089476-tmp.txt: a c h q t
batch-1183468762622477614-tmp.txt: e f n s z
batch-8049529251055879919-tmp.txt: k m p r y
batch-599503471508420161-tmp.txt: d i j v w
batch-3962370818738009047-tmp.txt: b g l o u
batch-4767176331276136211-tmp.txt: x
Sorting batch in memory: a b k e d x
Sorting batch in memory: b d c e x k
Sorting batch in memory: c d g e x k
Sorting batch in memory: d e g k x h
Sorting batch in memory: e h g k x i
Sorting batch in memory: f h g k x i
Sorting batch in memory: g h i k x n
Sorting batch in memory: h k i n x l
Sorting batch in memory: i k l n x q
Sorting batch in memory: j n k q x l
Sorting batch in memory: k n l q x v
Sorting batch in memory: l n m q x v
Sorting batch in memory: m n o q x v
Sorting batch in memory: n q o v x p
Sorting batch in memory: o q p v x s
Sorting batch in memory: p q s v x u
Sorting batch in memory: q u r v x s
Sorting batch in memory: r u s v x t
Sorting batch in memory: s u t v x y
Sorting batch in memory: t u y v x z
Sorting batch in memory: u v y z x
Sorting batch in memory: v x y z
Sorting batch in memory: w x y z
Sorting batch in memory: x z y
Sorting batch in memory: y z
Sorting batch in memory: z
sorted-2129565374279189657-tmp.txt: a b c d e f g h i j k l m n o p q r s t u v w x y z
```

Each invocation creates a new random unsorted input

```
$ java FileSorter | grep sort
unsorted-8959210494016634886-tmp.txt: n y u m t d a r h z x k e j c p i b o q l f s w v g
sorted-2534524721021426037-tmp.txt: a b c d e f g h i j k l m n o p q r s t u v w x y z

$ java FileSorter | grep sort
unsorted-159810782098107833-tmp.txt: n r b e p a k q t u z f h g x o v i d l j w c y s m
sorted-6532094786874767646-tmp.txt: a b c d e f g h i j k l m n o p q r s t u v w x y z

$ java FileSorter | grep sort
unsorted-3619529629016416611-tmp.txt: y l p b t n h q w d z v c j r k u x e f i m s a o g
sorted-8936874798062340995-tmp.txt: a b c d e f g h i j k l m n o p q r s t u v w x y z

```

The test files are created with unique random names in temp folder

```
$ cat $TMPDIR/unsorted-7968055055835202289-tmp.txt
t
q
a
h
c
z
f
e
n
s
p
r
y
k
m
w
v
i
d
j
u
o
b
g
l
x
$ cat $TMPDIR/sorted-2129565374279189657-tmp.txt
a
b
c
d
e
f
g
h
i
j
k
l
m
n
o
p
q
r
s
t
u
v
w
x
y
z
```
