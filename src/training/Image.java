package training;

/**
 * The class used to store user data and link it to an image:
 * 
 * @author checkoutwithyourface group members
 * @see https://github.com/opencv-java/face-detection
 */
public class Image {

	private String filePath = "Faces/";
	private String name;
	private String netID;
	private int year;

	/**
	 * Constructor to save image data
	 * 
	 * @param path file path
	 * @param name User's name
	 * @param netID User's netID
	 * @param year User's year in school (0: fresh, 1: soph, ..., 4: grad/doc)
	 */
	public Image(String path, String name, String netID, int year) {
		this.filePath += path;
		this.name = name;
		this.netID = netID;
		this.year = year;
	}

	public String getFilepath() {
		return filePath;
	}

	public void setFilepath(String filepath) {
		this.filePath += filepath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNetID() {
		return netID;
	}

	public void setNetID(String netID) {
		this.netID = netID;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
}
