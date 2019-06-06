procedure main() returns (){
    var x,y,i,j : int;

    assume true;
	x := i;
	y := j;
    while (x != 0)
		invariant ((i==j) ==> (x == y));
// 		invariant (x-y)==(i-j);
    {
		x := x - 1;
		y := y - 1;
    }
    assert (i == j) ==> (y == 0);
}
