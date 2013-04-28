package webcrank.password
package internal

import java.security._
import javax.crypto._, javax.crypto.spec._

object JCE {
  def gensalt(size: Int) = {
    val random =  SecureRandom.getInstance("SHA1PRNG")
    val salt = new Array[Byte](size)
    random.nextBytes(salt)
    salt
  }

  def genkey(password: String, salt: Array[Byte], rounds: Int, size: Int, alg: String) = {
    val spec = new PBEKeySpec(password.toCharArray, salt, rounds, size)
    val keys = SecretKeyFactory.getInstance(alg)
    val key = keys.generateSecret(spec)
    key.getEncoded
  }
}
