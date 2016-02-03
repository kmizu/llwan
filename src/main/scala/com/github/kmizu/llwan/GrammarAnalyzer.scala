package com.github.kmizu.llwan

/**
  * Created by Mizushima on 2016/02/03.
  */
object GrammarAnalyzer {
  def calculateFirstSet(grammar: Ast.Grammar, mapping: Map[Symbol, Ast.Exp]) = {
    def first(e: Ast.Exp): Set[String] = e match {
      case Ast.Seq(_, l, r) =>
        val firstOfL = first(l)
        if(firstOfL.contains("")) first(r) else firstOfL
      case Ast.Alt(_, l, r) =>
        first(l) ++ first(r)
      case Ast.Ident(_, name) =>
        first(mapping(name))
      case Ast.Opt(_, e) =>
        first(e) + ""
      case Ast.Rep0(_, e) =>
        first(e) + ""
      case Ast.Rep1(_, e) =>
        first(e) + ""
      case Ast.Str(_, c) =>
        Set(c)
    }
    ???
  }
  def calculateFollowSet(grammar: Ast.Grammar) = ???
  def calculateDirectorSet(grammar: Ast.Grammar) = ???
}
