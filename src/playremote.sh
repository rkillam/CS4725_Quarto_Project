#!/usr/bin/env bash

wins=0;
losses=0;
draws=0;

myAgent="QuartoPlayerAgent"

echo "Starting games"
for i in {1..10}; do
    echo "Playing game #$i"

    player1=$myAgent

    java $player1 $1 $2 >> playerOut.txt;

    playerNumText=$(cat playerOut.txt | awk 'NR==3 {print;exit}');
    playerNum=$(echo $playerNumText | awk '{print $1; exit}');

    endText=$(cat playerOut.txt | tail -n1);
    winnerNum=$(echo $endText | awk '{print $3}');

    echo "playerNumText: $playerNumText"
    echo "playerNum: $playerNum"
    echo "endText: $endText"
    echo "winnerNum: $winnerNum"
    if [[ "$endText" = "GAME_OVER: Game is a draw " ]]; then
        draws=$[$draws + 1];
        echo "Draw"
        echo "wins: $wins"
        echo "loses: $loses"
        echo "draws: $draws"

    elif [[ "$playerNum" != "$winnerNum" ]]; then
        wins=$[$wins + 1];
        echo "won"
        echo "wins: $wins"
        echo "loses: $loses"
        echo "draws: $draws"
    else
        loses=$[$loses + 1];
        echo "lost"
        echo "wins: $wins"
        echo "loses: $loses"
        echo "draws: $draws"
    fi;

    echo -e "\n"
done;

echo "wins: $wins"
echo "loses: $loses"
echo "draws: $draws"
echo " "
