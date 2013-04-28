webcrank-password
-----------------

[![Build Status](https://travis-ci.org/webcrank/webcrank-password.scala.png)](https://travis-ci.org/webcrank/webcrank-password.scala)

A toolkit for dealing with passwords. Provides key-derivation and verification.

This library has the following goals:
 - Secure by default: sensible algorithm choices, sensible defaults, sensible guidance.
 - Ease of use: should be clean and easy, removing any chance of accidental misuse, and
   assist in helping users understanding safe password storage.
 - Future proof: should allow for adjustment and modification of parameters over time
   with minimal impact on code.

Supported algorithms                          | Default parameters
--------------------------------------------- | --------------------------------------------------------------------
[scrypt](http://www.tarsnap.com/scrypt.html)  | N = 65536 (2 ^ 16). r = 8, p = 1
[bcrypt](http://static.usenix.org/events/usenix99/provos/provos_html/node1.html) | cost = 12
[PBKDF2](http://tools.ietf.org/html/rfc2898)  | rounds = 65536 (2 ^ 16), salt length: 16 bytes, key size: 256 bits, digest: sha1

Note that this library has _no_ dependencies on the rest of webcrank and can be used independently.


Getting webcrank-password
-------------------------

If you're using SBT, add the following dependency to your build file:

    "io.webcrank" %% "webcrank-password" % "0.3"


Using webcrank-password
-----------------------

Simply import, build an implementation with [appropriate parameters](https://github.com/webcrank/webcrank-password.scala#security-considerations),
and call crypt/verify as required.

```scala

import webcrank.password._

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

// pbkdf2 with default cost parameters, key size and digest
val passwords = Passwords.pbkdf2()

// scrypt with specified value for N: 65536 (2 ^ 16)
val passwords = Passwords.scrypt(n = 65536)

// bcrypt with specified cost parameters,
// note: cost is caluclated as 2^{cost} in bcrypt
val passwords = Passwords.bcrypt(cost = 12)

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

Password storage is up to you, but the crypt output is just
a string, so can be easily stored in a database or similar.



Security considerations
-----------------------

Each algorithm has sensible defaults, but consider careful research
and potentailly doing computation tests on your hardware to make
the most appropriate choice (read as: set as high as you can before
the performance becomes crippling).

In terms of algorithms selection there are a few factors that should be
considered:
 - work factors of algorithm
 - availability of algorithm
 - trust of underlying implementation
 - validation requirements


### Work factors

Work factors of underlying algorithm is also difficult to classify because
of the number of variables invloved (CPU, Memory, etc...).

Reading is possibly the only solution, but it is worth noting that scrypt
was specifically designed to be more difficult to run with modern computing
constraints (or lack there of) on FGPAs and alike. Scrypt is build on top
of PBKDF2 with HMAC-SHA256. The [scrypt paper](http://www.tarsnap.com/scrypt/scrypt.pdf)
has some insight into this.

In terms of choosing appropriate factors, measurement is often the best
approach. As a general rule, for interactive password storage (such as for a web app), you would want to tune the algorithm so password generation takes ~100ms.
There are some factors listed in the
[OWASP password cheatsheet](https://www.owasp.org/index.php/Password_Storage_Cheat_Sheet)
which is a pretty good source of information.


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


### Validation requirements

Validation requirements are easier. You should know if you have them.
PBKDF2 is the only NIST approved algorithm.



Algorithm migration
-------------------

One careful design decision of this library is to make it easy
to migrate algorithms, or the more likely scenario of modifying
the cost parameters over time.

### How does this library help?

So the first thing to note is that the algorithm choice, and
cost parameters _only_ affect the behaviour of `crypt`. The
parameters are then embedded in a Modular Crypt Format (MCF)
style string that encodes the algorithm, any parameters, the
salt and the derived key. This encoded information is used
for password verification.

### Modular Crypt Format (MCF) Strings

MCF strings can basically be described as:

    $identifier$content


They are no more then a loose convention, but they work pretty well
for what we want to achieve. The content part is a '$' seperated list,
of the parameters salt and derived key. Each component is base64 (or
in bcrypt's case base64 like) encoded to remove ambiguity.

Scrypt and bcrypt have well recognised MCF formats, and this library
should interoperate with other implementations.

PBKDF2 only specifies the key derivation, and this library defines
its on format string.

Identifiers supported by this library:

algorithm | MCF identifier
--------- | --------------
scrypt    | s0
bcrypt    | 2 and 2a
PBKDF2    | PBKDF2WithHMACSHA1, PBKDF2WithHMACSHA256 and PBKDF2WithHMACSHA512

Note the long winded PBKDF2 identifiers are based upon the JCE algorithm names
for consistency.


### So really how do I migrate?

Basically just update how you create the passwords implementation.

For example, using bcrypt with cost = 11, and moving to cost = 12:

```scala
val passwords = Passwords.bcrypt(cost = 11)
```

Becomes:

```scala
val passwords = Passwords.bcrypt(cost = 12)
```

Everything else remains the same. All existing passwords will
still verify, any new passwords will be generated with new
parameters.

In case it isn't clear, this means you will have multiple
types of crypted passwords over time.

If you are changing the parameters because something bad
happened (e.g. you chose poor parameters in the past, or
an algorithm was broken), you will potentially want to
force recreation of all passwords, but this is application
specific behaviour.
[OWASP password cheatsheet](https://www.owasp.org/index.php/Password_Storage_Cheat_Sheet)
provides some guidance on dealing with this sort of issue.


Interoperability
----------------

The bcrypt and scrypt implentations use standard MCF strings and
should be fully compatible with other implementations.

The PBKDF2 implementations uses a custom MCF format, if you want
to use it with another PBKDF2 implementation you will need to
understand the following format:

    $algorithm$rounds$keysize$base64(salt)$base64(key)

Where `algorithm` is one of: PBKDF2WithHMACSHA1, PBKDF2WithHMACSHA256 and PBKDF2WithHMACSHA512.

Any interop failure would be considered a _serious_ bug, please report.



Compatability
-------------

Compatability is paramount to the utility of this library.

All algorithms currently supported, will be supported for
verify indefinitely.

All algorithms currently supported, will be supported for
crypt, unless a serious issue occurs where something is
no longer deemed safe for password storage.

If you choose to take the defaults, there is _no_ guarantee
that they will not change. These defaults will be upgraded
to what is considered an acceptible level of security over
time. However, this should not affect your usage of the
libary.

Any compatability failure would be considered a _serious_ bug, please report.



Contributing
------------

Any contributions welcome, in particular any security reviews on this
library, its recommendations or the underlying crypto libraries would
be very helpful.

The [issue tracker](https://github.com/webcrank/webcrank-password.scala/issues)
is up to date with anything that is planned / desired.

Note that it is _unlikely_ that new algorithms will be added unless
they are deemed significantly better than what is currently available.
If unsure, just raise an issue to start the discussion.
