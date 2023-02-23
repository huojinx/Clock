//package pk2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;


//该类负责倒计时和响铃的开始和暂停
public class Sound implements Runnable{
  boolean flg = false;
  
  String musicfilepath = "otherfiles\\肖邦圆舞曲.wav";  //铃声文件的路径
  AudioInputStream am;
  SourceDataLine sd;
  final static int MUSICLENGTH = 23249852;       //铃声文件的字节数。 
  
  JDialog dialog = new JDialog();   //弹出框

  JLabel labhour;
  JLabel labminute;
  JLabel labsecond;


  public Sound(JLabel labhour, JLabel labminute, JLabel labsecond) {
      this.labhour = labhour;
      this.labminute = labminute;
      this.labsecond = labsecond;
      
      initmusic();
      initDialog();
  }
  
  //做一些播放.wav文件的必要准备。
  void initmusic() {
	  try {
		  am = AudioSystem.getAudioInputStream(new File(musicfilepath));
		  am.mark(MUSICLENGTH);         //在开始标记，MUSICLENGTH个字节后失效 102390
		  AudioFormat af = am.getFormat();
		  sd = AudioSystem.getSourceDataLine(af);
		  sd.open();
		  sd.start();
	  }catch (IOException e) {
		// TODO: handle exception
		  System.out.println("找不到铃声文件。");
	  }catch(UnsupportedAudioFileException e) {
		  System.out.println("不支持该类型的音乐文件。");
	  }catch(Exception e) {
		  e.getStackTrace();
	  }
  }
 
  //播放铃声文件
  void play() {
	  
	  int sumByteRead = 0;
	  byte[] b = new byte[320];
	  try {
		//  System.out.println(am.available());  //显示该音频文件的字节数
		  while(sumByteRead != -1) {
			  if(flg) break;
			  sumByteRead = am.read(b,0,b.length);
			  //System.out.println(sumByteRead);
			  if(sumByteRead >= 0) {
				  sd.write(b, 0, b.length);  //将数据写入混频器，开始播放
			  }
		  }
		  am.reset();   //铃声文件的流的指针回到起点
		  
	  }catch (IOException e) {
		// TODO: handle exception
		  e.printStackTrace();
	}
  }
  
  public void run() {
      flg = false;
      int h = Integer.parseInt(labhour.getText());
      int m = Integer.parseInt(labminute.getText());
      int s = Integer.parseInt(labsecond.getText());
      int count = h*3600+m*60+s;

      while( count>0 ) {
          try {
              Thread.sleep(1000);
          }catch (InterruptedException e) {
              // TODO: handle exception
          }
          count --;
          //System.out.println(count+" s");
          if( m==0 && s==0 ) {
              labhour.setText(String.format("%02d", --h));
              m = 60;
          }
          if( s==0 ) {
              labminute.setText( String.format("%02d", --m) );
              s = 60;
          }
          labsecond.setText( String.format("%02d", --s) );  //数字不足两位时左补零。
          if(flg) break;
      }
      if(count==0) {
    	  //时间到了弹出提示框。
    	  new Thread(()->{
    		  dialog.setVisible(true);    
    		  dialog.requestFocusInWindow();  //焦点移到弹出框上
    		  //JOptionPane.showMessageDialog(null, "时间到了，该休息一会儿了","计时器" , JOptionPane.INFORMATION_MESSAGE);
    	  }).start();    	  
    	  play();
    }
  //System.out.println("this thread is stop!");
  }
  //停止倒计时
  public boolean stopRun(){
      flg = true;
      return true;
  }
  
  //如果铃声结束，位置回到开始，返回true，如果铃声没有结束，返回false。
  //暂时不需要这个函数了
  public boolean stop(){
//      if( (sequencer.isRunning())||(sequencer.getMicrosecondPosition()==54853516) ){
//          sequencer.stop();
//          sequencer.setMicrosecondPosition(1000000);
//          return true;
//      }
      return false;
  }
  
  //用于结束程序时关闭相关的流。
  void exit() {
	  try {
		  sd.drain();
		  sd.close();
		  am.close();
	  }catch (IOException e) {
		// TODO: handle exception
		  e.getStackTrace();
	}
	  
  }
  //设置弹出框
  void initDialog() {
	  dialog.setTitle("计时器");
	  dialog.setLayout(new BorderLayout());
	  JPanel p1 = new JPanel();
	  p1.add(new JLabel("时间到了，该休息一会儿了"));
	  dialog.getContentPane().add(p1,BorderLayout.NORTH);
	  JButton b = new JButton("确定");
	  b.setSize(40,20);
	  dialog.setModal(true);
	  b.addActionListener(e -> {
		  
		  dialog.setVisible(false);
		  //dialog.dispose(); 		//关闭对话框
	  });
	  JPanel p2 = new JPanel();
	  p2.add(b);
	  dialog.getContentPane().add(p2,BorderLayout.CENTER);
	  Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	  dialog.setLocation(dim.width/2-150, dim.height/2-55);
	  dialog.setSize(300,110);
	  dialog.setAlwaysOnTop(true);   //设置窗体总是在前面
	  
  }

}


