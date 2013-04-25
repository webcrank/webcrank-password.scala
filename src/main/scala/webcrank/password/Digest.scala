package webcrank.password

sealed trait Digest
case object SHA1 extends Digest
case object SHA256 extends Digest
case object SHA512 extends Digest
