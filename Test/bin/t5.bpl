procedure lockingEx()
{
  var L, new, auld: int;

  L := 0;

  while (true) {
    assert(L != 1);
    L := 1;
    auld := new;
    if (*) {
      L :=0;
      new := new + 1;
    }
    if (new == auld) {
      break;
    }
  } 
}
