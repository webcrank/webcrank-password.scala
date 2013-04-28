package webcrank.password

object Callibrate {
  /**
   * Naive estimate of password cost in milliseconds.
   */
  def callibrate(passwords: Passwords): Long = {
    val sample = "0123456789"
    val start = System.nanoTime
    (1 to 1000).foreach(_ => passwords.crypt(sample))
    val end = System.nanoTime
    (end - start) / 1000 / 1000000
  }
}
