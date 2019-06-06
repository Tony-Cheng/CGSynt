procedure main()
{
  var n, p: int;

  assume p != 0;
  while (n >= 0) {
    assert(p != 0);
    if (n == 0) {
      p := 0;
    }
    n := n - 1;
  } 
}
