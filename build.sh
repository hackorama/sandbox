g++ -g -Wall -Wextra -Werror app.cpp -o app 
valgrind --error-exitcode=1 --leak-check=full --show-leak-kinds=all ./app
cppcheck --error-exitcode=1 *.cpp *.h
wget -q https://github.com/catchorg/Catch2/releases/download/v2.2.3/catch.hpp
g++ -coverage -g -o0 -std=c++11 -Wall -Wextra -Werror test.cpp -o test
valgrind --error-exitcode=1 --leak-check=full --show-leak-kinds=all ./test
lcov --directory . --capture --output-file coverage.info
lcov --remove coverage.info 'catch.hpp' '/usr/*' --output-file coverage.info
lcov --list coverage.info



