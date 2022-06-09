package huffman

import HuffmanError.*
import Node.*

// Student: Oriol Camps Pérez

def getFrequency[A](as: List[A]): Map[A, Int] =
  def _start(as: List[A], map: Map[A, Int]): Map[A, Int] = as match
    case Nil => map
    case hd :: tl => if (map.contains(hd)) _start(tl, map + ( hd -> (map.get(hd).get + 1) )) else _start(tl, map + (hd -> 1))

  _start(as, Map[A, Int]())

def createTree[A](freqInfo: Map[A, Int]): Either[HuffmanError, Node[A]] =
  def _createListOfNodes(freqInfoTuples: List[(A, Int)], nodeList: List[Node[A]]): List[Node[A]] = freqInfoTuples match
    case Nil => nodeList
    case (key: A, value: Int) :: tl => _createListOfNodes(tl, nodeList ::: List(Leaf(key, value)))

  def _start(nodeList: List[Node[A]]): Either[HuffmanError, Node[A]] = nodeList.sortBy(_.freq) match
    case Nil => Left(NoFrequencies)
    case (lastElement: Node[A]) :: Nil => Right(lastElement)
    case (elem1: Node[A]) :: (elem2: Node[A]) :: tl => _start(tl ::: List(Inner(elem1, elem2, elem1.freq + elem2.freq)))

  _start(_createListOfNodes(freqInfo.toList, List[Node[A]]()))

def encode[A](as: List[A], tree: Node[A]): Either[HuffmanError, List[Boolean]] =
  def _createMapDict(tree: Node[A], map: Map[A, List[Boolean]], acc: List[Boolean]): Map[A, List[Boolean]] = tree match
    case Leaf(value: A, freq: Int) => map + (value -> acc)
    case Inner(left: Node[A], right: Node[A], freq: Int) =>
      _createMapDict(left, map, acc ::: List[Boolean](true)) ++ _createMapDict(right, map, acc ::: List[Boolean](false))

  def _start(as: List[A], map: Map[A, List[Boolean]], bitsList: List[Boolean]): Either[HuffmanError, List[Boolean]] = as match
    case Nil => Right(bitsList)
    case (hd: A) :: tl => map.get(hd) match
      case None => Left(ValueNotFound)
      case Some(codeA) => _start(tl, map, bitsList ::: codeA)

  _start(as, _createMapDict(tree, Map[A, List[Boolean]](), List[Boolean]()), List[Boolean]())

def decode[A](bits: List[Boolean], tree: Node[A]): Either[HuffmanError, List[A]] =
  def _start(inputBits: List[Boolean], tree: Node[A], root: Node[A], acc: List[A]): Either[HuffmanError, List[A]] =
    inputBits match
    case Nil => tree match
      case Leaf(value: A, freq: Int) => Right(acc ::: List(value))
      case Inner(left: Node[A], right: Node[A], freq: Int) => Left(MissingBits)
    case (hd: Boolean) :: tl => tree match
      case Leaf(value: A, freq: Int) => _start(inputBits, root, root, acc ::: List(value))
      case Inner(left: Node[A], right: Node[A], freq: Int) => if (hd) _start(tl, left, root, acc) else _start(tl, right, root, acc)

  _start(bits, tree, tree, List[A]())

@main def main(): Unit =
  val result =
    for
      tree    <- createTree(getFrequency("abcdefghij".getBytes.toList))
      encoded <- encode("jihgfedcab".getBytes.toList, tree)
      decoded <- decode(encoded, tree)
    yield (encoded, new String(decoded.toArray))
  println(result)

  /*
  //////////////////////////////////////////////////////////////////
  println()
  val personalTest_frequency1 =
    val s: String = "aaasaddddsssfaaassdaaafffssds" // a->10 s->9 d->6 f->4
    getFrequency(s.toList)
  println(personalTest_frequency1)

  val personalTest_frequency2 =
    val s: String = "" // Map()
    getFrequency(s.toList)
  println(personalTest_frequency2)

  val personalTest_createTree1 =
    val s: String = "eeeeeddddddccccccbbbbbbbaaaaaaaaaaaaaaa"
    createTree(getFrequency(s.toList)) // Right( Inner( Leaf(a,15), Inner( Inner( Leaf(e,5), Leaf(d,6), 11), Inner( Leaf(c,6), Leaf(b,7), 13), 24), 39)
  println(personalTest_createTree1)

  val personalTest_createTree2 =
    createTree(personalTest_frequency2) // NoFrequencies
  println(personalTest_createTree2)

//////////////////////////////////////////////////////////////////
  println()

  val personalTest_encode1 =
    val s: String = "eeeeeddddddccccccbbbbbbbaaaaaaaaaaaaaaa" // 011011011011011001001001001001001010010010010010010000000000000000000000111111111111111 (this one is printed) or
    createTree(getFrequency(s.toList)) match // 011011011011011010010010010010010001001001001001001000000000000000000000111111111111111
      case Right(tree) => encode(s.toList,tree)
      case Left(e) => e
    /*
    a = 1           -> (true)
    b = 000         -> (false, false, false)
    c = 001 or 010  -> (false, false, true) or (false, true, false)
    d = 010 or 001  -> (false, true, false) or (false, false, true)
    e = 011         -> (false, true, true)
    */

  println(personalTest_encode1)

  val personalTest_encode2 =
    val s: String = ""
    createTree(getFrequency(s.toList)) match
      case Right(tree) => encode(s.toList,tree)
      case Left(e) => e // NoFrequencies
  println(personalTest_encode2)

  val personalTest_encode3 =
    val s1: String = "alibaba" // a:3 b:2 l:1 i:1
    val s2: String = "ability" // t & y not included in s2
    createTree(getFrequency(s1.getBytes.toList)) match
      case Right(tree) => encode(s2.toList,tree) // ValueNotFound
      case Left(e) => e
  println(personalTest_encode3)


  //////////////////////////////////////////////////////////////////
  println()

  val personalTest_decode1 =
    val s: String = "eeeeeddddddccccccbbbbbbbaaaaaaaaaaaaaaa"
    val l: List[Boolean] = List(false, true, true, false, true, true, false, true, true, false, true, true, false, true, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true)
    createTree(getFrequency(s.toList)) match
      case Right(t) => decode(l,t) // e, e, e, e, e, d, d, d, d, d, d, c, c, c, c, c, c, b, b, b, b, b, b, b, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a
      case Left(e) => e
  println(personalTest_decode1)

  val personalTest_decode2 =
    val s: String = "eeeeeddddddccccccbbbbbbbaaaaaaaaaaaaaaa"
    // all bits (booleans) of l are erased
    val l: List[Boolean] = List()
    createTree(getFrequency(s.toList)) match
      case Right(t) => decode(l,t) // MissingBits
      case Left(e) => e
  println(personalTest_decode2)

  */