package arith.lang

import java.util.LinkedList
import java_cup.runtime.Symbol
import java.io.FileReader
import scala.collection.JavaConversions._

object Compiler {

  sealed trait Term
  case class TmTrue() extends Term
  case class TmFalse() extends Term
  case class TmZero() extends Term
  case class TmSucc(t1: Term) extends Term
  case class TmPred(t1: Term) extends Term
  case class TmIf(t1: Term, t2: Term, t3: Term) extends Term
  case class TmIsZero(t1: Term) extends Term

  case class NoRuleApplies() extends Exception 


  def isNumerical(t: Term): Boolean = {
    t match {
      case TmZero()   => true
      case TmSucc(t1) => isNumerical(t1)
      case _          => false
    }
  }

  def printTerm(t: Term): String = {
    t match {
      case TmZero() => "0"
      case TmTrue() => "true"
      case TmFalse() => "false"
      case TmSucc(t1) => s"succ(${printTerm(t1)})"
    }
  }

  def eval1(t: Term): Term = {
    t match {
      case TmTrue()               => throw NoRuleApplies()
      case TmFalse()              => throw NoRuleApplies()
      case TmZero()               => throw NoRuleApplies()
      case TmIf(TmTrue(), t2, _)  => t2
      case TmIf(TmFalse(), _, t3) => t3
      case TmIf(t1,t2,t3)         => TmIf(eval1(t1), t2, t3)
      case TmSucc(t1)             => TmSucc(eval1(t1))
      case TmPred(t1 @ TmZero())  => t1
      case TmPred(TmSucc(t1)) 
               if isNumerical(t1) => t1
      case TmPred(t1)             => TmPred(eval1(t1))
      case TmIsZero(TmZero())     => TmTrue()
      case TmIsZero(t1 @ TmSucc(_)) 
               if isNumerical(t1) => TmFalse()
      case TmIsZero(t1)           => TmIsZero(eval1(t1))
    }
  }

  def main(args: Array[String]): Unit = {
    val r = new FileReader(args(0));
    //printTokens(r);

    try {
      interp(r);
    } catch {
      case e:Error => println(e.getMessage())
    }
    
  }

  def printTokens(r: FileReader): Unit = {
    val l = new Lexer(r);
    var s = l.next_token();
    while(s.sym != ParserSym.EOF) {
      println(ParserSym.terminalNames(s.sym));
      s = l.next_token();
    }
  }

  def interpTerm(t: Term): Term = {
    try {
      val tp = eval1(t) // take a step
      interpTerm(tp)
    } catch {
      case e:NoRuleApplies => t
    }
  }

  def interp(r: FileReader): Unit = {
        val p = new Parser(r);
        val s = p.parse();
        val cmds = s.value.asInstanceOf[LinkedList[Term]]
        cmds.foreach{t => println(printTerm(interpTerm(t)))}
  }
}


