package com.github.kmizu.llwan

/**
  * Created by Mizushima on 2016/02/03.
  */
object GrammarAnalyzer {
  def calculateFirstSet(grammar: Ast.Grammar) = {
    def first(e: Ast.Exp): Set[String] = e match {
      case Ast.Seq(_, l, r) => ???
      case Ast.Alt(_, l, r) => ???
      case Ast.Ident(_, name) => ???
      case Ast.Opt(_, e) => ???
      case Ast.Rep0(_, e) => ???
      case Ast.Rep1(_, e) => ???
      case Ast.Str(_, c) => Set(c)
    }
    ???
  }
  def calculateFollowSet(grammar: Ast.Grammar) = ???
  def calculateDirectorSet(grammar: Ast.Grammar) = ???
}
