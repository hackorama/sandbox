javac *.java && docker build -t rangeset . || echo "Build failed"
echo "To run: "
echo "   java Main"
echo "     or"
echo "   docker run rangeset"

