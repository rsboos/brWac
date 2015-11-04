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
    private List<String> stopwords = Arrays.asList("de","e","a","o","do","da","em","que","uma","um","é","com","no","para","na","por","os","foi","como","dos","O","A","se","as","ao","sua","mais","das","seu","à","não","Em","ou","pela","pelo","ser","também","são","entre","era","tem","mas","seus","nos","cidade","até","Os","onde","No","área","ele","São","foram","anos","sobre","nas","quando","população","parte","região","sendo","suas","primeiro","aos","grande","estado","mesmo","nome","Foi","É","muito","segundo","família","dois","ainda","já","está","durante","primeira","As","maior","pode","Rio","ano","outros","apenas","km²","Na","ter","forma","após","pelos","qual","depois","dia","século","três","município","duas","km²","banda","num","De","todos","sem","densidade","contra","às","ela","álbum","desde","sido","então","vez","Ele","tendo","acordo","comuna","grupo","localizada","partir","quais","tinha","cerca","este","alguns","espécie","teve","cobertos","outras","habitantes","cada","Estados","hab/km²","e","período","através","conhecido","bem","Com","of","Este","tempo","sistema","Brasil","assim","além","vários","Segundo","eram","esta","série","final","filme","música","Um","José","vida","habitantes","Estende-se","antes","história","estava","pertencente","podem","fez","departamento","possui","sob","km","João","província","novo","americano","principal","Sua","início","numa","só","muitos","estão","devido","Santa","pessoas","censo","número","distrito","lançado","administrativa","Por","dias","há","governo","eles","todo","passou","quatro","terra","Após","The","várias","vezes","grandes","francesa","algumas","Universidade","Guerra","que","começou","pois","Uma","the","chamado","enquanto","havia","gênero","seguinte","Para","lugar","Grande","Nova","todas","trabalho","Condado","censos","outro","sempre","nova","média","representa","Estado","década","pelas","Demografia","D.","fim","Possui","fazer","Paulo","rio","habitantes","qualquer","muitas","Esta","época","nível","anos,","Como","orbital","Maria","jogo","Geografia","segunda","mundo","Brasil","toda","meio","Seu","filho","título","programa","lado","Durante","redor","melhor","maioria","História","pouco","Quando","localizado","conhecida","asteróide","hoje","menos","capital","principais","brasileiro","sede","poder","mil","país","uso","origem","versão","quase","Ao","produção","tipo","mesma","faz","água.","canção","estimada","presidente","seria","Nacional","volta","carreira","desta","quem","Igreja","centro","longo","importante","localidades","local","junto","United","Também","Brasil.","sul","raio","construção","ficou","livro","Depois","têm","Unidos","morte","janeiro","Carlos","sucesso","aproximadamente","Além","março","norte-americano","clube","esse","maio","dezembro","primeiros","deste","diversos","populacional","cinco","The","obra","States","diagrama","chamada","desenvolvimento","processo","rei","diversas","lhe","dentro","Campeonato","conta","Bureau","vizinhança.","Ela","Census","principalmente","casa","Localidades","norte","outubro","Jogos","entanto,","isso","julho","base","setembro","relação","empresa","equipe","futebol","tanto","ilha","velocidade","essa","agosto","outra","bairro","abril","junho","ponto","Janeiro","caso","obras","próprio","América","Copa","novembro","and","água","embora","recebeu","maiores","TV","fora","estilo","Pedro","tornou-se","I","milhões","(em","seja","considerado","países","membros","papel","Apesar","linha","último","pai","Francisco","guerra","deve","anos.","John","República","acima","Entre","língua","tarde","fevereiro","logo","termo","jogos","Paulo,","nacional","conjunto","personagem","chegou","incluindo","O","corpo","somente","II","Reino","diferentes","usado","política","metros","político","posição","movimento","Não","criado","Mas","criação","oficial","único","porque","quanto","projeto","atual","feito","total","Portugal","género","A","tornou","áreas","formação","sexo","comunidade","Porto","NGC","edição","cidades","geralmente","Sul","nunca","deu","exemplo,","aumento","seis","temporada","fato","m","descoberto","tal","condado","bastante","fica","membro","Janeiro,","atualmente","única","ano,","importantes","grupos","antiga","fronteira","disso,","tais","antigo","escola");
    //private List<String> stopwords = Arrays.asList("de","e","a","o","do","da","em","que","é");

    /**
     * Recycles this instance.
     */
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
