/**
 * boilerpipe
 *
 * Copyright (c) 2009 Christian Kohlschütter
 *
 * The author licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.l3s.boilerpipe.sax;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.labels.LabelAction;
import de.l3s.boilerpipe.util.UnicodeTokenizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple SAX {@link ContentHandler}, used by {@link BoilerpipeSAXInput}. Can
 * be used by different parser implementations, e.g. NekoHTML and TagSoup.
 * 
 * @author Christian Kohlschütter
 */
public class BoilerpipeHTMLContentHandler implements ContentHandler {

    private final Map<String, TagAction> tagActions;
    private String title = null;
    static final String ANCHOR_TEXT_START = "$\ue00a<";
    static final String ANCHOR_TEXT_END = ">\ue00a$";
    StringBuilder tokenBuffer = new StringBuilder();
    StringBuilder textBuffer = new StringBuilder();
    int inBody = 0;
    int inAnchor = 0;
    int inIgnorableElement = 0;
    int tagLevel = 0;
    int blockTagLevel = -1;
    boolean sbLastWasWhitespace = false;
    private int textElementIdx = 0;
    private final List<TextBlock> textBlocks = new ArrayList<TextBlock>();
    private String lastStartTag = null;
    @SuppressWarnings("unused")
    private String lastEndTag = null;
    @SuppressWarnings("unused")
    private Event lastEvent = null;
    private int offsetBlocks = 0;
    private BitSet currentContainedTextElements = new BitSet();
    private boolean flush = false;
    boolean inAnchorText = false;
    LinkedList<LinkedList<LabelAction>> labelStacks = new LinkedList<LinkedList<LabelAction>>();
    LinkedList<Integer> fontSizeStack = new LinkedList<Integer>();
    private HashSet<String> stopwords = new HashSet<String>(Arrays.asList("eu", "me", "mim", "comigo", "tu", "te", "ti", "contigo", "ele", "ela", "o", "a", "lhe", "eles", "elas", "se", "si", "consigo", "nós", "nos", "conosco", "vós", "vos", "convosco", "eles", "elas", "os", "as", "lhes", "ele", "ela", "se", "si", "meu", "meus", "minha", "minhas", "teu", "teus", "tua", "tuas", "seu", "seus", "sua", "suas", "nosso", "nossos", "nossa", "nossas", "vosso", "vossos", "vossa", "vossas", "seu", "seus", "sua", "suas", "este", "estes", "esta", "estas", "esse", "esses", "essa", "essas", "aquele", "aqueles", "aquela", "aquelas", "mesmo", "o", "próprio", "semelhante", "tal", "a", "mesma", "própria", "mesmos", "os", "próprios", "semelhantes", "tais", "as", "mesmas", "próprias", "semelhantes", "tais", "àquela", "àquelas", "àquele", "àqueles", "àquilo", "da", "daquela", "daquelas", "daquele", "daqueles", "daquilo", "das", "dessa", "dessas", "desse", "desses", "desta", "destas", "deste", "destes", "disto", "do", "dos", "na", "naquela", "naquelas", "naquele", "naqueles", "naquilo", "nas", "nessa", "nessas", "nesse", "nesses", "neste", "nestas", "neste", "nestes", "nisto", "no", "nos", "aqueloutra", "aqueloutras", "aqueloutro", "aqueloutros", "essoutra", "essoutras", "essoutro", "essoutros", "estoutra", "estoutras", "estoutro", "estoutros", "àqueloutra", "àqueloutras", "àqueloutro", "àqueloutros", "daqueloutra", "daqueloutras", "daqueloutro", "daqueloutros", "dessoutra", "dessoutras", "dessoutro", "dessoutros", "destoutra", "destoutras", "destoutro", "destoutros", "naqueloutra", "naqueloutras", "naqueloutro", "naqueloutros", "nessoutra", "nessoutras", "nessoutro", "nessoutros", "nestoutra", "nestoutras", "nestoutro", "nestoutros", "cujo", "qual", "quanto", "cuja", "qual", "quanta", "cujos", "quais", "quantos", "cujas", "quais", "quantas", "quê", "quem", "quando", "qual", "quais", "quanto", "quanta", "quantos", "quantas", "algo", "alguém", "cada", "menos", "nada", "outrem", "ninguém", "tudo", "quem", "algum", "alguns", "alguma", "algumas", "certa", "certas", "certo", "certos", "nenhum", "nenhuma", "muita", "muitas", "muito", "muitos", "outra", "outras", "outro", "outros", "pouca", "poucas", "pouco", "poucos", "quaisquer", "qualquer", "tanta", "tantas", "tanto", "tantos", "toda", "todas", "todo", "todos", "o", "a", "os", "as", "a", "ante", "após", "até", "com", "contra", "de", "desde", "em", "entre", "para", "por", "perante", "segundo", "sem", "sob", "sobre", "trás", "afora", "fora", "exceto", "salvo", "malgrado", "durante", "mediante", "segundo", "menos", "conforme", "consoante", "tirante", "passante", "posto", "suposto", "dado", "atento", "visto", "não obstante", "mesmo", "enquanto", "via", "senão", "menos", "como", "que", "embora", "exclusive", "feito", "Afastamento", "Assunto", "Autoria", "Causa", "Causador", "Companhia", "Componente", "Conformidade", "Constituição", "Conteúdo", "destino", "dimensão", "e", "nem", "que", "mas", "outrossim", "tampouco", "também", "senão", "também", "ademais ", "demais", "mas", "porém", "todavia", "contudo", "antes", "entretanto", "entanto", "quando", "que", "mas", "senão", "entrementes", "e", "aliás", "ou", "ora", "quer", "seja", "nem", "já", "logo", "talvez", "quando", "que", "senão", "se", "logo", "portanto", "pois", "então", "consequentemente", "conseguintemente", "dessarte", "destarte", "daí", "donde ", "agora", "porque", "que", "porquanto", "ademais ", "demais", "outrossim", "senão", "porque", "como", "pois", "porquanto", "quando", "porque", "se", "como", "feito", "qual", "se", "embora", "conquanto", "que", "dado que", "malgrado", "empero", "se", "caso", "quando", "como", "que", "conforme", "segundo", "consoante", "como", "que", "e", "senão", "porque", "que", "se", "como", "conforme", "enquanto", "quando", "enquanto", "mal", "que", "como", "assim", "apenas"));
    public void recycle() {
        tokenBuffer.setLength(0);
        textBuffer.setLength(0);

        inBody = 0;
        inAnchor = 0;
        inIgnorableElement = 0;
        sbLastWasWhitespace = false;
        textElementIdx = 0;

        textBlocks.clear();

        lastStartTag = null;
        lastEndTag = null;
        lastEvent = null;

        offsetBlocks = 0;
        currentContainedTextElements.clear();

        flush = false;
        inAnchorText = false;
    }

    /**
     * Constructs a {@link BoilerpipeHTMLContentHandler} using the
     * {@link DefaultTagActionMap}.
     */
    public BoilerpipeHTMLContentHandler() {
        this(DefaultTagActionMap.INSTANCE);
        //this.stopwords = openStopWordsFile();
    }

    /**
     * Constructs a {@link BoilerpipeHTMLContentHandler} using the given
     * {@link TagActionMap}.
     * 
     * @param tagActions
     *            The {@link TagActionMap} to use, e.g.
     *            {@link DefaultTagActionMap}.
     */
    public BoilerpipeHTMLContentHandler(final TagActionMap tagActions) {
        this.tagActions = tagActions;
    }

    // @Override
    public void endDocument() throws SAXException {
            flushBlock();

    }

    // @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    // @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        if (!sbLastWasWhitespace) {
            textBuffer.append(' ');
            tokenBuffer.append(' ');
        }
        sbLastWasWhitespace = true;
    }

    // @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
    }

    // @Override
    public void setDocumentLocator(Locator locator) {
    }

    // @Override
    public void skippedEntity(String name) throws SAXException {
    }

    // @Override
    public void startDocument() throws SAXException {
    }

    // @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
    }

    // @Override
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        labelStacks.add(null);

        TagAction ta = tagActions.get(localName);
        if (ta != null) {
            if (ta.changesTagLevel()) {
                tagLevel++;
            }
            flush = ta.start(this, localName, qName, atts) | flush;
        } else {
            tagLevel++;
            flush = true;
        }

        lastEvent = Event.START_TAG;
        lastStartTag = localName;
    }

    // @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        TagAction ta = tagActions.get(localName);
        if (ta != null) {
            flush = ta.end(this, localName, qName) | flush;
        } else {
            flush = true;
        }

        if (ta == null || ta.changesTagLevel()) {
            tagLevel--;
        }

        if (flush) {
                flushBlock();

        }

        lastEvent = Event.END_TAG;
        lastEndTag = localName;

        labelStacks.removeLast();
    }

    // @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        textElementIdx++;


        if (flush) {
                flushBlock();

            flush = false;
        }

        if (inIgnorableElement != 0) {
            return;
        }

        char c;
        boolean startWhitespace = false;
        boolean endWhitespace = false;
        if (length == 0) {
            return;
        }

        final int end = start + length;
        for (int i = start; i < end; i++) {
            if (Character.isWhitespace(ch[i])) {
                ch[i] = ' ';
            }
        }
        while (start < end) {
            c = ch[start];
            if (c == ' ') {
                startWhitespace = true;
                start++;
                length--;
            } else {
                break;
            }
        }
        while (length > 0) {
            c = ch[start + length - 1];
            if (c == ' ') {
                endWhitespace = true;
                length--;
            } else {
                break;
            }
        }
        if (length == 0) {
            if (startWhitespace || endWhitespace) {
                if (!sbLastWasWhitespace) {
                    textBuffer.append(' ');
                    tokenBuffer.append(' ');
                }
                sbLastWasWhitespace = true;
            } else {
                sbLastWasWhitespace = false;
            }
            lastEvent = Event.WHITESPACE;
            return;
        }
        if (startWhitespace) {
            if (!sbLastWasWhitespace) {
                textBuffer.append(' ');
                tokenBuffer.append(' ');
            }
        }

        if (blockTagLevel == -1) {
            blockTagLevel = tagLevel;
        }

        textBuffer.append(ch, start, length);
        tokenBuffer.append(ch, start, length);
        if (endWhitespace) {
            textBuffer.append(' ');
            tokenBuffer.append(' ');
        }

        sbLastWasWhitespace = endWhitespace;
        lastEvent = Event.CHARACTERS;

        currentContainedTextElements.set(textElementIdx);
    }

    List<TextBlock> getTextBlocks() {
        return textBlocks;
    }

    private boolean compareStopWords(String word){
        if (this.stopwords.contains(word)) {
            return true;
        } else {
            return false;
        }
    }

   /* private Set<String> openStopWordsFile() {
        Set<String> stop = new HashSet<String>();
        String currdir = System.getProperty("user.dir");
        File arq_stops = new File(currdir, "stopwords.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(arq_stops));
            String line;
            while ((line = br.readLine()) != null) {
                stop.add(line.toLowerCase().trim());
            }
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BoilerpipeHTMLContentHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BoilerpipeHTMLContentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stop;
    } */

    public void flushBlock() {
        if (inBody == 0) {
            if ("TITLE".equalsIgnoreCase(lastStartTag) && inBody == 0) {
                setTitle(tokenBuffer.toString().trim());
            }
            textBuffer.setLength(0);
            tokenBuffer.setLength(0);
            return;
        }

        final int length = tokenBuffer.length();
        switch (length) {
            case 0:
                return;
            case 1:
                if (sbLastWasWhitespace) {
                    textBuffer.setLength(0);
                    tokenBuffer.setLength(0);
                    return;
                }
        }
        final String[] tokens = UnicodeTokenizer.tokenize(tokenBuffer);

        int numWords = 0;
        int numStopWords = 0;
        int numLinkedWords = 0;
        int numWrappedLines = 0;
        int currentLineLength = -1; // don't count the first space
        final int maxLineLength = 80;
        int numTokens = 0;
        int numWordsCurrentLine = 0;

        for (String token : tokens) {
            if (ANCHOR_TEXT_START.equals(token)) {
                inAnchorText = true;
            } else if (ANCHOR_TEXT_END.equals(token)) {
                inAnchorText = false;
            } else if (isWord(token)) {
                numTokens++;
                numWords++;
                if (compareStopWords(token)) {
                    numStopWords++;
                }
                numWordsCurrentLine++;
                if (inAnchorText) {
                    numLinkedWords++;
                }
                final int tokenLength = token.length();
                currentLineLength += tokenLength + 1;
                if (currentLineLength > maxLineLength) {
                    numWrappedLines++;
                    currentLineLength = tokenLength;
                    numWordsCurrentLine = 1;
                }
            } else {
                numTokens++;
            }
        }
        if (numTokens == 0) {
            return;
        }
        int numWordsInWrappedLines;
        if (numWrappedLines == 0) {
            numWordsInWrappedLines = numWords;
            numWrappedLines = 1;
        } else {
            numWordsInWrappedLines = numWords - numWordsCurrentLine;
        }

        TextBlock tb = new TextBlock(textBuffer.toString().trim(),
                currentContainedTextElements, numWords, numLinkedWords,
                numWordsInWrappedLines, numWrappedLines, offsetBlocks, numStopWords);
        currentContainedTextElements = new BitSet();

        offsetBlocks++;

        textBuffer.setLength(0);
        tokenBuffer.setLength(0);

        tb.setTagLevel(blockTagLevel);
        addTextBlock(tb);
        blockTagLevel = -1;
    }

    protected void addTextBlock(final TextBlock tb) {

        for (Integer l : fontSizeStack) {
            if (l != null) {
                tb.addLabel("font-" + l);
                break;
            }
        }
        for (LinkedList<LabelAction> labelStack : labelStacks) {
            if (labelStack != null) {
                for (LabelAction labels : labelStack) {
                    if (labels != null) {
                        labels.addTo(tb);
                    }
                }
            }
        }

        textBlocks.add(tb);
    }
    private static final Pattern PAT_VALID_WORD_CHARACTER = Pattern.compile("[\\p{L}\\p{Nd}\\p{Nl}\\p{No}]");

    private static boolean isWord(final String token) {
        return PAT_VALID_WORD_CHARACTER.matcher(token).find();
    }

    static private enum Event {

        START_TAG, END_TAG, CHARACTERS, WHITESPACE
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String s) {
        if (s == null || s.length() == 0) {
            return;
        }
        title = s;
    }

    /**
     * Returns a {@link TextDocument} containing the extracted {@link TextBlock}
     * s. NOTE: Only call this after parsing.
     * 
     * @return The {@link TextDocument}
     */
    public TextDocument toTextDocument(){
        // just to be sure
        flushBlock();

        return new TextDocument(getTitle(), getTextBlocks());
    }

    public void addWhitespaceIfNecessary() {
        if (!sbLastWasWhitespace) {
            tokenBuffer.append(' ');
            textBuffer.append(' ');
            sbLastWasWhitespace = true;
        }
    }

    public void addLabelAction(final LabelAction la)
            throws IllegalStateException {
        LinkedList<LabelAction> labelStack = labelStacks.getLast();
        if (labelStack == null) {
            labelStack = new LinkedList<LabelAction>();
            labelStacks.removeLast();
            labelStacks.add(labelStack);
        }
        labelStack.add(la);
    }
}
