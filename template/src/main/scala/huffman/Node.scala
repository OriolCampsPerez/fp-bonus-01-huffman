package huffman

enum Node[+A]:
  case Inner(left: Node[A], right: Node[A], freq: Int)
  case Leaf(value: A, freq: Int)
  def freq: Int
