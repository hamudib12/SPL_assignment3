CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lboost_thread -pthread

all: bin/BGSclient


bin/BGSclient: bin/echoClient.o bin/connectionHandler.o bin/Encoder.o bin/Decoder.o 
	@echo 'Building target: Client'
	@echo 'Invoking: C++ Linker'
	g++ -Wall -Weffc++ -o bin/BGSclient bin/echoClient.o bin/connectionHandler.o bin/Encoder.o bin/Decoder.o $(LDFLAGS)
	@echo 'Finished building target: Client'
	@echo ' '
bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp $(LDFLAGS)
bin/Encoder.o:	src/Encoder.cpp
	g++ $(CFLAGS) -o bin/Encoder.o src/Encoder.cpp
bin/Decoder.o:	src/Decoder.cpp
	g++ $(CFLAGS) -o bin/Decoder.o src/Decoder.cpp 

clean:
	rm -f bin/*
