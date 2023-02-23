//package pk2;


/*
 *
 * 试着设置一个可以调节声音大小的设置，可以考虑按键控制和图标控制并用。
 *
 * 最后需要打包成jar文件，所需要的相关文件要放在一个文件夹下。
 *
 * 程序的图标需要换一个好看点的。
 *
 * 程序可以伸展，不需要太多设置时可以缩小。
 *
 * 开始暂停等按钮需要换成图标。
 *
 * myfont设置的大小是和di变量设置的长宽大小一样的。
 *
 * 想办法设置标题栏的颜色，和窗口的颜色搭配一下，使得好看。可以考虑看一下java源码中jframe的代码，看看标题栏怎么设置的。
 *
 *===========================================================================
 * 以后要养成做注释的习惯
 * keylistener事件的keytyped函数检测不到esc键，其他两个函数可以。
 * 键盘事件中，如果选择空格键，需要在keytyped函数中用，否则按一次空格键事件出现两次。当选择别的字母键触发事件时，按空格键一样可以触发。
 * 当对版本做大改动时，要另起一个文件，名字要有区别。
 *
 *
 * */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.sound.midi.*;


public class Clockv3 extends JFrame 
	implements ActionListener,MouseWheelListener,MouseListener{

    Font myfont = new Font("宋体",Font.PLAIN,70);
    Font myfont2 = new Font("宋体",Font.PLAIN,36);
    static String hour = "00",mintes = "20",second = "00";
    static String timefile = "otherfiles\\time.txt";


    JLabel labhour = new JLabel(hour);
    JLabel labminute = new JLabel(mintes);
    JLabel labsencond = new JLabel(second);
    JLabel labmaohao1 = new JLabel(":");
    JLabel labmaohao2 = new JLabel(":");
    JLabel labcount = new JLabel("0  ");
    JButton butsecond = new JButton(second);
    JButton butminute = new JButton(mintes);
    JButton buthour = new JButton(hour);
    JButton butstart = new JButton();
    ImageIcon stop = new ImageIcon("imgs\\开始1.png");
    ImageIcon start = new ImageIcon("imgs\\暂停2.png");

    Sound musictask = new Sound(labhour,labminute,labsencond);
//    Thread thread = new Thread(task);
    boolean key = true;
    static Sequencer sequencer;

    public Clockv3() {
        // TODO Auto-generated constructor stub
        super("计时器");
        setLayout(new FlowLayout(FlowLayout.RIGHT,5,0));
        loadtime(timefile);


        labhour.setText(hour);labminute.setText(mintes);labsencond.setText(second);

        labhour.setFont(myfont);
        labminute.setFont(myfont);
        labsencond.setFont(myfont);
        labmaohao1.setFont(myfont);
        labmaohao2.setFont(myfont);
        labcount.setFont(myfont);
        butsecond.setFont(myfont2);
        butminute.setFont(myfont2);
        buthour.setFont(myfont2);

        labcount.setForeground(Color.yellow);

        add(labcount);
        add(labhour);
        add(buthour);
        add(labmaohao1);
        add(labminute);
        add(butminute);
        add(labmaohao2);
        add(labsencond);
        add(butsecond);

        //给时间标签和按钮加事件，方便他们切换。
        Dimension di = new Dimension(70,60);
        labsencond.addMouseListener(this);
        labminute.addMouseListener(this);
        labhour.addMouseListener(this);

        butsecond.setVisible(false);
        butsecond.addActionListener(this);
        butsecond.addMouseWheelListener(this);
        butsecond.setPreferredSize(di);

        butminute.setVisible(false);
        butminute.addActionListener(this);
        butminute.addMouseWheelListener(this);
        butminute.setPreferredSize(di);

        buthour.setVisible(false);
        buthour.addActionListener(this);
        buthour.addMouseWheelListener(this);
        buthour.setPreferredSize(di);


        butstart.addActionListener(this);
        butstart.setIcon(start);
        butstart.setPreferredSize(new Dimension(52,52));
        add(butstart);

        JButton butclear = new JButton(new ImageIcon("imgs\\清空2.png"));
        butclear.setPreferredSize(new Dimension(52,52));

        butclear.addActionListener( e -> {
            reset();
        });
        add(butclear);

        JButton butresetcount = new JButton(new ImageIcon("imgs\\重置2.png"));
        butresetcount.setPreferredSize(new Dimension(52,52));
        butresetcount.addActionListener( e->{
            labcount.setText("0  ");
            reset();
        });
        add(butresetcount);

        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                exit();

            }
        } );

        //三个按钮都添加键盘事件，约等于整个窗口添加了键盘事件，因为窗口无法获取焦点。
//        butstart.addKeyListener(this);
//        butclear.addKeyListener(this);
//        butresetcount.addKeyListener(this);
        
//        addCustomTitle();
        
//        setForeground(Color.white);
//        getContentPane().setBackground(Color.white);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(400, 300);
        setResizable(false);
        setSize(650,110);
    }
    
    //自定义一个标题
    void addCustomTitle() {
    	this.setUndecorated(true);
    	
    	JPanel titleJPanel = new JPanel();
    	titleJPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    	titleJPanel.add(new JLabel("计时器"));
    	titleJPanel.setBackground(Color.white);
    	JButton closeBtn = new JButton("X");
    	JButton minBtn = new JButton("一");
    	closeBtn.addActionListener(e -> {
    		this.dispose();
    	});
    	minBtn.addActionListener(e -> {
    		this.setExtendedState(ICONIFIED);
    	});
    	titleJPanel.add(closeBtn);
    	titleJPanel.add(minBtn);    	
    }

    void exit(){
        try {
            BufferedWriter bw = new BufferedWriter( new FileWriter(timefile) );
            bw.write( hour+":"+mintes+":"+second );
            bw.close();
            musictask.exit();
            System.exit(0);
        }catch (IOException ioe) {
            // TODO: handle exception
            ioe.printStackTrace();
        }
    }

    //导入本地文件的时间
    void loadtime( String file ) {
        try {
            File f = new File(".",file);
            if( !f.exists() ) return ;
            BufferedReader br = new BufferedReader( new FileReader(f) );
            String line = br.readLine();
            if( line==null ) return ;
            hour = line.substring(0,2);
            mintes = line.substring(3,5);
            second = line.substring(6);

        }catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    //重置时间
    void reset() {
    	butstart.requestFocus();
        labhour.setText(hour);
        labminute.setText(mintes);
        labsencond.setText(second);
    }


    @Override
    public void mouseWheelMoved( MouseWheelEvent e ) {
        JButton button = (JButton)e.getSource();
        int a = Integer.parseInt( button.getText() );
        int c = a+e.getWheelRotation();
        if( c==-1 )  c=59; 
        if( c==60 )  c=0;  
        button.setText( String.format("%02d", c ));
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        //e.setActionCommand();
        JButton but = (JButton)e.getSource();
        if(  key ) {
            if( but.equals(butsecond) ) {
                labsencond.setVisible(true);
                but.setVisible(false);
                assignvalue(but, labsencond);
                judgeenable();
                return ;
            }
            if( but.equals(butminute) ) {
                labminute.setVisible(true);
                but.setVisible(false);
                assignvalue(but, labminute);
                judgeenable();
                return ;
            }
            if( but.equals(buthour) ) {
                labhour.setVisible(true);
                but.setVisible(false);
                assignvalue(but, labhour);
                judgeenable();
                return ;
            }
        }
        startandstop(but);

    }

    void startandstop(JButton but){
        if( but.getIcon().equals(start) ) {  //开始
            but.setIcon(stop);
//            key = false;
            new Thread(musictask).start();
        }else {                              //暂停 
            but.setIcon(start);
            boolean bool = musictask.stopRun();   //终止倒计时线程
            if(bool){
            	//如果时间没到就暂停，那么不增加次数，不重置时间。
            	if(!labsencond.getText().equals("00")) return ;
            	if(!labminute.getText().equals("00")) return ;
            	if(!labhour.getText().equals("00")) return ;
                int count = Integer.parseInt((labcount.getText()).trim())+1;
                labcount.setText(count+"  ");
                reset();
            }

        }
    }

    @Override
    public void mousePressed( MouseEvent e) {

    }
    @Override
    public void mouseReleased( MouseEvent e ) {

    }
    @Override
    public void mouseEntered( MouseEvent e ) {

    }
    @Override
    public void mouseExited( MouseEvent e ) {

    }
    @Override
    public void mouseClicked( MouseEvent e) {
        if(!key) return;   //如果正在计时，就阻止标签和按钮切换。
        JLabel label = (JLabel)e.getSource();
        label.setVisible(false);
        if( label.equals(labsencond) ) {
            butsecond.setVisible(true);
            assignvalue(labsencond, butsecond);
            butstart.setEnabled(false);
            return ;
        }
        if( label.equals(labminute) ) {
            butminute.setVisible(true);
            assignvalue(labminute, butminute);
            butstart.setEnabled(false);
            return ;
        }
        if( label.equals(labhour)){
            buthour.setVisible(true);
            assignvalue(labhour, buthour);
            butstart.setEnabled(false);
            return ;
        }
    }



    void assignvalue( JLabel first,JButton last) {
        String a = first.getText();
        last.setText(a);
    }
    void assignvalue( JButton first,JLabel last) {
        String a = first.getText();
        last.setText(a);
        if( first.equals(butsecond) ) second=a;
        if( first.equals(butminute) ) mintes=a;
        if( first.equals(buthour) ) hour=a;
    }
    //判断，如果秒钟、分钟、时钟的标签都是可见的，那么开始的按钮就可用
    void judgeenable() {
        if( labsencond.isVisible()&&labminute.isVisible()&&labhour.isVisible() )
            butstart.setEnabled(true);
    }
    

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        SwingUtilities.invokeLater( ()->{
            new Clockv3().setVisible(true);
        });
    }

}
