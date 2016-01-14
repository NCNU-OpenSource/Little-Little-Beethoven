import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.io.FileInputStream;
import java.io.InputStream;
import sun.audio.*;
import javax.swing.*;
import javax.sound.sampled.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.nio.file.Path; 
import java.nio.file.Paths; 
import java.nio.file.DirectoryStream; 
import java.nio.file.Files; 
import jline.ConsoleReader;
import java.util.*;

public class Close{

	static boolean onPlay = false;
	static boolean onRecord = false;
	static TargetDataLine targetDataLine;
	static AudioFormat audioFormat;
	static HashMap<String,String> hashtable = new HashMap();
	
	
	public static void main(String[] args){
		try{
			ConsoleReader consolereader = new ConsoleReader();
			System.out.println("Welcome to Little Beethoven!!!");
			System.out.println("Q = Do, W = Re, E = Mi, R = Fa, U = So, I = La, O = Si");
			System.out.println("Press \\ to Record and Press \\ again to stop record");
			System.out.println("Press ] to list all the recorded song!");
			System.out.println("Have Fun!");
			System.out.println();
			while(true){
				int k = consolereader.readVirtualKey();
				char keyboard = (char)k;
				System.out.println(k + "(" + (char)k + ")");
				if(k==27){
					if(onRecord){
						System.out.println("You need to exit the Record Mode First!!!");
						continue;
					}
					break;}
				if(k==92){
					Record();
				}else if(k==93){
					onPlay = true;
					ListRecordedSong();
				}else if(k==110){
					onPlay = false;
					hashtable.clear();
					System.out.println();
				}else{
					if(onPlay){
						String x = hashtable.get(Character.toString(keyboard));
						System.out.println(x);
						if(x!=null)
						    PlaySong(x);
					}
				}
				
				exam(keyboard);
			}
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void exam(char key){
		
		String gongFile="";
		if(key=='q'){
            gongFile = "do.wav";
		}else if(key =='w'){
			gongFile = "re.wav";
		}else if(key =='e'){
			gongFile ="mi.wav";
		}else if(key == 'r'){
			gongFile = "fa.wav";
		}else if(key == 'u'){
			gongFile = "so.wav";
		}else if(key == 'i'){
			gongFile = "la.wav";
		}else if(key == 'o'){
			gongFile = "si.wav";
		}
		
		
		if(gongFile!=""){
			PlaySong(gongFile);
		}
	}
	
	public static void PlaySong(String au){
		if(au!=""){
			try{
                InputStream in = new FileInputStream(au);
                AudioStream audioStream = new AudioStream(in);
                AudioPlayer.player.start(audioStream);
            }catch (Exception exc){exc.printStackTrace();}
		}
	}
	
	static class CaptureAudioThread extends Thread{
		public void run(){
			try{
				targetDataLine.open();
				targetDataLine.start();
				
				AudioInputStream audioInputStream = new AudioInputStream(targetDataLine);
				String filename = getAudioFileName();
                File audioFile = new File("C://Users//Russel//Desktop//Close//Song//"+filename+".wav");
                try {
                    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, audioFile);
                }catch (IOException ioe){ioe.printStackTrace();}
                System.out.println("Stopped Recording");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static void Record(){
		if(!onRecord){
			System.out.println("Start Recording...");
			try {
				audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);

				DataLine.Info info = new DataLine.Info(TargetDataLine.class,audioFormat);
				if(!AudioSystem.isLineSupported(info)){System.out.println("Line not Supported!");}

				targetDataLine = (TargetDataLine)AudioSystem.getLine(info);
				new CaptureAudioThread().start();
				onRecord = true;
			}
			catch (LineUnavailableException le){le.printStackTrace();}
		}else{
			targetDataLine.stop();
			targetDataLine.close();
			System.out.println("Ended Recording");
			onRecord = false;	
		}
	}
	
	public static String getAudioFileName(){
		DateFormat dateformat = new SimpleDateFormat("yyyy/MM/ddHH:mm:ss");
		Date date = new Date();
		String x =  dateformat.format(date).toString();
		String result = "";
		result = x.replace('/','_');
		result = result.replace(':','_');
		return result;
		
	}
	public static void ListRecordedSong(){
		Path dir = Paths.get("C://Users//Russel//Desktop//Close//Song//");
		int i=0;
		try{
			DirectoryStream<Path> ds = Files.newDirectoryStream(dir, "*.{wav}");
            for (Path entry: ds) {
				i++;
				String entrytemp = entry.toString();
				String[] temp = entrytemp.split("\\\\");
				System.out.println(Integer.toString(i)+". "+temp[temp.length-1]);
				System.out.println(entrytemp);
				hashtable.put(Integer.toString(i),entrytemp);
            } 
			System.out.println();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}