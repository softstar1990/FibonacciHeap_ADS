#!/bin/bash
# try to run the dijkstra programm auto
printf "\n====================================================================\n";
a=(1 2 3 4 5 6 7 8 9 10)
for i in {0..4}
do
        t=$(($i+1))
	printf "\n-------------------test $t---------------------\n"
	java Dijkstra -r ${a[$i]}000 1 0;

done
