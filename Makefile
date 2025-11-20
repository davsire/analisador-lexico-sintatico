CC = javac
SRC = $(shell find src -name "*.java")
DEST = bin

all:
	$(CC) $(SRC) -d $(DEST)

clean:
	rm -rf $(DEST)
