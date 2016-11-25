/*- 
* 
* Date: 2016-11-25
* Author: Artur Brasil
* 
*/  

package watson.visualRecognition;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ImageClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.SwingConstants;

public class VisualRecog extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textTry;
	private JLabel image;
	private ArrayList<String> words = new ArrayList<String>();
	private JLabel lblTextScore; // TODO
	private JLabel lblScore;
	private JLabel lblTextHighscore;
	private JLabel lblHighScore; // TODO
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VisualRecog frame = new VisualRecog();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VisualRecog() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 335, 534);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		image = new JLabel("");
		image.setBounds(16, 88, 300, 300);
		contentPane.add(image);
		
		JLabel lblQuestion = new JLabel("What Watson think about this image?");
		lblQuestion.setBounds(51, 39, 241, 37);
		contentPane.add(lblQuestion);
		
		textTry = new JTextField();
		textTry.setBounds(16, 408, 300, 26);
		contentPane.add(textTry);
		textTry.setColumns(10);
		
		JButton btnTrynext = new JButton("Try/Next");
		btnTrynext.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//image.setIcon(null);
				checkAnswer(textTry.getText());
				//image.setText("");
				randomImage();
			}
		});

		btnTrynext.setBounds(105, 446, 117, 29);
		
		randomImage();
		
		contentPane.add(btnTrynext);
		
		lblTextScore = new JLabel("Score");
		lblTextScore.setHorizontalAlignment(SwingConstants.CENTER);
		lblTextScore.setBounds(16, 465, 45, 16);
		contentPane.add(lblTextScore);
		
		lblScore = new JLabel("0");
		lblScore.setHorizontalAlignment(SwingConstants.CENTER);
		lblScore.setBounds(16, 490, 45, 16);
		contentPane.add(lblScore);
		
		lblTextHighscore = new JLabel("High Score");
		lblTextHighscore.setBounds(248, 465, 68, 16);
		contentPane.add(lblTextHighscore);
		
		lblHighScore = new JLabel("0");
		lblHighScore.setHorizontalAlignment(SwingConstants.CENTER);
		lblHighScore.setBounds(248, 490, 68, 16);
		contentPane.add(lblHighScore);
	}
	

	//Take and shows a random image 
	public void randomImage(){
		URL urlRandomImage = null;
		Random r = new Random();
		int random = r.nextInt(1000);
		try {
			urlRandomImage = new URL("https://unsplash.it/300/300/?image="+random);
			System.out.println(urlRandomImage); // Just for tests
			try {
				urlRandomImage.getContent();
			} catch (IOException e) {
				randomImage();
				e.printStackTrace();
			}
		} catch (MalformedURLException e1) {
			randomImage();
			e1.printStackTrace();
		}
		image.setIcon(new ImageIcon (urlRandomImage));
		
		watsonClassify(urlRandomImage);
	}
	
	//Classify the random image
	public void watsonClassify(URL classify){
		words.clear(); 
		
		VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);		
		service.setApiKey("<api-key-here>"); //Need api key to work

		ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
				.url(classify.toString())
				.build();
		VisualClassification result = service.classify(options).execute();
		//System.out.println(result); // Just for tests

		//Get the classification and put in 'words' arraylist
		java.util.List<ImageClassification> imageClassified = result.getImages();
		int size = imageClassified.get(0).getClassifiers().get(0).getClasses().size();
		if (size == 0)
			randomImage();
		for(int i = 0; i<size; i++){
			words.add(imageClassified.get(0).getClassifiers().get(0).getClasses().get(i).getName().toString());
		}
		
		System.out.println(words);//Tests
	}
	
	public String wordsToString() {
		String message = "";
		for(int i = 0; i<words.size(); i++){
			message += words.get(i)+"\n";
		}
		return message;
	}

	public boolean checkAnswer(String answer){
		for(int i = 0; i<words.size(); i++){
			if(answer.equals(words.get(i))){
				 JOptionPane.showMessageDialog(null, "Watson Classification:\n" + wordsToString(), "Correct!", JOptionPane.PLAIN_MESSAGE);
				 return true;
			}
		}
		JOptionPane.showMessageDialog(null,  "Watson Classification:\n" + wordsToString(), "Wrong!", JOptionPane.ERROR_MESSAGE);
		return false;
	}
}
