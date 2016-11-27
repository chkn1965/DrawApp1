package drawrecorder2;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

public class AudioRecorder extends Thread
{
    TargetDataLine targetDataLine;
    AudioFileFormat.Type targetType;
    AudioInputStream audioInputStream;
    File outputFile;
    
    public AudioRecorder(TargetDataLine targetDataLine, AudioFileFormat.Type targetType, File outputFile)
    {
        this.targetDataLine = targetDataLine;
        this.audioInputStream = new AudioInputStream(targetDataLine);
        this.targetType = targetType;
        this.outputFile = outputFile;
    } 
    
    @Override
    public void start()
    {
        targetDataLine.start();
        super.start();
    } 
    
    public void stopRecording()
    {
        targetDataLine.stop();
        targetDataLine.close();
    } 
    
    @Override
    public void run()
    { 
        try
        { AudioSystem.write( audioInputStream, targetType, outputFile); } 
        catch (IOException e){} 
    }
}

