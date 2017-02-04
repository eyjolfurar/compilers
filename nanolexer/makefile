NanoLexer.class: NanoLexer.java
	javac NanoLexer.java
NanoLexer.java: nanolexer.jflex
	java -jar jflex-1.6.1.jar nanolexer.jflex
clean:
	rm -Rf *~ NanoLexer*.class NanoLexer.java
test: NanoLexer.class test.s
	java NanoLexer test.s