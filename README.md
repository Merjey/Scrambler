Scrambler
=========

Description
-----------

The program Scrambler is designed to encrypt and then decrypt the small (up to 50MB) files. For its work you need 
two files: "Scrambler.jar" -- executable java-archive (to run on your device should be installed java virtual machine) 
and "keys.properties" -- configuration file.
You are free to use a finished program, or the source code (in parts or completely) as you see fit. The author 
is not responsible for any consequences of using program.

Instruction
-----------

In order to perform encryption, you must press the button "Encrypt" and select the file in the dialog box. In this case 
the file is exposed to sequence of the following steps in a certain order several times:

1. It creates a random number generator with a specific entry point (create the same sequence 
   of pseudorandom numbers at each start; key.0 .. key.4 values from the configuration file are used as 
   entry points). Creates a new empty array (ArrayList at the fact, rather than the usual array), and 
   the file "random" bytes are transferred in turn to the end of the new array. If the file size is larger 
   than a certain number of bytes (maxpiece value of the configuration file), the file is first divided 
   in half, then each part is divided again in half, until the size of each part becomes less than 
   maxpiece, and then each part is exposed by the above operations, which are carried out separately.
2. In the "random" places of file random bytes are inserted. Their number is determined by the parameters 
   inclusions.0 inclusions.1 of the configuration file.
3. The file is compressed in a zip-archive.

Upon completion of these operations, certain short sequence of bytes ("signature") is added to the file, and originally 
opened file is overwritten with the resulting encryption.

To decrypt the file, you should press "Decrypt", and select the encrypted file in the dialog box. In this case the file 
exposed to all steps inverse to those performed in the encryption in the reverse order. It is worth noting that, since 
when you encrypt a file bytes are inserted in random places, when decoding the bytes from the proposed insertion sites 
will be deleted. If you try to decrypt an unencrypted file by this program, it can be irreversibly damaged. To prevent this, 
it checks for the "signature" at the end of the file, if it does not, decryption will not start. But if your file is 
accidentally ends the same sequence of bytes as in a "signature" (or was previously encrypted by the program with other 
settings, "signature" is always the same), and you try to decrypt it, if you needed this file... please accept my sincere 
condolences.

Used technologies
-----------------

The true purpose of creation of this program, of course, does not encrypting of files. I originally wrote the first version 
of this software to consolidate skills of working with collections, IO, and multithreading. Now, when I brought this program 
to the mind, the following technologies of the Java Core (and not only) used in it:

1. To create the user interface used JavaFX API.
2. For processing and intermediate storage of data used ArrayList. It is used as access to the items on 
   the index, and serial bypass of values (forward and reverse) with iterators.
3. To access the files using a variety of classes from package java.io.
4. While stirring bytes task is performed in parallel streams to accelerate the process. Was used 
   ForkJoin API. (In the old version I used CountDownLatch and splitting into a fixed number of sub-tasks.)
5. Written unit-test to verify the correctness of the individual methods of encryption / decryption (3 test, 
   one for each pair of methods) using the JUnit 4 library.
6. Since the methods of the preceding paragraph shall be declared in the class "scrambler.core.Core" with 
   access modifier "private", accessed via the Java Reflection API.
   