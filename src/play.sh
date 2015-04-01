#!/usr/bin/env bash

echo "Starting server"
java QuartoServer >> serverOut.txt &
serverPid=$!

wins=0;
loses=0;
draws=0;

p1Wins=0;
p2Wins=0;

p1Loses=0;
p2Loses=0;

p1Draws=0;
p2Draws=0;

myAgent="QuartoPlayerAgent"
opponentAgent="QuartoSemiRandomAgent"

echo "Starting games"
for i in {1..10}; do
    echo "Playing game #$i"

    if (($RANDOM % 2 == 0)); then
        player1=$myAgent
        player2=$opponentAgent

        echo "$myAgent is player 1"

        echo "Starting $player1"
        java $player1 localhost >> /dev/null &

        echo "Starting $player2"
        java $player2 localhost > playerOut.txt;

        playerNumText=$(cat playerOut.txt | awk 'NR==3 {print;exit}');
        playerNum=$(echo $playerNumText | awk '{print $2; exit}');

        endText=$(cat playerOut.txt | tail -n1);
        winnerNum=$(echo $endText | awk '{print $3}');

        echo "playerNumText: $playerNumText"
        echo "playerNum: $playerNum"
        echo "endText: $endText"
        echo "winnerNum: $winnerNum"
        if [[ "$endText" = "GAME_OVER: Game is a draw " ]]; then
            draws=$[$draws + 1];
            p1Draws=$[$p1Draws + 1];
            echo "Draw"
            echo "wins: $wins"
            echo "loses: $loses"
            echo "draws: $draws"

        elif [[ "$playerNum" != "$winnerNum" ]]; then
            wins=$[$wins + 1];
            p1Wins=$[$p1Wins + 1];
            echo "won"
            echo "wins: $wins"
            echo "loses: $loses"
            echo "draws: $draws"
        else
            loses=$[$loses + 1];
            p1Loses=$[$p1Loses + 1];
            echo "lost"
            echo "wins: $wins"
            echo "loses: $loses"
            echo "draws: $draws"
        fi;
    else
        player1=$opponentAgent
        player2=$myAgent

        echo "$myAgent is player 2"

        echo "Starting $player1"
        java $player1 localhost >> /dev/null &

        echo "Starting $player2"
        java $player2 localhost > playerOut.txt;

        playerNumText=$(cat playerOut.txt | awk 'NR==3 {print;exit}');
        playerNum=$(echo $playerNumText | awk '{print $2; exit}');

        endText=$(cat playerOut.txt | tail -n1);
        winnerNum=$(echo $endText | awk '{print $3}');

        echo "playerNumText: $playerNumText"
        echo "playerNum: $playerNum"
        echo "endText: $endText"
        echo "winnerNum: $winnerNum"
        if [[ "$endText" = "GAME_OVER: Game is a draw " ]]; then
            draws=$[$draws + 1];
            p2Draws=$[$p2Draws + 1];
            echo "Draw"
            echo "wins: $wins"
            echo "loses: $loses"
            echo "draws: $draws"

        elif [[ "$playerNum" = "$winnerNum" ]]; then
            wins=$[$wins + 1];
            p2Wins=$[$p2Wins + 1];
            echo "won"
            echo "wins: $wins"
            echo "loses: $loses"
            echo "draws: $draws"
        else
            loses=$[$loses + 1];
            p2Loses=$[$p2Loses + 1];
            echo "lost"
            echo "wins: $wins"
            echo "loses: $loses"
            echo "draws: $draws"
        fi;
    fi

    echo -e "\n"
done;

echo "wins: $wins"
echo "loses: $loses"
echo "draws: $draws"
echo " "

echo "p1Wins: $p1Wins"
echo "p1Loses: $p1Loses"
echo "p1Draws: $p1Draws"
echo " "

echo "p2Wins: $p2Wins"
echo "p2Loses: $p2Loses"
echo "p2Draws: $p2Draws"
echo " "

kill $serverPid
