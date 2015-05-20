import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Editor extends WindowAdapter implements ActionListener{
	JFrame frame;
    JTextPane pane;
    String fil=null,dir=null,text=null;
    JButton fdn_btn,fdl_btn,fnd_cnl,fok_btn,fcc_btn,rpl_btn,rll_btn,rpl_cnl,b3,b4;
    JDialog fnd_dlg,rpl_dlg;
    int index;
    JTextField target,result;
    boolean edited=false;
    public Editor(){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e){
            //
        }
        JMenuBar menubar;
        JMenu file,edit,build,setting,help;
        JMenuItem f_new,f_opn,f_sve,f_sva,f_ext,
                 e_und,e_red,e_cut,e_cpy,e_pst,e_dlt,e_fnd,e_rpl,
                 b_cpl,b_run,
                 s_fnt,
                 h_vhp,h_abt;
		frame=new JFrame();
		frame.setSize(800,600);
		frame.addWindowListener(this);
		frame.setTitle("Text Editor");

		menubar=new JMenuBar();
        //menubar.setFont(new Font("Times New Roman",Font.BOLD,18));
        //file菜单
        file=new JMenu("File");
        f_new=new JMenuItem("New");
        f_new.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_MASK));
        f_opn=new JMenuItem("Open");
        f_opn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));
        f_sve=new JMenuItem("Save");
        f_sve.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
        f_sva=new JMenuItem("Save As");
        f_ext=new JMenuItem("Exit");
        f_new.addActionListener(this);
        f_opn.addActionListener(this);
        f_sve.addActionListener(this);
        f_sva.addActionListener(this);
        f_ext.addActionListener(this);
        file.add(f_new);
        file.add(f_opn);
        file.add(f_sve);
        file.add(f_sva);
        file.addSeparator();
        file.add(f_ext);
        menubar.add(file);

        //edit菜单
        edit=new JMenu("Edit");
        e_und=new JMenuItem("Undo");
        e_und.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,InputEvent.CTRL_MASK));

        e_red=new JMenuItem("Redo");
        e_red.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,InputEvent.CTRL_MASK));
        e_cut=new JMenuItem("Cut");
        e_cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_MASK));
        e_cut.addActionListener(new ActionListener(){  //监听Cut
            public void actionPerformed(ActionEvent e){
                pane.cut();
            }
        });
        e_cpy=new JMenuItem("Copy");
        e_cpy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_MASK));
        e_cpy.addActionListener(new ActionListener(){  //监听Copy
            public void actionPerformed(ActionEvent e){
                pane.copy();
            }
        });
        e_pst=new JMenuItem("Paste");
        e_pst.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.CTRL_MASK));
        e_pst.addActionListener(new ActionListener(){  //监听Paste
            public void actionPerformed(ActionEvent e){
                pane.paste();
            }
        });
        e_dlt=new JMenuItem("Delete");
        e_dlt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
        e_dlt.addActionListener(new ActionListener(){  //监听
            public void actionPerformed(ActionEvent e){
                //pane.;
            }
        });
        e_fnd=new JMenuItem("Find");

        e_rpl=new JMenuItem("Replace");
        e_und.addActionListener(this);
        e_red.addActionListener(this);
        e_dlt.addActionListener(this);
        e_fnd.addActionListener(this);
        e_rpl.addActionListener(this);
        edit.add(e_und);
        edit.add(e_red);
        edit.addSeparator();
        edit.add(e_cut);
        edit.add(e_cpy);
        edit.add(e_pst);
        edit.add(e_dlt);
        edit.addSeparator();
        edit.add(e_fnd);
        edit.add(e_rpl);
        menubar.add(edit);

        //build菜单
		build=new JMenu("Build");
        b_cpl=new JMenuItem("Compile");
        b_run=new JMenuItem("Run");
        b_cpl.addActionListener(this);
        b_run.addActionListener(this);
        build.add(b_cpl);
        build.add(b_run);
        menubar.add(build);

        //setting菜单
        setting=new JMenu("Setting");
        s_fnt=new JMenuItem("Font");
        s_fnt.addActionListener(this);
        setting.add(s_fnt);
        menubar.add(setting);

		//help菜单
        help=new JMenu("Help");
        h_vhp=new JMenuItem("View Help");
        h_abt=new JMenuItem("About");
        h_vhp.addActionListener(this);
        h_abt.addActionListener(this);
        help.add(h_vhp);
        help.addSeparator();
        help.add(h_abt);
        menubar.add(help);

		frame.setJMenuBar(menubar);
        pane=new JTextPane();
        pane.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent e){
                edited=true;
            }
        });

        pane.getDocument().addDocumentListener(new Highlighting(pane));

        pane.setFont(new Font("Consolas",Font.PLAIN,14));
        pane.setEditable(false);
        pane.setVisible(false);
        pane.setBorder(new LineNumberBorder());
        frame.add(pane);
        frame.add(new JScrollPane(pane));
        frame.setVisible(true);


	}

    public void actionPerformed(ActionEvent event){

        String str=event.getActionCommand();
        if(str.equals("New")){
            frame.setTitle("Untitled - Text Editor");
            fil="";
            pane.setText(null);
            pane.setEditable(true);
            pane.setVisible(true);
        }
        if(str.equals("Open")){
            FileDialog dialog=new FileDialog(frame,"Open",FileDialog.LOAD);
            dialog.setVisible(true);
            fil=dialog.getFile();
            dir=dialog.getDirectory();
            if(!fil.equals("")){
                try {
                    frame.setTitle(fil + " - Text Editor");
                    FileInputStream fis=new FileInputStream(new File(dir, fil));
                    BufferedReader br=new BufferedReader(new InputStreamReader(fis,"GBK"));
                    text = "";
                    int ch;
                    while((ch=br.read()) != -1){
                        text += (char)ch;
                    }
                    pane.setText(text);
                    pane.setEditable(true);
                    pane.setVisible(true);

                }
                catch(IOException e) {
                    //e.printStackTrace();
                }
            }
        }
        if(str.equals("Save")){
            FileWriter fos;
            text=pane.getText();
            if(fil.equals("")){
                FileDialog dialog=new FileDialog(frame,"SELECT",FileDialog.SAVE);
                dialog.setVisible(true);
                fil=dialog.getFile();
                dir=dialog.getDirectory();
                try{
                    fos=new FileWriter(new File(dir,fil));
                    fos.write(text,0,text.length());
                    fos.close();
                    frame.setTitle(fil+" - Text Editor");
                }
                catch(IOException e){
                    //e.printStackTrace();
                }
            }
            else
                try{
                    fos=new FileWriter(new File(dir,fil));
                    fos.write(text,0,text.length());
                    fos.close();
                }
                catch (IOException e){
                    //e.printStackTrace();
                }
        }
        if(str.equals("Save As"))
        {
            FileDialog dialog=new FileDialog(frame,"Save As",FileDialog.SAVE);
            dialog.setVisible(true);
            fil=dialog.getFile();
            dir=dialog.getDirectory();
            text=pane.getText();
            try{
                FileWriter fos=new FileWriter(new File(dir,fil));
                fos.write(text,0,text.length());
                fos.close();
                frame.setTitle(fil+" - Text Editor");
            }
            catch(IOException e){
                //e.printStackTrace();
            }
        }
        if(str.equals("Exit")){
            System.exit(0);
        }

        if(str.equals("Find")){
            index=0;
            fnd_dlg=new JDialog();
            fnd_dlg.setTitle("Find");
            fnd_dlg.setSize(320,100);
            fnd_dlg.setLocation(100,100);
            fnd_dlg.addWindowListener(this);
            
            JPanel fnd_pnl=new JPanel();
            JLabel fnd_lbl=new JLabel("Find what:");
            fnd_pnl.add(fnd_lbl);
            target = new JTextField(32);
            fnd_pnl.add(target);

            fdn_btn=new JButton("Find Next");
            fdn_btn.addActionListener(this);
            fnd_pnl.add(fdn_btn);
            fdl_btn=new JButton("Find All");
            fdl_btn.addActionListener(this);
            fnd_pnl.add(fdl_btn);
            fnd_cnl=new JButton("Cancel");
            fnd_cnl.addActionListener(this);
            fnd_pnl.add(fnd_cnl);

            fnd_dlg.add(fnd_pnl);
            fnd_dlg.setVisible(true);
        }
        
        if(str.equals("Replace")){
            index=0;
            rpl_dlg = new JDialog();
            rpl_dlg.setTitle("Replace");
            rpl_dlg.setSize(320,128);
            rpl_dlg.addWindowListener(this);

            Panel rpl_pnl=new Panel();
            JLabel fnd_lbl=new JLabel("Find what:   ");
            rpl_pnl.add(fnd_lbl);
            target = new JTextField(32);
            rpl_pnl.add(target);
            JLabel rpl_lbl=new JLabel("Replace with:");
            rpl_pnl.add(rpl_lbl);
            result = new JTextField(32);
            rpl_pnl.add(result);

            fdn_btn=new JButton("Find Next");
            fdn_btn.addActionListener(this);
            rpl_pnl.add(fdn_btn);
            rll_btn=new JButton("Replace All");
            rll_btn.addActionListener(this);
            rpl_pnl.add(rll_btn);
            rpl_cnl=new JButton("Cancel");
            rpl_cnl.addActionListener(this);
            rpl_pnl.add(rpl_cnl);

            rpl_dlg.add(rpl_pnl);
            rpl_dlg.setVisible(true);
        }

        if(str.equals("Compile")){
            try{
                Process process = Runtime.getRuntime().exec("javac "+dir+fil);

                Frame fc = new Frame();
                fc.setSize(200,200);
                fc.addWindowListener(this);

                InputStream in=process.getErrorStream();
                int chc;
                String strc="";
                while((chc = in.read()) != -1) {
                    strc = strc+(char)chc;
                }

                if(strc.equals(""))
                    strc = "Process Completed";
                TextArea tc = new TextArea(strc);

                fc.add(tc);
                fc.setVisible(true);
            }
            catch (IOException e) {
                //e.printStackTrace();
            }
        }

        if(str.equals("Run")){
            String dosCommand = "cmd /c start cmd /k java ";
            String location = dir;
            int position = fil.lastIndexOf(".");
            String s2 = fil.substring(0,position);
            try
            {
                Process process = Runtime.getRuntime().exec(dosCommand + s2,null,new File(location));
                Frame fr = new Frame();
                fr.setSize(200, 200);
                fr.addWindowListener(this);
                InputStream in=process.getErrorStream();
                int chr;
                String strr="";
                while((chr = in.read()) != -1) {
                    strr = strr+(char)chr;
                }
                TextArea tr = new TextArea(strr);

                fr.add(tr);
                fr.setVisible(true);
            }
            catch (IOException er) {
                er.printStackTrace();
            }
        }


        if(str.equals("Font")){
            new Fonts();
        }

        if(str.equals("About")){
            JFrame about=new JFrame();
            about.setSize(240,110);
            about.addWindowListener(this);
            JPanel panel=new JPanel();
            about.setTitle("About");
            JLabel l1=new JLabel("Text Editor Version 1.0");
            JLabel l2=new JLabel("Developed by Zhenduo Liang");
            JLabel l3=new JLabel("https://liangzhenduo0608.github.io/");
            panel.add(l1);
            panel.add(l2);
            panel.add(l3);
            about.add(panel);
            about.setVisible(true);
        }

        if(event.getSource()==fdn_btn){
            String REGEX = target.getText();
            String INPUT =pane.getText().substring(0);
            INPUT=INPUT.replace("\r","");
            pane.select(INPUT.indexOf(REGEX),INPUT.indexOf(REGEX)+REGEX.length());
        }

        if(event.getSource()==rll_btn){
            String REGEX = target.getText();
            String INPUT = pane.getText();
            String REPLACE = result.getText();
            Pattern p = Pattern.compile(REGEX);
            Matcher m = p.matcher(INPUT);
            INPUT = m.replaceAll(REPLACE);
            pane.setText(INPUT);
        }

        if(event.getSource()==fnd_cnl){
            fnd_dlg.dispose();
        }

        if(event.getSource()==rpl_cnl){
            rpl_dlg.dispose();
        }
    }

	public void windowClosing(WindowEvent e){
        Window w=e.getWindow();
        w.dispose();
	}

    class Highlighting implements DocumentListener {
        private Set<String> keywords;
        private Style keywordStyle;
        private Style normalStyle;

        public Highlighting(JTextPane pane) {
            // 准备着色使用的样式
            keywordStyle = ((StyledDocument) pane.getDocument()).addStyle("Keyword_Style", null);
            normalStyle = ((StyledDocument) pane.getDocument()).addStyle("Keyword_Style", null);
            StyleConstants.setForeground(keywordStyle, Color.BLUE);
            StyleConstants.setForeground(normalStyle, Color.BLACK);
            
            keywords=new HashSet<String>(){{  //存储关键字
                add("private");
                add("protected");
                add("public");
                add("abstract");
                add("class");
                add("extends");
                add("final");
                add("implements");
                add("interface");
                add("native");
                add("new");
                add("static");
                add("strictfp");
                add("synchronized");
                add("transient");
                add("volatile");
                add("break");
                add("continue");
                add("return");
                add("do");
                add("while");
                add("if");
                add("else");
                add("for");
                add("instanceof");
                add("switch");
                add("case");
                add("default");
                add("assert");
                add("catch");
                add("finally");
                add("throw");
                add("throws");
                add("try");
                add("import");
                add("package");
                add("boolean");
                add("byte");
                add("char");
                add("double");
                add("float");
                add("int");
                add("long");
                add("short");
                add("null");
                add("super");
                add("this");
                add("void");
                add("goto");
            }};
        }

        public void colouring(StyledDocument doc, int pos, int len) throws BadLocationException{
            int start = indexOfWordStart(doc, pos);
            int end = indexOfWordEnd(doc, pos + len);
            char ch;
            while (start < end) {
                ch = getCharAt(doc, start);
                if (Character.isLetter(ch) || ch == '_') {
                    start = colouringWord(doc, start);
                } else {
                    SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, normalStyle));
                    ++start;
                }
            }
        }

        public int colouringWord(StyledDocument doc, int pos) throws BadLocationException {
            int wordEnd = indexOfWordEnd(doc, pos);
            String word = doc.getText(pos, wordEnd - pos);

            if (keywords.contains(word)) {
                SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, keywordStyle));
            } else {
                SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, normalStyle));
            }

            return wordEnd;
        }

        public char getCharAt(Document doc, int pos) throws BadLocationException {
            return doc.getText(pos, 1).charAt(0);
        }

        public int indexOfWordStart(Document doc, int pos) throws BadLocationException {
            while(pos > 0 && isWordCharacter(doc, pos - 1)) pos--;
            return pos;
        }

        public int indexOfWordEnd(Document doc, int pos) throws BadLocationException {
            while(isWordCharacter(doc, pos)) pos++;
            return pos;
        }

        public boolean isWordCharacter(Document doc, int pos) throws BadLocationException {
            char ch = getCharAt(doc, pos);
            return (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_');
        }

        @Override
        public void changedUpdate(DocumentEvent e) {}

        @Override
        public void insertUpdate(DocumentEvent e) {
            try {
                colouring((StyledDocument) e.getDocument(), e.getOffset(), e.getLength());
            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            try {
                colouring((StyledDocument) e.getDocument(), e.getOffset(), 0);
            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }
        }

        private class ColouringTask implements Runnable {
            private StyledDocument doc;
            private Style style;
            private int pos;
            private int len;

            public ColouringTask(StyledDocument doc, int pos, int len, Style style) {
                this.doc = doc;
                this.pos = pos;
                this.len = len;
                this.style = style;
            }

            public void run(){
                try{
                    doc.setCharacterAttributes(pos, len, style, true);
                }
                catch (Exception e){
                    //
                }
            }
        }
    }

    public class LineNumberBorder extends AbstractBorder{  //行号类
        public LineNumberBorder(){}

        public Insets getBorderInsets(Component c)
        {
            return getBorderInsets(c, new Insets(0, 0, 0, 0));
        }
        public Insets getBorderInsets(Component c, Insets insets){
            if (c instanceof JTextPane){
                insets.left = 20;
            }
            return insets;
        }
        public boolean isBorderOpaque()
        {
            return false;
        }
        public void paintBorder(Component c, Graphics g, int x, int y, int width,int height){
            java.awt.Rectangle clip = g.getClipBounds();
            FontMetrics fm = g.getFontMetrics();
            int fontHeight = fm.getHeight();
            int ybaseline = y + fm.getAscent();
            int startingLineNumber = (clip.y / fontHeight) + 1;
            if (startingLineNumber != 1){
                ybaseline = y + startingLineNumber * fontHeight
                        - (fontHeight - fm.getAscent());
            }
            int yend = ybaseline + height;
            if (yend > (y + height)){
                yend = y + height;
            }
            g.setColor(Color.GRAY);
            while (ybaseline < yend){
                String label = padLabel(startingLineNumber, 0, true);
                g.drawString(label, 0, ybaseline);
                ybaseline += fontHeight;
                startingLineNumber++;
            }
        }

        private String padLabel(int lineNumber, int length, boolean addSpace){
            StringBuffer buffer = new StringBuffer();
            buffer.append(lineNumber);
            for (int count = (length - buffer.length()); count > 0; count--){
                buffer.insert(0, ' ');
            }
            if (addSpace){
                buffer.append(' ');
            }
            return buffer.toString();
        }
    }

    class Fonts implements ActionListener{  //字体类
        final JDialog fontDialog;
        final JTextField tfFont,tfSize,tfStyle;
        final int fontStyleConst[]={Font.PLAIN,Font.ITALIC,Font.BOLD,Font.BOLD+Font.ITALIC};
        final JList listStyle,listFont,listSize;
        JLabel sample;

        public Fonts(){
            fontDialog = new JDialog(frame,"Font",true);
            Container con=fontDialog.getContentPane();
            con.setLayout(new FlowLayout(FlowLayout.LEFT));

            Font currentFont=pane.getFont();

            JLabel lblFont=new JLabel("Font:");
            lblFont.setPreferredSize(new Dimension(140,20));
            JLabel lblStyle=new JLabel("Font Style:");
            lblStyle.setPreferredSize(new Dimension(80,20));
            JLabel lblSize=new JLabel("Size:");
            lblSize.setPreferredSize(new Dimension(40,20));
            tfFont=new JTextField(22);
            tfFont.setText(currentFont.getFontName());
            tfFont.selectAll();
            tfFont.setPreferredSize(new Dimension(200,20));
            tfStyle=new JTextField(12);
            if(currentFont.getStyle()==Font.PLAIN)
                tfStyle.setText("Regular");
            else if(currentFont.getStyle()==Font.ITALIC)
                tfStyle.setText("Italic");
            else if(currentFont.getStyle()==Font.BOLD)
                tfStyle.setText("Bold");
            else if(currentFont.getStyle()==(Font.BOLD+Font.ITALIC))
                tfStyle.setText("Bold Italic");

            tfFont.selectAll();
            tfStyle.setPreferredSize(new Dimension(200,20));
            tfSize=new JTextField(9);
            tfSize.setText(currentFont.getSize()+"");
            tfSize.selectAll();
            tfSize.setPreferredSize(new Dimension(200,20));

            final String fontStyle[]={"Regular","Italic","Bold","Bold Italic"};
            listStyle=new JList(fontStyle);

            GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
            final String fontName[]=ge.getAvailableFontFamilyNames();
            int defaultFontIndex=0;
            for(int i=0;i<fontName.length;i++){
                if(fontName[i].equals(currentFont.getFontName())){
                    defaultFontIndex=i;
                    break;
                }
            }
            listFont=new JList(fontName);
            listFont.setSelectedIndex(defaultFontIndex);
            listFont.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listFont.setVisibleRowCount(7);
            listFont.setFixedCellWidth(119);
            listFont.setFixedCellHeight(20);
            listFont.addListSelectionListener(new ListSelectionListener(){
                    public void valueChanged(ListSelectionEvent event){
                        tfFont.setText(fontName[listFont.getSelectedIndex()]);
                        tfFont.requestFocus();
                        tfFont.selectAll();
                        updateSample();
                    }
                }
            );
            listStyle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            if(currentFont.getStyle()==Font.PLAIN)
                listStyle.setSelectedIndex(0);
            else if(currentFont.getStyle()==Font.ITALIC)
                listStyle.setSelectedIndex(2);
            else if(currentFont.getStyle()==Font.BOLD)
                listStyle.setSelectedIndex(1);
            else if(currentFont.getStyle()==(Font.BOLD+Font.ITALIC))
                listStyle.setSelectedIndex(3);

            listStyle.setVisibleRowCount(7);
            listStyle.setFixedCellWidth(77);
            listStyle.setFixedCellHeight(20);
            listStyle.addListSelectionListener(new ListSelectionListener(){
                    public void valueChanged(ListSelectionEvent event){
                        tfStyle.setText(fontStyle[listStyle.getSelectedIndex()]);
                        tfStyle.requestFocus();
                        tfStyle.selectAll();
                        updateSample();
                    }
                }
            );

            final String fontSize[]={"8","9","10","11","12","14","16","18","20","22","24","26","28","36","48","72"};
            listSize=new JList(fontSize);
            int defaultFontSizeIndex=0;
            for(int i=0;i<fontSize.length;i++){
                if(fontSize[i].equals(currentFont.getSize()+"")){
                    defaultFontSizeIndex=i;
                    break;
                }
            }
            listSize.setSelectedIndex(defaultFontSizeIndex);

            listSize.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listSize.setVisibleRowCount(7);
            listSize.setFixedCellWidth(40);
            listSize.setFixedCellHeight(20);
            listSize.addListSelectionListener(new ListSelectionListener(){
                    public void valueChanged(ListSelectionEvent event){
                        tfSize.setText(fontSize[listSize.getSelectedIndex()]);
                        tfSize.requestFocus();
                        tfSize.selectAll();
                        updateSample();
                    }
                }
            );
            fok_btn=new JButton("OK");
            fok_btn.addActionListener(this);
            fcc_btn=new JButton("Cancel");
            fcc_btn.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    fontDialog.dispose();
                }
            });

            sample=new JLabel("AaBbYyZz");
            sample.setHorizontalAlignment(SwingConstants.CENTER);
            sample.setPreferredSize(new Dimension(120,36));

            JPanel samplePanel=new JPanel();
            samplePanel.setBorder(BorderFactory.createTitledBorder("Sample"));
            samplePanel.add(sample);

            con.add(lblFont);
            con.add(lblStyle);
            con.add(lblSize);
            con.add(tfFont);
            con.add(tfStyle);
            con.add(tfSize);
            
            con.add(new JScrollPane(listFont));
            con.add(new JScrollPane(listStyle));
            con.add(new JScrollPane(listSize));
            con.add(samplePanel);
            updateSample();

            con.add(fok_btn);
            con.add(fcc_btn);

            fontDialog.setSize(310,360);
            fontDialog.setLocation(200,200);
            fontDialog.setResizable(false);
            fontDialog.setVisible(true);
        }

        public void updateSample(){
            Font sampleFont=new Font(tfFont.getText(),fontStyleConst[listStyle.getSelectedIndex()],Integer.parseInt(tfSize.getText()));
            sample.setFont(sampleFont);
        }

        public void actionPerformed(ActionEvent e){
            if(e.getSource()==fok_btn){
                Font tempFont=new Font(tfFont.getText(),fontStyleConst[listStyle.getSelectedIndex()],Integer.parseInt(tfSize.getText()));
                pane.setFont(tempFont);
                fontDialog.dispose();
            }
        }
    }

    public static void main(String[] args){
		new Editor();
	}
}
