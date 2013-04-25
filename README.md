webcrank-password
-----------------

[![Build Status](https://travis-ci.org/webcrank/webcrank-password.scala.png)](https://travis-ci.org/webcrank/webcrank-password.scala)

Primitive building blocks for dealing with passwords. Key-derivation and verification.

Supported algorithms                          | Default parameters                  
--------------------------------------------- | --------------------------------------------------------------------
[scrypt](http://www.tarsnap.com/scrypt.html)  | N = 16384 (2 ^ 14). r = 8, p = 1 |
[bcrypt](http://en.wikipedia.org/wiki/Bcrypt) | cost = 14 
[pbkdf2](http://tools.ietf.org/html/rfc2898)  | 65536 (2 ^ 16), salt length: 16 bytes, key size: 256 bits, hash: sha1


Getting webcrank-password
-------------------------

If you're using SBT, add the following dependency to your build file:

    "io.webcrank" %% "webcrank-password" % "0.2"
    
    
Using webcrank-password
-----------------------

Simply import, build an implementation with appropriate parameters, 
and call crypt/verify as required.

```scala

    import webcrank.password.Passwords
    
    val passwords = Passwords.scrypt()
    
    // crypted string is safe to store in db or elsewhere as required
    val crypted = passwords.crypt("password")
    
    // return true in this case where the password matches
    passwords.verify("password", crypted)
    
    // return false in this case where the password does not match
    passwords.verify("fido", crypted)

```

Example configurations:

```scala
    // scrypt with default cost parameters
    val passwords = Passwords.scrypt()

    // bcrypt with default cost parameters
    val passwords = Passwords.bcrypt()

    // pbkdf2 with default cost parameters, key size and hash
    val passwords = Passwords.pbkdf2()

    // scrypt with specified value for N: 65536 (2 ^ 16)
    val passwords = Passwords.scrypt(n = 65536)

    // bcrypt with specified cost parameters,
    // note: cost is caluclated as 2^{cost} in bcrypt
    val passwords = Passwords.bcrypt(cost = 16)

    // PBKDF2 with specified rounds, 16384 (2 ^ 14)
    val passwords = Passwords.pbkdf2(rounds = 16384)

    // PBKDF2 with alternative HMAC digest, SHA256
    // note: pbkdf2 with HMAC-SHA256 is only available
    // on JDK8 or with a custom JCE provider such as
    // IAIK or BSAFE.
    val passwords = Passwords.pbkdf2(digest = SHA256)

    // PBKDF2 with alternative HMAC digest, SHA512
    // note: PBKDF2 with HMAC-SHA512 is only available
    // on JDK8 or with a custom JCE provider such as
    // IAIK or BSAFE.
    val passwords = Passwords.pbkdf2(digest = SHA512)

    // PBKDF2 with specified rounds, 16384 (2 ^ 14)
    val passwords = Passwords.pbkdf2(rounds = 16384)

    // PBKDF2 with specified rounds and alternative HMAC digest
    // note: pbkdf2 with HMAC-SHA256 is only available
    // on JDK8 or with a custom JCE provider such as
    // IAIK or BSAFE.
    val passwords = Passwords.pbkdf2(rounds = 16384, digest = SHA256)
```

Security considerations
-----------------------

Each algorithm has sensible defaults, but consider careful research
and potentailly doing computation tests on your hardware to make
the most appropriate choice (read as: set as high as you can before
the performance becomes crippling).
 
In terms of algorithms selection there are a few factors that should be
considered:
 - availability of algorithm
 - trust of underlying implementation
 - work factors of algorithm
 - validation requirements
 
### Availability of algorithm

Of the provided algorithms scrypt, bcrypt and PBKDF2 with HMAC-SHA1 are
always available. PBKDF2 with HMAC-SHA256 or HMAC-SHA512 are only available
on JDK8 or with a custom JCE provider such as IAIK or BSAFE. 

The fact that PBKDF2 is stuck with HMAC-SHA1 by default potentially makes
it less desirable.

### Trust of underlying implementaion

Obviously this is difficult to measure, but all implementations
are open source and can be audited:
 - [bcrypt implementation](http://www.mindrot.org/projects/jBCrypt/)
 - [scrypt implementation](https://github.com/wg/scrypt)
 - [openjdk pbkdf2 implementations](http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/f4c62eecf7fa/src/share/classes/com/sun/crypto/provider/)

There is some ongoing effort to complete a review of the scrypt and bcrypt 
implementations.


### Work factors

Work factors of underlying algorithm is also difficult to classify because
of the number of variables invloved (CPU, Memory, etc...).

Reading is possibly the only solution, but it is worth noting that scrypt
was specifically designed to be more difficult to run with modern computing
constraints (or lack there of) on FGPAs and alike. Scrypt is build on top
of PBKDF2 with HMAC-SHA256. The [scrypt paper](http://www.tarsnap.com/scrypt/scrypt.pdf) 
has some insight into this.

In terms of choosing appropriate factors, measurement is often the best
approach. As a general rule you would want to tune the algorithm so password generation takes ~100ms.
There are some factors listed in the 
[OWASP password cheatsheet](https://www.owasp.org/index.php/Password_Storage_Cheat_Sheet)
which is a pretty good source of information. 


### Validation requirements

Validation requirements are easier. You should know if you have them.
PBKDF2 is the only NIST approved algorithm.

