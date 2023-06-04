# Check for java
which java >/dev/null 2>&1 || die "ERROR: Java could not be found in your PATH!"

java -jar ./SnakeBattleArenaServer.jar -Xmx1G

read -r -s -n 1 -p "Press any key to exit..."
