package com.github.kmizu.llwan

object Terminals {
  sealed abstract class Terminal(descriptor: String)
  case object EOF extends Terminal("<EOF>")
  case object EMPTY extends Terminal("<EMPTY>")
  case class Token(image: String) extends Terminal(s"token<${image}")
}
